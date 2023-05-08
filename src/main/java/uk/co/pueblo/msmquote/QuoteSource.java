package uk.co.pueblo.msmquote;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Map;
import java.util.Properties;

interface QuoteSource {

	// Constants
	static final ZoneId SYS_ZONE_ID = ZoneId.systemDefault();
	static final Properties PROPS = new Properties();
	static final int SOURCE_OK = 0;
	static final int SOURCE_WARN = 1;
	static final int SOURCE_ERROR = 2;
		
	Map<String, String> getNext() throws IOException;
	
	int getStatus();
}