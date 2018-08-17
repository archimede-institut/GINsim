package org.ginsim.core.graph;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.io.AbstractFormat;
import org.colomoto.biolqm.io.BaseLoader;
import org.colomoto.biolqm.io.ModelLoader;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;

import java.io.IOException;


// TODO: enable autodiscovery for this format when service files are properly merged
//@MetaInfServices(LogicalModelFormat.class)
public class GINsimFormat extends AbstractFormat {

	public GINsimFormat() {
		super("zginml", "GINsim");
	}

	@Override
	public ModelLoader getLoader() {
		return new GINsimLoader();
	}
}


class GINsimLoader extends BaseLoader {

	@Override
	protected LogicalModel performTask() throws Exception {
		Graph g = GSGraphManager.getInstance().open(streams.getFile());
		if (g instanceof RegulatoryGraph) {
			return ((RegulatoryGraph)g).getModel();
		}
		throw new IOException("The graph is not a logical model");
	}
}
