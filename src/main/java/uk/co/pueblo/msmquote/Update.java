package uk.co.pueblo.msmquote;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.healthmarketscience.jackcess.Database;

import uk.co.pueblo.msmcore.MsmCurrency;
import uk.co.pueblo.msmcore.MsmDb;
import uk.co.pueblo.msmcore.MsmDb.CliDatRow;
import uk.co.pueblo.msmcore.MsmDb.DhdColumn;
import uk.co.pueblo.msmcore.MsmSecurity;
import uk.co.pueblo.msmcore.MsmInstrument;

public class Update {

	// Constants
	private static final Logger LOGGER = LogManager.getLogger(Update.class);
	private static final int EXIT_OK = 0;
	// private static final int EXIT_WARN = 1;
	// private static final int EXIT_ERROR = 2;
	private static final int EXIT_FATAL = 3;

	public static void main(String[] args) {

		LOGGER.info("Version {}", Update.class.getPackage().getImplementationVersion());

		int finalExit = EXIT_OK;
		final Instant startTime = Instant.now();

		try {
			// Process command-line arguments
			if (args.length < 2) {
				throw new IllegalArgumentException("Usage: filename password [source]");
			}

			// Open Money database
			final MsmDb msmDb = new MsmDb(args[0], args[1]);

			try {
				// Instantiate Money objects
				final Database openedDb = msmDb.getDb();
				final MsmSecurity msmSecurity = new MsmSecurity(openedDb);
				final MsmCurrency msmCurrency = new MsmCurrency(openedDb);

				// Instantiate quote object according to quote source
				final QuoteSource quoteSource;
				if (args.length == 2) {
					quoteSource = new YahooApiQuote("", msmSecurity.getSymbols(msmDb), msmCurrency.getIsoCodes(msmDb.getDhdVal(DhdColumn.BASE_CURRENCY.getName())));
				} else if (args[2].matches("^https://query2.finance.yahoo.com/v[0-9]+/finance/quote.*")) {
					if (args[2].endsWith("symbols=") || args[2].endsWith("symbols=?")) {
						quoteSource = new YahooApiQuote(args[2], msmSecurity.getSymbols(msmDb), msmCurrency.getIsoCodes(msmDb.getDhdVal(DhdColumn.BASE_CURRENCY.getName())));
					} else {
						quoteSource = new YahooApiQuote(args[2]);
					}
				} else if (args[2].matches("^https://query2.finance.yahoo.com/v[0-9]+/finance/chart.*")) {
					quoteSource = new YahooApiHist(args[2]);
				} else if (args[2].endsWith(".csv")) {
					quoteSource = new YahooCsvHist(args[2]);
				} else if (args.length == 4 && args[3].startsWith("hist ")) {
					quoteSource = new GoogleSheetsHist(args[2], args[3]);
				} else if (args.length == 4) {
					quoteSource = new GoogleSheetsQuote(args[2], args[3]);
				} else {
					throw new IllegalArgumentException("Unrecognised quote source");
				}

				// Do update
				Map<String, String> quoteRow = new HashMap<>();
				String quoteType;
				while ((quoteRow = quoteSource.getNext()) != null) {
					quoteType = quoteRow.get("xType").toString();
					if (quoteType.equals("CURRENCY")) {
						msmCurrency.update(quoteRow); // update currency FX rates
					} else {
						msmSecurity.update(quoteRow); // update other security types
					}
				}

				// Post update processing
				msmSecurity.addNewSpRows(); // add any new rows to the SP table
				msmDb.updateCliDatVal(CliDatRow.OLUPDATE, LocalDateTime.now()); // update online update time-stamp

			} catch (Exception e) {
				LOGGER.fatal(e);
				LOGGER.debug("Exception occurred!", e);
				finalExit = EXIT_FATAL;
			} finally {
				msmDb.closeDb(); // close Money database
			}

		} catch (Exception e) {
			LOGGER.fatal(e);
			LOGGER.debug("Exception occurred!", e);
			finalExit = EXIT_FATAL;
		} finally {
			// Set exit code and finish
			int sourceStatus = QuoteSource.getStatus();
			int updateStatus = MsmInstrument.logSummary();
			int tmpExit = updateStatus > sourceStatus ? updateStatus : sourceStatus;
			finalExit = finalExit > tmpExit ? finalExit : tmpExit;

			LOGGER.info("Duration: {}", Duration.between(startTime, Instant.now()).toString());
			System.exit(finalExit);
		}
	}
}