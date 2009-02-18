package fr.univmrs.tagc.GINsim.interactionAnalysis;

import java.awt.Color;
import java.util.Set;

import fr.univmrs.tagc.GINsim.css.EdgeStyle;
import fr.univmrs.tagc.GINsim.css.Selector;
import fr.univmrs.tagc.GINsim.css.Style;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

public class InteractionAnalysisSelector extends Selector {
	public static final String IDENTIFIER = "interaction-analysis";
	public static final String CAT_NONFUNCTIONNAL = "non-functionnal";
	public static final String CAT_FUNCTIONNAL = "functionnal";
	
	public static final EdgeStyle STYLE_NONFUNCTIONNAL = new EdgeStyle(Color.red, EdgeStyle.NULL_SHAPE, EdgeStyle.NULL_LINEEND, EdgeStyle.NULL_BORDER);
	public static final EdgeStyle STYLE_FUNCTIONNAL = new EdgeStyle();
	
	private Set cache = null;

	public InteractionAnalysisSelector() {
		super(IDENTIFIER);
	}

	public void resetDefaultStyle() {
		addCategory(CAT_NONFUNCTIONNAL, (Style)STYLE_NONFUNCTIONNAL.clone());
		addCategory(CAT_FUNCTIONNAL, (Style)STYLE_FUNCTIONNAL.clone());
	}
	
	public boolean respondToNodes() {
		return false;
	}
	
	public String getCategoryForEdge(Object obj) {
		if (cache.contains(obj)) return CAT_NONFUNCTIONNAL; //Note because cache is a Set, contains is in constant time.
		return CAT_FUNCTIONNAL;
	}

	public String getCategoryForNode(Object obj) {return null;} //Doesn't respond to, but must be overridden.
	
	public Set getCache() {
		return cache;
	}
	
	public void setCache(Set cache) {
		this.cache = cache;
	}
	
	public Set initCache(GsRegulatoryGraph g) {
		return initCache(g, false, false, false, null);
	}
	
	public Set initCache(GsRegulatoryGraph g, boolean opt_color, boolean opt_annotate, boolean opt_verbose , Color opt_color_inactive) {
		SearchNonFunctionalInteractions fii = new SearchNonFunctionalInteractions(g, opt_color, opt_annotate, opt_verbose, opt_color_inactive);
		cache = fii.getNonFunctionalInteractions();
		return cache;
	}
	
	public void flush() {
		cache = null;
	}
}
