package org.ginsim.servicegui.tool.sccgraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.service.tool.connectivity.ConnectivityService;
import org.ginsim.service.tool.sccgraph.SCCGraphResult;
import org.ginsim.service.tool.sccgraph.SCCGraphService;
import org.mangosdk.spi.ProviderFor;


/**
 * register the connectivity service
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ConnectivityService.class)
public class SCCGraphServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new SCCGraphAction( graph));
		return actions;
	}

	@Override
	public int getWeight() {
		return W_GENERIC;
	}
}

class SCCGraphAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
	
	protected SCCGraphAction( Graph graph) {
        super( graph, "STR_constructReducedGraph", null, "STR_constructReducedGraph_descr", null);
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		SCCGraphService service = ServiceManager.getManager().getService(SCCGraphService.class);
        SCCGraphResult result = service.run(graph);
        GUIManager.getInstance().whatToDoWithGraph(result.getReducedGraph(), true);
	}
}
