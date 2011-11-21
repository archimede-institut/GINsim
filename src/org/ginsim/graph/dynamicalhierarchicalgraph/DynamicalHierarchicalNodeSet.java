package org.ginsim.graph.dynamicalhierarchicalgraph;

import java.util.HashSet;
import java.util.Iterator;

public class DynamicalHierarchicalNodeSet extends HashSet {
	private static final long serialVersionUID = -6542206623359579872L;

	public DynamicalHierarchicalNodeSet() {
		super();
	}
	
	public DynamicalHierarchicalNode getDHNodeForState(byte[] state) {
		DynamicalHierarchicalNode dhnode = null;
		boolean found = false;
		for (Iterator it = iterator(); it.hasNext();) {
			dhnode = (DynamicalHierarchicalNode) it.next();
			if (dhnode != null && dhnode.contains(state)) {
				found = true;
				break;
			}
		}
		if (!found) return null;
		return dhnode;
	}
}
