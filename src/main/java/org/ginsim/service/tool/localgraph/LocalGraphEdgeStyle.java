package org.ginsim.service.tool.localgraph;

import java.awt.Color;

import org.ginsim.core.graph.regulatorygraph.DefaultRegulatoryEdgeStyle;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.EdgeStyleOverride;

class LocalGraphEdgeStyle extends EdgeStyleOverride<RegulatoryNode,RegulatoryMultiEdge> {

	private LocalGraphCategory category = LocalGraphCategory.NONFUNCTIONNAL;

	private final Color C_POS, C_NEG, C_DUAL, C_NONE;
	
	public LocalGraphEdgeStyle(EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> style) {
		super(style);
		if (style instanceof DefaultRegulatoryEdgeStyle) {
			DefaultRegulatoryEdgeStyle estyle = (DefaultRegulatoryEdgeStyle)style;
			C_POS = estyle.getDefaultColor(RegulatoryEdgeSign.POSITIVE);
			C_NEG = estyle.getDefaultColor(RegulatoryEdgeSign.NEGATIVE);
			C_DUAL = estyle.getDefaultColor(RegulatoryEdgeSign.DUAL);
			C_NONE = estyle.getDefaultColor(RegulatoryEdgeSign.UNKNOWN);
		} else {
			C_POS = Color.GREEN;
			C_NEG = Color.RED;
			C_DUAL = Color.BLUE;
			C_NONE = Color.GRAY;
		}
	}

	public void setBaseStyle(EdgeStyle<RegulatoryNode, RegulatoryMultiEdge> style, LocalGraphCategory category) {
		super.setBaseStyle(style);
		this.category = category;
	}

	@Override
	public Color getColor(RegulatoryMultiEdge obj) {
		switch (category) {
		case POSITIVE:
			return C_POS;
		case NEGATIVE:
			return C_NEG;
		case DUAL:
			return C_DUAL;
		}

		return C_NONE;
	}

}