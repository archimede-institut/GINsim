package org.ginsim.servicegui.tool.composition;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;

public interface CompositionSpecificationDialog {
	
	public int getNumberInstances();
	public void updateNumberInstances(int instances);
	public RegulatoryGraph getGraph();

}
