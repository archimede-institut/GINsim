package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;

public class GsListInteraction {
	private GsRegulatoryMultiEdge grme;
	private int index;

	public GsListInteraction(GsRegulatoryMultiEdge e, int i) {
		grme = e;
		index = i;
	}
	public GsRegulatoryMultiEdge getEdge() {
		return grme;
	}
	public int getIndex() {
		return index;
	}
	public String toString() {
		if (index > 0) {
			return grme.getEdge(index - 1).getShortDetail(" ");
		} else if (index == -1) {
			return "!" + grme.getSource().getId();
		}
		return grme.getSource().getId();
	}
	public String stringValue() {
		int i = grme.getId(index - 1).lastIndexOf('_');
		if (index > 0) {
			return grme.getEdge(index - 1).getShortInfo("#");
		} else if (index == -1) {
			return "!" + grme.getId(index - 1).substring(0, i);
		}
		return grme.getId(index - 1).substring(0, i);
	}
}
