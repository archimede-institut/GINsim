package org.ginsim.service.tool.connectivity;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.css.CSSNodeStyle;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.core.graph.view.css.CSSStyle;

public class ConnectivitySelector extends Selector {
	public static final String IDENTIFIER = "scc";
	public static final String CAT_TRANSIENT_TRIVIAL = "transient-trivial";
	private static final String CAT_TERMINAL_TRIVIAL = "terminal-trivial";
	public static final String CAT_TRANSIENT_COMPLEX = "transient-complex";
	public static final String CAT_TERMINAL_COMPLEX = "terminal-complex";
	
	public static final CSSNodeStyle STYLE_TRANSIENT_TRIVIAL	= new CSSNodeStyle(Color.white, Color.black, Color.black, NodeBorder.SIMPLE, NodeShape.RECTANGLE);
	public static final CSSNodeStyle STYLE_TERMINAL_TRIVIAL	= new CSSNodeStyle(Color.red.darker(),   Color.black, Color.white, NodeBorder.SIMPLE, NodeShape.ELLIPSE);
	public static final CSSNodeStyle STYLE_TRANSIENT_COMPLEX	= new CSSNodeStyle(Color.blue.darker(),  Color.black, Color.red, NodeBorder.SIMPLE, NodeShape.RECTANGLE);
	public static final CSSNodeStyle STYLE_TERMINAL_COMPLEX	= new CSSNodeStyle(Color.orange,  Color.black, Color.blue, NodeBorder.SIMPLE, NodeShape.ELLIPSE);
	
	private List<NodeReducedData> cacheComponents;
	private int totalComplexComponents;
	private Graph cacheGraph;
	
	public static Color[] TRANSIENT_PALETTE = ColorPalette.createColorPaletteByHue(25, (float)0.5, (float)0.3, (float)0.6, (float)0.2);
	public static Color[] TERMINAL_PALETTE  = ColorPalette.createColorPaletteByHue(25, (float)1.0, (float)0.3, (float)0.9, (float)0.1);
	
	public ConnectivitySelector() {
		super(IDENTIFIER);
	}
	
	@Override
	public void resetDefaultStyle() {
		addCategory(CAT_TRANSIENT_TRIVIAL, (CSSStyle)STYLE_TRANSIENT_TRIVIAL.clone());
		addCategory(CAT_TERMINAL_TRIVIAL, (CSSStyle)STYLE_TERMINAL_TRIVIAL.clone());
		addCategory(CAT_TRANSIENT_COMPLEX, (CSSStyle)STYLE_TRANSIENT_COMPLEX.clone());
		addCategory(CAT_TERMINAL_COMPLEX, (CSSStyle)STYLE_TERMINAL_COMPLEX.clone());
	}

	@Override
	public String getCategoryForNode(Object obj) {
		// if we are dealing with a SCC graph directly
		if (obj instanceof NodeReducedData) {
			NodeReducedData node = (NodeReducedData) obj;
			if (node.isTrivial()) {
				if (node.isTransient(cacheGraph)) {
					return CAT_TRANSIENT_TRIVIAL;
				}
				return CAT_TERMINAL_TRIVIAL;
			}
			Collection outgoing = cacheGraph.getOutgoingEdges(obj);
			if (outgoing != null && outgoing.size() > 0) {
				return CAT_TRANSIENT_COMPLEX;
			}
			return CAT_TERMINAL_COMPLEX;
		}
		
		// if we don't have any further information
		if (cacheComponents == null) {
			return CAT_TRANSIENT_TRIVIAL;
		}
		
		int i_complex = 0;
		for (NodeReducedData nrd : cacheComponents) {
			if (!nrd.isTrivial()){
				i_complex++;
			}
			if (nrd.getContent().contains(obj)) {
				if (nrd.isTrivial()) {
					if (nrd.isTransient(cacheGraph)) {
						return CAT_TRANSIENT_TRIVIAL;
					}
					return CAT_TERMINAL_TRIVIAL;
				} else if (nrd.isTransient(cacheGraph)) {
					return "complex_"+TRANSIENT_PALETTE[i_complex%TRANSIENT_PALETTE.length].getRGB();
				} else {
					return "complex_"+TERMINAL_PALETTE[i_complex%TERMINAL_PALETTE.length].getRGB();
				}
			}
		}
		return CAT_TRANSIENT_TRIVIAL;
	}

	@Override
	public CSSStyle getStyle(String category) {
		int index = category.indexOf('_');
		if (index == -1) {
			return super.getStyle(category);
		} else {
			CSSNodeStyle style = (CSSNodeStyle)STYLE_TRANSIENT_COMPLEX.clone();
			int color = Integer.parseInt(category.substring(index+1)); 
			style.background = new Color(color);
			style.textcolor = ColorPalette.getConstrastedForegroundColor(style.background);
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
	
	public void setCache(List<NodeReducedData> cache, Graph<?,?> graph) {
		this.cacheComponents = cache;
		this.cacheGraph = graph;
		this.totalComplexComponents = 0;
		if (cache != null) {
			for (NodeReducedData nodeReducedData : cache) {
				if (!nodeReducedData.isTrivial()) {
					totalComplexComponents ++;
				}
			}
		}
	}
	
	public void flushCache() {
		this.cacheComponents = null;
		this.cacheGraph = null;
	}
}
