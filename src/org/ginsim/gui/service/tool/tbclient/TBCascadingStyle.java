package org.ginsim.gui.service.tool.tbclient;

import org.ginsim.graph.common.AttributesReader;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.view.css.CascadingStyle;
import org.ginsim.graph.view.css.EdgeStyle;


public class TBCascadingStyle extends CascadingStyle {
	public TBCascadingStyle(boolean shouldStoreOldStyle) {
		super(shouldStoreOldStyle);
	}
	public void applyOnEdge(EdgeStyle style, Object edge, AttributesReader areader) {
		if (shouldStoreOldStyle && getOldEdges().get(edge) == null) getOldEdges().put(edge, new EdgeStyle(areader));
		style.apply(areader);
	}
	public void restoreAllEdges(EdgeAttributesReader areader) {
		super.restoreAllEdges(areader);
		getOldEdges().clear();
	}
}
