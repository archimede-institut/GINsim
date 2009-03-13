package fr.univmrs.tagc.GINsim.gui.tbclient;

import fr.univmrs.tagc.GINsim.css.*;
import fr.univmrs.tagc.GINsim.graph.*;

public class TBCascadingStyle extends CascadingStyle {
	public TBCascadingStyle(boolean shouldStoreOldStyle) {
		super(shouldStoreOldStyle);
	}
	public void applyOnEdge(EdgeStyle style, Object edge, GsAttributesReader areader) {
		if (shouldStoreOldStyle) getOldEdges().put(edge, new TBEdgeStyle(areader));
		System.err.println("applyOnEdge : " + edge.toString() + " " + edge.hashCode());
		style.apply(areader);
	}

}
