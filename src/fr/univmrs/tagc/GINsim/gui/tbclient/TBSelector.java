package fr.univmrs.tagc.GINsim.gui.tbclient;

import fr.univmrs.tagc.GINsim.css.*;

public class TBSelector extends Selector {
	public static final String IDENTIFIER = "TBLinkValidator";
	public static final String CAT_DEFAULT = "default";
	public static final TBEdgeStyle STYLE_NOSIGNATURES = new TBEdgeStyle(EdgeStyle.NULL_LINECOLOR, EdgeStyle.NULL_SHAPE, EdgeStyle.NULL_LINEEND, TBEdgeStyle.DEFAULT_WIDTH);

	public TBSelector() {
		super(IDENTIFIER);
	}
	public void resetDefaultStyle() {
		addCategory(CAT_DEFAULT, (Style)STYLE_NOSIGNATURES.clone());
	}
	public String getCategoryForNode(Object obj) {
		return null;
	}
	public String getCategoryForEdge(Object obj) {
		return CAT_DEFAULT;
	}
	public boolean respondToNodes() {
		return false;
	}
}
