package org.ginsim.graph.hierachicaltransitiongraph;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class HierarchicalSigmaSetFactory {
	
	private HierarchicalSigmaSet root;
	
	private SortedSet tmpSet;
	
	public HierarchicalSigmaSetFactory() {
		this.root = new HierarchicalSigmaSet(null, null);
		this.tmpSet = new TreeSet();
	}

	public void beginNewSigma() {
		this.tmpSet.clear();
	}
	
	public void addToNewSigma(HierarchicalNode label) {
		tmpSet.add(label);
	}

	public void addAllToNewSigma(Collection labels) {
		tmpSet.addAll(labels);
	}

	public HierarchicalSigmaSet endNewSigma() {
		HierarchicalSigmaSet parent = root;
		for (Iterator it = tmpSet.iterator(); it.hasNext();) {
			HierarchicalNode label = (HierarchicalNode) it.next();
			parent = parent.addChild(label);
		}
		return parent;
	}

	public boolean isEmpty() {
		return root.hasChildren();
	}

	public void merge(HierarchicalNode master, HierarchicalNode slaveNode) {
		beginNewSigma();
		addAllToNewSigma(master.getSigma().getSigmaImage());
		addAllToNewSigma(slaveNode.getSigma().getSigmaImage());
		master.setSigma(endNewSigma());
	}
}
