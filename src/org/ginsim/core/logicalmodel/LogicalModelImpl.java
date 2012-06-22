package org.ginsim.core.logicalmodel;

import java.util.List;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.internal.MDDManagerOrderingProxy;
import org.ginsim.core.graph.common.NodeInfo;


public class LogicalModelImpl implements LogicalModel {

	private final List<NodeInfo> nodeOrder;
	private final MDDManager factory;
	private final int[] functions;
	private final MDDManagerOrderingProxy MDDorder;
	
	public LogicalModelImpl(List<NodeInfo> nodeOrder, MDDManager factory, int[] functions) {
		this.nodeOrder = nodeOrder;
		this.factory = factory;
		this.functions = functions;
		this.MDDorder = new MDDManagerOrderingProxy(factory, nodeOrder);
		
		for (int f: functions) {
			factory.use(f);
		}
	}
	
	@Override
	public List<NodeInfo> getNodeOrder() {
		return nodeOrder;
	}

	@Override
	public MDDManager getMDDFactory() {
		return factory;
	}

	@Override
	public int[] getLogicalFunctions() {
		return functions;
	}

	public LogicalModel clone() {
		int[] functionsClone = functions.clone();
		return new LogicalModelImpl(nodeOrder, factory, functionsClone);
	}

	@Override
	public byte getTargetValue(int nodeIdx, byte[] state) {
		return MDDorder.reach(functions[nodeIdx], state);
	}


	@Override
	public byte getComponentValue(int componentIdx, byte[] path) {
		return path[ MDDorder.custom2factory[componentIdx] ];
	}
}
