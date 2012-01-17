package org.ginsim.servicegui.layout;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.LayoutAction;
import org.ginsim.service.layout.LayoutService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@GUIFor(LayoutService.class)
public class LayoutServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		for (LayoutType type: LayoutType.values()) {
			actions.add( new BasicLayoutAction(graph, type));
		}
		
		if (graph instanceof DynamicGraph) {
			DynamicGraph dynGraph = (DynamicGraph)graph;
			for (DynamicalLayoutType type: DynamicalLayoutType.values()) {
				actions.add( new DynamicalLayoutAction(dynGraph, type));
			}
		}
		return actions;
	}

	@Override
	public int getWeight() {
		return W_GENERIC;
	}
}

enum LayoutType {
	LEVEL("STR_level_placement", LayoutService.LEVEL),
	LEVEL_INV("STR_level_placement_inv", LayoutService.LEVEL_INV),
	RING("STR_ring_placement", LayoutService.RING),
	RING_INV("STR_ring_placement_inv", LayoutService.RING_INV);
	
	public final String name;
	public final int key;
	
	private LayoutType(String name, int key) {
		this.name = name;
		this.key = key;
	}
}

class BasicLayoutAction extends LayoutAction<Graph<?,?>> {

	private final LayoutType type;
	
	protected BasicLayoutAction( Graph<?,?> graph, LayoutType type) {
		super(graph, type.name);
		this.type = type;
	}
	
	@Override
	public void doLayout( ActionEvent arg0) {
		try {
			LayoutService.runLayout(type.key, graph);
		} catch (GsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

enum DynamicalLayoutType {
	LAYOUT_3D("3D layout", LayoutService.LEVEL),
	LAYOUT_MD("Multidimension layout", LayoutService.LEVEL_INV);
	
	public final String name;
	public final int key;
	
	private DynamicalLayoutType(String name, int key) {
		this.name = name;
		this.key = key;
	}
}

class DynamicalLayoutAction extends LayoutAction<DynamicGraph> {

	private final DynamicalLayoutType type;
	
	protected DynamicalLayoutAction( DynamicGraph graph, DynamicalLayoutType type) {
		super(graph, type.name);
		this.type = type;
	}
	
	@Override
	public void doLayout( ActionEvent arg0) {
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
