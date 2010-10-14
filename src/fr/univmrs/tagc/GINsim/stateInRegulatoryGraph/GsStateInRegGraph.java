package fr.univmrs.tagc.GINsim.stateInRegulatoryGraph;

import java.util.Iterator;
import java.util.List;

import fr.univmrs.tagc.GINsim.css.CascadingStyle;
import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;


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
	public GsStateInRegGraph(GsRegulatoryGraph regGraph, boolean store) {
		this.regGraph = regGraph;
		this.nodeOrder = regGraph.getNodeOrder();
		this.shouldStore = store;

		cs = new CascadingStyle(false);  //Create a cs and save the current color manually
		if (store) {
			cs.storeAllEdges(regGraph.getGraphManager().getAllEdges(), regGraph.getGraphManager().getEdgeAttributesReader());
			cs.storeAllNodes(nodeOrder, regGraph.getGraphManager().getVertexAttributesReader());        	
		}
		selector = new GsStateInRegGraphSelector(regGraph);		
	}

	/**
	 * Restore the original color of the graph if it was saved
	 */
	public void restoreColorization() {
		if (shouldStore) {
			cs.restoreAllEdges(regGraph.getGraphManager().getEdgeAttributesReader());  //Restore the original color of the regulatory graph
			cs.restoreAllNodes(regGraph.getGraphManager().getVertexAttributesReader());
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
		GsVertexAttributesReader vreader = regGraph.getGraphManager().getVertexAttributesReader();
		GsEdgeAttributesReader ereader = regGraph.getGraphManager().getEdgeAttributesReader();

		cs.restoreAllEdges(regGraph.getGraphManager().getAllEdges(), ereader);
		//Cannot use cs.applySelectorOnEdges because we need the multiarcs, so do it manually
		for (Iterator it = nodeOrder.iterator(); it.hasNext();) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex) it.next();

			// apply the vertex's color
			vreader.setVertex(vertex);
			cs.applyOnNode(selector, vertex, vreader);

			// colorize edges
			for (Iterator it2 = regGraph.getGraphManager().getOutgoingEdges(vertex).iterator(); it2.hasNext();) {
				GsRegulatoryMultiEdge edge = (GsRegulatoryMultiEdge)((GsDirectedEdge)it2.next()).getUserObject();
				ereader.setEdge(edge);
				cs.applyOnEdge(selector, edge, ereader);
			}
		}
	}


}
