package fr.univmrs.tagc.GINsim.gui.tbclient;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;
import fr.univmrs.tagc.GINsim.css.EdgeStyle;
import fr.univmrs.tagc.GINsim.graph.GsAttributesReader;

public class TBCascadingStyle extends CascadingStyle {
	public TBCascadingStyle(boolean shouldStoreOldStyle) {
		super(shouldStoreOldStyle);
	}
	public void applyOnEdge(EdgeStyle style, Object edge, GsAttributesReader areader) {
		if (shouldStoreOldStyle) {
            getOldEdges().put(edge, new EdgeStyle(areader));
        }
		style.apply(areader);
	}
}
