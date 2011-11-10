package org.ginsim.gui.service.action.graphcomparator;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.css.EdgeStyle;
import fr.univmrs.tagc.GINsim.css.Style;
import fr.univmrs.tagc.GINsim.css.VertexStyle;
import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraphicalAttributesStore;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;

/**
 * Compare 2 GsGraph
 * @author Berenguier Duncan
 * @since January 2009
 *
 */
public abstract class GraphComparator {
	
	protected Graph gm, g1m, g2m;
	protected HashMap stylesMap;
	protected Set verticesIdsSet;
	protected GsGraphicalAttributesStore g1gas, g2gas;
	protected StringBuffer log;
	
	public static Color SPECIFIC_G1_COLOR = new Color(0, 255, 0); //green
	public static Color SPECIFIC_G2_COLOR = new Color(255, 0, 0); //red
	public static Color COMMON_COLOR = new Color(51, 153, 255);   //blue

	protected GraphComparator() {
		verticesIdsSet = new HashSet();
		log = new StringBuffer(2048);
	}

	/**
	 * Return an HashMap containing all the styles for all the elements (vertices and edges) from both graphs. 
	 * With the IDs as a key and a ItemStore (private class) as value. 
	 * 
	 * @return the stylesMap
	 */	
	public HashMap getStyleMap() {
		return stylesMap;
	}
	
	/**
	 * Indicates if a vertex corresponding to the id is common to both graphs.
	 */
	public boolean isCommonVertex(Object id) {
		return ((VertexStyle)((ItemStore)stylesMap.get(id)).v).background == COMMON_COLOR;
	}
	
	/**
	 * Build the basic topology for the diff graph (vertex+edges) by calling others functions
	 *  1) addVerticesFromGraph on both graphs
	 *  2) setVerticesColor
	 *  3) addEdgesFromGraph on each node on both graphs
	 */
	public void buildDiffGraph() {
		log("Comparing graphs : \n");
		setDiffGraphName();
		log("\n");
		addVerticesFromGraph(g1m);
		addVerticesFromGraph(g2m);
		
		g1gas = new GsGraphicalAttributesStore(g1m);
		g2gas = new GsGraphicalAttributesStore(g2m);
		setVerticesColor();
		log("\n");
		
		GsEdgeAttributesReader ereader = gm.getEdgeAttributeReader();
		for (Iterator it = verticesIdsSet.iterator(); it.hasNext();) { 		//For all edges
			String id = (String) it.next();
			Color col = ((VertexStyle)((ItemStore)stylesMap.get(gm.getVertexByName(id))).v).background;
			
			addEdgesFromGraph(g1m, g2m, id, col, SPECIFIC_G1_COLOR, ereader);
			addEdgesFromGraph(g2m, g1m, id, col, SPECIFIC_G2_COLOR, ereader);
		}
	}

	/**
	 * Copy the vertex graphical attributes from the vertex source, to the newly created vertex v. The background color of the vertex is set to the parent color.
	 * 
	 * @param v the vertex just created
	 * @param source the vertex to copy from
	 * @param vreader a vertexAttributesReader for the new graph
	 * @param vsourcereader a vertexAttributesReader for the old graph
	 * @param col the color to apply to its background
	 */
	protected void mergeVertexAttributes(Object v, Object source, Object aux, GsVertexAttributesReader vreader, GsVertexAttributesReader vsourcereader, GsVertexAttributesReader vauxreader, Color col) {
		vreader.setVertex(v);
		if (source != null) {
			vsourcereader.setVertex(source);
			vreader.copyFrom(vsourcereader);
		}
		vreader.setBackgroundColor(col);
		vreader.refresh();

		if (col == SPECIFIC_G1_COLOR) stylesMap.put(v, new ItemStore(new VertexStyle(vsourcereader), null, new VertexStyle(vreader)));
		else if (col == SPECIFIC_G2_COLOR) stylesMap.put(v, new ItemStore(null, new VertexStyle(vsourcereader), new VertexStyle(vreader)));
		else {
			vauxreader.setVertex(aux);
			stylesMap.put(v, new ItemStore(new VertexStyle(vsourcereader), new VertexStyle(vauxreader), new VertexStyle(vreader)));
		}
	}

