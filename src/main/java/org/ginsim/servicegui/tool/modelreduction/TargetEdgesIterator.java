package org.ginsim.servicegui.tool.modelreduction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

public class TargetEdgesIterator implements Iterator<RegulatoryNode> {

	LinkedList<RegulatoryNode> queue = new LinkedList<RegulatoryNode>();
	Set<RegulatoryNode> m_visited = new HashSet<RegulatoryNode>();
	Map<RegulatoryNode, List<RegulatoryNode>> m_removed;
	
	RegulatoryNode next;
	
	public TargetEdgesIterator(Map<RegulatoryNode, List<RegulatoryNode>> m_removed) {
		this.m_removed = m_removed;
	}
	
	public boolean hasNext() {
		return next != null;
	}

	public RegulatoryNode next() {
		if (next == null) {
			throw new NoSuchElementException();
		}
		RegulatoryNode ret = next;
		
		// find the next.
		// it can be a "normal next target" if it was not removed
		// if it was removed, it may be one of the targets of the removed node
		next = null;
		while (queue.size() > 0) {
			RegulatoryNode vertex = queue.removeFirst();
			if (m_visited.contains(vertex)) {
				// this node was checked already, skip it
				continue;
			}
			m_visited.add(vertex);
			List<RegulatoryNode> targets = m_removed.get(vertex);
			if (targets == null) {
				// "clean" node: go for it!
				next = vertex;
				break;
			}
			
			// "dirty" node: enqueue its targets
			for (RegulatoryNode v: targets) {
				queue.addLast(v);
			}
		}
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public void setOutgoingList(Collection<RegulatoryMultiEdge> outgoing) {
		m_visited.clear();
		queue.clear();
		for (RegulatoryMultiEdge e: outgoing) {
			queue.addLast(e.getTarget());
		}
		next();
	}
}
