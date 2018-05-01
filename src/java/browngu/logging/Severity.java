package browngu.logging;

import java.lang.annotation.Native;

public enum Severity {
	@Native DEBUG("Information of use to the programmer"),
	@Native GPU("Messages from the GPU validation layer"),
	@Native INFO("Detailed information"),
	@Native WARNING("Non-fatal issues"),
	@Native ERROR("Fatal issues");
	
	private final String description;
	
	Severity(String description) {
		this.description = description;
	}
	
	public String description() {
		return description;
	}
}
