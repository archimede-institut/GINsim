package org.ginsim.servicegui.tool.localgraph;

import java.awt.Color;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.view.css.EdgeStyle;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.core.graph.view.css.Style;

public class LocalGraphSelector extends Selector {
	public static final String IDENTIFIER = "interaction-analysis";
	public static final String CAT_NONFUNCTIONNAL = "non-functionnal";
	public static final String CAT_POSITIVE = "positive";
	public static final String CAT_NEGATIVE = "negative";
	public static final String CAT_DUAL = "dual";

	public static final EdgeStyle STYLE_NONFUNCTIONNAL = new EdgeStyle(
			Color.lightGray, null, EdgeStyle.NULL_CURVE, 1);
	public static final EdgeStyle STYLE_POSITIVE = new EdgeStyle(Color.green,
			null, EdgeStyle.NULL_CURVE, 2);
	public static final EdgeStyle STYLE_NEGATIVE = new EdgeStyle(Color.red,
			null, EdgeStyle.NULL_CURVE, 2);
	public static final EdgeStyle STYLE_DUAL = new EdgeStyle(Color.blue, null,
			EdgeStyle.NULL_CURVE, 2);

	private Map<RegulatoryMultiEdge, String> cache = null;

	public LocalGraphSelector() {
		super(IDENTIFIER);
	}

	public void resetDefaultStyle() {
		addCategory(CAT_NONFUNCTIONNAL, (Style) STYLE_NONFUNCTIONNAL.clone());
		addCategory(CAT_POSITIVE, (Style) STYLE_POSITIVE.clone());
		addCategory(CAT_NEGATIVE, (Style) STYLE_NEGATIVE.clone());
		addCategory(CAT_DUAL, (Style) STYLE_DUAL.clone());
	}

	public boolean respondToNodes() {
		return false;
	}

	public String getCategoryForEdge(Object obj) {
		Object res = cache.get(obj);
		if (res == null)
			return CAT_NONFUNCTIONNAL;
		return (String) res;
	}

	public String getCategoryForNode(Object obj) {
		return null;
	} // Doesn't respond to, but must be overridden.

	public Map<RegulatoryMultiEdge, String> getCache() {
		return cache;
	}

	public void setCache(Map<RegulatoryMultiEdge, String> cache) {
		this.cache = cache;
	}

	// public Map<RegulatoryMultiEdge, String> initCache(RegulatoryGraph g,
	// SimulationUpdater updater) throws GsException{
	//
	// LocalGraph lg = new LocalGraph(g);
	// lg.setUpdater(updater);
	// this.cache = lg.getFunctionality();
	// return this.cache;
	// }

	public void flush() {
		cache = null;
	}
}
