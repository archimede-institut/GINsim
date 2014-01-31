package org.ginsim.service.tool.scc;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.colomoto.common.task.AbstractTask;
import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeShape;


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
    protected ReducedGraph doGetResult() {
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
		ReducedGraph reducedGraph = GraphManager.getInstance().getNewGraph( ReducedGraph.class, (Graph)graph);
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


		NodeAttributesReader vreader = reducedGraph.getNodeAttributeReader();
		int index = 1;
		for (NodeReducedData component : components) {				//For each component
			vreader.setNode(component);
			if (component.isTrivial()) {
            	if (component.isTransient(graph)) {
                	vreader.setBackgroundColor(Color.white);
            		vreader.setTextColor(Color.black);
            	} else {
            		vreader.setBackgroundColor(Color.red.darker());
                	vreader.setTextColor(Color.white);
            	}
			} else {
				Collection outgoing = reducedGraph.getOutgoingEdges(component);
				if (outgoing != null && outgoing.size() > 0) {
	            	Color backgroundColor = ConnectivityStyleProvider.TRANSIENT_PALETTE[index++%ConnectivityStyleProvider.TRANSIENT_PALETTE.length];
					vreader.setBackgroundColor(backgroundColor);
	            	vreader.setTextColor(ColorPalette.getConstrastedForegroundColor(backgroundColor));
				} else {
	            	Color backgroundColor = ConnectivityStyleProvider.TERMINAL_PALETTE[index++%ConnectivityStyleProvider.TERMINAL_PALETTE.length];
					vreader.setBackgroundColor(backgroundColor);
	            	vreader.setTextColor(ColorPalette.getConstrastedForegroundColor(backgroundColor));
				}
			}
            if (reducedGraph.getOutgoingEdges(component) == null) {	//  set the node's shape to ellipse if the node has no outgoing edges (is terminal).
                vreader.setShape(NodeShape.ELLIPSE);
            } else {
                vreader.setShape(NodeShape.RECTANGLE);
            }
        }

		return reducedGraph;
	}
}
