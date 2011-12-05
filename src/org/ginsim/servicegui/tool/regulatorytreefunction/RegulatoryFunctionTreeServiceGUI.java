package org.ginsim.servicegui.tool.regulatorytreefunction;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.tree.Tree;
import org.ginsim.core.graph.tree.TreeImpl;
import org.ginsim.core.graph.tree.TreeBuilder;
import org.ginsim.core.graph.tree.TreeBuilderFromRegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.service.tool.regulatoryfunctiontree.RegulatoryFunctionTreeService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
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
		
		TreeBuilder parser = new TreeBuilderFromRegulatoryGraph();
		Tree tree = GraphManager.getInstance().getNewGraph( Tree.class, parser);
			
		parser.setParameter(TreeBuilderFromRegulatoryGraph.PARAM_NODEORDER, graph.getNodeOrder());
		parser.setParameter(TreeBuilderFromRegulatoryGraph.PARAM_REGGRAPH, graph);
		parser.setParameter(TreeBuilderFromRegulatoryGraph.PARAM_INITIALVERTEXINDEX, getSelectedNode( graph));
		GUIManager.getInstance().newFrame(tree);
	}
	
	/**
	 * Return the index of the first selected gene in the regulatory graph, or 0 if none are selected.
	 * @param regGraph
	 * @return
	 */
	private Integer getSelectedNode(RegulatoryGraph regGraph) {
		
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
