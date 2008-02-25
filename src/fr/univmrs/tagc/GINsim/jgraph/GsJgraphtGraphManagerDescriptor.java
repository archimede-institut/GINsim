package fr.univmrs.tagc.GINsim.jgraph;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphManagerDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;

/**
 * register the jgraph graph manager
 */
public class GsJgraphtGraphManagerDescriptor extends GsGraphManagerDescriptor {

	/**
	 * @see fr.univmrs.tagc.GINsim.graph.GsGraphManagerDescriptor#getGraphManagerName()
	 */
	public String getGraphManagerName() {
		return "jgraph/t";
	}

	/**
	 * @see fr.univmrs.tagc.GINsim.graph.GsGraphManagerDescriptor#getNew(fr.univmrs.tagc.GINsim.graph.GsGraph, fr.univmrs.tagc.GINsim.gui.GsMainFrame)
	 */
	public GsGraphManager getNew(GsGraph g, GsMainFrame m) {
		return new GsJgraphtGraphManager(g, m);
	}
}
