package org.ginsim.gui.service.action.regulatorytreefunction;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.graph.tree.GsTree;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsActionAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.service.action.regulatoryfunctiontree.RegulatoryFunctionTreeService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

@ProviderFor( GsServiceGUI.class)
@GUIFor( RegulatoryFunctionTreeService.class)
public class RegulatoryFunctionTreeServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsRegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new RegulatoryFunctionTreeAction( (GsRegulatoryGraph)graph));
			return actions;
		}
		return null;
	}

}

class RegulatoryFunctionTreeAction extends GsActionAction {

	private static final Integer ZERO = new Integer(0);
	
	private final GsRegulatoryGraph graph;
	
	public RegulatoryFunctionTreeAction( GsRegulatoryGraph graph) {
		
		super( "STR_treeViewer_regulatoryPlugin", "STR_treeViewer_regulatoryPlugin_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		GsTreeParser parser = new GsTreeParserFromRegulatoryGraph();
		GsTree tree = new GsTree( parser);
			
		parser.setParameter(GsTreeParserFromRegulatoryGraph.PARAM_NODEORDER, graph.getNodeOrder());
		parser.setParameter(GsTreeParserFromRegulatoryGraph.PARAM_REGGRAPH, graph);
		parser.setParameter(GsTreeParserFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX, getSelectedVertex( graph));
		GsEnv.newMainFrame(tree);
	}
	
	/**
	 * Return the index of the first selected gene in the regularoty graph, or 0 if none are selected.
	 * @param regGraph
	 * @return
	 */
	private Integer getSelectedVertex(GsRegulatoryGraph regGraph) {
		
		GsRegulatoryVertex selectedNode = null;	
		Iterator it = regGraph.getGraphManager().getSelectedVertexIterator();
		if (it.hasNext()) {
			selectedNode = (GsRegulatoryVertex) it.next();
		} 
		else{
			return ZERO;
		}
		int i = 0;
		for (Iterator it2 = regGraph.getNodeOrder().iterator(); it2.hasNext(); i++) {
			GsRegulatoryVertex v = (GsRegulatoryVertex) it2.next();
			if (v.equals(selectedNode)) {
				return new Integer(i);
			}
		}
		return ZERO;
	}
	
}
