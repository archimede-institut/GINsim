package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import fr.univmrs.ibdm.GINsim.data.GsAnnotation;


public class GsRegulatoryEdge {
	short threshold;
	short sign;
	
	public short index;
	public GsRegulatoryMultiEdge me;
	
	GsAnnotation annotation = new GsAnnotation();
	
	public GsRegulatoryEdge(GsRegulatoryMultiEdge me) {
		this.me = me;
	}
	
	public Object clone() {
		GsRegulatoryEdge clone = new GsRegulatoryEdge(me);
		clone.threshold = threshold;
		clone.sign = sign;
		clone.annotation = (GsAnnotation)annotation.clone();
		return clone;
	}
	
	public short getMin() {
		return threshold;
	}
	
	public short getMax() {
		return me.getMax(index);
	}

	public String getSEdge() {
		return me.getFullId(index);
	}
	
	public String toString() {
		return me.getSource()+" "+index+" "+me.getEdgeName(index);
	}
}
