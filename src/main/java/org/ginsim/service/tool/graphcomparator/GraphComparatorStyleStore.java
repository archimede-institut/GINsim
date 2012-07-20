package org.ginsim.service.tool.graphcomparator;

import org.ginsim.core.graph.view.css.Style;

public class GraphComparatorStyleStore {
	public Style v1;
	public Style v2;
	public Style v;
	
	public GraphComparatorStyleStore(Style v1, Style v2, Style v) {
		this.v1 = v1;
		this.v2 = v2;
		this.v = v;
	}
}
