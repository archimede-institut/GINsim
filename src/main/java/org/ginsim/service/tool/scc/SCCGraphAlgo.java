package org.ginsim.service.tool.scc;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.colomoto.common.task.AbstractTask;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;


/**
 * Search for strongly connected components, and build the SCC graph.
 *
 * @author Cecile Menahem
 * @author Aurelien Naldi
 */
public class SCCGraphAlgo extends AbstractTask<ReducedGraph> {
	private final Graph graph;

	/**
	 * get ready to run.
	 * 
	 * @param graph
	 */
	public SCCGraphAlgo(Graph graph) {
		this.graph = graph;
	}

    @Override
    protected ReducedGraph doGetResult() throws Exception {
        StronglyConnectedComponentTask sccTask = new StronglyConnectedComponentTask(graph);
		List<NodeReducedData> components = sccTask.call();
		ReducedGraph reducedGraph = constructGraph(components);
		return reducedGraph;
	}

	/**
	 * Construct a reducedGraph from a list of components
	 * @param components
	 * @return the reducedGraph
	 */
	private ReducedGraph constructGraph(List<NodeReducedData> components) {
		ReducedGraph reducedGraph = GSGraphManager.getInstance().getNewGraph( ReducedGraph.class, (Graph)graph);
		HashMap<Object, NodeReducedData> nodeParentSCC = new HashMap<Object, NodeReducedData>(); //Map the a node to its parent SCC
		
		int totalComplexComponents = 0;
		for (NodeReducedData component : components) {		//For each component
			reducedGraph.addNode(component);
			if (!component.isTrivial()) {
				totalComplexComponents++;
			}
			for (Object node : component.getContent()) {	//  for each nodes in the component
				nodeParentSCC.put(node, component);		//     add the node in the map nodeParentSCC as a key, with the current SCC node as value

			}
		}
		
		for (NodeReducedData component : components) {		//For each component
			for (Object node : component.getContent()) {	//  for each nodes in the component
				Collection<Edge<?>> outgoingEdges = graph.getOutgoingEdges(node);
				for (Edge edge: outgoingEdges) {									//    for each edge outgoing from this node
					Object targetNode = edge.getTarget();
					NodeReducedData targetParent = nodeParentSCC.get(targetNode);
					if (nodeParentSCC.get(targetNode) != component) {			//      if the target of the edge is not in the SCC
						reducedGraph.addEdge(component, targetParent);
					}
				}
			}
		}

		return reducedGraph;
	}
}
