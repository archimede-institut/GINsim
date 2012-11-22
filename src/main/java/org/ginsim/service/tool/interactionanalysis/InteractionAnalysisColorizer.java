package org.ginsim.service.tool.interactionanalysis;

import java.util.Map;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
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
			if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_POSITIVE && me.getSign() != RegulatoryEdgeSign.POSITIVE) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_NEGATIVE && me.getSign() != RegulatoryEdgeSign.NEGATIVE) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_NONFUNCTIONNAL && me.getSign() != RegulatoryEdgeSign.UNKNOWN) {
				cs.applyOnEdge(selector, me, ereader);
			} else if (functionalityMap.get(me) == InteractionAnalysisSelector.CAT_DUAL) {
				cs.applyOnEdge(selector, me, ereader);
			}
		}
	}

}
