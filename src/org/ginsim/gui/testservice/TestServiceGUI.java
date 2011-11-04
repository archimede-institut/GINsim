package org.ginsim.gui.testservice;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsExportAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.service.testservice.TestService;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(GsServiceGUI.class)
@GUIFor(TestService.class)
public class TestServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof TestGraph) {
			actions.add( new TestAction((TestGraph) graph));
		}
		
		return actions;
	}
}

class TestAction extends GsExportAction {

	private final TestGraph graph;
	
	protected TestAction( TestGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		System.out.println("test action performed on graph: "+ graph);
	}
}