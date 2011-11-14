package fr.univmrs.tagc.GINsim.annotation;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.common.Graph;

import fr.univmrs.tagc.common.HttpHelper;
import fr.univmrs.tagc.common.OpenHelper;
import fr.univmrs.tagc.common.Tools;

public class AnnotationLink {
	
	protected static Map m_helper = new HashMap();
	static {
		HttpHelper.setup();
	}
	static void addHelperClass( String key, String objectKey) {
		
		m_helper.put(key, objectKey);
	}
	
	private OpenHelper helper = null;
	String proto;
	String value;

	public AnnotationLink(String s, Graph graph) {
		setText(s, graph);
	}
	public void setText(String s, Graph graph) {
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
		if (graph!= null && okey != null) {
			helper = (OpenHelper)graph.getObject(okey, true);
			if (getHelper() != null) {
				getHelper().add(proto, value);
			}
		}
	}
	public void open() {
		if (getHelper() != null) {
			getHelper().open(proto, value);
			return;
		}
		// no helper, use a generic open call
		Tools.open(proto, value);
	}
	
	public String toString() {
		if (proto == null || proto.equals("")) {
			return value;
		}
		return proto+":"+value;
	}

	public OpenHelper getHelper() {
		return helper;
	}
	public String getProto() {
		return proto;
	}
	public String getValue() {
		return value;
	}
	public boolean equals(Object o) {
		return toString().equals(o.toString());
	}
}
