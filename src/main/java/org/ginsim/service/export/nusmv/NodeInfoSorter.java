package org.ginsim.service.export.nusmv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.PathSearcher;

public class NodeInfoSorter {
	public NodeInfoSorter() {
	}

	public List<NodeInfo> getNodesWithInputsAtEnd(LogicalModel model) {
		List<NodeInfo> internal = new ArrayList<NodeInfo>();
		List<NodeInfo> inputs = new ArrayList<NodeInfo>();
		for (NodeInfo node : model.getNodeOrder()) {
			if (node.isInput()) {
				inputs.add(node);
			} else {
				internal.add(node);
			}
		}
		internal.addAll(inputs);
		return internal;
	}

	public List<NodeInfo> getNodesByIncNumberRegulators(LogicalModel model) {
		Set<NodeInfoDegree> inDegree = new TreeSet<NodeInfoDegree>();
		List<NodeInfo> lNodeOrder = model.getNodeOrder();

		// Compute the IN degree
		PathSearcher searcher = new PathSearcher(model.getMDDManager(), 1);
		int[] path = searcher.getPath();
		int[] kMDDs = model.getLogicalFunctions();
		
		for (int n = 0; n < lNodeOrder.size(); n++) {
			NodeInfoDegree nid = new NodeInfoDegree(lNodeOrder.get(n));
			searcher.setNode(kMDDs[n]);
			for (@SuppressWarnings("unused") int l : searcher) {
				for (int i = 0; i < path.length; i++) {
					if (i == n) {
						// Auto regulations are not considered
						continue;
					}
					if (path[i] != -1) {
						nid.addRegulator(lNodeOrder.get(i));
					}
				}
			}
			inDegree.add(nid);
		}
		
		List<NodeInfo> lOrdered = new ArrayList<NodeInfo>();
		for (NodeInfoDegree nid : inDegree) {
			lOrdered.add(nid.getNode());
		}
		return lOrdered;
	}
}

class NodeInfoDegree implements Comparable<NodeInfoDegree> {

	private NodeInfo node;
	public Set<NodeInfo> regulators;

	public NodeInfoDegree(NodeInfo node) {
		this.node = node;
		this.regulators = new HashSet<NodeInfo>();
	}

	public void addRegulator(NodeInfo regulator) {
		this.regulators.add(regulator);
	}
	
	public NodeInfo getNode() {
		return node;
	}

	@Override
	public int compareTo(NodeInfoDegree obj) {
		if (this.regulators.size() < obj.regulators.size())
			return -1;
		else if (this.regulators.size() > obj.regulators.size())
			return 1;
		return node.getNodeID().compareTo(obj.getNode().getNodeID());
	}
}