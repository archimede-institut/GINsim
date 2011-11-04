package org.ginsim.gui.layout;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.graph.GraphView;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsLayoutAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.layout.GsLayoutService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.GsException;

@ProviderFor(GsServiceGUI.class)
@GUIFor(GsLayoutService.class)
public class LayoutServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new LayoutAction((TestGraph) graph));
		
		return actions;
	}
}

class LayoutAction extends GsLayoutAction {

	private final TestGraph graph;
	
	protected LayoutAction( TestGraph graph) {
		super("layout");
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		try {
			GsLayoutService.runLayout(GsLayoutService.LEVEL, graph, (GraphView)graph);
		} catch (GsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}