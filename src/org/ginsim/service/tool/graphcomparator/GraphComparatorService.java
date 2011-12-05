package org.ginsim.service.tool.graphcomparator;

import java.util.List;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.resource.Translator;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
public class GraphComparatorService implements Service {


	private final int GRAPH_TYPE_UNCOMPATIBLE = -1;
	private final int GRAPH_TYPE_NULL = 0;
	private final int GRAPH_TYPE_REGULATORY = 1;
	private final int GRAPH_TYPE_DYNAMIC = 2;


	public GraphComparatorResult run(Graph graph_frame, Graph g1, Graph g2) {

		int g_type= getGraphsType(g1, g2);
		GraphComparator gc = null;
		Graph g;

		switch (g_type) {
		case GRAPH_TYPE_UNCOMPATIBLE:
			NotificationManager.publishError(graph_frame, Translator.getString("STR_gcmp_graphFromDiffTypes"));
			return null;
		case GRAPH_TYPE_NULL:
			NotificationManager.publishError(graph_frame, Translator.getString("STR_gcmp_graphFromDiffTypes"));
			return null;
		case GRAPH_TYPE_REGULATORY:
			g = GraphManager.getInstance().getNewGraph();
			gc = new RegulatoryGraphComparator(g1, g2, g);
			break;
		case GRAPH_TYPE_DYNAMIC:
			List nodeOrder = DynamicGraphComparator.getNodeOrder( (DynamicGraph) g1, (DynamicGraph) g2);
			if (nodeOrder != null) {
				g = GraphManager.getInstance().getNewGraph( DynamicGraph.class, nodeOrder);
				gc = new DynamicGraphComparator(g1, g2, g);
			} else {
				NotificationManager.publishError(graph_frame, Translator.getString("STR_gcmp_graphFromDiffTypes"));
				return null;
			}
			break;
		}
		if (gc != null) {
			return gc.buildDiffGraph();
		}
		return null;
	}
	
	
	private int getGraphsType( Graph g1, Graph g2) {
		if (g1 == null || g2 == null) return GRAPH_TYPE_NULL;
		if (g1  instanceof RegulatoryGraph) {
			if (g2 instanceof RegulatoryGraph) 
				return GRAPH_TYPE_REGULATORY ;
		} else if ((g1  instanceof DynamicGraph) 	&& (g2 instanceof DynamicGraph)) 		return GRAPH_TYPE_DYNAMIC ;
		return GRAPH_TYPE_UNCOMPATIBLE;
	}

}
