/**
 * A logging module that implements {@link System.Logger}
 */
module browngu.logging {
	exports browngu.logging;

	provides System.LoggerFinder with browngu.logging.LoggerFinder;
}