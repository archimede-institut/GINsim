package org.ginsim.servicegui.tool.tbclient;

import org.ginsim.core.graph.view.css.EdgeStyle;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.core.graph.view.css.Style;

public class TBSelector extends Selector {
	public static final String IDENTIFIER = "TBLinkValidator";
	public static final String CAT_DEFAULT = "default";
	public static final EdgeStyle STYLE_NOSIGNATURES = new EdgeStyle();

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
