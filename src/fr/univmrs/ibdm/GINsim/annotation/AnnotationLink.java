package fr.univmrs.ibdm.GINsim.annotation;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.Tools;

public class AnnotationLink {
	
	protected static Map m_helper = new HashMap();
	static {
		HttpHelper.setup();
		ReferencerHelper.setup();
	}
	static void addHelperClass(String key, AnnotationHelper helper) {
		m_helper.put(key, helper);
	}
	
	AnnotationHelper helper = null;
	String proto;
	String value;

	public AnnotationLink(String s, GsGraph graph) {
		setText(s, graph);
	}
	public void setText(String s, GsGraph graph) {
		String[] ts = s.split(":", 2);
		if (ts.length == 1) {
			this.helper = null;
			this.proto = "";
			this.value = s;
			return;
		}
		proto = ts[0].trim();
		value = ts[1].trim();
		helper = (AnnotationHelper)m_helper.get(proto);
		if (helper != null) {
			helper.update(this, graph);
		}
	}
	
	public void open() {
		if (helper != null) {
			helper.open(this);
			return;
		}
		// no helper, assume web page!
		Tools.webBrowse(proto+value);
	}
	
	public String toString() {
		if (proto == null || proto.equals("")) {
			return value;
		}
		return proto+": "+value;
	}
}
