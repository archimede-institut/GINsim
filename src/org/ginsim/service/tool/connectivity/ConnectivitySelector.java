package org.ginsim.service.tool.connectivity;

import java.awt.Color;
import java.util.List;

import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.css.NodeStyle;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.core.graph.view.css.Style;

public class ConnectivitySelector extends Selector {
	public static final String IDENTIFIER = "scc";
	public static final String CAT_TRIVIAL = "trivial";
	public static final String CAT_COMPLEX = "complex";
	
	public static final NodeStyle STYLE_TRIVIAL		= new NodeStyle(Color.green.darker(), null, NodeBorder.SIMPLE, NodeShape.ELLIPSE);
	public static final NodeStyle STYLE_COMPLEX 	= new NodeStyle(Color.red.darker(), null, NodeBorder.SIMPLE, NodeShape.RECTANGLE);
	
	private List<NodeReducedData> cache;
	private int totalComplexComponents;
	
	public ConnectivitySelector() {
		super(IDENTIFIER);
	}
	
	@Override
	public void resetDefaultStyle() {
		addCategory(CAT_TRIVIAL, (Style)STYLE_TRIVIAL.clone());
		addCategory(CAT_COMPLEX, (Style)STYLE_COMPLEX.clone());
	}

	@Override
	public String getCategoryForNode(Object obj) {
		if (obj instanceof NodeReducedData) {
			NodeReducedData node = (NodeReducedData) obj;
			if (node.isTrivial()) {
				return CAT_TRIVIAL;	
			}
		} else {
			if (cache == null) {
				return CAT_COMPLEX;
			}
			int saturation = 0; //color (HSB)
			for (NodeReducedData nrd : cache) {
				if (!nrd.isTrivial()){
					saturation += 200/totalComplexComponents;
				}

				if (nrd.getContent().contains(obj)) {
					if (nrd.isTrivial()) {
						return CAT_TRIVIAL;
					} else {
						return "complex_"+Color.getHSBColor(0, saturation, 80).getRGB();
					}
				}
			}
		}
		
		return CAT_COMPLEX;
	}

	@Override
	public Style getStyle(String category) {
		int index = category.indexOf('_');
		if (index == -1) {
			return super.getStyle(category);
		} else {
			NodeStyle style = (NodeStyle)STYLE_COMPLEX.clone();
			int color = Integer.parseInt(category.substring(index+1)); 
			style.background = new Color(color);
			return style;
		}
	}
	
	@Override
	public String getCategoryForEdge(Object obj) {
		return null;
	}
	
	@Override
	public boolean respondToEdges() {
		return false;
	}
	
	public void setCache(List<NodeReducedData> cache) {
		this.cache = cache;
		this.totalComplexComponents = 0;
		for (NodeReducedData nodeReducedData : cache) {
			if (!nodeReducedData.isTrivial()) {
				totalComplexComponents ++;
			}
		}
	}
	
	public void flushCache() {
		this.cache = null;
	}
}
