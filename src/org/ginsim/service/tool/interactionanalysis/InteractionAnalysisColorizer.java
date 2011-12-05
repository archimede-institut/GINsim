package org.ginsim.service.tool.interactionanalysis;

import java.util.Map;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.css.Colorizer;
import org.ginsim.core.graph.view.css.Selector;

public class InteractionAnalysisColorizer extends Colorizer {

	private Map<RegulatoryMultiEdge, String> functionalityMap;

	public InteractionAnalysisColorizer(Selector selector, Graph<?, ?> graph, Map<RegulatoryMultiEdge, String> functionalityMap) {
		super(selector);
		this.functionalityMap = functionalityMap;
	}

	@Override
	public void colorize(Graph<?, ?> graph) {
		if (functionalityMap == null) {
            return;
        }
		
		EdgeAttributesReader ereader = graph.getEdgeAttributeReader();
		
		for (RegulatoryMultiEdge me: functionalityMap.keySet()) {
			ereader.setEdge(me);
			if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_POSITIVE && me.getSign() != RegulatoryMultiEdge.SIGN_POSITIVE) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_NEGATIVE && me.getSign() != RegulatoryMultiEdge.SIGN_NEGATIVE) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_NONFUNCTIONNAL && me.getSign() != RegulatoryMultiEdge.SIGN_UNKNOWN) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_DUAL) {
				cs.applyOnEdge(selector, me, ereader);
			}
		}
	}

	@Override
	public void undoColorize(Graph<?, ?> graph) {
		if (cs != null) 
			cs.restoreAllEdges(functionalityMap.keySet(), graph.getEdgeAttributeReader()); //Only restore the affected edges
	}

}
