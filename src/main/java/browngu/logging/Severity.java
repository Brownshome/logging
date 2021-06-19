package browngu.logging;

import java.lang.annotation.Native;

/**
 * Pre-defined severity levels matching the levels used by {@link System.Logger.Level}
 */
public enum Severity implements Logger.Level {
	/**
	 * Diagnostic information
	 */
	@Native TRACE(System.Logger.Level.TRACE.getSeverity(), "Diagnostic information"),

	/**
	 * Information of use to developers
	 */
	@Native DEBUG(System.Logger.Level.DEBUG.getSeverity(), "Information of use to developers"),

	/**
	 * Detailed information
	 */
	@Native INFO(System.Logger.Level.INFO.getSeverity(), "Detailed information"),

	/**
	 * Non-fatal issues
	 */
	@Native WARNING(System.Logger.Level.WARNING.getSeverity(), "Non-fatal issues"),

	/**
	 * Fatal issues
	 */
	@Native ERROR(System.Logger.Level.ERROR.getSeverity(), "Fatal issues");

	private final String description;
	private final int level;

	Severity(int level, String description) {
		this.description = description;
		this.level = level;
	}

	/**
	 * Describes this severity
	 * @return a string description of this severity
	 */
	public String description() {
		return description;
	}

	@Override
	public int level() {
		return level;
	}
}
