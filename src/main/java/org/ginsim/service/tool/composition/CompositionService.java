package org.ginsim.service.tool.composition;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.Alias;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
@Alias("composition")

public class CompositionService implements Service {
	
/**
 * Run the Composition by replicating a Regulatory graph, according to a Topology and an IntegrationFunctionMapping
 * and return the composed graph after invoking the necessary reduction operations
 @param graph the current RegulatoryGraph
 @param topology the composition Topology, indicating the neighbourgood relationships
 @param mapping the integration functions to apply to each mapped input
 *
 @return RegulatoryGraph
 */

	public RegulatoryGraph run( RegulatoryGraph  graph, Topology topology, IntegrationFunctionMapping mapping) {
	
		return computeComposedGraph(graph,topology,mapping);
	}
	
	public RegulatoryGraph computeComposedGraph(RegulatoryGraph graph, Topology topology, IntegrationFunctionMapping mapping){
		RegulatoryGraph composedGraph = GraphManager.getInstance().getNewGraph(RegulatoryGraph.class);
		
		List<RegulatoryNode> components = graph.getNodeOrder();
		
		Iterator<RegulatoryNode> rni = components.iterator();
		while(rni.hasNext()){
			RegulatoryNode node = rni.next();
			for (int i = 0; i < topology.getNumberInstances(); i++){
				RegulatoryNode newNode = composedGraph.addNewNode(computeNewName(node.getId(),i), computeNewName(node.getName(),i), node.getMaxValue());
				if (node.isInput() && !mapping.isMapped(node)){
					newNode.setInput(node.isInput(), composedGraph);
				}

			}
		}
		
		Collection<RegulatoryMultiEdge> edges = graph.getEdges();
		Iterator<RegulatoryMultiEdge> rei = edges.iterator();
		while(rei.hasNext()){
			RegulatoryMultiEdge multiEdge = rei.next();
			for (int i = 0; i < topology.getNumberInstances(); i++){
				RegulatoryNode source = multiEdge.getSource();
				RegulatoryNode target = multiEdge.getTarget();
				RegulatoryEdgeSign sign = multiEdge.getSign();
				
				RegulatoryNode newSource = composedGraph.getNodeByName(computeNewName(source.getName(),i));
				RegulatoryNode newTarget = composedGraph.getNodeByName(computeNewName(target.getName(),i));
				
				for (int j = 1; j<=multiEdge.getEdgeCount();j++){
					RegulatoryEdge edge = multiEdge.getEdge(j);
					byte threshold = edge.threshold;
					RegulatoryMultiEdge newMultiEdge = new RegulatoryMultiEdge(composedGraph, newSource, newTarget, sign, threshold);
					composedGraph.addEdge(newMultiEdge);
					
				}

			}
		}
		
		// TODO: copy logical parameters
		// discover how this is done
		// reduce the graph
		
		return composedGraph;
	}
	
	private String computeNewName(String original, int moduleId){
		return original + "_" + moduleId;
	}
	
}