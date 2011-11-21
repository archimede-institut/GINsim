package org.ginsim.gui.service.tool.graphcomparator;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.tool.graphcomparator.GraphComparatorService;
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

class GraphComparatorAction extends GsToolsAction {

	private final Graph graph;
	
	public GraphComparatorAction( Graph graph) {
		
		super("STR_gcmp", "STR_gcmp_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        // TODO : REFACTORING ACTION
		// TODO : what is ref? Is this test useful? Ref was set to 0 in the GsPluggableActionDescriptor definition in the getT_action
		//if (ref == 0) {
	           new GraphComparatorFrame( GUIManager.getInstance().getFrame( graph), graph);
		//	}

	}
	
}
