package org.ginsim.core.graph.tree;

import java.util.Collection;

import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.utils.Dotify;


public class TreeNode implements Dotify {
	public static long nextUid = 0;
	
	public static final byte TYPE_LEAF = 0;
	public static final byte TYPE_BRANCH = 1;

	public static final int PADDING_VERTICAL = 60;
	public static final int PADDING_HORIZONTAL = 60;
	
	public static final int LEAF_DEFAULT_DEPTH = -1;

	/**
	 * The value should be -42 if the node is skipped and added in the tree.
	 */
	public static final byte SKIPPED = -42;
	
	/**
	 * The type of the node (terminal or branch).
	 */
	private byte type = TYPE_BRANCH;
	
	/**
	 * The label of the node, to display
	 */
	private String label;
	
	/**
	 * Indicates the depth of the node in the tree
	 * Equivalent to the Omdd's level.
	 */
	private int depth;
	
	/**
	 * Indicates the position on the width
	 */
	private int width;

	/**
	 * A unique identifier
	 */
	private long uid;

	/**
	 * Indicates the value of the leafs.
	 * Equivalent to the Omdd's value
	 */
	private byte value;

	/**
	 * Create a new TreeNode with a default value of -1
	 * @param label
	 * @param depth the depth in the tree
	 * @param width
	 * @param type
	 */
	public TreeNode(String label, int depth, int width, byte type) {
		this(label, depth, width, type, (byte) LEAF_DEFAULT_DEPTH);
	}
	public TreeNode(String label, int depth, int width, byte type, byte value) {
		this.uid = nextUid++;
		this.label = label;
		this.type = type;
		this.depth = depth;
		this.width = width;
		this.value = value;
	}
	
	/**
	 * Create a new TreeNode by copying the values of the other TreeNode
	 * @param other
	 */
	public TreeNode(TreeNode other) {
		this.uid = nextUid++;
		this.label = other.label;
		this.type = other.type;
		this.depth = other.depth;
		this.width = other.width;
		this.value = other.value;
	}
	/*
	 * HELPERS
	 */
	public boolean isLeaf() {
		return type == TYPE_LEAF;
	}
	public boolean isBranch() {
		return type == TYPE_BRANCH;
	}

	/*
	 * Getters and setters
	 */
	public byte getValue() { return value; }
	public byte getType() { return type; }
	public void setLabel(String label) { this.label = label; }
	public String getLabel() { return label; }
	public long getUniqueId() { return uid; }
	public void setDepth(int depth) { this.depth = depth; }
	public int getDepth() { return depth; }
	public void setWidth(int width) { this.width = width; }
	public int getWidth() { return width; }
	
	/*
	 * toStrings and toDot
	 */
	public String toString() {
		return label;
	}
	public String toUniqueString() {
		return label+"::"+uid;
	}
	public String toDescrString() {
		return label+"::"+uid+"  dethp:"+getDepth()+", width:"+getWidth();
	}

	public String toDot() {
		String options;
    	if (this.getType() == TYPE_LEAF) options = "shape=rectangle,style=filled, width=\"1.1\", height=\"1.1\",color=\"#9CBAEB\"";
    	else 							 options = "shape=ellipse,style=filled,color=\"#00FF00\"";
		return  this.getUniqueId()+" [label=\""+this.label+"\", "+options+"];";
	}

	public String toDot(Object to) {
		return  this.getUniqueId()+" -> "+((TreeNode) to).getUniqueId()+";";
	}

	public boolean equals(Object other) {
		if (!(other instanceof TreeNode)) return false;
		return this.uid == ((TreeNode)other).uid;
	}
	public TreeNode deepCopy( Tree gm) {
		TreeNode self = new TreeNode(this);
		LogManager.debug( self);
		gm.addNode(self);
		for (Edge<TreeNode> e: (Collection<Edge<TreeNode>>) gm.getOutgoingEdges(this)) {
			TreeNode target = e.getTarget().deepCopy( gm);
			gm.addNode(target);
			Edge<TreeNode> edge = new Edge<TreeNode>(gm, self, target);
			gm.addEdge(edge);
		}
		return self;
	}
}