package org.ginsim.servicegui.tool.pathfinding;


import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.css.EdgeStyle;
import org.ginsim.core.graph.view.css.NodeStyle;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.core.graph.view.css.Style;


public class PathFindingSelector extends Selector {
	public static final String IDENTIFIER = "pathFinding";
	public static final String CAT_PATH_NODE = "path-node";
	public static final String CAT_PATH_EDGE = "path-edge";
	public static final String CAT_OUTOFPATH_NODE = "outpath-node";
	public static final String CAT_OUTOFPATH_EDGE = "outpath-edge";
	
	public static final EdgeStyle STYLE_PATH_EDGE			= new EdgeStyle(Color.blue,  null, false,  3);
	public static final NodeStyle STYLE_PATH_NODE			= new NodeStyle(Color.green, Color.white, null, null);
	public static final EdgeStyle STYLE_OUTOFPATH_EDGE		= new EdgeStyle(Color.gray,  null, false,  1);
	public static final NodeStyle STYLE_OUTOFPATH_NODE	= new NodeStyle(Color.white,     Color.gray, null, null);
	
	private static final Object CACHE_PATH = "PATH";
	
	private Map cache = null;

	public PathFindingSelector() {
		super(IDENTIFIER);
	}

	public void resetDefaultStyle() {
		addCategory(CAT_PATH_NODE, (Style)STYLE_PATH_NODE.clone());
		addCategory(CAT_PATH_EDGE, (Style)STYLE_PATH_EDGE.clone());
		addCategory(CAT_OUTOFPATH_NODE, (Style)STYLE_OUTOFPATH_NODE.clone());
		addCategory(CAT_OUTOFPATH_EDGE, (Style)STYLE_OUTOFPATH_EDGE.clone());
	}
	
	public boolean respondToNodes() {
		return false;
	}
	
	public String getCategoryForEdge(Object obj) {
		Edge e = (Edge) obj;
		List path = getPath();
		int i = path.indexOf(e.getSource());
		if (i == -1) return CAT_OUTOFPATH_EDGE;
		if (i+1 < path.size() && path.get(i+1) == e.getTarget()) {
			return CAT_PATH_EDGE;
		}
		return CAT_OUTOFPATH_EDGE;
	}

	public String getCategoryForNode(Object obj) {
		if (getPath().contains(obj)) {
			return CAT_PATH_NODE;
		}
		return CAT_OUTOFPATH_NODE;
	}
	
	public Map getCache() {
		return cache;
	}
	
	public void setCache(Map cache) {
		this.cache = cache;
	}
	
	public Map initCache(List path) {
		this.cache = new HashMap();
		cache.put(CACHE_PATH, path);
		return this.cache;
	}
	
	public void flush() {
		cache = null;
	}
	
	public List getPath() {
		return (List) cache.get(CACHE_PATH);
	}
}
