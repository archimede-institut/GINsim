package org.ginsim.gui.service.action.tbclient;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;
import fr.univmrs.tagc.GINsim.css.EdgeStyle;
import fr.univmrs.tagc.GINsim.graph.GsAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;

public class TBCascadingStyle extends CascadingStyle {
	public TBCascadingStyle(boolean shouldStoreOldStyle) {
		super(shouldStoreOldStyle);
	}
	public void applyOnEdge(EdgeStyle style, Object edge, GsAttributesReader areader) {
		if (shouldStoreOldStyle && getOldEdges().get(edge) == null) getOldEdges().put(edge, new EdgeStyle(areader));
		style.apply(areader);
	}
	public void restoreAllEdges(GsEdgeAttributesReader areader) {
		super.restoreAllEdges(areader);
		getOldEdges().clear();
	}
}
