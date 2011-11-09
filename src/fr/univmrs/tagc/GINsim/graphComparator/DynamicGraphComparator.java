package fr.univmrs.tagc.GINsim.graphComparator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;

import fr.univmrs.tagc.GINsim.graph.GsGraphManager;

/**
 * Compare 2 GsDynamicGraph
 * @author Berenguier Duncan
 * @since January 2009
 *
 */
public class DynamicGraphComparator extends GraphComparator {
	private GsDynamicGraph g, g1, g2; //g is the graph merging g1 and g2, the graphs to compare.

	public DynamicGraphComparator( Graph g1, Graph g2, Graph g) {
	    if (g  == null || !(g  instanceof GsDynamicGraph))  return;
	    if (g1 == null || !(g1 instanceof GsDynamicGraph))  return;
	    if (g2 == null || !(g2 instanceof GsDynamicGraph))  return;
		this.g = (GsDynamicGraph)g; 
		this.g1 = (GsDynamicGraph)g1; 
		this.g2 = (GsDynamicGraph)g2;
		
		g1m = g1; g2m = g2; gm = g;
		stylesMap = new HashMap();
		buildDiffGraph();
	}
	
	public DynamicGraphComparator( Graph g1, Graph g2) {
		
		this(g1, g2, new GsDynamicGraph());
	}


	protected void setVerticesColor() {
		for (Iterator it=verticesIdsSet.iterator() ; it.hasNext() ;) {	//For all the vertices
			GsDynamicNode v, v1, v2;
			String id = (String)it.next();
			v1 = (GsDynamicNode)g1m.getVertexByName(id);
			v2 = (GsDynamicNode)g2m.getVertexByName(id);
			//Check which graph own the vertex, set the appropriate color to it and if it is owned by both graph, compare its attributes.
			if (v1 == null) {
				log("The vertex "+id+" is specific to g2\n");
				v = new GsDynamicNode(v2.state);
				gm.addVertex(v);
				mergeVertexAttributes(v, v2, null, gm.getVertexAttributeReader(), g2m.getVertexAttributeReader(), null, SPECIFIC_G2_COLOR);
			} else if (v2 == null) {
				log("The vertex "+id+" is specific to g1\n");
				v = new GsDynamicNode(v1.state);
				gm.addVertex(v);
				mergeVertexAttributes(v, v1, null, gm.getVertexAttributeReader(), g1m.getVertexAttributeReader(), null, SPECIFIC_G1_COLOR);
			} else {
				log("The vertex "+id+" is common to both g1 and g2\n");
				v = new GsDynamicNode(v1.state);
				gm.addVertex(v);
				mergeVertexAttributes(v, v1, v2, gm.getVertexAttributeReader(), g1m.getVertexAttributeReader(), g2m.getVertexAttributeReader(), COMMON_COLOR);
				//compareVertices(v ,v1, v2);
			}
		}
	}

	protected void addVerticesFromGraph( Graph gm) {
		for (Iterator it=gm.getVertices().iterator() ; it.hasNext() ;) {
			GsDynamicNode vertex = (GsDynamicNode)it.next();
			String id = vertex.toString(); //Beware, the real node id is not getId, but toString
			verticesIdsSet.add(id); 
		}
	}

	protected void addEdgesFromGraph( Graph gm_main, Graph gm_aux, String id, Color vcol, Color pcol, GsEdgeAttributesReader ereader) {
		GsDynamicNode v = (GsDynamicNode) gm_main.getVertexByName(id);
		GsDirectedEdge<GsDynamicNode> e = null;
		GsEdgeAttributesReader e1reader = gm_main.getEdgeAttributeReader();
		GsEdgeAttributesReader e2reader = gm_aux.getEdgeAttributeReader();
		
		if (v != null) { //If v is a vertex from the studied graph, we look at its edges
			Collection<GsDirectedEdge<GsDynamicNode>> edges = gm_main.getOutgoingEdges(v);
			for (GsDirectedEdge<GsDynamicNode> e1: edges) {
				String tid = ((GsDynamicNode)e1.getTarget()).toString();
				GsDynamicNode source = (GsDynamicNode) gm.getVertexByName(id);
				GsDynamicNode target = (GsDynamicNode) gm.getVertexByName(tid);
				GsDirectedEdge<GsDynamicNode> e2 = gm.getEdge(source, target);
				
				if (e2 == null) //The edge doesn't not already exists.
					e = g.addEdge(v, e1.getTarget(), false);
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
	

	
	public Graph getDiffGraph() {
		
		return g;
	}
	
	public Graph getG1() {
		
		return g1;
	}
	
	public Graph getG2() {
		
		return g2;
	}

	/**
	 * Return a common nodeOrder for g1 and g2.
	 * Return null if there is any kind of incompatibility between both nodeOrder (different sizes, different nodes...) 
	 * 
	 * @param g1 a graph (in any order)
	 * @param g2 another graph (in any order)
	 * @return a node order or null if incompatible.
	 */
	public static List getNodeOrder( Graph g1, Graph g2) {
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