	/**
	 * Copy the edge graphical attributes from the edge source, to the newly created edge e. The line color for the edge is set to the parent color.
	 * 
	 * @param e the edge just created
	 * @param source the edge to copy from
	 * @param ereader a vertexAttributesReader for the new graph
	 * @param esourcereader a vertexAttributesReader for the graph to copy from
	 * @param col the color to apply to its lineColor
	 */
	protected void mergeEdgeAttributes(Object e, Object source, Object aux, Color col, GsEdgeAttributesReader ereader, GsEdgeAttributesReader esourcereader, GsEdgeAttributesReader eauxreader) {
		ereader.setEdge(e);
		esourcereader.setEdge(source);
		ereader.copyFrom(esourcereader);
		ereader.setLineColor(col);
		ereader.refresh();			
		
		if (col == SPECIFIC_G1_COLOR) stylesMap.put(e, new ItemStore(new EdgeStyle(esourcereader), null, new EdgeStyle(ereader)));
		else if (col == SPECIFIC_G2_COLOR) stylesMap.put(e, new ItemStore(null, new EdgeStyle(esourcereader), new EdgeStyle(ereader)));
		else {
			eauxreader.setEdge(aux);
			stylesMap.put(e, new ItemStore(new EdgeStyle(esourcereader), new EdgeStyle(eauxreader), new EdgeStyle(ereader)));
		}
	}
	/**
	 * define the name of the diff graph
	 */
	protected void setDiffGraphName() {
		try {
			String g1name = getG1().getGraphName();
			String g2name = getG2().getGraphName();
			
			log("Generating diff_"+g1name+"_"+g2name+"\n");
			getDiffGraph().setGraphName("diff_"+g1name+"_"+g2name);
		} catch (GsException e) {} //Could not append normally, the g1 and g2 graph name can't be invalid at this point
	}
	
	public void setEdgeAutomatingRouting() {
		Graph gm1 = getG1();
		for (Iterator it = gm.getEdges().iterator(); it.hasNext();) {
			GsDirectedEdge e = (GsDirectedEdge) it.next();
			
			GsDirectedEdge e1 = (GsDirectedEdge) gm1.getEdge(gm1.getVertexByName(e.getSource().toString()), gm1.getVertexByName(e.getTarget().toString()));
			if (e1 == null) {//The edge is (only or not) in the first graph. So its intermediary point are right.
				GsEdgeAttributesReader ereader = gm.getEdgeAttributeReader();
				ereader.setEdge(e);
				ereader.setRouting(GsEdgeAttributesReader.ROUTING_AUTO);
				ereader.refresh();
			}
		}
	}
	
	/**
	 * Add all the vertices from a graph to the verticeMap.
	 * The key of the map should be the vertex ID
	 * The value should be null for the moment.
	 * @param gm the graph manager for the graph containing the vertices.
	 */
	abstract protected void addVerticesFromGraph( Graph gm);

	 /**
	 * Set the value for the vertex to the right color in the verticeMap.
	 */
	abstract protected void setVerticesColor() ;
	
	/**
	 * Add the edges for one vertex from one graph to the merge graph.
	 * 
	 * @param gm the graph manager for the studied graph
	 * @param gm_aux a graph manager for the other graph
	 * @param id the vertex id's 
	 * @param col the vertex color (parent)
	 * @param pcol the color corresponding to the studied graph (gm).
	 * @param ereader an edge attribute reader for the diff graph.
	 * 
	 */
	abstract protected void addEdgesFromGraph( Graph gm, Graph gm_aux, String id, Color col, Color pcol, GsEdgeAttributesReader ereader);
	
	/**
	 * Return a merge graph colored to indicates vertices and edges parent graph.
	 * @return the diff graph
	 */
	abstract public Graph getDiffGraph();
	/**
	 * Return the first graph to compare
	 * @return the graph
	 */
	abstract public Graph getG1();
	/**
	 * Return the second graph to compare
	 * @return the graph
	 */
	abstract public Graph getG2();
	
	/**
	 * append the string 's' to the log
	 * @param s
	 */
	public void log(String s) {
		log.append(s);
	}

	/**
	 * append the long l to the log
	 * @param l
	 */
	public void log(long l) {
		log.append(l);
	}

	/**
	 * append the int i to the log
	 * @param i
	 */
	public void log(int i) {
		log.append(i);
	}
	
	/**
	 * append the boolean b to the log
	 * @param b
	 */
	public void log(boolean b) {
		log.append(b);
	}
	
	/**
	 * append the object o to the log
	 * @param o
	 */
	public void log(Object o) {
		log.append(o);
	}

	/**
	 * get the content of the log
	 */
	public StringBuffer getLog() {
		return log;
	}

}


class ItemStore
{
	protected Style v1;
	protected Style v2;
	protected Style v;
	
	protected ItemStore(Style v1, Style v2, Style v) {
		this.v1 = v1;
		this.v2 = v2;
		this.v = v;
	}
}