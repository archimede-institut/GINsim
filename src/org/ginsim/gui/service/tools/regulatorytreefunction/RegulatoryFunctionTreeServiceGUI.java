package org.ginsim.gui.service.tools.regulatorytreefunction;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.graph.tree.Tree;
import org.ginsim.graph.tree.GsTreeParser;
import org.ginsim.graph.tree.GsTreeParserFromRegulatoryGraph;
import org.ginsim.graph.tree.TreeImpl;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.tools.regulatoryfunctiontree.RegulatoryFunctionTreeService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GsServiceGUI.class)
@GUIFor( RegulatoryFunctionTreeService.class)
public class RegulatoryFunctionTreeServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new RegulatoryFunctionTreeAction( (RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}

}

class RegulatoryFunctionTreeAction extends GsToolsAction {

	private static final Integer ZERO = new Integer(0);
	
	private final RegulatoryGraph graph;
	private final GraphGUI<?, ?, ?> gui;
	
	public RegulatoryFunctionTreeAction( RegulatoryGraph graph) {
		
		super( "STR_treeViewer_regulatoryPlugin", "STR_treeViewer_regulatoryPlugin_descr");
		this.graph = graph;
		this.gui = GUIManager.getInstance().getGraphGUI(graph);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		GsTreeParser parser = new GsTreeParserFromRegulatoryGraph();
		Tree tree = new TreeImpl( parser);
			
		parser.setParameter(GsTreeParserFromRegulatoryGraph.PARAM_NODEORDER, graph.getNodeOrder());
		parser.setParameter(GsTreeParserFromRegulatoryGraph.PARAM_REGGRAPH, graph);
		parser.setParameter(GsTreeParserFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX, getSelectedVertex( graph));
		GUIManager.getInstance().newFrame(tree);
	}
	
	/**
	 * Return the index of the first selected gene in the regulatory graph, or 0 if none are selected.
	 * @param regGraph
	 * @return
	 */
	private Integer getSelectedVertex(RegulatoryGraph regGraph) {
		
		RegulatoryVertex selectedNode = null;
		Collection<?> vertices = gui.getSelection().getSelectedNodes();
		if (vertices != null && vertices.size() > 0) {
			selectedNode = (RegulatoryVertex) vertices.iterator().next();
		} else {
			return ZERO;
		}
		int i = 0;
		for (Iterator it2 = regGraph.getNodeOrder().iterator(); it2.hasNext(); i++) {
			RegulatoryVertex v = (RegulatoryVertex) it2.next();
			if (v.equals(selectedNode)) {
				return new Integer(i);
			}
		}
		return ZERO;
	}
}
