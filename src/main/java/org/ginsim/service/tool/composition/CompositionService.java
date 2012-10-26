package org.ginsim.service.tool.composition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.ginsim.common.application.GsException;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.regulatorygraph.LogicalModel2RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameterList;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifier;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfig;
import org.mangosdk.spi.ProviderFor;
import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.tool.reduction.ModelReducer;


@ProviderFor(Service.class)
@Alias("composition")
/**
 * Module composition service
 * @author Nuno D. Mendes
 */
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
	 * @return RegulatoryGraph
	 * @throws GsException
	 */

	public RegulatoryGraph run(RegulatoryGraph graph, CompositionConfig config)
			throws GsException {

		return computeComposedGraph(graph, config);

	}

	public RegulatoryGraph computeComposedGraph(RegulatoryGraph graph,
			CompositionConfig config) throws GsException {

		Topology topology = config.getTopology();
		IntegrationFunctionMapping mapping = config.getMapping();
		boolean reduce = config.isReduce();

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
						computeNewName(node.getNodeInfo().getNodeID(), i),
						computeNewName(node.getNodeInfo().getNodeID(), i),
						node.getMaxValue());

				newNode.getNodeInfo().setMax(node.getNodeInfo().getMax());
				newNode.getNodeInfo().setNodeID(
						computeNewName(node.getNodeInfo().getNodeID(), i));
				newNode.setName(computeNewName(node.getNodeInfo().getNodeID(),
						i));
				newNode.setGsa((Annotation) (node.getAnnotation().clone()));

				// mapped inputs with neighbours will no longer be inputs
				// unmapped input remain free
				if (node.isInput()
						&& (!mapping.isMapped(node) || !topology
								.hasNeighbours(i))) {
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
						.getNodeByName(computeNewName(source.getNodeInfo()
								.getNodeID(), i));
				RegulatoryNode newTarget = composedGraph
						.getNodeByName(computeNewName(target.getNodeInfo()
								.getNodeID(), i));

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
						.getNodeByName(computeNewName(node.getNodeInfo()
								.getNodeID(), i));
				LogicalParameterList logicalParameterList = node
						.getV_logicalParameters();
				for (LogicalParameter logicalParameter : logicalParameterList) {
					List<RegulatoryEdge> edgeList = (List<RegulatoryEdge>) logicalParameter
							.getEdges();
					List<RegulatoryEdge> newEdgeIndex = new ArrayList<RegulatoryEdge>();
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
		Collection<RegulatoryNode> mappedInputs = mapping.getMappedInputs();
		Collection<RegulatoryNode> newMappedInputs = new ArrayList<RegulatoryNode>();

		for (int i = 0; i < topology.getNumberInstances(); i++) {

			for (RegulatoryNode oldInput : mappedInputs) {
				RegulatoryNode newMapped = composedGraph
						.getNodeByName(computeNewName(oldInput.getNodeInfo()
								.getNodeID(), i));
				if (topology.hasNeighbours(i))
					newMappedInputs.add(newMapped);

				List<RegulatoryNode> properList = mapping
						.getProperComponentsForInput(oldInput);

				for (int j = 0; j < topology.getNumberInstances(); j++) {
					if (!topology.areNeighbours(i, j))
						continue;
					for (RegulatoryNode oldProper : properList) {
						RegulatoryNode newProper = composedGraph
								.getNodeByName(computeNewName(oldProper
										.getNodeInfo().getNodeID(), j));

						// Currently thresholds are being ignored
						// Add as many edges as necessary
						// Consider special case of THRESHOLD2

						// For the time being only one edge is being added per
						// interaction. because only one functional level is
						// being considered

						// TODO: Add edges for different thresholds

						RegulatoryMultiEdge newInteraction = new RegulatoryMultiEdge(
								composedGraph, newProper, newMapped);
						composedGraph.addEdge(newInteraction);

					}
				}

				// add appropriate logical parameters to the function
				// depending on the integration function

				Collection<RegulatoryMultiEdge> listMultiEdges = composedGraph
						.getIncomingEdges(newMapped);
				List<RegulatoryEdge> listEdges = new ArrayList<RegulatoryEdge>();

				if (listMultiEdges != null) {
					// Make sure the list is not null
					// which will happen if (i,j) are not neighbours
					for (RegulatoryMultiEdge multiEdge : listMultiEdges) {
						for (int k = 0; k < multiEdge.getEdgeCount(); k++) {
							listEdges.add(multiEdge.getEdge(k));
						}
					}
				}

				IntegrationFunction integrationFunction = mapping
						.getIntegrationFunctionForInput(oldInput);
				if (integrationFunction != null && listEdges.size() != 0) {

					switch (integrationFunction) {
					case OR:
						int bitmask = 0x00;
						while (++bitmask < Math.pow(2, (listEdges.size()))) {
							List<RegulatoryEdge> edgeList = new ArrayList<RegulatoryEdge>();
							for (int pos = 0; pos < listEdges.size(); pos++) {
								int curmask = (int) Math.pow(2, pos);
								if ((bitmask & curmask) != 0)
									edgeList.add(listEdges.get(pos));
							}
							// System.out.println("bitmask is " + bitmask +
							// " and edgeList is" + edgeList.size());
							newMapped.addLogicalParameter(new LogicalParameter(
									edgeList, 1), true);

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

		}

		// Reduce the graph

		if (reduce) {
			// Build a ModelSimplifierConfig object
			ModelSimplifierConfig simplifierConfig = new ModelSimplifierConfig();
			for (RegulatoryNode input : newMappedInputs)
				simplifierConfig.remove(input);

			// Mimmick a ReductionLauncher object
			ReductionStub launcher = new ReductionStub(composedGraph);

			ModelSimplifier simplifier = new ModelSimplifier(composedGraph,
					simplifierConfig, launcher, false);
			simplifier.run();

			RegulatoryGraph finalComposedGraph = launcher.getReducedGraph();

			return finalComposedGraph;
		}

		return composedGraph;
	}

	/**
	 * @param original The original name of the component
	 * 
	 * @param moduleId The index of the module if belongs to
	 * 
	 * @return The new name of the component
	 */
	private String computeNewName(String original, int moduleId) {
		// moduleId starts at 1, as all iterations begin at 0, we add 1 here
		return original + "_" + (moduleId + 1);
	}

}