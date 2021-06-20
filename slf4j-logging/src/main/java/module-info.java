import browngu.logging.slf4j.SLF4JLoggingProvider;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * A logging module that implements {@link org.slf4j.spi.SLF4JServiceProvider}
 */
module browngu.logging.slf4j {
	requires transitive org.slf4j;
	requires browngu.logging;

	provides SLF4JServiceProvider with SLF4JLoggingProvider;
}