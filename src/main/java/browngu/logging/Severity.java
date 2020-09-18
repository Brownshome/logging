package browngu.logging;

import java.lang.annotation.Native;

public enum Severity implements Logger.Level {
	@Native DEBUG(10, "Information of use to the programmer"),
	@Native INFO(20, "Detailed information"),
	@Native WARNING(30, "Non-fatal issues"),
	@Native ERROR(40, "Fatal issues");

	private final String description;
	private final int level;

	Severity(int level, String description) {
		this.description = description;
		this.level = level;
	}
	
	public String description() {
		return description;
	}

	@Override
	public int level() {
		return level;
	}
}
