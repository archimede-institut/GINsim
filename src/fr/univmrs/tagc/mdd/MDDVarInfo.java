package fr.univmrs.tagc.mdd;


public class MDDVarInfo {

	public int order;
	public int vOrder;

	public MDDVarInfo(int order) {
		this(order, order);
	}

	public MDDVarInfo(int order, int vOrder) {
		this.order = order;
		this.vOrder = vOrder;
	}
}
