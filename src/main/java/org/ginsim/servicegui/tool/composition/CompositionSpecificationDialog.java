package org.ginsim.servicegui.tool.composition;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;

/**
 * Interface for the Specification of Composition Parameters
 * 
 * @author Nuno D. Mendes
 */

public interface CompositionSpecificationDialog {

	public int getNumberInstances();

	public void updateNumberInstances(int instances);

	public RegulatoryGraph getGraph();

}
