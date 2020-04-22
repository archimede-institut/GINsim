package org.ginsim.servicegui.tool.regulatorygraphanimation;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.style.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LRGCustomStyleProvider implements StyleProvider<RegulatoryNode, RegulatoryMultiEdge> {

	private final CustomColoredNodeStyle nodeStyle;
	private final CustomColoredEdgeStyle edgeStyle;
	
	private final Map<RegulatoryNode, Integer> nodeIndex = new HashMap<RegulatoryNode, Integer>();
	
	private byte[] state = null;
	private Map<Byte, Color> colormap = new HashMap<>();

	public LRGCustomStyleProvider(RegulatoryGraph lrg) {
		
		// build node index
		int i=0;
		for (RegulatoryNode node: lrg.getNodeOrder()) {
			nodeIndex.put(node,  i);
			i++;
		}
		
		StyleManager<RegulatoryNode, RegulatoryMultiEdge> styleManager = lrg.getStyleManager();
		nodeStyle = new CustomColoredNodeStyle(styleManager.getDefaultNodeStyle());
		edgeStyle = new CustomColoredEdgeStyle(styleManager.getDefaultEdgeStyle());
	}

	public void setState(byte[] state) {
		this.state = state;
	}

	public void mapState2Color(byte state, Color color) {
		this.colormap.put(state, color);
	}

	public void mapState2Color(byte state, int r, int g, int b) {
		this.mapState2Color(state, new Color(r,g,b));
	}

	@Override
	public NodeStyle<RegulatoryNode> getNodeStyle(RegulatoryNode node, NodeStyle<RegulatoryNode> baseStyle) {
		if (state == null) {
			return baseStyle;
		}
		
		Integer i = nodeIndex.get(node);
		nodeStyle.config(this.state[i], colormap.get(this.state[i]));
		return nodeStyle;
	}

	@Override
	public EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> getEdgeStyle(RegulatoryMultiEdge edge, EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> baseStyle) {
		if (state == null) {
			return baseStyle;
		}
		
		Integer i = nodeIndex.get(edge.getSource());
		boolean isActive = (i != null && state[i] >= edge.getMin(0));
		edgeStyle.setBaseStyle(baseStyle, isActive);
		return edgeStyle;
	}

	@Override
	public String getCSS() {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<Byte, Color> e: colormap.entrySet()) {
			String bg = ColorPalette.getColorCode(e.getValue());
			String key = (""+e.getKey()).replace("-", "m");
			sb.append(".custom_"+key+" .shape { fill: "+bg+"; }\n");
		}
		return sb.toString();
	}
}

class CustomColoredNodeStyle extends NodeStyleOverride<RegulatoryNode> {

	private Color bg = null;
	private byte value = 0;

	public CustomColoredNodeStyle(NodeStyle<RegulatoryNode> style) {
		super(style);
	}

	public void setBaseStyle(NodeStyle<RegulatoryNode> style) {
		super.setBaseStyle(style);
	}

	void config(byte value, Color bg) {
		this.value = value;
		this.bg = bg;
	}

	@Override
	public Color getBackground(RegulatoryNode obj) {
		if (this.bg == null) {
			return Color.WHITE;
		}
		return this.bg;
	}

	@Override
	public Color getForeground(RegulatoryNode obj) {
		return Color.BLACK;
	}

	@Override
	public Color getTextColor(RegulatoryNode obj) {
		return Color.BLACK;
	}

	@Override
	public String getCSSClass(RegulatoryNode node) {
		return "node custom_"+this.value;
	}
}

class CustomColoredEdgeStyle extends EdgeStyleOverride<RegulatoryNode,RegulatoryMultiEdge> {

	private boolean isActive = false;

	public CustomColoredEdgeStyle(EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> style) {
		super(style);
	}

	public void setBaseStyle(EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> style, boolean isActive) {
		super.setBaseStyle(style);
		this.isActive = isActive;
	}

	@Override
	public Color getColor(RegulatoryMultiEdge obj) {
		if (isActive) {
			return Color.BLUE;
		}
		return Color.GRAY;
	}

}