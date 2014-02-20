package org.ginsim.service.tool.graphcomparator;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.view.style.*;

import java.awt.*;
import java.util.Map;

/**
 * Provide styles for compared graphs.
 *
 * @author Aurelien Naldi
 */
public class GraphComparatorStyleProvider implements StyleProvider {

    private final ComparatorNodeStyle nodeStyle;
    private final ComparatorEdgeStyle edgeStyle;
    private final StyleManager manager;

    GraphComparatorStyleProvider(Graph graph, GraphComparatorResult cmpResult) {
        this.manager = graph.getStyleManager();
        this.edgeStyle = new ComparatorEdgeStyle(manager.getDefaultEdgeStyle(), cmpResult.comparedEdges);
        this.nodeStyle = new ComparatorNodeStyle(manager.getDefaultNodeStyle(), cmpResult.comparedNodes);
    }

    @Override
    public NodeStyle getNodeStyle(Object node, NodeStyle baseStyle) {
        nodeStyle.setBaseStyle(baseStyle);
        return nodeStyle;
    }

    @Override
    public EdgeStyle getEdgeStyle(Edge edge, EdgeStyle baseStyle) {
        edgeStyle.setBaseStyle(baseStyle);
        return edgeStyle;
    }
}

class ComparatorNodeStyle extends NodeStyleOverride {

    private final Map<Object, ComparedItemInfo> m;

    ComparatorNodeStyle(NodeStyle defaultStyle, Map<Object, ComparedItemInfo> m) {
        super(defaultStyle);
        this.m = m;
    }

    @Override
    public Color getTextColor(Object obj) {
        return Color.BLACK;
    }

    @Override
    public Color getForeground(Object obj) {
        return Color.BLACK;
    }

    @Override
    public Color getBackground(Object obj) {
        ComparedItemInfo info = m.get(obj);
        if (info == null) {
            return Color.YELLOW;
        }

        if (info.first == null) {
            return Color.GREEN;
        }

        if (info.second == null) {
            return Color.RED;
        }

        // TODO: also highlight differences

        return Color.WHITE;
    }
}


class ComparatorEdgeStyle extends EdgeStyleOverride {

    private final Map<Object, ComparedItemInfo> m;

    ComparatorEdgeStyle(EdgeStyle defaultStyle, Map<Object, ComparedItemInfo> m) {
        super(defaultStyle);
        this.m = m;
    }

    @Override
    public Color getColor(Edge edge) {
        ComparedItemInfo info = m.get(edge);
        if (info == null) {
            return Color.YELLOW;
        }

        if (info.first == null) {
            return Color.GREEN;
        }

        if (info.second == null) {
            return Color.RED;
        }

        // TODO: also highlight differences

        return Color.BLACK;
    }

    @Override
    public int getWidth(Edge edge) {
        return 1;
    }
}
