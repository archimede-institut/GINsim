package org.ginsim.servicegui.tool.composition;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * Interface for the Specification of Composition Parameters
 * 
 * @author Nuno D. Mendes
 */

public interface CompositionSpecificationDialog {

	public int getNumberInstances();

	public void updateNumberInstances(int instances);
	
	public void setAsMapped(RegulatoryNode node);
	public void unsetAsMapped(RegulatoryNode node);
	public List<RegulatoryNode> getMappedNodes();
	
	public RegulatoryGraph getGraph();

}
