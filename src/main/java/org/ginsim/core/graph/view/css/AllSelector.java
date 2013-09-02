package org.ginsim.core.graph.view.css;

/**
 * 
 * A simple selector with two categories to select all the nodes or all the edges.
 * 
 * selectors : *.node and *.edges
 *
 */
public class AllSelector extends Selector {
	public static final String IDENTIFIER = "all";
	
	public static final String CAT_NODES = "nodes";
	public static final String CAT_EDGES = "edges";
	
	public static final CSSNodeStyle STYLE_NODES = new CSSNodeStyle();
	public static final CSSEdgeStyle   STYLE_EDGES = new CSSEdgeStyle();

	public AllSelector() {
		super(IDENTIFIER);
	}

	public void resetDefaultStyle() {
		addCategory(CAT_NODES, (CSSStyle)STYLE_NODES.clone());
		addCategory(CAT_EDGES, (CSSStyle)STYLE_EDGES.clone());		
	}

	public String getCategoryForEdge(Object obj) {
		return CAT_EDGES;
	}

	public String getCategoryForNode(Object obj) {
		return CAT_NODES;
	}

}
