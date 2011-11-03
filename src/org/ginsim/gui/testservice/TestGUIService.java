package org.ginsim.gui.testservice;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.gui.service.GsExportAction;
import org.ginsim.gui.service.GsGUIService;
import org.ginsim.gui.service.GsImportAction;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(GsGUIService.class)
public class TestGUIService implements GsGUIService {

	@Override
	public void registerActions(List<Action> actions, Graph<?, ?> graph) {
		if (graph instanceof TestGraph) {
			TestGraph g = (TestGraph)graph;
			actions.add(new TestExport(g));
			actions.add(new TestImport(g));
		}
	}
}

class TestExport extends GsExportAction {

	private final TestGraph graph;
	
	protected TestExport(TestGraph graph) {
		super("test export");
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("test export performed on graph: "+ graph);
	}
}

class TestImport extends GsImportAction {

	private final TestGraph graph;
	
	protected TestImport(TestGraph graph) {
		super("test import");
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("test import performed on graph: "+ graph);
	}
}