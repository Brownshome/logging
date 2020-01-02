package browngu.logging;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for logging messages provided to it.
 */
public class Logger {
	private static final Logger logger = new Logger();

	private static final class Output {
		final Writer writer;
		final int minimumLevel;

		Output(Writer writer, int minimumLevel) {
			this.writer = writer;
			this.minimumLevel = minimumLevel;
		}
	}

	private final List<Output> outputs = new ArrayList<>();
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(
			"'['yyyy-MM-dd']['HH:mm:ss.ss']'"
	).withZone(ZoneId.systemDefault());

	@FunctionalInterface
	public interface Level {
		int level();
	}

	/**
	 * Adds a logging output stream to be used.
	 * @param stream The stream to output logging data to, the data will be encoded in UTF-8. Each log will be followed
	 *               by a system specific separator.
	 * @param minimumLevel Any events less important than this are no logged to this stream.
	 */
	public void addLoggingOutput(OutputStream stream, Level minimumLevel) {
		outputs.add(new Output(new OutputStreamWriter(stream, StandardCharsets.UTF_8), minimumLevel.level()));
		outputs.sort(Comparator.comparingInt(a -> a.minimumLevel));
	}

	/**
	 * Adds a logging output stream to be used.
	 * @param writer The writer to output logging data to. Each log will be followed by a system specific separator.
	 * @param minimumLevel Any events less important than this are no logged to this stream.
	 */
	public void addLoggingOutput(Writer writer, Level minimumLevel) {
		outputs.add(new Output(writer, minimumLevel.level()));
		outputs.sort(Comparator.comparingInt(a -> a.minimumLevel));
	}

	/**
	 * Returns a default shared logger to use.
	 * If the output streams access shared state then this is not thread safe.
	 **/
	public static Logger logger() {
		return logger;
	}

	public void log(Level level, Throwable throwable, String message) {
		var stackWalker = StackWalker.getInstance(Set.of(
				StackWalker.Option.RETAIN_CLASS_REFERENCE,
				StackWalker.Option.SHOW_HIDDEN_FRAMES,
				StackWalker.Option.SHOW_REFLECT_FRAMES));

		var frame = stackWalker
				.walk(stream -> stream
						.dropWhile(f -> Logger.class.isAssignableFrom(f.getDeclaringClass()))
						.findFirst()
						.orElse(null));

		Instant time = Instant.now();

		Thread callingThread = Thread.currentThread();

		StringBuilder log = new StringBuilder();

		log.append(timeFormatter.format(time));
		log.append('[').append(level.toString()).append(']');
		log.append('[').append(callingThread.getName()).append(']');
		log.append(' ').append(message).append(' ');
		log.append('(').append(frame).append(')');
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

			if(output.minimumLevel > level.level())
				break;

			try {
				// Synchronize on the stream to try and guard against race conditions with other logger calls.
				synchronized(output.writer) {
					output.writer.write(log.toString());
					output.writer.flush();
				}
			} catch(IOException e) {
				iterator.remove();
				log(Severity.ERROR, e, "Failed to log a message, removing output '%s'", output);
			}
		}
	}

	private Level defaultLevel() {
		return Severity.INFO;
	}

	public void log(Level level, Throwable throwable, String message, Object... args) {
		log(level, throwable, String.format(message, args));
	}

	/**
	 * Logs the given message with the ERROR level for this logger.
	 */
	public void log(Throwable throwable, String message, Object... args) {
		log(Severity.ERROR, throwable, message, args);
	}

	public void log(Level level, String message, Object... args) {
		log(level, null, message, args);
	}

	public void log(Level level, Throwable throwable) {
		log(level, throwable, (String) null);
	}

	/**
	 * Logs the given message with the default level for this logger.
	 */
	public void log(String message, Object... args) {
		log(defaultLevel(), null, message, args);
	}

	/**
	 * Logs the given message with the ERROR level for this logger.
	 */
	public void log(Throwable throwable) {
		log(Severity.ERROR, throwable, null);
	}
}
