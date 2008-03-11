package fr.univmrs.tagc.GINsim.annotation;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.HttpHelper;
import fr.univmrs.tagc.common.OpenHelper;
import fr.univmrs.tagc.common.Tools;

public class AnnotationLink {
	
	protected static Map m_helper = new HashMap();
	static {
		HttpHelper.setup();
	}
	static void addHelperClass(String key, String objectKey) {
		m_helper.put(key, objectKey);
	}
	
	OpenHelper helper = null;
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
		String okey = (String)m_helper.get(proto);
		if (okey != null) {
			helper = (OpenHelper)graph.getObject(okey, true);
			if (helper != null) {
				helper.add(proto, value);
			}
		}
	}
	public void open() {
		if (helper != null) {
			helper.open(proto, value);
			return;
		}
		// no helper, use a generic open call
		Tools.open(proto, value);
	}
	
	public String toString() {
		if (proto == null || proto.equals("")) {
			return value;
		}
		return proto+": "+value;
	}
}
