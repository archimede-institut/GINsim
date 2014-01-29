package org.ginsim.service.tool.interactionanalysis;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
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
    private final StyleManager manager;

    InteractionAnalysisStyleProvider(RegulatoryGraph graph, Map<RegulatoryMultiEdge, InteractionStatus> functionalityMap) {
        this.manager = graph.getStyleManager();
        this.edgeStyle = new InteractionAnalysisEdgeStyle(manager.getDefaultEdgeStyle(), functionalityMap);
    }

    @Override
    public NodeStyle getNodeStyle(Object node, NodeStyle baseStyle) {
        return baseStyle;
    }

    @Override
    public EdgeStyle getEdgeStyle(Edge edge, EdgeStyle baseStyle) {
        edgeStyle.setBaseStyle(baseStyle);
        return edgeStyle;
    }
}

class InteractionAnalysisEdgeStyle extends EdgeStyleOverride<RegulatoryNode, RegulatoryMultiEdge> {

    private final Map<RegulatoryMultiEdge, InteractionStatus> functionalityMap;

    InteractionAnalysisEdgeStyle(EdgeStyle defaultStyle, Map<RegulatoryMultiEdge, InteractionStatus> functionalityMap) {
        super(defaultStyle);
        this.functionalityMap = functionalityMap;
    }

    @Override
    public Color getColor(RegulatoryMultiEdge edge) {
        InteractionStatus status = functionalityMap.get(edge);
        if (status == null || status == InteractionStatus.WELL_DEFINED) {
            switch (edge.getSign()) {
                case POSITIVE:
                case NEGATIVE:
                case DUAL:
                    return defaultStyle.getColor(edge);
                default:
                    return Color.BLACK;
            }
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
    public int getWidth(RegulatoryMultiEdge edge) {
        InteractionStatus status = functionalityMap.get(edge);
        if (status == null || status == InteractionStatus.WELL_DEFINED) {
            return 1;
        }

        return 4;
    }
}
