package org.ginsim.service.tool.graphcomparator;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.GraphicalAttributesStore;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.css.CSSEdgeStyle;
import org.ginsim.core.graph.view.css.CSSNodeStyle;


/**
 * Compare 2 GsGraph
 * @author Duncan Berenguier
 * @since January 2009
 */
public abstract class GraphComparator<G extends Graph<?,?>> {
	
	public G graph_new;
	public G graph_1;
	public G graph_2;
	protected HashMap<Object, GraphComparatorStyleStore> stylesMap;
	protected Set<String> verticesIdsSet;
	protected GraphicalAttributesStore g1gas, g2gas;
	
	public static Color SPECIFIC_G1_COLOR = new Color(0, 255, 0); //green
	public static Color SPECIFIC_G2_COLOR = new Color(255, 0, 0); //red
	public static Color COMMON_COLOR = new Color(51, 153, 255);   //blue
	private GraphComparatorResult result;

	protected GraphComparator() {
		verticesIdsSet = new HashSet<String>();
		result = new GraphComparatorResult();
        stylesMap = new HashMap<Object, GraphComparatorStyleStore>();
        result.setStylesMap(stylesMap);
	}

	/**
	 * Return an HashMap containing all the styles for all the elements (vertices and edges) from both graphs. 
	 * With the IDs as a key and a GraphComparatorStyleStore (private class) as value. 
	 * 
	 * @return the stylesMap
	 */	
	public HashMap<Object, GraphComparatorStyleStore> getStyleMap() {
		return stylesMap;
	}
	
	/**
	 * Indicates if a node corresponding to the id is common to both graphs.
	 */
	public boolean isCommonNode(Object id) {
		return ((CSSNodeStyle)stylesMap.get(id).v).background == COMMON_COLOR;
	}
	
	/**
	 * Build the basic topology for the diff graph (node+edges) by calling others functions
	 *  1) addNodesFromGraph on both graphs
	 *  2) setNodesColor
	 *  3) addEdgesFromGraph on each node on both graphs
	 */
	public GraphComparatorResult buildDiffGraph() {
		log("Comparing graphs : \n");
		setDiffGraphName();
		log("\n");
		addNodesFromGraph(graph_1);
		addNodesFromGraph(graph_2);
		
		g1gas = new GraphicalAttributesStore(graph_1);
		g2gas = new GraphicalAttributesStore(graph_2);
		setNodesColor();
		log("\n");
		
		EdgeAttributesReader ereader = graph_new.getEdgeAttributeReader();
		for (String id : verticesIdsSet) {
			Color col = ((CSSNodeStyle)stylesMap.get(graph_new.getNodeByName(id)).v).background;
			
			addEdgesFromGraph(graph_1, graph_2, id, col, SPECIFIC_G1_COLOR, ereader);
			addEdgesFromGraph(graph_2, graph_1, id, col, SPECIFIC_G2_COLOR, ereader);
		}
		
		result.setGraphs(graph_new, graph_1, graph_2);
		
		return result;
	}

	/**
	 * Copy the node graphical attributes from the node source, to the newly created node v. The background color of the node is set to the parent color.
	 * 
	 * @param v the node just created
	 * @param source the node to copy from
	 * @param vreader a nodeAttributesReader for the new graph
	 * @param vsourcereader a nodeAttributesReader for the old graph
	 * @param col the color to apply to its background
	 */
	protected void mergeNodeAttributes(Object v, Object source, Object aux, NodeAttributesReader vreader, NodeAttributesReader vsourcereader, NodeAttributesReader vauxreader, Color col) {
		vreader.setNode(v);
		if (source != null) {
			vsourcereader.setNode(source);
			vreader.copyFrom(vsourcereader);
		}
		vreader.setBackgroundColor(col);
		vreader.refresh();

		if (col == SPECIFIC_G1_COLOR) stylesMap.put(v, new GraphComparatorStyleStore(new CSSNodeStyle(vsourcereader), null, new CSSNodeStyle(vreader)));
		else if (col == SPECIFIC_G2_COLOR) stylesMap.put(v, new GraphComparatorStyleStore(null, new CSSNodeStyle(vsourcereader), new CSSNodeStyle(vreader)));
		else {
			vauxreader.setNode(aux);
			stylesMap.put(v, new GraphComparatorStyleStore(new CSSNodeStyle(vsourcereader), new CSSNodeStyle(vauxreader), new CSSNodeStyle(vreader)));
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
	protected void mergeEdgeAttributes(Object e, Object source, Object aux, Color col, EdgeAttributesReader ereader, EdgeAttributesReader esourcereader, EdgeAttributesReader eauxreader) {
		ereader.setEdge((Edge)e);
		esourcereader.setEdge((Edge)source);
		ereader.copyFrom(esourcereader);
		ereader.setLineColor(col);
		ereader.refresh();			
		
		if (col == SPECIFIC_G1_COLOR) stylesMap.put(e, new GraphComparatorStyleStore(new CSSEdgeStyle(esourcereader), null, new CSSEdgeStyle(ereader)));
		else if (col == SPECIFIC_G2_COLOR) stylesMap.put(e, new GraphComparatorStyleStore(null, new CSSEdgeStyle(esourcereader), new CSSEdgeStyle(ereader)));
		else {
			eauxreader.setEdge((Edge)aux);
			stylesMap.put(e, new GraphComparatorStyleStore(new CSSEdgeStyle(esourcereader), new CSSEdgeStyle(eauxreader), new CSSEdgeStyle(ereader)));
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
	
	
	/**
	 * Add all the vertices from a graph to the verticeMap.
	 * The key of the map should be the node ID
	 * The value should be null for the moment.
	 * @param graph the graph containing the nodes.
	 */
	abstract protected void addNodesFromGraph( G graph);

	 /**
	 * Set the value for the node to the right color in the verticeMap.
	 */
	abstract protected void setNodesColor() ;
	
	/**
	 * Add the edges for one node from one graph to the merge graph.
	 * 
	 * @param gm the graph manager for the studied graph
	 * @param gm_aux a graph manager for the other graph
	 * @param id the node id's 
	 * @param col the node color (parent)
	 * @param pcol the color corresponding to the studied graph (gm).
	 * @param ereader an edge attribute reader for the diff graph.
	 * 
	 */
	abstract protected void addEdgesFromGraph( G gm, G gm_aux, String id, Color col, Color pcol, EdgeAttributesReader ereader);
	
	/**
	 * Return a merge graph colored to indicates vertices and edges parent graph.
	 * @return the diff graph
	 */
	public G getDiffGraph() {
		return graph_new;
	}

	/**
	 * Return the first graph to compare
	 * @return the graph
	 */
	public G getG1() {
		return graph_1;
	}

	/**
	 * Return the second graph to compare
	 * @return the graph
	 */
	public G getG2() {
		return graph_2;
	}
	
	/**
	 * append the string 's' to the log
	 * @param s
	 */
	public void log(String s) {
		result.getLog().append(s);
	}

	/**
	 * append the long l to the log
	 * @param l
	 */
	public void log(long l) {
		result.getLog().append(l);
	}

	/**
	 * append the int i to the log
	 * @param i
	 */
	public void log(int i) {
		result.getLog().append(i);
	}
	
	/**
	 * append the boolean b to the log
	 * @param b
	 */
	public void log(boolean b) {
		result.getLog().append(b);
	}
	
	/**
	 * append the object o to the log
	 * @param o
	 */
	public void log(Object o) {
		result.getLog().append(o);
	}


}
