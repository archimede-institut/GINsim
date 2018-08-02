package org.ginsim.core.graph;

import java.io.File;
import java.io.IOException;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.io.AbstractFormat;
import org.colomoto.biolqm.io.InputStreamProvider;
import org.colomoto.biolqm.io.LogicalModelFormat;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.kohsuke.MetaInfServices;

// TODO: enable autodiscovery for this format when service files are properly merged
//@MetaInfServices(LogicalModelFormat.class)
public class GINsimFormat extends AbstractFormat {

	public GINsimFormat() {
		super("zginml", "GINsim");
	}

	@Override
	public LogicalModel loadImpl(InputStreamProvider ip) throws IOException {
		
		try {
			Graph g = GSGraphManager.getInstance().open(ip.getFile());
			if (g instanceof RegulatoryGraph) {
				return ((RegulatoryGraph)g).getModel();
			}
			throw new IOException("The graph is not a logical model");
		} catch (GsException e) {
			throw new IOException(e);
		}
	}

}
