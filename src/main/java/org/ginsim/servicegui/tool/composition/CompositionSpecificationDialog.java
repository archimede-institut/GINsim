package org.ginsim.servicegui.tool.composition;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;

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

	public void addNeighbour(int m, int n);

	public void removeNeighbour(int m, int n);

	public boolean hasNeihgbours(int m);

	public boolean areNeighbours(int m, int n);

	public IntegrationFunctionMapping getMapping();

	public RegulatoryGraph getGraph();

	public boolean isTrulyMapped(RegulatoryNode node, int m);

	public Collection<AbstractMap.Entry<RegulatoryNode, Integer>> getInfluencedModuleInputs(
			RegulatoryNode proper, int moduleIndex);

	public Collection<AbstractMap.Entry<RegulatoryNode, Integer>> getMappedToModuleArguments(
			RegulatoryNode input, int moduleIndex);
}
