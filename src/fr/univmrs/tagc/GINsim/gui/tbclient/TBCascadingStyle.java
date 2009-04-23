package fr.univmrs.tagc.GINsim.gui.tbclient;

import fr.univmrs.tagc.GINsim.css.*;
import fr.univmrs.tagc.GINsim.graph.*;

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
