package fr.univmrs.tagc.GINsim.gui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;


/**
 * Here are the callback for entry in the "edit" menu
 * 
 * 
 */
public class GsEditCallBack {
	
	private GsMainFrame main;
	
	/**
	 * simple constructor
	 * @param m
	 */
	public GsEditCallBack(GsMainFrame m) {
		main = m;
	}
	
	/**
	 * undo last action
	 */
	public void undo() {
	    main.getGraph().getGraphManager().undo();
	}
	
	/**
	 * redo last "undoed" action
	 */
	public void redo() {
	    main.getGraph().getGraphManager().redo();
	}
	
	/**
	 * select all elements
	 */
	public void selectAll() {
	    main.getGraph().getGraphManager().selectAll();
	}
	
	/**
	 * select all nodes
	 */
	public void selectAllNodes() {
	    main.getGraph().getGraphManager().select(main.getGraph().getNodeOrder());
	}
	
	/**
	 * select all edges
	 */
	public void selectAllEdges() {
		Set sel = new HashSet();
		for (Iterator it = main.getGraph().getGraphManager().getEdgeIterator(); it.hasNext();) {
			Object e = (Object) it.next();
			sel.add(e);
		}
	    main.getGraph().getGraphManager().select(sel);
	}
	
	/**
	 * select all previously unselected nodes and vice-versa
	 */
	public void invertSelection() {
	    main.getGraph().getGraphManager().invertSelection();
	}
	
	/**
	 * select all previously unselected nodes and vice-versa
	 */
	public void invertEdgeSelection() {
		Set sel, edges = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedEdgeIterator(); it.hasNext();) {
			Object e = (Object) it.next();
			edges.add(e);
		}
		sel = new HashSet();
		for (Iterator it = main.getGraph().getGraphManager().getEdgeIterator(); it.hasNext();) {
			Object e = (Object) it.next();
			sel.add(e);
		}
		sel.removeAll(edges);
		gm.select(sel);	
	}
	
	/**
	 * select all previously unselected nodes and vice-versa
	 */
	public void invertVertexSelection() {
		Set sel, vertices = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedVertexIterator(); it.hasNext();) {
			Object v = (Object) it.next();
			vertices.add(v);
		}
		sel = new HashSet(main.getGraph().getNodeOrder());
		sel.removeAll(vertices);
		gm.select(sel);	
	}
	
	/**
	 * copy the selection
	 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#copy()
	 */
	public void copy() {
	    main.getGraph().copy();
	}
	
	/**
	 * paste the previously copied selection
	 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#paste()
	 */
	public void paste() {
	    main.getGraph().paste();
	}
	
	/**
	 * delete selection
	 */
	public void delete() {
	    main.getGraph().getGraphManager().delete();
	}
	
	/**
	 * search a node
	 */
	public void searchNode() {
		new GsSearchFrame(main);
	}

	public void selectOutgoingArcs() {
		Set edges = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedVertexIterator(); it.hasNext();) {
			Object v = (Object) it.next();
			edges.addAll(gm.getOutgoingEdges(v));
		}
		gm.select(edges);
	}

	public void selectIncomingArcs() {
		Set edges = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedVertexIterator(); it.hasNext();) {
			Object v = (Object) it.next();
			edges.addAll(gm.getIncomingEdges(v));
		}
		gm.select(edges);
	}

	public void selectOutgoingVertices() {
		Set vertices = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedVertexIterator(); it.hasNext();) {
			Object v = (Object) it.next();
			for (Iterator it2 = gm.getOutgoingEdges(v).iterator(); it2.hasNext();) {
				GsDirectedEdge edge = (GsDirectedEdge) it2.next();
				vertices.add(edge.getTargetVertex());
			}
		}
		for (Iterator it = main.getSelectedEdges().iterator(); it.hasNext();) {
			GsDirectedEdge e = (GsDirectedEdge) it.next();
			vertices.add(e.getTargetVertex());
		}
		gm.select(vertices);		
	}

	public void selectIncomingVertices() {
		Set vertices = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedVertexIterator(); it.hasNext();) {
			Object v = (Object) it.next();
			for (Iterator it2 = gm.getIncomingEdges(v).iterator(); it2.hasNext();) {
				GsDirectedEdge edge = (GsDirectedEdge) it2.next();
				vertices.add(edge.getSourceVertex());
			}
		}
		for (Iterator it = main.getSelectedEdges().iterator(); it.hasNext();) {
			GsDirectedEdge e = (GsDirectedEdge) it.next();
			vertices.add(e.getSourceVertex());
		}
		gm.select(vertices);		
	}

	public void extendSelectionToOutgoingArcs() {
		Set edges = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedVertexIterator(); it.hasNext();) {
			Object v = (Object) it.next();
			edges.addAll(gm.getOutgoingEdges(v));
		}
		for (Iterator it = main.getSelectedEdges().iterator(); it.hasNext();) {
			Object e = (Object) it.next();
			edges.add(e);
		}
		gm.addSelection(edges);		
	}

	public void extendSelectionToIncomingArcs() {
		Set edges = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedVertexIterator(); it.hasNext();) {
			Object v = (Object) it.next();
			edges.addAll(gm.getIncomingEdges(v));
		}
		gm.addSelection(edges);		
	}

	public void extendSelectionToOutgoingVertices() {
		Set vertices = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedVertexIterator(); it.hasNext();) {
			Object v = (Object) it.next();
			for (Iterator it2 = gm.getOutgoingEdges(v).iterator(); it2.hasNext();) {
				GsDirectedEdge edge = (GsDirectedEdge) it2.next();
				vertices.add(edge.getTargetVertex());
			}
		}
		for (Iterator it = main.getSelectedEdges().iterator(); it.hasNext();) {
			GsDirectedEdge e = (GsDirectedEdge) it.next();
			vertices.add(e.getTargetVertex());
		}
		gm.addSelection(vertices);		
	}

	public void extendSelectionToIncomingVertices() {
		Set vertices = new HashSet();
		GsGraphManager gm = main.getGraph().getGraphManager();
		for (Iterator it = gm.getSelectedVertexIterator(); it.hasNext();) {
			Object v = (Object) it.next();
			for (Iterator it2 = gm.getIncomingEdges(v).iterator(); it2.hasNext();) {
				GsDirectedEdge edge = (GsDirectedEdge) it2.next();
				vertices.add(edge.getSourceVertex());
			}
		}
		for (Iterator it = main.getSelectedEdges().iterator(); it.hasNext();) {
			GsDirectedEdge e = (GsDirectedEdge) it.next();
			vertices.add(e.getSourceVertex());
		}
		gm.addSelection(vertices);		
	}
	
}
