package org.ginsim.service.export.sat;

public enum SATExportType {
	STABILITY_CONDITION("stability conditions"), INTERVENTION("interventions"), STABLE_STATE(
			"stable states");
	private String type;

	private SATExportType(String type) {
		this.type = type;
	}

	public String toString() {
		return this.type;
	}
}