package fr.univmrs.tagc.GINsim.css;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.GINsim.graph.GsAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;

public class CascadingStyle {
	private Map old_nodes, old_edges;
	public boolean shouldStoreOldStyle;
	
	public CascadingStyle(boolean shouldStoreOldStyle) {
		old_nodes = new HashMap();
		old_edges = new HashMap();
		this.shouldStoreOldStyle = shouldStoreOldStyle;
	}
	
	/**
	 * Apply a selector on an edge using its attribute reader.
	 * If shouldStoreOldStyle is true it save the old style too.
	 * @param sel
	 * @param edge
	 * @param areader
	 */
	public void applyOnEdge(Selector sel, Object edge, GsAttributesReader areader) {
		if (shouldStoreOldStyle) old_edges.put(edge, new EdgeStyle(areader));
		sel.applyStyleForEdge(edge, areader);
	}

	/**
	 * Apply a selector on a node using its attribute reader.
	 * If shouldStoreOldStyle is true it save the old style too.
	 * @param sel
	 * @param edge
	 * @param areader
	 */
	public void applyOnNode(Selector sel, Object node, GsAttributesReader areader) {
		if (shouldStoreOldStyle) old_nodes.put(node, new VertexStyle(areader));
		sel.applyStyleForNode(node, areader);
	}

	/**
	 * Apply a style on an edge using its attribute reader.
	 * If shouldStoreOldStyle is true it save the old style too.
	 * @param sel
	 * @param edge
	 * @param areader
	 */
	public void applyOnEdge(EdgeStyle style, Object edge, GsAttributesReader areader) {
		if (shouldStoreOldStyle) old_edges.put(edge, new EdgeStyle(areader));
		style.apply(areader);
	}

	/**
	 * Apply a style on an edge using its attribute reader.
	 * If shouldStoreOldStyle is true it save the old style too.
	 * @param sel
	 * @param node
	 * @param areader
	 */
	public void applyOnNode(VertexStyle style, Object node, GsAttributesReader areader) {
		if (shouldStoreOldStyle) old_edges.put(node, new EdgeStyle(areader));
		style.apply(areader);
	}


	/**
	 * Apply all the selectors in cascade on each edges
	 * 
	 * If you want to apply only one selector, use applySelectorOnEdges instead.
	 * 
	 * @param selectors an ordered list of selector to apply on each edges
	 * @param edges a collection of edges to apply the selectors on
	 * @param ereader an edge AttributesReader
	 */
	public void applySelectorsOnEdges(List selectors, Collection edges, GsEdgeAttributesReader ereader) {
		for (Iterator it_edges = edges.iterator(); it_edges.hasNext();) {			//For each edges
			Object edge = (Object) it_edges.next();
			ereader.setEdge(edge);
			EdgeStyle style = new EdgeStyle(ereader);								//  get the style
			if (shouldStoreOldStyle) old_edges.put(edge, style.clone());					//  save a copy if needed
			for (Iterator it_sel = selectors.iterator(); it_sel.hasNext();) {		//  For each selector
				Selector sel = (Selector) it_sel.next();
				style.merge(sel.getStyleForEdge(edge));								//     update the style using the selector
			}
			style.apply(ereader);													//  apply the style to the edge.
		}
	}

	/**
	 * Apply all the selectors in cascade on each nodes
	 * 
	 * If you want to apply only one selector, use applySelectorOnNodes instead.
	 * 
	 * @param selectors an ordered list of selector to apply on each nodes
	 * @param nodes a collection of nodes to apply the selectors on
	 * @param vreader a vertex AttributesReader
	 */
	public void applySelectorsOnNodes(List selectors, Collection nodes, GsVertexAttributesReader vreader) {
		for (Iterator it_nodes = nodes.iterator(); it_nodes.hasNext();) {			//For each nodes
			Object node = (Object) it_nodes.next();
			vreader.setVertex(node);
			VertexStyle style = new VertexStyle(vreader);							//  get the style
			if (shouldStoreOldStyle) old_nodes.put(node, style.clone());					//  save a copy if needed
			for (Iterator it_sel = selectors.iterator(); it_sel.hasNext();) {		//  For each selector
				Selector sel = (Selector) it_sel.next();
				style.merge(sel.getStyleForNode(node));								//     update the style using the selector
			}
			style.apply(vreader);													//  apply the style to the node.
		}
	}
	
