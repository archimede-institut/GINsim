package fr.univmrs.tagc.GINsim.treeViewer;

import java.util.Iterator;

import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class GsRegulatoryFunctionTreeViewPlugin implements GsActionProvider, GsPlugin {
	private static final Integer ZERO = new Integer(0);
	
	private GsPluggableActionDescriptor[] t_action = null;

	public void registerPlugin() {
		GsRegulatoryGraphDescriptor.registerActionProvider(this);
	}
	
	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		if (actionType != ACTION_ACTION) {
			return null;
		}
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_treeViewer_regulatoryPlugin", "STR_treeViewer_regulatoryPlugin_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
	}
	
	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
		if (actionType != ACTION_ACTION) {
			return;
		}
		if (ref == 0) {
			if (graph instanceof GsRegulatoryGraph) {
				GsRegulatoryGraph regGraph = (GsRegulatoryGraph) graph;
				
				GsTreeParser parser = new GsTreeParserFromRegulatoryGraph();
				GsTree tree = new GsTree(parser);
					
				parser.setParameter(GsTreeParserFromRegulatoryGraph.PARAM_NODEORDER, regGraph.getNodeOrder());
				parser.setParameter(GsTreeParserFromRegulatoryGraph.PARAM_REGGRAPH, regGraph);
				parser.setParameter(GsTreeParserFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX, getSelectedVertex(regGraph));
				GsEnv.newMainFrame(tree);
				
			//	run(); //No need to run, because the actionListener will take care of it
			}
		}
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
		} else return ZERO;
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
