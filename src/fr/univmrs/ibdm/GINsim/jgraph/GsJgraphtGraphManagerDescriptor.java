package fr.univmrs.ibdm.GINsim.jgraph;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManagerDescriptor;
import fr.univmrs.ibdm.GINsim.gui.GsMainFrame;

/**
 * register the jgraph graph manager
 */
public class GsJgraphtGraphManagerDescriptor extends GsGraphManagerDescriptor {

	/**
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraphManagerDescriptor#getGraphManagerName()
	 */
	public String getGraphManagerName() {
		return "jgraph/t";
	}

	/**
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraphManagerDescriptor#getNew(fr.univmrs.ibdm.GINsim.graph.GsGraph, fr.univmrs.ibdm.GINsim.gui.GsMainFrame)
	 */
	public GsGraphManager getNew(GsGraph g, GsMainFrame m) {
		return new GsJgraphtGraphManager(g, m);
	}
}
