package org.ginsim.graph.tree;

import java.util.Iterator;
import java.util.List;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;

public class TreeParserFromRegulatoryGraph extends TreeParserFromOmdd {
	
	public static final String PARAM_REGGRAPH = "pfrg_regGraph";
	public static final String PARAM_INITIALVERTEXINDEX = "pfrg_initialVertex";
	
	protected RegulatoryGraph regGraph;


	public void init() {
		int initial_gene_id = ((Integer)getParameter(PARAM_INITIALVERTEXINDEX)).intValue();
		nodeOrder = (List)getParameter(PARAM_NODEORDER);
		regGraph = (RegulatoryGraph)getParameter(PARAM_REGGRAPH);

		RegulatoryNode initialVertex = (RegulatoryNode) nodeOrder.get(initial_gene_id);
		
		this.root = initialVertex.getTreeParameters(regGraph).reduce();
		widthPerDepth = widthPerDepth_acc = realDetph = null;
		total_levels = max_depth = 0;
		max_terminal = initialVertex.getMaxValue()+1;
		initRealDepth(initialVertex);
	}
	
	/**
	 * Initialize the <b>realDepth</b> array, and <b>max_terminal</b> from an initial vertex, assuming regGraph is defined
	 * @param initialVertex
	 */
	public void initRealDepth(RegulatoryNode initialVertex) {
		realDetph = new int[nodeOrder.size()+1]; //+1 for the leafs
		int i = 0;
		for (RegulatoryMultiEdge e: regGraph.getIncomingEdges(initialVertex)) {
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
		return ((RegulatoryNode)nodeOrder.get(level)).getId();
	}

}