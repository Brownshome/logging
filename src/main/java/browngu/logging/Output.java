package browngu.logging;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Defines an output channel for the logger.
 *
 * @param writer the writer to output logs to
 * @param minimumLevel Any events less important than this are not logged to this writer.
 */
public record Output(Writer writer, int minimumLevel) {
	/**
	 * Defines an output channel using an output stream
	 *
	 * @param stream The stream to output logging data to, the data will be encoded in UTF-8. Each log will be followed
	 *               by a system specific separator.
	 * @param minimumLevel Any events less important than this are not logged to this stream.
	 */
	public Output(OutputStream stream, int minimumLevel) {
		this(new OutputStreamWriter(stream, StandardCharsets.UTF_8), minimumLevel);
	}

	public Output {
		assert writer != null;
	}
}
