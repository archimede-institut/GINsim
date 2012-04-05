package org.ginsim.gui.graph.canvas;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JPanel;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.graph.BaseGraphGUI;
import org.ginsim.gui.graph.EditActionManager;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.GraphGUIListener;
import org.ginsim.gui.graph.GraphSelection;

public class CanvasGraphGUIImpl<G extends Graph<V,E>, V, E extends Edge<V>> extends BaseGraphGUI<G, V, E> {

	public static final boolean USECANVAS = false;
	
	private final SimpleCanvas canvas;
	private final GraphCanvasRenderer renderer;
	
	public CanvasGraphGUIImpl(G g, GraphGUIHelper<G, V, E> helper,
			boolean can_be_saved) {
		super(g, helper, can_be_saved);

		this.canvas = new SimpleCanvas();
		renderer = new GraphCanvasRenderer(g, canvas, getSelection());
	}

	@Override
	public Component getGraphComponent() {
		return canvas;
	}

	@Override
	public void selectionChanged() {
		renderer.updateSelectionCache();
		fireSelectionChange();
	}

	@Override
	public void repaint() {
		canvas.clearOffscreen();
	}

	@Override
	public void refresh(Object o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setZoomLevel(int direction) {
		canvas.zoom(direction);
	}

}
