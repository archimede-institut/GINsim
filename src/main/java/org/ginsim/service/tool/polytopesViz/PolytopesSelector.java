package org.ginsim.service.tool.polytopesViz;

import java.awt.Color;

import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.StatesSet;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.css.CSSNodeStyle;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.core.graph.view.css.CSSStyle;

public class PolytopesSelector extends Selector {
	public static final String IDENTIFIER = "polytopes";
	public static final String CAT_IN = "transient-trivial";
	private static final String CAT_OUT = "terminal-trivial";
	public static final String CAT_COMPLEX = "complex";
	
	public static final CSSNodeStyle STYLE_IN	= new CSSNodeStyle(Color.green.darker(), Color.black, Color.white, NodeBorder.SIMPLE, null);
	public static final CSSNodeStyle STYLE_OUT	= new CSSNodeStyle(Color.red.darker(),   Color.black, Color.white, NodeBorder.SIMPLE, null);
	
	private StatesSet polytope;
	
	public PolytopesSelector() {
		super(IDENTIFIER);
	}
	
	@Override
	public void resetDefaultStyle() {
		addCategory(CAT_IN, (CSSStyle)STYLE_IN.clone());
		addCategory(CAT_OUT, (CSSStyle)STYLE_OUT.clone());
	}

	@Override
	public String getCategoryForNode(Object obj) {
		if (obj instanceof DynamicNode) {
			DynamicNode node = (DynamicNode) obj;
			if (this.polytope.contains(node.state)) return CAT_IN;
			return CAT_OUT;
		}
		return "";
	}
	
	@Override
	public String getCategoryForEdge(Object obj) {
		return null;
	}
	
	@Override
	public boolean respondToEdges() {
		return false;
	}
	
	public void setCache(StatesSet polytope) {
		this.polytope = polytope;
	}
	
	public void flushCache() {
		this.polytope = null;
	}
}
