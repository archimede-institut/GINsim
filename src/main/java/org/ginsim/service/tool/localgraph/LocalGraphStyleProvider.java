package org.ginsim.service.tool.localgraph;

import java.awt.Color;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.EdgeStyleOverride;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProperty;
import org.ginsim.core.graph.view.style.StyleProvider;

public class LocalGraphStyleProvider implements StyleProvider<RegulatoryNode, RegulatoryMultiEdge>  {

	private final Map<RegulatoryMultiEdge, LocalGraphCategory> functionalityMap;
	private final LocalGraphEdgeStyle edgeStyle;
	
	public LocalGraphStyleProvider(StyleManager<RegulatoryNode, RegulatoryMultiEdge> styleManager, Map<RegulatoryMultiEdge, LocalGraphCategory> functionalityMap) {
		
		this.edgeStyle = new LocalGraphEdgeStyle(styleManager.getDefaultEdgeStyle());
		this.functionalityMap = functionalityMap;
	}
	
	@Override
	public NodeStyle<RegulatoryNode> getNodeStyle(RegulatoryNode node,	NodeStyle<RegulatoryNode> baseStyle) {
		return baseStyle;
	}

	@Override
	public EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> getEdgeStyle(	RegulatoryMultiEdge edge, EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> baseStyle) {
		LocalGraphCategory category = functionalityMap.get(edge);
		if (category == null) {
			category = LocalGraphCategory.NONFUNCTIONNAL;
		}
		edgeStyle.setBaseStyle(baseStyle, category);
		return edgeStyle;
	}

}

class LocalGraphEdgeStyle extends EdgeStyleOverride<RegulatoryNode,RegulatoryMultiEdge> {

	private LocalGraphCategory category = LocalGraphCategory.NONFUNCTIONNAL;

	public LocalGraphEdgeStyle(EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> style) {
		super(style);
	}

	public void setBaseStyle(EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> style, LocalGraphCategory category) {
		super.setBaseStyle(style);
		this.category = category;
	}

	@Override
	public Color getColor(RegulatoryMultiEdge obj) {
		switch (category) {
		case POSITIVE:
			return Color.GREEN;
		case NEGATIVE:
			return Color.RED;
		case DUAL:
			return Color.BLUE;
		}

		return Color.GRAY;
	}

}
