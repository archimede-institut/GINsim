package org.ginsim.servicegui.tool.pathfinding;


import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.EdgeStyleOverride;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.NodeStyleOverride;
import org.ginsim.core.graph.view.style.Style;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.graph.view.style.StyleProvider;


public class PathStyleProvider implements StyleProvider {

	private final Collection pathNodes = new HashSet();

	private final NodeInPathStyle nodeStyle;
	private final EdgeInPathStyle edgeStyle;
	
	public PathStyleProvider(StyleManager manager) {
		this.nodeStyle = new NodeInPathStyle(manager.getDefaultNodeStyle());
		this.edgeStyle = new EdgeInPathStyle(manager.getDefaultEdgeStyle());
	}
	
	@Override
	public NodeStyle getNodeStyle(Object node, NodeStyle baseStyle) {
		nodeStyle.setBaseStyle(baseStyle, pathNodes.contains(node));
		return nodeStyle;
	}

	@Override
	public EdgeStyle getEdgeStyle(Edge edge, EdgeStyle baseStyle) {
		Object src = edge.getSource();
		Object tgt = edge.getTarget();
		boolean inPath = pathNodes.contains(src) && pathNodes.contains(tgt);
		edgeStyle.setBaseStyle(baseStyle, inPath);
		return edgeStyle;
	}

	public void setPath(List path) {
		pathNodes.clear();
		pathNodes.addAll(path);
	}
}

class NodeInPathStyle extends NodeStyleOverride {

	private boolean inPath = false;
	
	public NodeInPathStyle(NodeStyle style) {
		super(style);
	}
	
	public void setBaseStyle(Style baseStyle, boolean inPath) {
		super.setBaseStyle(baseStyle);
		this.inPath = inPath;
	}

	@Override
	public Color getBackground(Object obj) {
		if (inPath) {
			return Color.GREEN;
		}
		return Color.WHITE;
	}

	@Override
	public Color getForeground(Object obj) {
		return Color.BLACK;
	}

	@Override
	public Color getTextColor(Object obj) {
		return Color.BLACK;
	}
}

class EdgeInPathStyle extends EdgeStyleOverride {

	private boolean inPath = false;
	
	public EdgeInPathStyle(EdgeStyle style) {
		super(style);
	}
	
	public void setBaseStyle(Style baseStyle, boolean inPath) {
		super.setBaseStyle(baseStyle);
		this.inPath = inPath;
	}

	@Override
	public Color getColor(Edge edge) {
		if (inPath) {
			return Color.BLUE;
		}
		return Color.GRAY;
	}

	@Override
	public int getWidth(Edge edge) {
		if (inPath) {
			return 2;
		}
		return 1;
	}
}
