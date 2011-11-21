package org.ginsim.gui.service.tools.interactionanalysis;

import java.awt.Color;
import java.util.Map;

import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.mutant.RegulatoryMutantDef;
import org.ginsim.graph.view.css.EdgeStyle;
import org.ginsim.graph.view.css.Selector;
import org.ginsim.graph.view.css.Style;


public class InteractionAnalysisSelector extends Selector {
	public static final String IDENTIFIER = "interaction-analysis";
	public static final String CAT_NONFUNCTIONNAL = "non-functionnal";
	public static final String CAT_POSITIVE = "positive";
	public static final String CAT_NEGATIVE = "negative";
	public static final String CAT_DUAL = "dual";
	
	public static final EdgeStyle STYLE_NONFUNCTIONNAL	= new EdgeStyle(Color.yellow, 	EdgeAttributesReader.ARROW_UNKNOWN,EdgeStyle.NULL_SHAPE,  5);
	public static final EdgeStyle STYLE_POSITIVE 		= new EdgeStyle(Color.green, 	EdgeAttributesReader.ARROW_POSITIVE, EdgeStyle.NULL_SHAPE,  5);
	public static final EdgeStyle STYLE_NEGATIVE 		= new EdgeStyle(Color.red, 		EdgeAttributesReader.ARROW_NEGATIVE, EdgeStyle.NULL_SHAPE,  5);
	public static final EdgeStyle STYLE_DUAL	 		= new EdgeStyle(Color.blue, 	EdgeAttributesReader.ARROW_DOUBLE, EdgeStyle.NULL_SHAPE,  5);
	
	private Map cache = null;

	public InteractionAnalysisSelector() {
		super(IDENTIFIER);
	}

	public void resetDefaultStyle() {
		addCategory(CAT_NONFUNCTIONNAL, (Style)STYLE_NONFUNCTIONNAL.clone());
		addCategory(CAT_POSITIVE, (Style)STYLE_POSITIVE.clone());
		addCategory(CAT_NEGATIVE, (Style)STYLE_NEGATIVE.clone());
		addCategory(CAT_DUAL, (Style)STYLE_DUAL.clone());
	}
	
	public boolean respondToNodes() {
		return false;
	}
	
	public String getCategoryForEdge(Object obj) {
		Object res = cache.get(obj);
		if (res == null) return CAT_NONFUNCTIONNAL; 
		return (String) res;
	}

	public String getCategoryForNode(Object obj) {return null;} //Doesn't respond to, but must be overridden.
	
	public Map getCache() {
		return cache;
	}
	
	public void setCache(Map cache) {
		this.cache = cache;
	}
	
	public Map initCache(RegulatoryGraph g) {
		this.cache = initCache(g, false, false, null);
		return this.cache;
	}
	
	public Map initCache(RegulatoryGraph g, boolean opt_annotate, boolean opt_verbose, RegulatoryMutantDef mutant) {
		InteractionAnalysis fii = new InteractionAnalysis(g, opt_annotate, mutant);
		this.cache = fii.getFunctionality();
		return this.cache;
	}
	
	public void flush() {
		cache = null;
	}
}
