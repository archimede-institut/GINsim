package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class GsHierarchicalSigmaSetFactory {
	
	private GsHierarchicalSigmaSet root;
	
	private SortedSet tmpSet;
	
	public GsHierarchicalSigmaSetFactory() {
		this.root = new GsHierarchicalSigmaSet(null, null);
		this.tmpSet = new TreeSet();
	}

	public void beginNewSigma() {
		this.tmpSet.clear();
	}
	
	public void addToNewSigma(GsHierarchicalNode label) {
		tmpSet.add(label);
	}

	public void addAllToNewSigma(Collection labels) {
		tmpSet.addAll(labels);
	}

	public GsHierarchicalSigmaSet endNewSigma() {
		GsHierarchicalSigmaSet parent = root;
		for (Iterator it = tmpSet.iterator(); it.hasNext();) {
			GsHierarchicalNode label = (GsHierarchicalNode) it.next();
			parent = parent.addChild(label);
		}
		return parent;
	}

	public boolean isEmpty() {
		return root.hasChildren();
	}

	public void merge(GsHierarchicalNode master, GsHierarchicalNode slaveNode) {
		beginNewSigma();
		addAllToNewSigma(master.getSigma().getSigmaImage());
		addAllToNewSigma(slaveNode.getSigma().getSigmaImage());
		master.setSigma(endNewSigma());
	}
}
