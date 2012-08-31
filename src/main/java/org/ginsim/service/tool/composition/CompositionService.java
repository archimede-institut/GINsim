package org.ginsim.service.tool.composition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameterList;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(Service.class)
@Alias("composition")
public class CompositionService implements Service {

	/**
	 * Run the Composition by replicating a Regulatory graph, according to a
	 * Topology and an IntegrationFunctionMapping and return the composed graph
	 * after invoking the necessary reduction operations
	 * 
	 * @param graph
	 *            the current RegulatoryGraph
	 * @param topology
	 *            the composition Topology, indicating the neighbourhood
	 *            relationships
	 * @param mapping
	 *            the integration functions to apply to each mapped input
	 * 
	 @return RegulatoryGraph
	 */

	public RegulatoryGraph run(RegulatoryGraph graph, Topology topology,
			IntegrationFunctionMapping mapping) {

		return computeComposedGraph(graph, topology, mapping);
	}

	public RegulatoryGraph computeComposedGraph(RegulatoryGraph graph,
			Topology topology, IntegrationFunctionMapping mapping) {
		RegulatoryGraph composedGraph = GraphManager.getInstance().getNewGraph(
				RegulatoryGraph.class);

		NodeAttributesReader oldNodeReader = graph.getNodeAttributeReader();
		NodeAttributesReader newNodeReader = composedGraph
				.getNodeAttributeReader();

		EdgeAttributesReader oldEdgeReader = graph.getEdgeAttributeReader();
		EdgeAttributesReader newEdgeReader = composedGraph
				.getEdgeAttributeReader();

		for (RegulatoryNode node : graph.getNodeOrder()) {

			for (int i = 0; i < topology.getNumberInstances(); i++) {
				RegulatoryNode newNode = composedGraph.addNewNode(
						computeNewName(node.getId(), i),
						computeNewName(node.getName(), i), node.getMaxValue());

				newNode.getNodeInfo().setMax(node.getNodeInfo().getMax());
				newNode.setGsa((Annotation) (node.getAnnotation().clone()));

				// mapped inputs will no longer be inputs
				// unmapped input remain free
				if (node.isInput() && !mapping.isMapped(node)) {
					newNode.setInput(node.isInput(), composedGraph);
				}

				oldNodeReader.setNode(node);
				newNodeReader.setNode(newNode);
				newNodeReader.copyFrom(oldNodeReader);

			}
		}

		HashMap<RegulatoryMultiEdge, List<RegulatoryMultiEdge>> edgeAssociation = new HashMap<RegulatoryMultiEdge, List<RegulatoryMultiEdge>>();

		for (RegulatoryMultiEdge multiEdge : graph.getEdges()) {
			for (int i = 0; i < topology.getNumberInstances(); i++) {
				RegulatoryNode source = multiEdge.getSource();
				RegulatoryNode target = multiEdge.getTarget();

				RegulatoryNode newSource = composedGraph
						.getNodeByName(computeNewName(source.getName(), i));
				RegulatoryNode newTarget = composedGraph
						.getNodeByName(computeNewName(target.getName(), i));

				RegulatoryMultiEdge newMultiEdge = new RegulatoryMultiEdge(
						composedGraph, newSource, newTarget,
						RegulatoryEdgeSign.POSITIVE);

				newMultiEdge.copyFrom(multiEdge);
				composedGraph.addEdge(newMultiEdge);

				oldEdgeReader.setEdge(multiEdge);
				newEdgeReader.setEdge(newMultiEdge);
				newEdgeReader.copyFrom(oldEdgeReader);

				if (!edgeAssociation.containsKey(multiEdge)) {
					edgeAssociation.put(multiEdge,
							new ArrayList<RegulatoryMultiEdge>());
				}

				edgeAssociation.get(multiEdge).add(i, newMultiEdge);

			}
		}

		for (RegulatoryNode node : graph.getNodeOrder()) {
			for (int i = 0; i < topology.getNumberInstances(); i++) {

				RegulatoryNode newNode = composedGraph
						.getNodeByName(computeNewName(node.getName(), i));
				LogicalParameterList logicalParameterList = node
						.getV_logicalParameters();
				for (LogicalParameter logicalParameter : logicalParameterList) {
					List<RegulatoryEdge> edgeList = (List<RegulatoryEdge>) logicalParameter
							.getEdges();
					List<RegulatoryEdge> newEdgeIndex = (List<RegulatoryEdge>) new ArrayList();
					for (RegulatoryEdge edge : edgeList) {
						RegulatoryMultiEdge oldMultiEdge = edge.me;
						RegulatoryMultiEdge newMultiEdge = edgeAssociation.get(
								oldMultiEdge).get(i);
						newEdgeIndex.add(newMultiEdge.getEdge(edge.index));
					}

					// logical parameters are copied
					// logical functions, if any, should be copied too
					newNode.addLogicalParameter(new LogicalParameter(
							newEdgeIndex, logicalParameter.getValue()), true);
				}
			}
		}

		// TODO: add integration function interactions and respective functions

		// Add integration function interactions
		for (int i = 0; i < topology.getNumberInstances(); i++) {
			Collection<RegulatoryNode> mappedInputs = mapping.getMappedInputs();

			for (RegulatoryNode oldInput : mappedInputs) {
				RegulatoryNode newMapped = composedGraph
						.getNodeByName(computeNewName(oldInput.getName(), i));

				List<RegulatoryNode> properList = mapping
						.getProperComponentsForInput(oldInput);

				for (int j = 0; j < topology.getNumberInstances(); j++) {
					if (!topology.areNeighbours(i, j))
						continue;
					for (RegulatoryNode oldProper : properList) {
						RegulatoryNode newProper = composedGraph
								.getNodeByName(computeNewName(
										oldProper.getName(), j));

						// Currently thresholds are being ignores
						// Add as many edges as necessary
						// Consider special case of THRESHOLD2
						RegulatoryMultiEdge newInteraction = new RegulatoryMultiEdge(
								composedGraph, newMapped, newProper);
						composedGraph.addEdge(newInteraction);

					}
				}

				// add appropriate logical parameters to the function
				// depending on the integration function

				Collection<RegulatoryMultiEdge> listMultiEdges = composedGraph
						.getIncomingEdges(newMapped);
				List<RegulatoryEdge> listEdges = new ArrayList<RegulatoryEdge>();

				for (RegulatoryMultiEdge multiEdge : listMultiEdges) {
					for (int k = 0; k < multiEdge.getEdgeCount(); k++) {
						listEdges.add(multiEdge.getEdge(k));
					}
				}

				// For the time being we will be assuming that each
				// MultiEdge has only one Edge

				switch (mapping.getIntegrationFunctionForInput(newMapped)) {
				case OR:
					for (int e1 = 0; e1 <= listEdges.size(); e1++) {
						List<RegulatoryEdge> edgeList = new ArrayList<RegulatoryEdge>();
						edgeList.add(listEdges.get(e1));
						newMapped.addLogicalParameter(new LogicalParameter(
								edgeList, 1), true);
						for (int e2 = e1 + 1; e2 <= listEdges.size(); e2++) {
							edgeList.add(listEdges.get(e2));
							newMapped.addLogicalParameter(new LogicalParameter(
									edgeList, 1), true);
						}
					}
					continue;
				case AND:
					newMapped.addLogicalParameter(new LogicalParameter(
							listEdges, 1), true);
					continue;
				case MIN:
					continue;
				case MAX:
					continue;
				case THRESHOLD2:
					continue;
				case MAX_LEFT:
					continue;
				case MAX_RIGHT:
					continue;
				}

			}

		}

		// reduce the graph

		return composedGraph;
	}

	private String computeNewName(String original, int moduleId) {
		return original + "_" + moduleId;
	}

}