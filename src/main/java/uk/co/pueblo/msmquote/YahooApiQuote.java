package uk.co.pueblo.msmquote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

public class YahooApiQuote extends YahooSource {

	// Constants
	static final Logger LOGGER = LogManager.getLogger(YahooApiQuote.class);
	private static final String JSON_ROOT = "/quoteResponse/result";
	private static final String PROPS_FILE = "YahooSource.properties";

	// Instance variables
	private Iterator<JsonNode> resultIt;
	private Map<String, String> symbolXlate = new HashMap<>();

	/**
	 * Constructor for auto-completed URL.
	 * 
	 * @param apiUrl   the base URL
	 * @param symbols  the list of investment symbols + country codes
	 * @param isoCodes the list of currency ISO codes, last element is base currency
	 * @throws IOException
	 * @throws InterruptedException
	 */
	YahooApiQuote(String apiUrl, List<String[]> symbols, List<String> isoCodes) throws IOException, InterruptedException {
		super(PROPS_FILE);

		String yahooSymbol = "";
		int n;

		// Build Yahoo security symbols string
		String invSymbols = "";
		String[] symbol = new String[2];
		for (n = 0; n < symbols.size(); n++) {
			// Append the symbols pair to the symbol translation table and the Yahoo symbol to the investment symbols string
			symbol = symbols.get(n);
			if ((yahooSymbol = getYahooSymbol(symbol[0], symbol[1], PROPS)) != null) {
				symbolXlate.put(yahooSymbol, symbol[0]);
				invSymbols = invSymbols + yahooSymbol + ",";
			}
		}
		if (invSymbols.isEmpty()) {
			LOGGER.warn("No security symbols found to update in Money file");
		} else {
			LOGGER.info("Building URL with these security symbols: {}", invSymbols.substring(0, invSymbols.length() - 1));
		}

		// Build Yahoo currency symbols string
		String baseIsoCode = null;
		String fxSymbols = "";
		int isoCodesSz = isoCodes.size();
		for (n = isoCodesSz; n > 0; n--) {
			if (n == isoCodesSz) {
				baseIsoCode = isoCodes.get(n - 1);
				continue;
			}
			// Append the symbols pair to the symbol translation table and to the FX symbols string
			yahooSymbol = baseIsoCode + isoCodes.get(n - 1) + "=X";
			symbolXlate.put(yahooSymbol, yahooSymbol);
			fxSymbols = fxSymbols + yahooSymbol + ",";
		}
		if (fxSymbols.isEmpty()) {
			LOGGER.warn("No FX symbols found to update in Money file");
		} else {
			LOGGER.info("Building URL with these FX symbols: {}", fxSymbols.substring(0, fxSymbols.length() - 1));
		}

		String allSymbols = invSymbols + fxSymbols;
		if (!apiUrl.endsWith("symbols=?") && !allSymbols.isEmpty()) {
			// Get quote data
			resultIt = getJson(apiUrl + allSymbols.substring(0, allSymbols.length() - 1)).at(JSON_ROOT).elements();
		}
	}

	/**
	 * Constructor for user-completed URL.
	 * 
	 * @param apiUrl the complete Yahoo Finance quote API URL
	 * @throws IOException
	 * @throws InterruptedException
	 */
	YahooApiQuote(String apiUrl) throws IOException, InterruptedException {
		super(PROPS_FILE);
		resultIt = getJson(apiUrl).at(JSON_ROOT).elements();
	}

	/**
	 * Gets the next row of quote data from the JSON iterator.
	 * 
	 * @return the quote row or null if no more data
	 */
	public Map<String, String> getNext() {
		// Get next JSON node from iterator
		if (resultIt == null || !resultIt.hasNext()) {
			return null;
		}

		JsonNode result = resultIt.next();
		Map<String, String> returnRow = new HashMap<>();

		try {
			// Add quote type to return row
			String quoteType = result.get("quoteType").asText();
			returnRow.put("xType", quoteType);

			// Add symbol to return row
			String yahooSymbol = result.get("symbol").asText();
			if (symbolXlate.isEmpty()) {
				returnRow.put("xSymbol", yahooSymbol);
			} else {
				returnRow.put("xSymbol", symbolXlate.get(yahooSymbol));
			}

			// Get divisor or multiplier for quote currency and quote type
			String quoteCurrency = result.get("currency").asText();
			String prop;
			int quoteDivisor = ((prop = PROPS.getProperty("divisor." + quoteCurrency + "." + quoteType)) == null) ? 1 : Integer.parseInt(prop);
			int quoteMultiplier = ((prop = PROPS.getProperty("multiplier." + quoteCurrency + "." + quoteType)) == null) ? 100 : Integer.parseInt(prop);

			// Add quote values to return row
			int n = 1;
			while ((prop = PROPS.getProperty("api." + quoteType + "." + n++)) != null) {
				String[] apiMap = prop.split(",");
				if (result.has(apiMap[0])) {
					String value = result.get(apiMap[0]).asText();
					if (apiMap.length == 3) {
						if (apiMap[2].equals("d")) {
							value = String.valueOf(Double.parseDouble(value) / quoteDivisor);
						} else if (apiMap[2].equals("m")) {
							value = String.valueOf(Double.parseDouble(value) * quoteMultiplier);
						}
					}
					returnRow.put(apiMap[1], value);
				}
			}

		} catch (NullPointerException e) {
			LOGGER.debug("Exception occurred!", e);
		}

		return returnRow;
	}

	/**
	 * Generates a Yahoo symbol from the Money symbol.
	 * 
	 * @param symbol  the Money symbol for the security
	 * @param country the Money country for the security
	 * @param props   the YahooQuote properties
	 * @return the equivalent Yahoo symbol
	 */
	private static String getYahooSymbol(String symbol, String country, Properties props) {
		String yahooSymbol = symbol;
		String prop;
		if (symbol.matches("^\\$US:.*")) {
			// Symbol is in Money index format '$US:symbol'
			if ((prop = props.getProperty("index." + symbol.substring(4))) != null) {
				yahooSymbol = prop;
			}
		} else if (symbol.matches("^\\$..:.*")) {
			// Symbol is in Money index format '$xx:symbol'
			yahooSymbol = "^" + symbol.substring(4);
		} else if (symbol.matches("^\\$.*")) {
			// Symbol is in Money index format '$symbol'
			yahooSymbol = "^" + symbol.substring(1);
		} else if (symbol.matches("^..:.*")) {
			// Symbol is in Money security format 'xx:symbol'
			if ((prop = props.getProperty("exchange." + country)) != null) {
				yahooSymbol = symbol.substring(3) + prop;
			}
		} else if (!symbol.matches("(.*\\..$|.*\\...$|^\\^.*)")) {
			// Symbol is not already in Yahoo format 'symbol.x', 'symbol.xx' or '^symbol"
			if ((prop = props.getProperty("exchange." + country)) != null) {
				yahooSymbol = symbol + prop;
			}
		}
		return yahooSymbol.toUpperCase();
	}
}