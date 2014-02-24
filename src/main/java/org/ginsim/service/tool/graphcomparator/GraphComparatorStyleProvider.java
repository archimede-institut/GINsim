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

    public static final Color SPEC_FIRST = Color.RED;
    public static final Color SPEC_SECOND = Color.GREEN;
    public static final Color CHANGED = Color.CYAN;
    public static final Color META_CHANGED = Color.BLUE;
    public static final Color IDENTICAL = Color.WHITE;

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
            return GraphComparatorStyleProvider.SPEC_SECOND;
        }

        if (info.second == null) {
            return GraphComparatorStyleProvider.SPEC_FIRST;
        }

        if (info.changed) {
            return GraphComparatorStyleProvider.CHANGED;
        }

        if (info.metaChanged) {
            return GraphComparatorStyleProvider.META_CHANGED;
        }

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
            return GraphComparatorStyleProvider.SPEC_SECOND;
        }

        if (info.second == null) {
            return GraphComparatorStyleProvider.SPEC_FIRST;
        }

        if (info.changed) {
            return GraphComparatorStyleProvider.CHANGED;
        }

        if (info.metaChanged) {
            return GraphComparatorStyleProvider.META_CHANGED;
        }

        return Color.BLACK;
    }

    @Override
    public int getWidth(Edge edge) {
        return 1;
    }
}
