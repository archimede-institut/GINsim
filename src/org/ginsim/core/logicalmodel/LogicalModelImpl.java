package org.ginsim.core.logicalmodel;

import java.util.List;

import org.ginsim.core.graph.common.NodeInfo;

import fr.univmrs.tagc.javaMDD.MDDFactory;

public class LogicalModelImpl implements LogicalModel {

	private final List<NodeInfo> nodeOrder;
	private final MDDFactory factory;
	private final int[] functions;
	
	public LogicalModelImpl(List<NodeInfo> nodeOrder, MDDFactory factory, int[] functions) {
		this.nodeOrder = nodeOrder;
		this.factory = factory;
		this.functions = functions;
	}
	
	
	@Override
	public List<NodeInfo> getNodeOrder() {
		return nodeOrder;
	}

	@Override
	public MDDFactory getMDDFactory() {
		return factory;
	}

	@Override
	public int[] getLogicalFunctions() {
		return functions;
	}

	
}
