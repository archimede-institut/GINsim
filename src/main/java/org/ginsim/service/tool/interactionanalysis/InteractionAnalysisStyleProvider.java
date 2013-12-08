package org.ginsim.service.tool.interactionanalysis;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.view.style.*;

import java.awt.*;
import java.util.Map;

/**
 * Provide a style to view interaction analysis results on the graph.
 *
 * @author Aurelien Naldi
 */
public class InteractionAnalysisStyleProvider implements StyleProvider {

    private final InteractionAnalysisEdgeStyle edgeStyle;

    InteractionAnalysisStyleProvider(RegulatoryGraph graph, Map<RegulatoryMultiEdge, InteractionStatus> functionalityMap) {
        StyleManager manager = graph.getStyleManager();
        this.edgeStyle = new InteractionAnalysisEdgeStyle(manager.getDefaultEdgeStyle(), functionalityMap);
    }

    @Override
    public NodeStyle getNodeStyle(Object node, NodeStyle baseStyle) {
        return null;
    }

    @Override
    public EdgeStyle getEdgeStyle(Edge edge, EdgeStyle baseStyle) {
        return edgeStyle;
    }
}

class InteractionAnalysisEdgeStyle extends EdgeStyleOverride {

    private final Map<RegulatoryMultiEdge, InteractionStatus> functionalityMap;

    InteractionAnalysisEdgeStyle(EdgeStyle defaultStyle, Map<RegulatoryMultiEdge, InteractionStatus> functionalityMap) {
        super(defaultStyle);
        this.functionalityMap = functionalityMap;
    }

    @Override
    public Color getColor(Edge edge) {
        InteractionStatus status = functionalityMap.get(edge);
        if (status == null) {
            return super.getColor(edge);
        }
        switch (status) {
            case POSITIVE:
                return Color.green;
            case NEGATIVE:
                return Color.red;
            case DUAL:
                return Color.blue;
            case NON_FUNCTIONAL:
                return Color.darkGray;
        }
        return super.getColor(edge);
    }

    @Override
    public int getWidth(Edge edge) {
        if (true) {
            return 2;
        }
        return super.getWidth(edge);
    }
}
