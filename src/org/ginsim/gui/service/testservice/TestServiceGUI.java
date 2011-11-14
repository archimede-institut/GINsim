package org.ginsim.gui.service.testservice;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsExportAction;
import org.ginsim.gui.service.common.GsImportAction;
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
			actions.add( new OtherTestAction((TestGraph) graph));
		}
		
		return actions;
	}
}

class TestAction extends GsExportAction {

	private final TestGraph graph;
	
	protected TestAction( TestGraph graph) {
		super("test export");
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		System.out.println("test action performed on graph: "+ graph);
	}
}

class OtherTestAction extends GsImportAction {

	private final TestGraph graph;
	
	protected OtherTestAction( TestGraph graph) {
		super("test import");
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		System.out.println("test action performed on graph: "+ graph);
	}
}