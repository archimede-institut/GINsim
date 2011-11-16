package org.ginsim.gui.service.tools.stateinregulatorygraph;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;


/**
 * 
 * Contains methods to colorize a graph depending on a state of its node.
 * 
 * Init with GsStateInRegGraph(GsRegulatoryGraph regGraph)
 * Call colorizeGraph(state) any time you want
 * Restore the orginal color of the graph with restoreColorization()
 * 
 * @see GsStateInRegGraphSelector
 *
 */
public class GsStateInRegGraph {

	private GsRegulatoryGraph regGraph;
	private GsStateInRegGraphSelector selector;
	private CascadingStyle cs;
	private List nodeOrder;
	private boolean shouldStore;

	public GsStateInRegGraph(GsRegulatoryGraph regGraph) {
		this(regGraph, true);
	}

	/**
	 * Prepare the colorization of regGraph
	 * @param regGraph a regualtory graph to colorize
	 * @param shouldStore if true (default) save the initial color of regGraph's edges and nodes to be restored later
	 */
	public GsStateInRegGraph(GsRegulatoryGraph regGraph, boolean shouldStore) {
		this.regGraph = regGraph;
		this.nodeOrder = regGraph.getNodeOrder();
		this.shouldStore = shouldStore;

		cs = new CascadingStyle(false);  //Create a cs and save the current color manually
		if (shouldStore) {
			cs.storeAllEdges(regGraph.getEdges(), regGraph.getEdgeAttributeReader());
			cs.storeAllNodes(nodeOrder, regGraph.getVertexAttributeReader());        	
		}
		selector = new GsStateInRegGraphSelector(regGraph);		
	}

	/**
	 * Restore the original color of the graph if it was saved
	 */
	public void restoreColorization() {
		if (shouldStore) {
			cs.restoreAllEdges(regGraph.getEdgeAttributeReader());  //Restore the original color of the regulatory graph
			cs.restoreAllNodes(regGraph.getVertexAttributeReader());
		}
	}

	/**
	 * Colorize the regulatory graph according to a given state (node of the dynamic graph).
	 * 
	 * @param state a byte representation of the state considering the graph's nodeOrder
	 */
	public void colorizeGraph(byte[] state) {
		if (state == null || state.length != nodeOrder.size()) {
			return;
		}
		selector.setState(state);
		colorizeGraph();
	}

	/**
	 * Colorize the regulatory graph according to a given state (node of the dynamic graph).
	 * 
	 * @param state a String representation of the state considering the graph's nodeOrder
	 */
	public void colorizeGraph(String state) {
		if (state == null || state.length() != nodeOrder.size()) {
			return;
		}
		selector.setState(state);
		colorizeGraph();
	}

	private void colorizeGraph() {
		VertexAttributesReader vreader = regGraph.getVertexAttributeReader();
		EdgeAttributesReader ereader = regGraph.getEdgeAttributeReader();

		cs.restoreAllEdges(regGraph.getEdges(), ereader);
		//Cannot use cs.applySelectorOnEdges because we need the multiarcs, so do it manually
		for (Iterator it = nodeOrder.iterator(); it.hasNext();) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex) it.next();

			// apply the vertex's colour
			vreader.setVertex(vertex);
			cs.applyOnNode(selector, vertex, vreader);

			// colorize edges
			Collection<GsRegulatoryMultiEdge> edges = regGraph.getOutgoingEdges(vertex);
			for (GsRegulatoryMultiEdge edge: edges) {
				ereader.setEdge(edge);
				cs.applyOnEdge(selector, edge, ereader);
			}
		}
	}


}
