package org.ginsim.gui.service.tool.graphcomparator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.graph.dynamicgraph.DynamicNode;


/**
 * Compare 2 DynamicGraph
 * @author Berenguier Duncan
 * @since January 2009
 *
 */
public class DynamicGraphComparator extends GraphComparator<DynamicGraph> {
	public DynamicGraphComparator( Graph g1, Graph g2, Graph g) {
	    if (g  == null || !(g  instanceof DynamicGraph))  return;
	    if (g1 == null || !(g1 instanceof DynamicGraph))  return;
	    if (g2 == null || !(g2 instanceof DynamicGraph))  return;
		
		this.g1m = (DynamicGraph)g1;
		this.g2m = (DynamicGraph)g2;
		this.gm = (DynamicGraph)g;
		stylesMap = new HashMap();
		buildDiffGraph();
	}
	
	public DynamicGraphComparator( Graph g1, Graph g2) {
		
		this(g1, g2, GraphManager.getInstance().getNewGraph( DynamicGraph.class));
	}


	protected void setVerticesColor() {
		for (Iterator it=verticesIdsSet.iterator() ; it.hasNext() ;) {	//For all the vertices
			DynamicNode v, v1, v2;
			String id = (String)it.next();
			v1 = (DynamicNode)g1m.getVertexByName(id);
			v2 = (DynamicNode)g2m.getVertexByName(id);
			//Check which graph own the vertex, set the appropriate color to it and if it is owned by both graph, compare its attributes.
			if (v1 == null) {
				log("The vertex "+id+" is specific to g2\n");
				v = new DynamicNode(v2.state);
				gm.addVertex(v);
				mergeVertexAttributes(v, v2, null, gm.getVertexAttributeReader(), g2m.getVertexAttributeReader(), null, SPECIFIC_G2_COLOR);
			} else if (v2 == null) {
				log("The vertex "+id+" is specific to g1\n");
				v = new DynamicNode(v1.state);
				gm.addVertex(v);
				mergeVertexAttributes(v, v1, null, gm.getVertexAttributeReader(), g1m.getVertexAttributeReader(), null, SPECIFIC_G1_COLOR);
			} else {
				log("The vertex "+id+" is common to both g1 and g2\n");
				v = new DynamicNode(v1.state);
				gm.addVertex(v);
				mergeVertexAttributes(v, v1, v2, gm.getVertexAttributeReader(), g1m.getVertexAttributeReader(), g2m.getVertexAttributeReader(), COMMON_COLOR);
				//compareVertices(v ,v1, v2);
			}
		}
	}

	protected void addVerticesFromGraph( Graph gm) {
		for (Iterator it=gm.getVertices().iterator() ; it.hasNext() ;) {
			DynamicNode vertex = (DynamicNode)it.next();
			String id = vertex.toString(); //Beware, the real node id is not getId, but toString
			verticesIdsSet.add(id); 
		}
	}

	protected void addEdgesFromGraph( Graph gm_main, Graph gm_aux, String id, Color vcol, Color pcol, EdgeAttributesReader ereader) {
		DynamicNode v = (DynamicNode) gm_main.getVertexByName(id);
		Edge<DynamicNode> e = null;
		EdgeAttributesReader e1reader = gm_main.getEdgeAttributeReader();
		EdgeAttributesReader e2reader = gm_aux.getEdgeAttributeReader();
		
		if (v != null) { //If v is a vertex from the studied graph, we look at its edges
			Collection<Edge<DynamicNode>> edges = gm_main.getOutgoingEdges(v);
			for (Edge<DynamicNode> e1: edges) {
				String tid = ((DynamicNode)e1.getTarget()).toString();
				DynamicNode source = (DynamicNode) gm.getVertexByName(id);
				DynamicNode target = (DynamicNode) gm.getVertexByName(tid);
				Edge<DynamicNode> e2 = gm.getEdge(source, target);
				
				if (e2 == null) //The edge doesn't not already exists.
					e = gm.addEdge(v, e1.getTarget(), false);
				else
					continue;
				
				String comment = "This edge ";
				if (vcol != COMMON_COLOR || !isCommonVertex(target)) { //The edge's vertices are specific to one graph therefore the edge is specific, and we add it with the right color.
					comment+= "is specific to "+(pcol == SPECIFIC_G1_COLOR ? "g1":"g2");
					mergeEdgeAttributes(e, e1, null, pcol, ereader, e1reader, null);
				} else {
					e2 = gm_aux.getEdge(gm_aux.getVertexByName(id), gm_aux.getVertexByName(tid));
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
	public static List getNodeOrder( DynamicGraph g1, DynamicGraph g2) {
		List no1 = g1.getNodeOrder();
		List no2 = g2.getNodeOrder();
		List gNodeOrder = new ArrayList();
		
		if (no1.size() == no2.size()) {
			for (Iterator it1 = no1.iterator(), it2 = no2.iterator(); it1.hasNext();) {
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