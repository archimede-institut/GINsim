package org.ginsim.gui.service.tool.regulatorytreefunction;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.tree.Tree;
import org.ginsim.graph.tree.TreeParser;
import org.ginsim.graph.tree.TreeParserFromRegulatoryGraph;
import org.ginsim.graph.tree.TreeImpl;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.regulatoryfunctiontree.RegulatoryFunctionTreeService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@GUIFor( RegulatoryFunctionTreeService.class)
public class RegulatoryFunctionTreeServiceGUI implements ServiceGUI {
	
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

class RegulatoryFunctionTreeAction extends ToolAction {

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
		
		TreeParser parser = new TreeParserFromRegulatoryGraph();
		Tree tree = new TreeImpl( parser);
			
		parser.setParameter(TreeParserFromRegulatoryGraph.PARAM_NODEORDER, graph.getNodeOrder());
		parser.setParameter(TreeParserFromRegulatoryGraph.PARAM_REGGRAPH, graph);
		parser.setParameter(TreeParserFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX, getSelectedVertex( graph));
		GUIManager.getInstance().newFrame(tree);
	}
	
	/**
	 * Return the index of the first selected gene in the regulatory graph, or 0 if none are selected.
	 * @param regGraph
	 * @return
	 */
	private Integer getSelectedVertex(RegulatoryGraph regGraph) {
		
		RegulatoryNode selectedNode = null;
		Collection<?> vertices = gui.getSelection().getSelectedNodes();
		if (vertices != null && vertices.size() > 0) {
			selectedNode = (RegulatoryNode) vertices.iterator().next();
		} else {
			return ZERO;
		}
		int i = 0;
		for (Iterator it2 = regGraph.getNodeOrder().iterator(); it2.hasNext(); i++) {
			RegulatoryNode v = (RegulatoryNode) it2.next();
			if (v.equals(selectedNode)) {
				return new Integer(i);
			}
		}
		return ZERO;
	}
}
