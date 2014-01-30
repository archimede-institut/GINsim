package org.ginsim.core.graph.hierarchicaltransitiongraph;

import java.awt.Color;
import java.util.Collection;

import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.style.NodeStyleImpl;
import org.ginsim.core.graph.view.style.StyleProperty;


/**
 * Default style for the HTG.
 *
 * @author Aurelien Naldi
 */
public class DefaultHTGNodeStyle extends NodeStyleImpl<HierarchicalNode> {

	private final HierarchicalTransitionGraph graph;
	
	public DefaultHTGNodeStyle(HierarchicalTransitionGraph graph) {
		this.graph = graph;
    	setProperty(StyleProperty.WIDTH, 5+10*graph.getNodeOrderSize());
    	setProperty(StyleProperty.HEIGHT, 25);
	}

	@Override
	public Color getBackground(HierarchicalNode hnode) {
        if (hnode == null) {
            return Color.WHITE;
        }
		switch (hnode.getType()) {
		case HierarchicalNode.TYPE_STABLE_STATE:
			return (HierarchicalNode.TYPE_STABLE_STATE_COLOR);
		case HierarchicalNode.TYPE_TRANSIENT_CYCLE:
			return (HierarchicalNode.TYPE_TRANSIENT_CYCLE_COLOR);
		case HierarchicalNode.TYPE_TERMINAL_CYCLE:
			return(HierarchicalNode.TYPE_TERMINAL_CYCLE_COLOR);

		case HierarchicalNode.TYPE_TRANSIENT_COMPONENT:
			Collection in = graph.getIncomingEdges(hnode);
			if (in == null || in.isEmpty()) {
				return HierarchicalNode.TYPE_EDEN_TRANSIENT_COMPONENT_COLOR;
			}
			if (hnode.statesSet.getSizeOrOverApproximation() > 1) {
				return HierarchicalNode.TYPE_TRANSIENT_COMPONENT_COLOR;
			}
			return HierarchicalNode.TYPE_TRANSIENT_COMPONENT_ALONE_COLOR;
		}
		return Color.WHITE;
	}

	@Override
	public NodeShape getNodeShape(HierarchicalNode hnode) {
        if (hnode == null) {
            return NodeShape.RECTANGLE;
        }

		switch (hnode.getType()) {
			case HierarchicalNode.TYPE_TRANSIENT_CYCLE:
			case HierarchicalNode.TYPE_TERMINAL_CYCLE:
			case HierarchicalNode.TYPE_TRANSIENT_COMPONENT:
				return NodeShape.ELLIPSE;

			case HierarchicalNode.TYPE_STABLE_STATE:
			default:
				return NodeShape.RECTANGLE;
		}
	}
	
}
