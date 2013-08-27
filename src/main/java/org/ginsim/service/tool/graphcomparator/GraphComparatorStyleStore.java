package org.ginsim.service.tool.graphcomparator;

import org.ginsim.core.graph.view.css.CSSStyle;

public class GraphComparatorStyleStore {
	public CSSStyle v1;
	public CSSStyle v2;
	public CSSStyle v;
	
	public GraphComparatorStyleStore(CSSStyle v1, CSSStyle v2, CSSStyle v) {
		this.v1 = v1;
		this.v2 = v2;
		this.v = v;
	}
}
