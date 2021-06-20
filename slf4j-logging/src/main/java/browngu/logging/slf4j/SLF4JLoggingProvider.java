package browngu.logging.slf4j;

import browngu.logging.Logger;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * This class delegates all SLF4J calls to the default logger instance
 */
public class SLF4JLoggingProvider implements SLF4JServiceProvider {
	private ILoggerFactory loggerFactory;
	private IMarkerFactory markerFactory;
	private MDCAdapter mdcAdapter;

	@Override
	public ILoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	@Override
	public IMarkerFactory getMarkerFactory() {
		return markerFactory;
	}

	@Override
	public MDCAdapter getMDCAdapter() {
		return mdcAdapter;
	}

	@Override
	public String getRequesteApiVersion() {
		return "1.8.99";
	}

	@Override
	public void initialize() {
		loggerFactory = name -> new SLF4JLoggingAdaptor(Logger.logger());
		markerFactory = new BasicMarkerFactory();
		mdcAdapter = new BasicMDCAdapter();
	}
}
