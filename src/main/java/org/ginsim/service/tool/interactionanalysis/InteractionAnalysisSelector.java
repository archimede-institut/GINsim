package org.ginsim.service.tool.interactionanalysis;

import java.awt.Color;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.css.CSSEdgeStyle;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.core.graph.view.css.CSSStyle;


public class InteractionAnalysisSelector extends Selector {
	public static final String IDENTIFIER = "interaction-analysis";
	public static final String CAT_NONFUNCTIONNAL = "non-functionnal";
	public static final String CAT_POSITIVE = "positive";
	public static final String CAT_NEGATIVE = "negative";
	public static final String CAT_DUAL = "dual";
	
	public static final CSSEdgeStyle STYLE_NONFUNCTIONNAL	= new CSSEdgeStyle(Color.yellow, 	EdgeEnd.UNKNOWN,  CSSEdgeStyle.NULL_CURVE,  5);
	public static final CSSEdgeStyle STYLE_POSITIVE 		= new CSSEdgeStyle(Color.green, 	EdgeEnd.POSITIVE, CSSEdgeStyle.NULL_CURVE,  5);
	public static final CSSEdgeStyle STYLE_NEGATIVE 		= new CSSEdgeStyle(Color.red, 		EdgeEnd.NEGATIVE, CSSEdgeStyle.NULL_CURVE,  5);
	public static final CSSEdgeStyle STYLE_DUAL	 		= new CSSEdgeStyle(Color.blue, 	EdgeEnd.DUAL,     CSSEdgeStyle.NULL_CURVE,  5);
	
	private Map<RegulatoryMultiEdge, String> cache = null;

	public InteractionAnalysisSelector() {
		super(IDENTIFIER);
	}

	public void resetDefaultStyle() {
		addCategory(CAT_NONFUNCTIONNAL, (CSSStyle)STYLE_NONFUNCTIONNAL.clone());
		addCategory(CAT_POSITIVE, (CSSStyle)STYLE_POSITIVE.clone());
		addCategory(CAT_NEGATIVE, (CSSStyle)STYLE_NEGATIVE.clone());
		addCategory(CAT_DUAL, (CSSStyle)STYLE_DUAL.clone());
	}
	
	public boolean respondToNodes() {return false;}
	public String getCategoryForNode(Object obj) {return null;} //Doesn't respond to, but must be overridden.

	public String getCategoryForEdge(Object obj) {
		Object res = cache.get(obj);
		if (res == null) return CAT_NONFUNCTIONNAL; 
		return (String) res;
	}

	
	public Map<RegulatoryMultiEdge, String> getCache() {
		return cache;
	}
	
	public void setCache(Map<RegulatoryMultiEdge, String> cache) {
		this.cache = cache;
	}
	
	public void initCache(Map<RegulatoryMultiEdge, String> cache) {
		this.cache = cache;
	}
	
	public void flush() {
		cache = null;
	}
}
