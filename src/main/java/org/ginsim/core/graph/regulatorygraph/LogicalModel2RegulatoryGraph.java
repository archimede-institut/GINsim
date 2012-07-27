package org.ginsim.core.graph.regulatorygraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.ConnectivityMatrix;
import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.PathSearcher;
import org.colomoto.mddlib.VariableEffect;

/**
 * Create a RegulatoryGraph based on a LogicalModel.
 * 
 * @author Aurelien Naldi
 */
public class LogicalModel2RegulatoryGraph {

	private final LogicalModel model;
	private final MDDManager ddmanager;
	private final ConnectivityMatrix matrix;
	private final RegulatoryGraph lrg;

	private final List<NodeInfo> coreNodes;
	private final MDDVariable[] variables;
	private final Map<NodeInfo, RegulatoryNode> node2node;
	
	/**
	 * Create a new Regulatory graph, based on an existing logical model.
	 * 
	 * @param model
	 * @return
	 */
	public static RegulatoryGraph importModel(LogicalModel model) {
		return new LogicalModel2RegulatoryGraph(model).getRegulatoryGraph();
	}
	
	
	private LogicalModel2RegulatoryGraph( LogicalModel model) {
		this.model = model;
		this.ddmanager = model.getMDDManager();
		this.variables = ddmanager.getAllVariables();
		this.lrg = RegulatoryGraphFactory.getInstance().create();
		this.matrix = new ConnectivityMatrix(model);
		
		// mapping
		coreNodes = model.getNodeOrder();
		node2node = new HashMap<NodeInfo, RegulatoryNode>();
		
		// add all components
		addNodes(coreNodes);
		addNodes(model.getExtraComponents());
		
		// import the logical functions
		addRegulators(model.getLogicalFunctions(), coreNodes, false);
		addRegulators(model.getExtraLogicalFunctions(), model.getExtraComponents(), true);
	}
	
	private RegulatoryGraph getRegulatoryGraph() {
		return lrg;
	}
	
	private void addNodes(List<NodeInfo> nodes) {
    	for (NodeInfo ni: nodes) {
    		RegulatoryNode node = lrg.addNode( );
    		node.setId(ni.getNodeID());
    		node.setMaxValue(ni.getMax(), lrg);
    	}
	}

	private void addRegulators(int[] functions, List<NodeInfo> nodes, boolean extra) {
		PathSearcher searcher = new PathSearcher(ddmanager);
		for (int idx=0 ; idx<functions.length ; idx++) {
			NodeInfo target = nodes.get(idx);
			RegulatoryNode regNode = node2node.get(target);
			
			int[] regulators = matrix.getRegulators(idx, extra);
			VariableEffect allEffects[][] = matrix.getRegulatorEffects(idx, extra);
			
			for (int regIdx=0 ; regIdx<regulators.length ; regIdx++) {
				int reg = regulators[regIdx];
				VariableEffect[] effects = allEffects[regIdx];
				NodeInfo regulator = coreNodes.get(reg);
				
				byte idxTh=1;
				RegulatoryMultiEdge me = null;
				for (VariableEffect curEffect: effects) {
					if (curEffect != null && curEffect != VariableEffect.NONE) {
						RegulatoryEdgeSign sign = getSign(curEffect);
						me = lrg.addEdge(node2node.get(regulator), node2node.get(target), sign);
						me.setMin(0, idxTh, lrg);
						break;
					}
					idxTh++;
				}
				
				for ( ; idxTh < effects.length ; idxTh++) {
					VariableEffect curEffect = effects[idxTh];
					if (curEffect != null && curEffect != VariableEffect.NONE) {
						RegulatoryEdgeSign sign = getSign(curEffect);
						me.addEdge(sign, idxTh, lrg);
					}
				}
				
			}
			
			// import logical functions
			int mdd = functions[idx];
			int[] path = searcher.setNode(mdd);
			for (int leaf: searcher) {
				if (leaf == 0) {
					continue;
				}
				
				// TODO: generate corresponding logical parameters
				// int[] selectedValue	
			}

		}
	}
	
	private RegulatoryEdgeSign getSign(VariableEffect[] effects) {
		VariableEffect effect = VariableEffect.NONE;
		for (VariableEffect ef: effects) {
			effect = effect.combine(ef);
		}
		return getSign(effect);
	}
	private RegulatoryEdgeSign getSign(VariableEffect effect) {
		switch (effect) {
		case POSITIVE:
			return RegulatoryEdgeSign.POSITIVE;
		case NEGATIVE:
			return RegulatoryEdgeSign.NEGATIVE;
		case DUAL:
			return RegulatoryEdgeSign.DUAL;
		}
		return RegulatoryEdgeSign.UNKNOWN;
	}
}
