package org.ginsim.gui.graph.canvas.events;

import java.awt.Color;
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
	private Point p1, p2;
	
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
			p1 = null;
		} else {
			p1 = p;
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
		if (p1 != null) {
			renderer.repaintCanvas();
			dragged = true;
			p2 = p;
		}
	}

	@Override
	public void cancel() {
	}

	@Override
	public void overlay(Graphics2D g, Rectangle area) {
		if (dragged && p1 != null && p2 != null) {
			Object o = renderer.getObjectUnderPoint(p2);
			if (o != null && !(o instanceof Edge)) {
				g.setColor(Color.GREEN);
			}
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
	}

}
