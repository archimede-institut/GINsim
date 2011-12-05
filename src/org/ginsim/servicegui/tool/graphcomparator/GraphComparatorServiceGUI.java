package org.ginsim.servicegui.tool.graphcomparator;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.service.tool.graphcomparator.GraphComparatorService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@GUIFor( GraphComparatorService.class)
public class GraphComparatorServiceGUI implements ServiceGUI{
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new GraphComparatorAction( graph));
		return actions;

	}

}

class GraphComparatorAction extends ToolAction {

	private final Graph graph;
	
	public GraphComparatorAction( Graph graph) {
		
		super("STR_gcmp", "STR_gcmp_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new GraphComparatorFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}
