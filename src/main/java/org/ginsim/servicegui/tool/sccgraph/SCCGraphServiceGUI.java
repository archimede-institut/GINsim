package org.ginsim.servicegui.tool.sccgraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.connectivity.ConnectivityService;
import org.ginsim.service.tool.sccgraph.SCCGraphResult;
import org.ginsim.service.tool.sccgraph.SCCGraphService;
import org.mangosdk.spi.ProviderFor;


/**
 * register the connectivity service
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ConnectivityService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class SCCGraphServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new SCCGraphAction( graph, this));
		return actions;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 30;
	}
}

class SCCGraphAction extends ToolAction {
	private static final long serialVersionUID = 8294301473668672512L;
	private Graph graph;
	
	protected SCCGraphAction( Graph graph, ServiceGUI serviceGUI) {
        super( "STR_constructReducedGraph", "STR_constructReducedGraph_descr", serviceGUI);
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		SCCGraphService service = ServiceManager.getManager().getService(SCCGraphService.class);
        SCCGraphResult result = service.run(graph);
        GUIManager.getInstance().whatToDoWithGraph(result.getReducedGraph(), true);
	}
}
