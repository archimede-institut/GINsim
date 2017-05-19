package org.ginsim.servicegui.tool.regulatorygraphanimation;

import java.awt.Color;

import org.ginsim.core.graph.dynamicgraph.DynamicEdge;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.EdgeStyleOverride;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.NodeStyleOverride;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProvider;

public class STGPathStyleProvider implements StyleProvider<DynamicNode, DynamicEdge> {

	private final PathColoredNodeStyle nodeStyle;
	private final PathColoredEdgeStyle edgeStyle;
	
	private final RegulatoryAnimator animator;
	
	public STGPathStyleProvider(DynamicGraph stg, RegulatoryAnimator animator) {
		this.animator = animator;
		StyleManager<DynamicNode, DynamicEdge> styleManager = stg.getStyleManager();
		nodeStyle = new PathColoredNodeStyle(styleManager.getDefaultNodeStyle());
		edgeStyle = new PathColoredEdgeStyle(styleManager.getDefaultEdgeStyle());
	}

	@Override
	public NodeStyle<DynamicNode> getNodeStyle(DynamicNode node, NodeStyle<DynamicNode> baseStyle) {
		nodeStyle.setBaseStyle(baseStyle, animator.getStatus(node));
		return nodeStyle;
	}

	@Override
	public EdgeStyle<DynamicNode, DynamicEdge> getEdgeStyle(DynamicEdge edge, EdgeStyle<DynamicNode, DynamicEdge> baseStyle) {
		edgeStyle.setBaseStyle(baseStyle, animator.getStatus(edge));
		return edgeStyle;
	}
}

class PathColoredNodeStyle extends NodeStyleOverride<DynamicNode> {

	private int status = -1;
	
	public PathColoredNodeStyle(NodeStyle<DynamicNode> style) {
		super(style);
	}

	public void setBaseStyle(NodeStyle<DynamicNode> style, int status) {
		super.setBaseStyle(style);
		this.status = status;
	}

	@Override
	public Color getBackground(DynamicNode obj) {
		switch (status) {
		case 1:
			return Color.ORANGE;
		case 0:
			return Color.CYAN;
		default:
			return Color.WHITE;
		}
	}

	@Override
	public Color getForeground(DynamicNode obj) {
		return Color.BLACK;
	}

	@Override
	public Color getTextColor(DynamicNode obj) {
		return Color.BLACK;
	}
}

class PathColoredEdgeStyle extends EdgeStyleOverride<DynamicNode,DynamicEdge> {

	private int status = -1;

	public PathColoredEdgeStyle(EdgeStyle<DynamicNode, DynamicEdge> style) {
		super(style);
	}

	public void setBaseStyle(EdgeStyle<DynamicNode, DynamicEdge> style, int status) {
		super.setBaseStyle(style);
		this.status = status;
	}

	@Override
	public Color getColor(DynamicEdge obj) {
		switch (status) {
		case 1:
			return Color.BLACK;
		case 0: 
			return Color.CYAN;
		}
		return Color.GRAY;
	}

}