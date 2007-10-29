package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import fr.univmrs.ibdm.GINsim.annotation.Annotation;


public class GsRegulatoryEdge {
	short threshold;
	short sign;
	
	public short index;
	public GsRegulatoryMultiEdge me;
	
	Annotation annotation = new Annotation();
	
	public GsRegulatoryEdge(GsRegulatoryMultiEdge me) {
		this.me = me;
	}
	
	public Object clone() {
		GsRegulatoryEdge clone = new GsRegulatoryEdge(me);
		clone.threshold = threshold;
		clone.sign = sign;
		clone.annotation = (Annotation)annotation.clone();
		return clone;
	}
	
	public short getMin() {
		return threshold;
	}
	
	public short getMax() {
		return me.getMax(index);
	}

	public String toString() {
		return getShortDetail("_");
	}
	
	public String getShortInfo(String separator) {
		return me.getSource()+getStringIndex(separator);
	}
	public String getLongInfo(String separator) {
		return me.getSource()+separator+me.getTarget()+getStringIndex(separator);
	}
	public String getShortDetail(String separator) {
		return getShortInfo(separator)+separator+getRangeAndSign();
	}
	public String getLongDetail(String separator) {
		return getLongInfo(separator)+separator+getRangeAndSign();
	}
	private String getStringIndex(String prefix) {
		if (me.getEdgeCount() == 1) {
			return "";
		}
		return prefix+(index+1);
	}
	private String getRangeAndSign() {
		return "["+threshold+","+getMaxAsString()+"] ; "+GsRegulatoryMultiEdge.SIGN[sign];
	}
	public String getMaxAsString() {
		String smax;
		if (index == me.getEdgeCount()-1) {
			smax = "max";
		} else if (me.getMin(index+1)> threshold) {
			smax = ""+(me.getMin(index+1)-1);
		} else {
			smax = "INVALID";
		}
		return smax;
	}
}
