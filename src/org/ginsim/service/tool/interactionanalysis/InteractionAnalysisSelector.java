package org.ginsim.service.tool.interactionanalysis;

import java.awt.Color;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.css.EdgeStyle;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.core.graph.view.css.Style;


public class InteractionAnalysisSelector extends Selector {
	public static final String IDENTIFIER = "interaction-analysis";
	public static final String CAT_NONFUNCTIONNAL = "non-functionnal";
	public static final String CAT_POSITIVE = "positive";
	public static final String CAT_NEGATIVE = "negative";
	public static final String CAT_DUAL = "dual";
	
	public static final EdgeStyle STYLE_NONFUNCTIONNAL	= new EdgeStyle(Color.yellow, 	EdgeEnd.UNKNOWN,  false,  5);
	public static final EdgeStyle STYLE_POSITIVE 		= new EdgeStyle(Color.green, 	EdgeEnd.POSITIVE, false,  5);
	public static final EdgeStyle STYLE_NEGATIVE 		= new EdgeStyle(Color.red, 		EdgeEnd.NEGATIVE, false,  5);
	public static final EdgeStyle STYLE_DUAL	 		= new EdgeStyle(Color.blue, 	EdgeEnd.DUAL,     false,  5);
	
	private Map<RegulatoryMultiEdge, String> cache = null;

	public InteractionAnalysisSelector() {
		super(IDENTIFIER);
	}

	public void resetDefaultStyle() {
		addCategory(CAT_NONFUNCTIONNAL, (Style)STYLE_NONFUNCTIONNAL.clone());
		addCategory(CAT_POSITIVE, (Style)STYLE_POSITIVE.clone());
		addCategory(CAT_NEGATIVE, (Style)STYLE_NEGATIVE.clone());
		addCategory(CAT_DUAL, (Style)STYLE_DUAL.clone());
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
