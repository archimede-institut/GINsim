package org.ginsim.gui.service.action.localgraph;

import java.awt.Color;
import java.util.Map;

import fr.univmrs.tagc.GINsim.css.EdgeStyle;
import fr.univmrs.tagc.GINsim.css.Selector;
import fr.univmrs.tagc.GINsim.css.Style;
import fr.univmrs.tagc.GINsim.reg2dyn.SimulationUpdater;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

public class LocalGraphSelector extends Selector {
	public static final String IDENTIFIER = "interaction-analysis";
	public static final String CAT_NONFUNCTIONNAL = "non-functionnal";
	public static final String CAT_POSITIVE = "positive";
	public static final String CAT_NEGATIVE = "negative";
	public static final String CAT_DUAL = "dual";
	
	public static final EdgeStyle STYLE_NONFUNCTIONNAL	= new EdgeStyle(Color.white, 	EdgeStyle.NULL_LINEEND, EdgeStyle.NULL_SHAPE,  1);
	public static final EdgeStyle STYLE_POSITIVE 		= new EdgeStyle(Color.green, 	EdgeStyle.NULL_LINEEND, EdgeStyle.NULL_SHAPE,  2);
	public static final EdgeStyle STYLE_NEGATIVE 		= new EdgeStyle(Color.red, 		EdgeStyle.NULL_LINEEND, EdgeStyle.NULL_SHAPE,  2);
	public static final EdgeStyle STYLE_DUAL	 		= new EdgeStyle(Color.blue,		EdgeStyle.NULL_LINEEND, EdgeStyle.NULL_SHAPE,  2);
	
	private Map cache = null;

	public LocalGraphSelector() {
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
	
	public Map initCache(GsRegulatoryGraph g, SimulationUpdater updater) {
		LocalGraph lg = new LocalGraph(g);
		lg.setUpdater(updater);
		this.cache = lg.getFunctionality();
		return this.cache;
	}
	
	public void flush() {
		cache = null;
	}
}
