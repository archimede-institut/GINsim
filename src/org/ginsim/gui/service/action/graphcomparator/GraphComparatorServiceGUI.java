package org.ginsim.gui.service.action.graphcomparator;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsActionAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.service.action.graphcomparator.GraphComparatorService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GsServiceGUI.class)
@GUIFor( GraphComparatorService.class)
public class GraphComparatorServiceGUI implements GsServiceGUI{
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new GraphComparatorAction( graph));
		return actions;

	}

}

class GraphComparatorAction extends GsActionAction {

	private final Graph graph;
	
	public GraphComparatorAction( Graph graph) {
		
		super("STR_gcmp", "STR_gcmp_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        // TODO : REFACTORING ACTION
        // TODO : get the parent frame
		// TODO : what is ref? Is this test useful? Ref was set to 0 in the GsPluggableActionDescriptor definition in the getT_action
		//if (ref == 0) {
	           new GraphComparatorFrame( null, graph);
		//	}

	}
	
}
