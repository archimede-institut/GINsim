package org.ginsim.servicegui.tool.tbclient;

import org.ginsim.core.graph.view.AttributesReader;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.css.CascadingStyle;
import org.ginsim.core.graph.view.css.EdgeStyle;


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
