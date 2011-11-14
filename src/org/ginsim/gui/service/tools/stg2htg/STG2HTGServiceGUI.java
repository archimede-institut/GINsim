package org.ginsim.gui.service.tools.stg2htg;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.service.action.stg2htg.STG2HTGService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.css.Selector;

@ProviderFor(GsServiceGUI.class)
@GUIFor( STG2HTGService.class)
public class STG2HTGServiceGUI implements GsServiceGUI {

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

class STG2HTGAction extends GsToolsAction {
	
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