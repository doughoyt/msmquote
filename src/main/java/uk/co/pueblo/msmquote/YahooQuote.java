package uk.co.pueblo.msmquote;

import java.io.IOException;
import java.util.Map;

public interface YahooQuote {
	public Map<String, Object> getNext() throws IOException;
	public boolean isQuery();
}