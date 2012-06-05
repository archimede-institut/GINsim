package org.ginsim.core.graph.common;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;


/**
 * Contains the basic informations of a regulatory node (gene) : 
 * 	- nodeID : a unique identifier for the gene
 *  - max : the maximal regulatory level 
 *
 */
public class NodeInfo {
	
	public static final byte UNDEFINED_MAX = -1;
	
	private String nodeID;
	private byte max;

	public NodeInfo(String name) {
		super();
		this.nodeID = name;
		this.max = UNDEFINED_MAX;
	}
	
	public NodeInfo(String name, byte max) {
		super();
		this.nodeID = name;
		this.max = max;
	}

	public String getNodeID() {
		return nodeID;
	}
	
	public void setNodeID( String id) {
		this.nodeID = id;
	}
	
	public byte getMax() {
		return max;
	}
	
	public void setMax(byte max) {
		this.max = max;
	}
	
	/**
	 * Compare the object to the given one. If the given object is a RegulatoryNode, the IDs are compared
	 */
	@Override
	public boolean equals( Object obj) {
		
		if ( obj instanceof NodeInfo) {
			return super.equals(obj);
		} else if( obj instanceof RegulatoryNode) {
			return super.equals(((RegulatoryNode)obj).getNodeInfo());
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		
		return nodeID;
	}


}
