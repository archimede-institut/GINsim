package org.ginsim.service.tool.graphcomparator;

import java.util.Collection;
import java.util.HashMap;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;

/**
 * Contains the result of the comparison of two graphs graph_1 and graph_2 and the 
 * proper methods to handle the display of theses results
 *
 */
public class GraphComparatorResult {
	private Graph graph_new, graph_1, graph_2;
	private StringBuffer log;
	private HashMap<Object, GraphComparatorStyleStore> stylesMap;

	/**
	 * Create a new result
	 */
	protected GraphComparatorResult() {
		log = new StringBuffer(2048);
	}
	
	/**
	 * Return the new graph created for the comparison
	 * @return the new graph created for the comparison
	 */
	public Graph getDiffGraph() {
		return graph_new;
	}

	
	protected void setStylesMap(HashMap<Object, GraphComparatorStyleStore> stylesMap) {
		this.stylesMap = stylesMap;
	}

	/**
	 * Return the map of the graphical attributes associated to each edge and node of the new graph. 
	 * @return the map of the graphical attributes
	 */
	public HashMap<Object, GraphComparatorStyleStore> getStyleMap() {
		return stylesMap;
	}


	protected void setGraphs(Graph graph_new, Graph graph_1, Graph graph_2) {
		this.graph_new = graph_new;
		this.graph_1 = graph_1;
		this.graph_2 = graph_2;
	}

	/**
	 * Try to fix the edges routing
	 */
	public void setEdgeAutomatingRouting() {
		for (Edge<?> e: (Collection<Edge>)graph_new.getEdges()) {
			
			Edge e1 = graph_1.getEdge(graph_1.getNodeByName(e.getSource().toString()), graph_1.getNodeByName(e.getTarget().toString()));
			if (e1 == null) {//The edge is (only or not) in the first graph. So its intermediary point are right.
				EdgeAttributesReader ereader = graph_new.getEdgeAttributeReader();
				ereader.setEdge(e);
				ereader.setRouting(EdgeAttributesReader.ROUTING_AUTO);
				ereader.refresh();
			}
		}
	}
	
	/**
	 * get the content of the log
	 */
	public StringBuffer getLog() {
		return log;
	}

	/**
	 * Get the name of the first graph
	 * @return the name of the first graph
	 */
	public String getGraph1Name() {
		return graph_1.getGraphName();
	}
	/**
	 * Get the name of the second graph
	 * @return the name of the second graph
	 */
	public String getGraph2Name() {
		return graph_2.getGraphName();
	}

	
}
