package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import java.util.HashSet;
import java.util.Iterator;

public class GsDynamicalHierarchicalNodeSet extends HashSet {
	
	public GsDynamicalHierarchicalNodeSet() {
		super();
	}
	
	public GsDynamicalHierarchicalNode get(byte[] state) {
		GsDynamicalHierarchicalNode dhnode = null;
		int i = 0;
		boolean found = false;
		for (Iterator it = iterator(); it.hasNext();) {
			dhnode = (GsDynamicalHierarchicalNode) it.next();
			if (dhnode != null && dhnode.contains(state)) {
				found = true;
				break;
			}
			i++;
		}
		if (!found || dhnode == null) return null;
//		if (i != 0) { //kindof optimisation on list, if a node is matching it will be read first next time
//			set.addFirst(set.remove(i));
//		}
		return dhnode;
	}
}
