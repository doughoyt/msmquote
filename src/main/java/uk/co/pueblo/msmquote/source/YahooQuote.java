package uk.co.pueblo.msmquote.source;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class YahooQuote {

	// Constants
	protected static final Logger LOGGER = LogManager.getLogger(YahooQuote.class);
	protected static final ZoneId SYS_ZONE_ID = ZoneId.systemDefault();
	private static final String BASE_PROPS = "YahooQuote.properties";

	// Class variables
	protected static Properties baseProps;

	//Instance variables
	protected boolean isQuery;
	protected QuoteSummary quoteSummary;

	static {
		try {
			// Set up base properties			
			InputStream propsIs = YahooApiQuote.class.getClassLoader().getResourceAsStream(BASE_PROPS);
			baseProps = new Properties();
			baseProps.load(propsIs);
		} catch (IOException e) {
			LOGGER.fatal(e);
		}
	}

	public abstract Map<String, Object> getNext() throws IOException;

	public boolean isQuery() {
		return isQuery;
	}
}