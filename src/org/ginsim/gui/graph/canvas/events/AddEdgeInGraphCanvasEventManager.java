package org.ginsim.gui.graph.canvas.events;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.graph.AddEdgeAction;
import org.ginsim.gui.graph.AddNodeAction;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.EditMode;
import org.ginsim.gui.graph.canvas.CanvasEventManager;
import org.ginsim.gui.graph.canvas.GraphCanvasRenderer;
import org.jgrapht.event.GraphEdgeChangeEvent;

public class AddEdgeInGraphCanvasEventManager implements CanvasEventManager {

	private final GraphCanvasRenderer renderer;

	private Object start = null;
	private boolean dragged = false;
	
	public AddEdgeInGraphCanvasEventManager(GraphCanvasRenderer renderer) {
		this.renderer = renderer;
	}
	
	@Override
	public void click(Point p, boolean alternate) {
	}

	@Override
	public void pressed(Point p, boolean alternate) {
		dragged = false;
		start = renderer.getObjectUnderPoint(p);
		if (start instanceof Edge) {
			start = null;
		}
	}

	@Override
	public void released(Point p) {
		if (dragged == false) {
			return;
		}
		dragged = false;
		
		Object to = renderer.getObjectUnderPoint(p);
		if (start == null || to == null || to instanceof Edge) {
			start = null;
			return;
		}
		
		EditAction currentAction = renderer.amanager.getSelectedAction();
		if (currentAction instanceof AddEdgeAction) {
			AddEdgeAction action = (AddEdgeAction)currentAction;
			action.addEdge(renderer.amanager, start, to);
		}
	}

	@Override
	public void dragged(Point p) {
		dragged = true;
		// TODO: overlay information
	}

	@Override
	public void cancel() {
	}

	@Override
	public void overlay(Graphics2D g, Rectangle area) {
		if (dragged) {
			System.out.println("overlay for edge");
		}
	}

}
