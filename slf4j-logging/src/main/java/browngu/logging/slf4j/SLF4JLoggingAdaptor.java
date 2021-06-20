package browngu.logging.slf4j;

import browngu.logging.Logger;
import browngu.logging.Severity;
import org.slf4j.helpers.MarkerIgnoringBase;

class SLF4JLoggingAdaptor extends MarkerIgnoringBase {
	private final Logger delegate;

	SLF4JLoggingAdaptor(Logger delegate) {
		this.delegate = delegate;

		delegate.ignoreFramesFrom(getClass());
	}

	private static String convertFormat(String format) {
		return format.replace("%", "%%").replace("{}", "%s");
	}

	@Override
	public boolean isTraceEnabled() {
		return delegate.isLoggingEnabled(Severity.TRACE);
	}

	@Override
	public void trace(String msg) {
		delegate.log(Severity.TRACE, msg);
	}

	@Override
	public void trace(String format, Object arg) {
		delegate.log(Severity.TRACE, convertFormat(format), arg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		delegate.log(Severity.TRACE, convertFormat(format), arg1, arg2);
	}

	@Override
	public void trace(String format, Object... arguments) {
		delegate.log(Severity.TRACE, convertFormat(format), arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		delegate.log(Severity.TRACE, msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return delegate.isLoggingEnabled(Severity.DEBUG);
	}

	@Override
	public void debug(String msg) {
		delegate.log(Severity.DEBUG, msg);
	}

	@Override
	public void debug(String format, Object arg) {
		delegate.log(Severity.DEBUG, convertFormat(format), arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		delegate.log(Severity.DEBUG, convertFormat(format), arg1, arg2);
	}

	@Override
	public void debug(String format, Object... arguments) {
		delegate.log(Severity.DEBUG, convertFormat(format), arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		delegate.log(Severity.DEBUG, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return delegate.isLoggingEnabled(Severity.INFO);
	}

	@Override
	public void info(String msg) {
		delegate.log(Severity.INFO, msg);
	}

	@Override
	public void info(String format, Object arg) {
		delegate.log(Severity.INFO, convertFormat(format), arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		delegate.log(Severity.INFO, convertFormat(format), arg1, arg2);
	}

	@Override
	public void info(String format, Object... arguments) {
		delegate.log(Severity.INFO, convertFormat(format), arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		delegate.log(Severity.INFO, msg, t);
	}
	
	@Override
	public boolean isWarnEnabled() {
		return delegate.isLoggingEnabled(Severity.WARNING);
	}

	@Override
	public void warn(String msg) {
		delegate.log(Severity.WARNING, msg);
	}

	@Override
	public void warn(String format, Object arg) {
		delegate.log(Severity.WARNING, convertFormat(format), arg);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		delegate.log(Severity.WARNING, convertFormat(format), arg1, arg2);
	}

	@Override
	public void warn(String format, Object... arguments) {
		delegate.log(Severity.WARNING, convertFormat(format), arguments);
	}

	@Override
	public void warn(String msg, Throwable t) {
		delegate.log(Severity.WARNING, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return delegate.isLoggingEnabled(Severity.ERROR);
	}

	@Override
	public void error(String msg) {
		delegate.log(Severity.ERROR, msg);
	}

	@Override
	public void error(String format, Object arg) {
		delegate.log(Severity.ERROR, convertFormat(format), arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		delegate.log(Severity.ERROR, convertFormat(format), arg1, arg2);
	}

	@Override
	public void error(String format, Object... arguments) {
		delegate.log(Severity.ERROR, convertFormat(format), arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		delegate.log(Severity.ERROR, msg, t);
	}
}
