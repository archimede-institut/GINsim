package org.ginsim.gui.graph.canvas.events;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.graph.AddNodeAction;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.EditMode;
import org.ginsim.gui.graph.canvas.CanvasEventManager;
import org.ginsim.gui.graph.canvas.GraphCanvasRenderer;
import org.jgrapht.event.GraphEdgeChangeEvent;

public class AddNodeInGraphCanvasEventManager implements CanvasEventManager {

	private final GraphCanvasRenderer renderer;

	public AddNodeInGraphCanvasEventManager(GraphCanvasRenderer renderer) {
		this.renderer = renderer;
	}
	
	@Override
	public void click(Point p, boolean alternate) {
		EditAction currentAction = renderer.amanager.getSelectedAction();
		if (currentAction instanceof AddNodeAction) {
			AddNodeAction action = (AddNodeAction)currentAction;
			action.addNode(renderer.amanager, p.x, p.y);
		}
	}

	@Override
	public void pressed(Point p, boolean alternate) {
	}

	@Override
	public void released(Point p) {
	}

	@Override
	public void dragged(Point p) {
	}

	@Override
	public void cancel() {
	}

	@Override
	public void overlay(Graphics2D g, Rectangle area) {
	}

	@Override
	public void helpOverlay(Graphics2D g, Rectangle area) {
	}


}
