package org.ginsim.servicegui.tool.graphcomparator;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.graphcomparator.GraphComparatorService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@GUIFor( GraphComparatorService.class)
@ServiceStatus( ServiceStatus.UNDER_DEVELOPMENT)
public class GraphComparatorServiceGUI extends AbstractServiceGUI{
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new GraphComparatorAction( graph, this));
		return actions;

	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 80;
	}
}

class GraphComparatorAction extends ToolAction {

	private final Graph graph;
	
	public GraphComparatorAction( Graph graph, ServiceGUI serviceGUI) {
		
		super("STR_gcmp", "STR_gcmp_descr", serviceGUI);
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new GraphComparatorFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}
