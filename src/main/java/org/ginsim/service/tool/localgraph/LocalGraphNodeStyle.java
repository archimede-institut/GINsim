package org.ginsim.service.tool.localgraph;

import java.awt.Color;

import org.ginsim.core.graph.regulatorygraph.ActivityLevel;
import org.ginsim.core.graph.regulatorygraph.DefaultRegulatoryNodeStyle;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.NodeStyleOverride;

class LocalGraphNodeStyle extends NodeStyleOverride<RegulatoryNode> {

	private final Color C_INACTIVE, C_MIDLEVEL, C_ACTIVE;
	private ActivityLevel level = ActivityLevel.INACTIVE;

	public LocalGraphNodeStyle(NodeStyle<RegulatoryNode> style) {
		super(style);
		if (style instanceof DefaultRegulatoryNodeStyle) {
			DefaultRegulatoryNodeStyle nstyle = (DefaultRegulatoryNodeStyle)style;
			C_INACTIVE = nstyle.getDefaultColor(ActivityLevel.INACTIVE);
			C_MIDLEVEL = nstyle.getDefaultColor(ActivityLevel.MIDLEVEL);
			C_ACTIVE = nstyle.getDefaultColor(ActivityLevel.ACTIVE);
		} else {
			C_INACTIVE = Color.WHITE;
			C_MIDLEVEL = Color.PINK;
			C_ACTIVE = Color.ORANGE;
		}
	}

	public void setBaseStyle(NodeStyle<RegulatoryNode> style, ActivityLevel level) {
		super.setBaseStyle(style);
		this.level = level;
	}

	@Override
	public Color getBackground(RegulatoryNode obj) {
		switch (level) {
		case MIDLEVEL:
			return C_MIDLEVEL;
		case ACTIVE:
			return C_ACTIVE;
		}
		return C_INACTIVE;
	}

}