package org.ginsim.servicegui.tool.stg2htg;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.service.tool.stg2htg.STG2HTGService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@GUIFor( STG2HTGService.class)
public class STG2HTGServiceGUI implements ServiceGUI {

	static {
		Selector.registerSelector(STG2HTGSelector.IDENTIFIER, STG2HTGSelector.class);
	}

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new STG2HTGAction(graph));
		return actions;
	}

}

class STG2HTGAction extends ToolAction {
	
	private final Graph<?, ?> graph;
	
	public STG2HTGAction(Graph<?, ?> graph) {
		super("STR_STG2HTG", "STR_STG2HTG_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Thread thread = new STG2HTG(graph);
		thread.start();
	}
	
}