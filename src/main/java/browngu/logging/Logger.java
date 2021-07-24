package browngu.logging;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class is responsible for logging messages provided to it.
 */
public class Logger {
	private static final Logger logger = new Logger();

	static {
		logger().addLoggingOutput(System.out, Severity.INFO);
		logger().ignoreFramesFrom(Logger.class);
		logger().ignoreFramesFrom(System.Logger.class);
	}

	private final List<Output> outputs = new ArrayList<>();
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(
			"'['yyyy-MM-dd']['HH:mm:ss.SSS']'"
	).withZone(ZoneId.systemDefault());
	private final Set<Class<?>> ignoredFrames = new HashSet<>();

	/**
	 * A level of logging severity. This is intended to be used to define custom logging levels.
	 */
	@FunctionalInterface
	public interface Level {
		/**
		 * The severity of this level. The numbers are compatible with {@link java.lang.System.Logger.Level}
		 * @return the severity of this level
		 */
		int level();
	}

	/**
	 * Removes all existing outputs and adds the specified outputs
	 * @param outputs an array of outputs
	 */
	public void setLoggingOutputs(Output... outputs) {
		assert outputs != null;

		this.outputs.clear();
		this.outputs.addAll(Arrays.asList(outputs));
		this.outputs.sort(Comparator.comparingInt(Output::minimumLevel));
	}

	/**
	 * Removes all existing outputs and adds the specified outputs
	 * @param outputs an iterable of outputs
	 */
	public void setLoggingOutputs(Iterable<Output> outputs) {
		assert outputs != null;

		this.outputs.clear();
		outputs.forEach(this.outputs::add);
		this.outputs.sort(Comparator.comparingInt(Output::minimumLevel));
	}

	/**
	 * Adds a logging output stream to be used.
	 * @param stream The stream to output logging data to, the data will be encoded in UTF-8. Each log will be followed
	 *               by a system specific separator.
	 * @param minimumLevel Any events less important than this are no logged to this stream.
	 */
	public void addLoggingOutput(OutputStream stream, Level minimumLevel) {
		outputs.add(new Output(new OutputStreamWriter(stream, StandardCharsets.UTF_8), minimumLevel.level()));
		outputs.sort(Comparator.comparingInt(Output::minimumLevel));
	}

	/**
	 * Adds a logging output stream to be used.
	 * @param writer The writer to output logging data to. Each log will be followed by a system specific separator.
	 * @param minimumLevel Any events less important than this are no logged to this stream.
	 */
	public void addLoggingOutput(Writer writer, Level minimumLevel) {
		outputs.add(new Output(writer, minimumLevel.level()));
		outputs.sort(Comparator.comparingInt(Output::minimumLevel));
	}

	/**
	 * Adds a logging output. Note that this does not remove existing outputs
	 * @param output the output object to add
	 */
	public void addLoggingOutput(Output output) {
		outputs.add(output);
		outputs.sort(Comparator.comparingInt(Output::minimumLevel));
	}

	/**
	 * Returns a default shared logger to use, upon creation this logger will have one configured output to {@link System#out}.
	 * If the output streams access shared state then this is not thread safe.
	 *
	 * @return a shared logging object
	 */
	public static Logger logger() {
		return logger;
	}

	/**
	 * Configures this logger to skip stack frames from any subclass of the provided class
	 * @param clazz the class to ignore
	 */
	public void ignoreFramesFrom(Class<?> clazz) {
		ignoredFrames.add(clazz);
	}

	/**
	 * Configures this logger to stop ignoring stack frames from any subclass of the provided class
	 * @param clazz the class to stop ignoring
	 */
	public void acceptFramesFrom(Class<?> clazz) {
		ignoredFrames.remove(clazz);
	}

