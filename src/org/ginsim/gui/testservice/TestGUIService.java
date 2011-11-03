package org.ginsim.gui.testservice;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.gui.service.GsExportAction;
import org.ginsim.gui.service.GsGUIService;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(GsGUIService.class)
public class TestGUIService implements GsGUIService {

	@Override
	public void registerActions(List<Action> actions, Graph<?, ?> graph) {
		if (graph instanceof TestGraph) {
			actions.add(new TestAction((TestGraph) graph));
		}
	}
}

class TestAction extends GsExportAction {

	private final TestGraph graph;
	
	protected TestAction(TestGraph graph) {
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("test action performed on graph: "+ graph);
	}
}