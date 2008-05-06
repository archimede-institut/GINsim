package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.util.List;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

public abstract class LogicalFunctionBrowser {

	protected List nodeOrder;
	protected int[][] path;
	
	public LogicalFunctionBrowser(List nodeOrder) {
		this.nodeOrder = nodeOrder;
		this.path = new int[nodeOrder.size()][3];
		for (int i=0 ; i<path.length ; i++) {
			path[i][0] = path[i][1] = -1;
			path[i][2] = ((GsRegulatoryVertex)nodeOrder.get(i)).getMaxValue();
		}
	}
	
	public void browse(OmddNode node) {
		if (node.next == null) {
			leafReached(node);
			return;
		}
		int begin=0, end=1;
		OmddNode next = node.next[begin];
		while (end < node.next.length) {
			if (node.next[end] != next) {
				path[node.level][0] = begin;
				path[node.level][1] = end-1;
				browse(next);
				begin = end;
				next = node.next[begin];
			}
			end++;
		}
		path[node.level][0] = begin;
		path[node.level][1] = end-1;
		browse(next);

		path[node.level][0] = -1;
		path[node.level][1] = -1;
	}
	
	abstract protected void leafReached(OmddNode leaf);
}