	/**
	 * Logs and prints a throwable stack-trace
	 * @param level the log level
	 * @param throwable the throwable to log
	 * @param message a message to log
	 */
	public void log(Level level, Throwable throwable, String message) {
		if(outputs.isEmpty() || outputs.get(0).minimumLevel() > level.level())
			return;

		var stackWalker = StackWalker.getInstance(Set.of(
				StackWalker.Option.RETAIN_CLASS_REFERENCE,
				StackWalker.Option.SHOW_HIDDEN_FRAMES,
				StackWalker.Option.SHOW_REFLECT_FRAMES));

		var frame = stackWalker
				.walk(stream -> stream
						.dropWhile(f -> ignoredFrames.stream().anyMatch(c -> c.isAssignableFrom(f.getDeclaringClass())))
						.findFirst()
						.orElse(null));

		Instant time = Instant.now();

		Thread callingThread = Thread.currentThread();

		StringBuilder log = new StringBuilder();

		log.append(timeFormatter.format(time));
		log.append('[').append(level).append(']');
		log.append('[').append(callingThread.getName()).append(']');
		log.append(' ');

		if (message != null) {
			log.append(message).append(' ');
		}

		log.append('(').append(Objects.toString(frame, "UNKNOWN-FRAME")).append(')');
		log.append(System.lineSeparator());

		if(throwable != null) {
			log.append("*********************** STACK TRACE ***********************").append(System.lineSeparator());

			// No need to close these, as they are not IO based
			StringWriter stacktraceWriter = new StringWriter();
			throwable.printStackTrace(new PrintWriter(stacktraceWriter));
			log.append(stacktraceWriter).append(System.lineSeparator());

			log.append("*********************** TRACE ENDS ************************").append(System.lineSeparator());
		}

		for(var iterator = outputs.iterator(); iterator.hasNext(); ) {
			var output = iterator.next();

			if(output.minimumLevel() > level.level())
				break;

			try {
				// Synchronize on the stream to try and guard against race conditions with other logger calls.
				synchronized(output.writer()) {
					output.writer().write(log.toString());
					output.writer().flush();
				}
			} catch(IOException e) {
				iterator.remove();
				log(Severity.ERROR, e, "Failed to log a message, removing output '%s'", output);
			}
		}
	}

	/**
	 * The default logging level for this logger
	 * @return a level
	 */
	protected Level defaultLevel() {
		return Severity.INFO;
	}

	/**
	 * Queries if a message at the given level will ever be logged
	 * @param level the level to query
	 * @return true if the level will be logged
	 */
	public boolean isLoggingEnabled(Level level) {
		return !outputs.isEmpty() && outputs.get(0).minimumLevel() <= level.level();
	}

	/**
	 * Logs and prints an exception stack-trace with formatting
	 * @param level the level
	 * @param throwable the exception to print the trace of
	 * @param message the format string
	 * @param args format string arguments
	 */
	public void log(Level level, Throwable throwable, String message, Object... args) {
		log(level, throwable, String.format(message, args));
	}

	/**
	 * Logs the given message with the ERROR level for this logger and prints the stack trace.
	 * @param throwable the exception to print the stack trace of
	 * @param message the format string
	 * @param args format string arguments
	 */
	public void log(Throwable throwable, String message, Object... args) {
		log(Severity.ERROR, throwable, message, args);
	}

	/**
	 * Logs a formatted message
	 * @param level the level to log at
	 * @param message the format string to log
	 * @param args format string arguments
	 */
	public void log(Level level, String message, Object... args) {
		log(level, null, message, args);
	}

	/**
	 * Prints an exception stack-trace
	 * @param level the level to log at
	 * @param throwable the exception to log
	 */
	public void log(Level level, Throwable throwable) {
		log(level, throwable, (String) null);
	}

	/**
	 * Logs the given message with the default level for this logger. The default level is defined by {@link Logger#defaultLevel()}
	 * @param message the format string to log
	 * @param args format string arguments
	 */
	public void log(String message, Object... args) {
		log(defaultLevel(), null, message, args);
	}

	/**
	 * Logs the given message with the ERROR level for this logger and prints the stack trace
	 * @param throwable the exception to log.
	 */
	public void log(Throwable throwable) {
		log(Severity.ERROR, throwable, null);
	}
}
