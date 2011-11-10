package org.ginsim.gui.service.action.regulatorytreefunction;

import java.util.Iterator;
import java.util.List;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class GsTreeParserFromRegulatoryGraph extends GsTreeParserFromOmdd {
	
	public static final String PARAM_REGGRAPH = "pfrg_regGraph";
	public static final String PARAM_INITIALVERTEXINDEX = "pfrg_initialVertex";
	
	protected GsRegulatoryGraph regGraph;


	public void init() {
		int initial_gene_id = ((Integer)getParameter(PARAM_INITIALVERTEXINDEX)).intValue();
		nodeOrder = (List)getParameter(PARAM_NODEORDER);
		regGraph = (GsRegulatoryGraph)getParameter(PARAM_REGGRAPH);

		GsRegulatoryVertex initialVertex = (GsRegulatoryVertex) nodeOrder.get(initial_gene_id);
		
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
	public void initRealDepth(GsRegulatoryVertex initialVertex) {
		realDetph = new int[nodeOrder.size()+1]; //+1 for the leafs
		int i = 0;
		for (Iterator it = regGraph.getIncomingEdges(initialVertex).iterator(); it.hasNext();) {
			GsDirectedEdge e = ((GsDirectedEdge) it.next());
			GsRegulatoryVertex source = (GsRegulatoryVertex) e.getSource();
			i = 0;
			for (Iterator it2 = nodeOrder.iterator(); it2.hasNext(); i++) {
				GsRegulatoryVertex v = (GsRegulatoryVertex) it2.next();
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
		return ((GsRegulatoryVertex)nodeOrder.get(level)).getId();
	}

}