package org.ginsim.core.logicalmodel;

import java.util.List;

import org.ginsim.core.graph.common.NodeInfo;

import fr.univmrs.tagc.javaMDD.MDDFactory;

public class LogicalModelImpl implements LogicalModel {

	private final List<NodeInfo> nodeOrder;
	private final MDDFactory factory;
	private final int[] functions;
	private final int[] factory2modelOrderMap;
	private final int[] model2factoryOrderMap;
	
	public LogicalModelImpl(List<NodeInfo> nodeOrder, MDDFactory factory, int[] functions) {
		this.nodeOrder = nodeOrder;
		this.factory = factory;
		this.functions = functions;
		this.factory2modelOrderMap = factory.getOrderMapping(nodeOrder);
		model2factoryOrderMap = new int[nodeOrder.size()];
		int i=0;
		for (int k: factory2modelOrderMap) {
			if (k >= 0) {
				model2factoryOrderMap[k] = i;
			}
			i++;
		}
		
		for (int f: functions) {
			factory.use(f);
		}
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

	public LogicalModel clone() {
		int[] functionsClone = functions.clone();
		return new LogicalModelImpl(nodeOrder, factory, functionsClone);
	}

	@Override
	public byte getTargetValue(int nodeIdx, byte[] state) {
		return factory.reach(functions[nodeIdx], state, factory2modelOrderMap);
	}


	@Override
	public byte getComponentValue(int componentIdx, byte[] path) {
		return path[ model2factoryOrderMap[componentIdx] ];
	}
}
