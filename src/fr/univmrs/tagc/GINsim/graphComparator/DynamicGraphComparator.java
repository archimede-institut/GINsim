package fr.univmrs.tagc.GINsim.graphComparator;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;

/**
 * Compare 2 GsDynamicGraph
 * @author Berenguier Duncan
 * @since January 2009
 *
 */
public class DynamicGraphComparator extends GraphComparator {
	private GsDynamicGraph g, g1, g2; //g is the graph merging g1 and g2, the graphs to compare.

	public DynamicGraphComparator(GsGraph g1, GsGraph g2, GsGraph g) {
	    if (g  == null || !(g  instanceof GsDynamicGraph))  return;
	    if (g1 == null || !(g1 instanceof GsDynamicGraph))  return;
	    if (g2 == null || !(g2 instanceof GsDynamicGraph))  return;
		this.g = (GsDynamicGraph)g; 
		this.g1 = (GsDynamicGraph)g1; 
		this.g2 = (GsDynamicGraph)g2;
		
		g1m = g1.getGraphManager(); g2m = g2.getGraphManager(); gm = g.getGraphManager();
		stylesMap = new HashMap();
		buildDiffGraph();
	}
	
	public DynamicGraphComparator(GsGraph g1, GsGraph g2) {
		this(g1, g2, new GsDynamicGraph());
	}

	public void buildDiffGraph() {
		super.buildDiffGraph();
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
				mergeVertexAttributes(v, v2, null, gm.getVertexAttributesReader(), g2m.getVertexAttributesReader(), null, SPECIFIC_G2_COLOR);
			} else if (v2 == null) {
				log("The vertex "+id+" is specific to g1\n");
				v = new GsDynamicNode(v1.state);
				gm.addVertex(v);
				mergeVertexAttributes(v, v1, null, gm.getVertexAttributesReader(), g1m.getVertexAttributesReader(), null, SPECIFIC_G1_COLOR);
			} else {
				log("The vertex "+id+" is common to both g1 and g2\n");
				v = new GsDynamicNode(v1.state);
				gm.addVertex(v);
				mergeVertexAttributes(v, v1, v2, gm.getVertexAttributesReader(), g1m.getVertexAttributesReader(), g2m.getVertexAttributesReader(), COMMON_COLOR);
				//compareVertices(v ,v1, v2);
			}
		}
	}

	protected void addVerticesFromGraph(GsGraphManager gm) {
		for (Iterator it=gm.getVertexIterator() ; it.hasNext() ;) {
			GsDynamicNode vertex = (GsDynamicNode)it.next();
			verticesIdsSet.add(vertex.toString()); //Beware, the real node id is not getId, but toString
		}
	}

	protected void addEdgesFromGraph(GsGraphManager gm_main, GsGraphManager gm_aux, String id, Color vcol, Color pcol, GsEdgeAttributesReader ereader) {
		GsDynamicNode v = (GsDynamicNode) gm_main.getVertexByName(id);
		GsDirectedEdge e = null;
		GsDirectedEdge e1, e2;
		GsEdgeAttributesReader e1reader = gm_main.getEdgeAttributesReader();
		GsEdgeAttributesReader e2reader = gm_aux.getEdgeAttributesReader();
		
		if (v != null) { //If v is a vertex from the studied graph, we look at its edges
			for (Iterator edge_it = gm_main.getOutgoingEdges(v).iterator(); edge_it.hasNext();) {
				e1 = (GsDirectedEdge) edge_it.next();
				String tid = ((GsDynamicNode)e1.getTargetVertex()).toString();
				GsDynamicNode source = (GsDynamicNode) gm.getVertexByName(id);
				GsDynamicNode target = (GsDynamicNode) gm.getVertexByName(tid);
				e2 = (GsDirectedEdge) gm.getEdge(source, target);
				
				if (e2 == null) //The edge doesn't not already exists.
					e = (GsDirectedEdge)g.addEdge(v, e1.getTargetVertex(), false);
				else
					continue;
				
				String comment = "This edge ";
				if (vcol != COMMON_COLOR || !isCommonVertex(target)) { //The edge's vertices are specific to one graph therefore the edge is specific, and we add it with the right color.
					comment+= "is specific to "+(pcol == SPECIFIC_G1_COLOR ? "g1":"g2");
					mergeEdgeAttributes(e, e1, null, pcol, ereader, e1reader, null);
				} else {
					e2 = (GsDirectedEdge) gm_aux.getEdge(gm_aux.getVertexByName(id), gm_aux.getVertexByName(tid));
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
	

	
	public GsGraph getDiffGraph() {
		return g;
	}
	public GsGraph getG1() {
		return g1;
	}
	public GsGraph getG2() {
		return g2;
	}
}
