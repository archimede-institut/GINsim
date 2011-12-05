package org.ginsim.graph.common;

import org.ginsim.graph.regulatorygraph.RegulatoryNode;

public class NodeInfo {
	
	public static final byte UNDEFINED_MAX = -1;
	
	public final String name;
	public final byte max;

	public NodeInfo(String name) {
		super();
		this.name = name;
		this.max = UNDEFINED_MAX;
	}
	
	public NodeInfo(String name, byte max) {
		super();
		this.name = name;
		this.max = max;
	}

	public NodeInfo(RegulatoryNode vertex) {
		
		this( vertex.getId(), vertex.getMaxValue());
	}
}
