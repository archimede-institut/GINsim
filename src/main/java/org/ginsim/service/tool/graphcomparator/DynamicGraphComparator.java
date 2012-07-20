package org.ginsim.service.tool.graphcomparator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;


/**
 * Compare 2 DynamicGraph
 * @author Berenguier Duncan
 * @since January 2009
 *
 */
public class DynamicGraphComparator extends GraphComparator {
	public DynamicGraphComparator( Graph g1, Graph g2, Graph g) {
		super();
	    if (g  == null || !(g  instanceof DynamicGraph))  return;
	    if (g1 == null || !(g1 instanceof DynamicGraph))  return;
	    if (g2 == null || !(g2 instanceof DynamicGraph))  return;
	     
        this.graph_new = g;
        this.graph_1 = g1;
        this.graph_2 = g2;
	}
	
	public DynamicGraphComparator( Graph g1, Graph g2) {
		this(g1, g2, GraphManager.getInstance().getNewGraph( DynamicGraph.class));
	}


	protected void setNodesColor() {
		for (Iterator<String> it=verticesIdsSet.iterator() ; it.hasNext() ;) {	//For all the vertices
			DynamicNode v, v1, v2;
			String id = (String)it.next();
			v1 = (DynamicNode)graph_1.getNodeByName(id);
			v2 = (DynamicNode)graph_2.getNodeByName(id);
			//Check which graph own the vertex, set the appropriate color to it and if it is owned by both graph, compare its attributes.
			if (v1 == null) {
				log("The node "+id+" is specific to g2\n");
				v = new DynamicNode(v2.state);
				graph_new.addNode(v);
				mergeNodeAttributes(v, v2, null, graph_new.getNodeAttributeReader(), graph_2.getNodeAttributeReader(), null, SPECIFIC_G2_COLOR);
			} else if (v2 == null) {
				log("The node "+id+" is specific to g1\n");
				v = new DynamicNode(v1.state);
				graph_new.addNode(v);
				mergeNodeAttributes(v, v1, null, graph_new.getNodeAttributeReader(), graph_1.getNodeAttributeReader(), null, SPECIFIC_G1_COLOR);
			} else {
				log("The node "+id+" is common to both g1 and g2\n");
				v = new DynamicNode(v1.state);
				graph_new.addNode(v);
				mergeNodeAttributes(v, v1, v2, graph_new.getNodeAttributeReader(), graph_1.getNodeAttributeReader(), graph_2.getNodeAttributeReader(), COMMON_COLOR);
				//compareNodes(v ,v1, v2);
			}
		}
	}

	protected void addNodesFromGraph( Graph gm) {
		for (Iterator it=gm.getNodes().iterator() ; it.hasNext() ;) {
			DynamicNode vertex = (DynamicNode)it.next();
			String id = vertex.toString(); //Beware, the real node id is not getId, but toString
			verticesIdsSet.add(id); 
		}
	}

	protected void addEdgesFromGraph( Graph gm_main, Graph gm_aux, String id, Color vcol, Color pcol, EdgeAttributesReader ereader) {
		DynamicNode v = (DynamicNode) gm_main.getNodeByName(id);
		Edge<DynamicNode> e = null;
		EdgeAttributesReader e1reader = gm_main.getEdgeAttributeReader();
		EdgeAttributesReader e2reader = gm_aux.getEdgeAttributeReader();
		
		if (v != null) { //If v is a vertex from the studied graph, we look at its edges
			Collection<Edge<DynamicNode>> edges = gm_main.getOutgoingEdges(v);
			for (Edge<DynamicNode> e1: edges) {
				String tid = ((DynamicNode)e1.getTarget()).toString();
				DynamicNode source = (DynamicNode) graph_new.getNodeByName(id);
				DynamicNode target = (DynamicNode) graph_new.getNodeByName(tid);
				Edge<DynamicNode> e2 = graph_new.getEdge(source, target);
				
				if (e2 == null) //The edge doesn't not already exists.
					e = ((DynamicGraph)graph_new).addEdge(v, e1.getTarget(), false);
				else
					continue;
				
				String comment = "This edge ";
				if (vcol != COMMON_COLOR || !isCommonNode(target)) { //The edge's vertices are specific to one graph therefore the edge is specific, and we add it with the right color.
					comment+= "is specific to "+(pcol == SPECIFIC_G1_COLOR ? "g1":"g2");
					mergeEdgeAttributes(e, e1, null, pcol, ereader, e1reader, null);
				} else {
					e2 = gm_aux.getEdge(gm_aux.getNodeByName(id), gm_aux.getNodeByName(tid));
					if (e2 != null) {
						comment+= "is common to both graphs";
						mergeEdgeAttributes(e, e1, e2, vcol, ereader, e1reader, e2reader);
					} else {
						comment+= "is specific to "+(pcol == SPECIFIC_G1_COLOR ? "g1":"g2");
						mergeEdgeAttributes(e, e1, null, pcol, ereader, e1reader, null);
					}				
				}
				log(comment+" ("+e+")\n");
			}
		}
	}
	

	
	/**
	 * Return a common nodeOrder for g1 and g2.
	 * Return null if there is any kind of incompatibility between both nodeOrder (different sizes, different nodes...) 
	 * 
	 * @param g1 a graph (in any order)
	 * @param g2 another graph (in any order)
	 * @return a node order or null if incompatible.
	 */
	public static List<String> getNodeOrder( DynamicGraph g1, DynamicGraph g2) {
		List<NodeInfo> no1 = g1.getNodeOrder();
		List<NodeInfo> no2 = g2.getNodeOrder();
		List<String> gNodeOrder = new ArrayList<String>();
		
		if (no1.size() == no2.size()) {
			for (Iterator<NodeInfo> it1 = no1.iterator(), it2 = no2.iterator(); it1.hasNext();) {
				String v1 = it1.next().toString();
				String v2 = it2.next().toString();
				gNodeOrder.add(v1);
				
				if (!v1.equals(v2)) {
					gNodeOrder = null;
					break;
				}
			}			
		}
	
		return gNodeOrder;
	}
}