package org.ginsim.graph.view.css;

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
	
	public static final VertexStyle STYLE_NODES = new VertexStyle();
	public static final EdgeStyle   STYLE_EDGES = new EdgeStyle();

	public AllSelector() {
		super(IDENTIFIER);
	}

	public void resetDefaultStyle() {
		addCategory(CAT_NODES, (Style)STYLE_NODES.clone());
		addCategory(CAT_EDGES, (Style)STYLE_EDGES.clone());		
	}

	public String getCategoryForEdge(Object obj) {
		return CAT_EDGES;
	}

	public String getCategoryForNode(Object obj) {
		return CAT_NODES;
	}

	public boolean requireCategory() {
		return true;
	}
}
