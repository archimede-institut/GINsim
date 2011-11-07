package org.ginsim.gui.service.layout;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.graph.GraphView;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsLayoutAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.service.layout.GsLayoutService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.GsException;

@ProviderFor(GsServiceGUI.class)
@GUIFor(GsLayoutService.class)
public class LayoutServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		for (LayoutType type: LayoutType.values()) {
			actions.add( new LayoutAction(graph, type));
		}
		
		return actions;
	}
}

enum LayoutType {
	LEVEL("Level layout", GsLayoutService.LEVEL),
	LEVEL_INV("Inversed level layout", GsLayoutService.LEVEL_INV),
	RING("Ring layout", GsLayoutService.RING),
	RING_INV("Inverser ring layout", GsLayoutService.RING_INV);
	
	public final String name;
	public final int key;
	
	private LayoutType(String name, int key) {
		this.name = name;
		this.key = key;
	}
}

class LayoutAction extends GsLayoutAction {

	private final Graph<?,?> graph;
	private final LayoutType type;
	
	protected LayoutAction( Graph<?,?> graph, LayoutType type) {
		super(type.name);
		this.graph = graph;
		this.type = type;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		try {
			GsLayoutService.runLayout(type.key, graph, (GraphView)graph);
		} catch (GsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}