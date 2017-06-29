package org.ginsim.servicegui.tool.lrgcolorizer;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.EdgeStyleOverride;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.NodeStyleOverride;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProvider;


public class LRGPatternStyleProvider implements StyleProvider<RegulatoryNode, RegulatoryMultiEdge> {

	private final PatternColoredNodeStyle nodeStyle;
	private final PatternColoredEdgeStyle edgeStyle;
	
	private final StyleManager<RegulatoryNode, RegulatoryMultiEdge> styleManager;
	
	private final Map<RegulatoryNode, Integer> nodeIndex = new HashMap<RegulatoryNode, Integer>();
	
	private byte[] pattern = null;
	private boolean[] marked = null;
	
	public LRGPatternStyleProvider(RegulatoryGraph lrg) {
		
		// build node index
		int i=0;
		for (RegulatoryNode node: lrg.getNodeOrder()) {
			nodeIndex.put(node,  i);
			i++;
		}
		
		styleManager = lrg.getStyleManager();
		nodeStyle = new PatternColoredNodeStyle(styleManager.getDefaultNodeStyle());
		edgeStyle = new PatternColoredEdgeStyle(styleManager.getDefaultEdgeStyle());
		
	}

	public void setPattern(byte[] state, boolean[] marked) {
		this.pattern = state;
		this.marked = marked;
		
		styleManager.stylesUpdated();
	}
	
	@Override
	public NodeStyle<RegulatoryNode> getNodeStyle(RegulatoryNode node, NodeStyle<RegulatoryNode> baseStyle) {
		if (pattern == null) {
			return baseStyle;
		}
		
		Integer i = nodeIndex.get(node);
		if (i < 0) {
			nodeStyle.setBaseStyle(baseStyle, ActivityType.REDUCED, true);
			return nodeStyle;
		}
		ActivityType activity;
		if (i == null || pattern[i] < 0) {
			activity = ActivityType.FREE;
		} else if ( pattern[i] == 0) {
				activity = ActivityType.INACTIVE;
		} else if (pattern[i] == node.getMaxValue()) {
			activity = ActivityType.ACTIVE;
		} else {
			activity = ActivityType.PARTIAL;
		}
		
		boolean isMarked = (marked != null && marked[i]);
		
		nodeStyle.setBaseStyle(baseStyle, activity, isMarked);
		return nodeStyle;
	}

	@Override
	public EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> getEdgeStyle(RegulatoryMultiEdge edge, EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> baseStyle) {
		if (pattern == null) {
			return baseStyle;
		}
		
		Integer i = nodeIndex.get(edge.getSource());
		boolean isActive = false;
		boolean isInactive = false;
		if (i != null) {
			byte v = pattern[i];
			isActive = (v >= edge.getMin(0));
			isInactive = (v>=0 && v < edge.getMin(0));
		}
		edgeStyle.setBaseStyle(baseStyle, isActive, isInactive);
		return edgeStyle;
	}
}

enum ActivityType {
	INACTIVE, ACTIVE, PARTIAL, FREE, REDUCED;
}


class PatternColoredNodeStyle extends NodeStyleOverride<RegulatoryNode> {

	private ActivityType activity = ActivityType.INACTIVE;
	private boolean marked = false;
	
	public PatternColoredNodeStyle(NodeStyle<RegulatoryNode> style) {
		super(style);
	}

	public void setBaseStyle(NodeStyle<RegulatoryNode> style, ActivityType activity, boolean marked) {
		super.setBaseStyle(style);
		this.activity = activity;
		this.marked = marked;
	}

	@Override
	public Color getBackground(RegulatoryNode obj) {
		switch (activity) {
		case ACTIVE:
			return Color.ORANGE;
		case PARTIAL:
			return Color.YELLOW;
		case INACTIVE:
			return Color.CYAN;
		case REDUCED:
			return Color.PINK;
		default:
			return Color.WHITE;
		}
	}

	@Override
	public NodeShape getNodeShape(RegulatoryNode obj) {
		if (marked) {
			return NodeShape.ELLIPSE;
		}
		return NodeShape.RECTANGLE;
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

class PatternColoredEdgeStyle extends EdgeStyleOverride<RegulatoryNode,RegulatoryMultiEdge> {

	private boolean isActive = false;
	private boolean isInactive = false;

	public PatternColoredEdgeStyle(EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> style) {
		super(style);
	}

	public void setBaseStyle(EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> style, boolean isActive, boolean isInactive) {
		super.setBaseStyle(style);
		this.isActive = isActive;
		this.isInactive = isInactive;
	}

	@Override
	public Color getColor(RegulatoryMultiEdge obj) {
		if (isActive) {
			return Color.ORANGE.darker();
		}
		if (isInactive) {
			return Color.GRAY.brighter();
		}
		
		return Color.BLACK;
	}

}