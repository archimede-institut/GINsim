package org.ginsim.core.logicalmodel;

import java.util.List;

import org.colomoto.mddlib.MDDManager;
import org.ginsim.core.graph.common.NodeInfo;


public class LogicalModelImpl implements LogicalModel {

	private final List<NodeInfo> nodeOrder;
	private final MDDManager ddmanager;
	private final int[] functions;
	
	public LogicalModelImpl(List<NodeInfo> nodeOrder, MDDManager factory, int[] functions) {
		this.nodeOrder = nodeOrder;
		this.ddmanager = factory.getManager(nodeOrder);
		this.functions = functions;
		
		for (int f: functions) {
			factory.use(f);
		}
	}
	
	@Override
	public List<NodeInfo> getNodeOrder() {
		return nodeOrder;
	}

	@Override
	public MDDManager getMDDManager() {
		return ddmanager;
	}

	@Override
	public int[] getLogicalFunctions() {
		return functions;
	}

	public LogicalModel clone() {
		int[] functionsClone = functions.clone();
		return new LogicalModelImpl(nodeOrder, ddmanager, functionsClone);
	}

	@Override
	public byte getTargetValue(int nodeIdx, byte[] state) {
		return ddmanager.reach(functions[nodeIdx], state);
	}


//	@Override
//	public byte getComponentValue(int componentIdx, byte[] path) {
//		return path[ ddmanager.custom2factory[componentIdx] ];
//	}

}
