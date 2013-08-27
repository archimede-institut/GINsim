package org.ginsim.core.graph.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;


public abstract class TreeBuilderFromOmdd extends TreeBuilder {
	protected OMDDNode root;
	

	
	public void parseOmdd() {
		if (tree.getMode() == TreeImpl.MODE_TREE) {
			createTreeFromOmdd(root);
		} else {
			createDiagramFromOmdd(root);
		}	
	}

	
	
	public void createDiagramFromOmdd(OMDDNode root) {
		computeWidthPerDepthFromRegGraph();
		int[] currentWidthPerDepth = new int[widthPerDepth.length];
		tree.setRoot( _createDiagramFromOmdd(root, 0, currentWidthPerDepth, tree.getEdgeAttributeReader()));
	}
	
	private TreeNode _createDiagramFromOmdd(OMDDNode o, int lastLevel, int[] currentWidthPerDepth, EdgeAttributesReader ereader) {
		TreeNode treeNode;
		int mult;
		if (o.next == null) {
			mult = jump(lastLevel, max_depth, currentWidthPerDepth);
		
			if (tree.getMode() == TreeImpl.MODE_DIAGRAM_WITH_MULTIPLE_LEAFS) {
				treeNode = new TreeNode(""+o.value, max_depth, ++currentWidthPerDepth[max_depth], TreeNode.TYPE_LEAF, o.value);
				if (mult > 1) currentWidthPerDepth[max_depth] += mult-1;
				tree.addNode(treeNode);
			} else { // if (mode == MODE_DIAGRAM) {
				treeNode = TreeImpl.leafs[o.value];
				if (!tree.containsNode(treeNode)) {
					tree.addNode(treeNode);
				}
			}
			return treeNode;
		}
		
		
		mult = jump(lastLevel, o.level, currentWidthPerDepth);
		
		treeNode = new TreeNode(getNodeName(o.level), o.level, ++currentWidthPerDepth[o.level], TreeNode.TYPE_BRANCH); 
		tree.addNode(treeNode);
				
		for (int i = 0 ; i < o.next.length ; i++) { //For all the children
	    	TreeNode child = _createDiagramFromOmdd(o.next[i], o.level, currentWidthPerDepth, ereader);
	    	linkNode(treeNode, child, i);
	    }
		
		if (mult > 1) {
			mult = jump(o.level, max_depth, currentWidthPerDepth);
			if (mult > 1) {
				currentWidthPerDepth[max_depth] += mult;
			}
		}
	    return treeNode;
	}
	
	public void createTreeFromOmdd(OMDDNode root) {
		computeWidthPerDepthFromRegGraph();
		int[] currentWidthPerDepth = new int[widthPerDepth.length];
		tree.setRoot( (TreeNode) _createTreeFromOmdd(root, 0, null, 0, currentWidthPerDepth, tree.getEdgeAttributeReader()).get(0));
	}
	private List<TreeNode> _createTreeFromOmdd(OMDDNode o, int lastLevel, TreeNode parent, int childIndex, int[] currentWidthPerDepth, EdgeAttributesReader ereader) {
		TreeNode treeNode = null;
		List<TreeNode> parents = new ArrayList<TreeNode>();
		parents.add(parent);
		int mult, last_real;
		if (o.next == null) {
			mult = 1;

			last_real = 0;
			for (int j = lastLevel+1 ; j < max_depth ; j++) { //For all the missing genes
				if (realDetph[j] != -2) {
					parents = addChildren(j, mult, parents, childIndex, currentWidthPerDepth, ereader);
					mult = widthPerDepth[j];
					last_real = j;
				}
			}
			for (Iterator<TreeNode> it = parents.iterator(); it.hasNext();) {
				TreeNode p = (TreeNode) it.next();
				if (mult > 1) {
					for (int i = 0; i < widthPerDepth[last_real]; i++) {
						treeNode = new TreeNode(""+o.value, max_depth, ++currentWidthPerDepth[max_depth], TreeNode.TYPE_LEAF, o.value);
						tree.addNode(treeNode);
				    	linkNode(p, treeNode, i);
					}
				} else {
					treeNode = new TreeNode(""+o.value, max_depth, ++currentWidthPerDepth[max_depth], TreeNode.TYPE_LEAF, o.value);
					tree.addNode(treeNode);
			    	linkNode(p, treeNode, childIndex);	
				}
			}
			return null;
		}
		
		mult = 1;
		last_real = 0;
		List<TreeNode> skippedParents = parents;
		for (int j = lastLevel+1 ; j < o.level ; j++) { //For all the missing genes
			if (realDetph[j] != -2) {
				skippedParents = addChildren(j, mult, skippedParents, childIndex, currentWidthPerDepth, ereader);
				mult = widthPerDepth[j];
				last_real = j;
			}
		}
		List<TreeNode> currentNodes = new ArrayList<TreeNode>();
		int nodeCountToCreate = 1;
		if (mult > 1) nodeCountToCreate = widthPerDepth[last_real];
		for (int k = 0; k < skippedParents.size(); k++) {
			for (int i = 0; i < nodeCountToCreate; i++) {
				treeNode = new TreeNode(getNodeName(o.level), o.level, ++currentWidthPerDepth[o.level], TreeNode.TYPE_BRANCH); 
				tree.addNode(treeNode);
				currentNodes.add(treeNode);
		
				for (int j = 0 ; j < o.next.length ; j++) { //For all the children
					if (o.next[j] != null) {
				    	List<TreeNode> childs = _createTreeFromOmdd(o.next[j], o.level, treeNode, j, currentWidthPerDepth, ereader);
						if (childs != null) {
							for (Iterator<TreeNode> it2 = childs.iterator(); it2 .hasNext();) {
								TreeNode child = it2.next();
								linkNode(treeNode, child, j);
							}
						}
					}
			    }
			}
		}

		if (skippedParents.equals(parents)) {
		    return currentNodes;
		} else {
			int max = currentNodes.size()/skippedParents.size();
			for (int i = 0; i < skippedParents.size(); i++) {
				TreeNode p = skippedParents.get(i);
				for (int j = 0; j < max; j++) {
					TreeNode child = currentNodes.get(j+i*max);
					linkNode(p, child, j);
				}
			}
			return null;
		}
	}
	
}