	/**
	 * Apply one selector on each edges from a collection
	 * 
	 * If you want to apply several selector, use applySelectorsOnEdges instead.
	 * 
	 * @param sel the selector to apply on each edges
	 * @param edges a collection of edges to apply the selectors on
	 * @param ereader an edge AttributesReader
	 */
	public void applySelectorOnEdges(Selector sel, Collection edges, GsEdgeAttributesReader ereader) {
		for (Iterator it_edges = edges.iterator(); it_edges.hasNext();) {			//For each edge
			Object edge = (Object) it_edges.next();
			ereader.setEdge(edge);
			EdgeStyle style = new EdgeStyle(ereader);								//  get the style
			if (shouldStoreOldStyle) old_edges.put(edge, style.clone());					//  save a copy if needed
			((EdgeStyle)sel.getStyleForEdge(edge)).apply(ereader);		//  apply the style to the edge.
		}
	}

	/**
	 * Apply one selector on each nodes from a collection
	 * 
	 * If you want to apply several selector, use applySelectorsOnNodes instead.
	 * 
	 * @param sel the selector to apply on each nodes
	 * @param edges a collection of nodes to apply the selectors on
	 * @param vreader an vertex AttributesReader
	 */
	public void applySelectorOnNodes(Selector sel, Collection nodes, GsVertexAttributesReader vreader) {
		for (Iterator it_nodes = nodes.iterator(); it_nodes.hasNext();) {			//For each edge
			Object node = (Object) it_nodes.next();
			vreader.setVertex(node);
			VertexStyle style = new VertexStyle(vreader);								//  get the style
			if (shouldStoreOldStyle) old_nodes.put(node, style.clone());					//  save a copy if needed
			((VertexStyle)sel.getStyleForNode(node)).apply(vreader);	//  apply the style to the node.
		}
	}
	
	/**
	 * Restore an edge if it has been previously saved.
	 * @param edge
	 * @param areader a edge attributesReader (must be set to the right edge)
	 */
	public void restoreEdge(Object edge, GsEdgeAttributesReader areader) {
		((Style)old_edges.get(edge)).apply(areader);
	}
	
	/**
	 * Restore a node if it has been previously saved.
	 * @param node
	 * @param areader a vertex attributesReader (must be set to the right vertex)
	 */
	public void restoreNode(Object node, GsVertexAttributesReader areader) {
		((Style)old_nodes.get(node)).apply(areader);		
	}

	/**
	 * Restore all the edges previously saved.
	 * @param areader an edge attributesReader
	 */
	public void restoreAllEdges(GsEdgeAttributesReader areader) {
		for (Iterator it_edges = old_edges.keySet().iterator(); it_edges.hasNext();) {
			Object edge = (Object) it_edges.next();
			areader.setEdge(edge);
			((Style)old_edges.get(edge)).apply(areader);
		}
	}

	/**
	 * Restore all the nodes previously saved.
	 * @param areader a vertex attributesReader
	 */
	public void restoreAllNodes(GsVertexAttributesReader areader) {
		for (Iterator it_nodes = old_nodes.keySet().iterator(); it_nodes.hasNext();) {
			Object node = (Object) it_nodes.next();
			areader.setVertex(node);
			((Style)old_nodes.get(node)).apply(areader);
		}
	}

	/**
	 * Restore all the edges from the collection edges.
	 * @param edges a collection of edges to restore
	 * @param areader a edge attributesReader
	 */
	public void restoreAllEdges(Collection edges, GsEdgeAttributesReader areader) {
		for (Iterator it_edges = edges.iterator(); it_edges.hasNext();) {
			Object edge = (Object) it_edges.next();
			areader.setEdge(edge);
			Style style = (Style)old_edges.get(edge);
			if (style != null) style.apply(areader);
		}
	}
	
	/**
	 * Restore all the nodes from the collection nodes.
	 * @param nodes a collection of nodes to restore
	 * @param areader a vertex attributesReader
	 */
	public void restoreAllNodes(Collection nodes, GsVertexAttributesReader areader) {
		for (Iterator it_nodes = nodes.iterator(); it_nodes.hasNext();) {
			Object node = (Object) it_nodes.next();
			areader.setVertex(node);
			Style style = (Style)old_nodes.get(node);
			if (style != null) style.apply(areader);
		}
	}

}
