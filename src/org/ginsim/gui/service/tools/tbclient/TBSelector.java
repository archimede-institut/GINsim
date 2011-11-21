package org.ginsim.gui.service.tools.tbclient;

import org.ginsim.graph.view.css.EdgeStyle;
import org.ginsim.graph.view.css.Selector;
import org.ginsim.graph.view.css.Style;

public class TBSelector extends Selector {
	public static final String IDENTIFIER = "TBLinkValidator";
	public static final String CAT_DEFAULT = "default";
	public static final EdgeStyle STYLE_NOSIGNATURES = new EdgeStyle(EdgeStyle.NULL_LINECOLOR, EdgeStyle.NULL_LINEEND,  EdgeStyle.NULL_SHAPE,  EdgeStyle.NULL_BORDER);

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
