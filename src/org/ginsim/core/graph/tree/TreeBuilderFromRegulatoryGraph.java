package org.ginsim.core.graph.tree;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

public class TreeBuilderFromRegulatoryGraph extends TreeBuilderFromOmdd {
	
	public static final String PARAM_REGGRAPH = "pfrg_regGraph";
	public static final String PARAM_INITIALVERTEXINDEX = "pfrg_initialNode";
	
	protected RegulatoryGraph regGraph;


	public void init() {
		int initial_gene_id = ((Integer)getParameter(PARAM_INITIALVERTEXINDEX)).intValue();
		nodeOrder = (List<RegulatoryNode>)getParameter(PARAM_NODEORDER);
		regGraph = (RegulatoryGraph)getParameter(PARAM_REGGRAPH);

		RegulatoryNode initialNode = nodeOrder.get(initial_gene_id);
		
		this.root = initialNode.getTreeParameters(regGraph).reduce();
		widthPerDepth = widthPerDepth_acc = realDetph = null;
		total_levels = max_depth = 0;
		max_terminal = initialNode.getMaxValue()+1;
		initRealDepth(initialNode);
	}
	
	/**
	 * Initialize the <b>realDepth</b> array, and <b>max_terminal</b> from an initial node, assuming regGraph is defined
	 * @param initialNode
	 */
	public void initRealDepth(RegulatoryNode initialNode) {
		realDetph = new int[nodeOrder.size()+1]; //+1 for the leafs
		int i = 0;
		for (RegulatoryMultiEdge e: regGraph.getIncomingEdges(initialNode)) {
			RegulatoryNode source = e.getSource();
			i = 0;
			for (RegulatoryNode v: (List<RegulatoryNode>)nodeOrder) {
				if (v.equals(source)) {
					realDetph[i] = -1;
				}
			}
		}
		int next_realDepth = 0;
		for (i = 0; i < realDetph.length; i++) {
			if (realDetph[i] == -1) {
				total_levels++;
				realDetph[i] = next_realDepth++;
			} else realDetph[i] = -2;
		}
	}
	
	protected String getNodeName(int level) {
		return nodeOrder.get(level).getId();
	}

}