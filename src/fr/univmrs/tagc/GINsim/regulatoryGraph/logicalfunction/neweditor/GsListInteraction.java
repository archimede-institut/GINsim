package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor;

import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;

import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreeElementDeco;

public class GsListInteraction {
	private RegulatoryMultiEdge grme;
	private int index;
	private boolean not;
	private DTreeElementDeco node;

	public GsListInteraction(RegulatoryMultiEdge e, int i, boolean n) {
		grme = e;
		index = i;
		not = n;
	}
	public RegulatoryMultiEdge getEdge() {
		return grme;
	}
	public int getIndex() {
		return index;
	}
	public boolean getNot() {
		return not;
	}
	public void setNot(boolean n) {
		not = n;
		node.setSelected(not);
	}
	public void setNode(DTreeElementDeco n) {
		node = n;
	}
	public String getGene() {
		String s = grme.getSource().getId();
		return s;
	}
	public String getThreshold() {
		if (index >= 0)
			return String.valueOf(grme.getEdge(index).getMin());
		else
			return "All";
	}
	public String getSign() {
		String s = "";
		if (index >= 0)
			s = RegulatoryMultiEdge.SIGN_SHORT[grme.getSign(index)];
		return s;
	}
	public boolean equals(Object o) {
		GsListInteraction li = (GsListInteraction)o;
		return li.getGene().equals(getGene()) && li.getSign().equals(getSign()) && li.getIndex() == getIndex() && (li.getNot() == not);
	}
	public boolean equalsIgnoreNot(Object o) {
		GsListInteraction li = (GsListInteraction)o;
		return li.getGene().equals(getGene()) && li.getSign().equals(getSign()) && li.getIndex() == getIndex();
	}
	public boolean isSelected() {
		return node.isSelected();
	}
	public void setSelected(boolean b) {
		node.setSelected(b);
	}
}
