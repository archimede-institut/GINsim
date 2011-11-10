package org.ginsim.graph.dynamicalhierarchicalgraph;

import java.util.HashSet;
import java.util.Iterator;

public class GsDynamicalHierarchicalNodeSet extends HashSet {
	private static final long serialVersionUID = -6542206623359579872L;

	public GsDynamicalHierarchicalNodeSet() {
		super();
	}
	
	public GsDynamicalHierarchicalNode getDHNodeForState(byte[] state) {
		GsDynamicalHierarchicalNode dhnode = null;
		boolean found = false;
		for (Iterator it = iterator(); it.hasNext();) {
			dhnode = (GsDynamicalHierarchicalNode) it.next();
			if (dhnode != null && dhnode.contains(state)) {
				found = true;
				break;
			}
		}
		if (!found) return null;
		return dhnode;
	}
}
