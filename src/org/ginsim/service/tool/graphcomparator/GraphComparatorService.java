package org.ginsim.service.tool.graphcomparator;

import java.util.List;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
public class GraphComparatorService implements Service {


	private final int GRAPH_TYPE_UNCOMPATIBLE = -1;
	private final int GRAPH_TYPE_NULL = 0;
	private final int GRAPH_TYPE_REGULATORY = 1;
	private final int GRAPH_TYPE_DYNAMIC = 2;


	/**
	 * Compare graph_1 and graph_2 and return a GraphComparatorResult object.
	 * 
	 * @param graph_frame used for the error notifications
	 * @param graph_1 the first graph to compare
	 * @param graph_2 the second graph to compare
	 * @return a GraphComparatorResult object handling the computed data
	 */
	public GraphComparatorResult run(Graph graph_frame, Graph graph_1, Graph graph_2) {

		int g_type= getGraphsType(graph_1, graph_2);
		GraphComparator graphComparator = null;
		Graph graph_new;

		switch (g_type) {
		case GRAPH_TYPE_UNCOMPATIBLE:
			NotificationManager.publishError(graph_frame, "STR_gcmp_graphFromDiffTypes");
			return null;
		case GRAPH_TYPE_NULL:
			NotificationManager.publishError(graph_frame, "STR_gcmp_graphFromDiffTypes");
			return null;
		case GRAPH_TYPE_REGULATORY:
			graph_new = GraphManager.getInstance().getNewGraph();
			graphComparator = new RegulatoryGraphComparator(graph_1, graph_2, graph_new);
			break;
		case GRAPH_TYPE_DYNAMIC:
			List nodeOrder = DynamicGraphComparator.getNodeOrder( (DynamicGraph) graph_1, (DynamicGraph) graph_2);
			if (nodeOrder != null) {
				graph_new = GraphManager.getInstance().getNewGraph( DynamicGraph.class, nodeOrder);
				graphComparator = new DynamicGraphComparator(graph_1, graph_2, graph_new);
			} else {
				NotificationManager.publishError(graph_frame, "STR_gcmp_graphFromDiffTypes");
				return null;
			}
			break;
		}
		if (graphComparator != null) {
			return graphComparator.buildDiffGraph();
		}
		return null;
	}
	
	
	private int getGraphsType( Graph graph_1, Graph graph_2) {
		if (graph_1 == null || graph_2 == null) return GRAPH_TYPE_NULL;
		if (graph_1  instanceof RegulatoryGraph) {
			if (graph_2 instanceof RegulatoryGraph) 
				return GRAPH_TYPE_REGULATORY ;
		} else if ((graph_1  instanceof DynamicGraph) 	&& (graph_2 instanceof DynamicGraph)) 		return GRAPH_TYPE_DYNAMIC ;
		return GRAPH_TYPE_UNCOMPATIBLE;
	}

}
