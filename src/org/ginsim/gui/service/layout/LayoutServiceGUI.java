package org.ginsim.gui.service.layout;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsLayoutAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.service.layout.GsLayoutService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;

@ProviderFor(GsServiceGUI.class)
@GUIFor(GsLayoutService.class)
public class LayoutServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		for (LayoutType type: LayoutType.values()) {
			actions.add( new LayoutAction(graph, type));
		}
		
		if (graph instanceof GsDynamicGraph) {
			GsDynamicGraph dynGraph = (GsDynamicGraph)graph;
			for (DynamicalLayoutType type: DynamicalLayoutType.values()) {
				actions.add( new DynamicalLayoutAction(dynGraph, type));
			}
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
			GsLayoutService.runLayout(type.key, graph);
		} catch (GsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

enum DynamicalLayoutType {
	LAYOUT_3D("3D layout", GsLayoutService.LEVEL),
	LAYOUT_MD("Multidimension layout", GsLayoutService.LEVEL_INV);
	
	public final String name;
	public final int key;
	
	private DynamicalLayoutType(String name, int key) {
		this.name = name;
		this.key = key;
	}
}

class DynamicalLayoutAction extends GsLayoutAction {

	private final GsDynamicGraph graph;
	private final DynamicalLayoutType type;
	
	protected DynamicalLayoutAction( GsDynamicGraph graph, DynamicalLayoutType type) {
		super(type.name);
		this.graph = graph;
		this.type = type;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		switch (type) {
		case LAYOUT_3D:
			// FIXME: run 3D layout
			break;
		case LAYOUT_MD:
			// FIXME: run MD layout
			break;
		}
	}
}
