package org.ginsim.core.graph.view.css;

import org.ginsim.core.graph.common.Graph;

/**
 * Do some generic work to colorize properly a graph, given a selector.
 * 
 *  - Save the initial style of the graph elements only at the first call of doColorize()
 *  - Restore the style of all the graph elements when calling
 */
public class Colorizer {
	
	protected Selector selector;
	protected CascadingStyle cs = null;

	/**
	 * Construct the colorizer from a selector and a graph.
	 * 
	 * @param selector the css selector
	 */
	public Colorizer(Selector selector) {
		this.selector = selector;
	}
	
	/**
	 * Initialise the CascadingStyle to handle the original style save at the first call.
	 * Then it call the abstract method colorize()
	 * 
	 * @param graph the graph to apply the style on
	 */
	public void doColorize(Graph<?, ?> graph) {
		if (cs == null) {
            cs = new CascadingStyle(true);
        } else {
            cs.shouldStoreOldStyle = false;
        }
		colorize(graph);
	}
	
	/**
	 * Apply the selector on all the edges and nodes of the graph
	 * 
	 * If you apply the style only on a known subset of edges/nodes, surcharging this method to 
	 * be more specific could be more efficient.
	 * 
	 * @param graph the graph to apply the style on
	 */
	protected void colorize(Graph<?, ?> graph) {
		if (selector.respondToEdges()) cs.applySelectorOnEdges(selector, graph.getEdges(), graph.getEdgeAttributeReader());
		if (selector.respondToNodes()) cs.applySelectorOnNodes(selector, graph.getNodes(), graph.getNodeAttributeReader());		
	}

	/**
	 * Supposing that the style have been saved, this function will restore the original style 
	 * of all the edges/nodes if the selector respond to the the edges/nodes respectively.
	 * 
	 * If you apply the style only on a known subset of edges/nodes, surcharging this method to 
	 * be more specific could be more efficient.
	 * 
	 * @param graph the graph to apply the style on
	 */
	public void undoColorize(Graph<?, ?> graph) {
		if (cs == null) return;
		if (selector.respondToEdges()) cs.restoreAllEdges(graph.getEdgeAttributeReader());
		if (selector.respondToNodes()) cs.restoreAllNodes(graph.getNodeAttributeReader());
	}
		
}
