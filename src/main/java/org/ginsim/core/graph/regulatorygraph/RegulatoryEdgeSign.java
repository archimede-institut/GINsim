package org.ginsim.core.graph.regulatorygraph;

public enum RegulatoryEdgeSign {
	POSITIVE("positive", "+", 0),
	NEGATIVE("negative", "-", 1),
	UNKNOWN("unknown", "?", 2),
	DUAL("dual", "\u00B1", 3);

	private final String longDesc;
	private final String shortDesc;
	private final int indexGUI;

	private RegulatoryEdgeSign(String longDesc, String shortDesc, int index) {
		this.longDesc = longDesc;
		this.shortDesc = shortDesc;
		this.indexGUI = index;
	}

	public String getLongDesc() {
		return longDesc;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public int getIndexForGUI() {
		return indexGUI;
	}
	
	public static String[] getShortDescForGUI() {
		String[] saShort = { POSITIVE.shortDesc, NEGATIVE.shortDesc, UNKNOWN.shortDesc, DUAL.shortDesc };
		return saShort;
	}

	public static RegulatoryEdgeSign getFromPos(int index) {
		RegulatoryEdgeSign res;
		switch (index) {
		case 1:
			res = RegulatoryEdgeSign.NEGATIVE;
			break;
		case 2:
			res = RegulatoryEdgeSign.UNKNOWN;
			break;
		case 3:
			res = RegulatoryEdgeSign.DUAL;
			break;
		default:
			res = RegulatoryEdgeSign.POSITIVE;
		}
		return res;
	}
}
