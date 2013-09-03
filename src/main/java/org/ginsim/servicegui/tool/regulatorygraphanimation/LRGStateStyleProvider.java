package org.ginsim.servicegui.tool.regulatorygraphanimation;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.EdgeStyleOverride;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.NodeStyleOverride;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProvider;

public class LRGStateStyleProvider implements StyleProvider<RegulatoryNode, RegulatoryMultiEdge> {

	private final StateColoredNodeStyle nodeStyle;
	private final StateColoredEdgeStyle edgeStyle;
	
	private final Map<RegulatoryNode, Integer> nodeIndex = new HashMap<RegulatoryNode, Integer>();
	
	private byte[] state = null;
	
	public LRGStateStyleProvider(RegulatoryGraph lrg) {
		
		// build node index
		int i=0;
		for (RegulatoryNode node: lrg.getNodeOrder()) {
			nodeIndex.put(node,  i);
			i++;
		}
		
		StyleManager<RegulatoryNode, RegulatoryMultiEdge> styleManager = lrg.getStyleManager();
		nodeStyle = new StateColoredNodeStyle(styleManager.getDefaultNodeStyle());
		edgeStyle = new StateColoredEdgeStyle(styleManager.getDefaultEdgeStyle());
	}

	public void setState(byte[] state) {
		this.state = state;
	}
	
	@Override
	public NodeStyle<RegulatoryNode> getNodeStyle(RegulatoryNode node, NodeStyle<RegulatoryNode> baseStyle) {
		if (state == null) {
			return baseStyle;
		}
		
		Integer i = nodeIndex.get(node);
		ActivityType activity;
		if (i == null || state[i] <= 0) {
			activity = ActivityType.INACTIVE;
		} else if (state[i] == node.getMaxValue()) {
			activity = ActivityType.ACTIVE;
		} else {
			activity = ActivityType.PARTIAL;
		}
		
		nodeStyle.setBaseStyle(baseStyle, activity);
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
}

enum ActivityType {
	INACTIVE, ACTIVE, PARTIAL;
}


class StateColoredNodeStyle extends NodeStyleOverride<RegulatoryNode> {

	private ActivityType activity = ActivityType.INACTIVE;
	
	public StateColoredNodeStyle(NodeStyle<RegulatoryNode> style) {
		super(style);
	}

	public void setBaseStyle(NodeStyle<RegulatoryNode> style, ActivityType activity) {
		super.setBaseStyle(style);
		this.activity = activity;
	}

	@Override
	public Color getBackground(RegulatoryNode obj) {
		switch (activity) {
		case ACTIVE:
			return Color.ORANGE;
		case PARTIAL:
			return Color.YELLOW;
		default:
			return Color.WHITE;
		}
	}

	@Override
	public Color getForeground(RegulatoryNode obj) {
		return Color.BLACK;
	}

	@Override
	public Color getTextColor(RegulatoryNode obj) {
		return Color.BLACK;
	}
}

class StateColoredEdgeStyle extends EdgeStyleOverride<RegulatoryNode,RegulatoryMultiEdge> {

	private boolean isActive = false;

	public StateColoredEdgeStyle(EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> style) {
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