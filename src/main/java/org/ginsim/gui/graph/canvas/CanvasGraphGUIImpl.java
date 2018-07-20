package org.ginsim.gui.graph.canvas;

import java.awt.Component;

import javax.swing.JMenu;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.gui.graph.BaseGraphGUI;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.ZoomEffect;

public class CanvasGraphGUIImpl<G extends Graph<V,E>, V, E extends Edge<V>> extends BaseGraphGUI<G, V, E> {

	private final SimpleCanvas canvas;
	private final VirtualScrollPane canvasPanel;
	private final GraphCanvasRenderer renderer;
	
	public CanvasGraphGUIImpl(G g, GraphGUIHelper<G, V, E> helper,
			boolean can_be_saved) {
		super(g, helper, can_be_saved);

		this.canvas = new SimpleCanvas();
		renderer = new GraphCanvasRenderer(g, canvas, getSelection(), getEditActionManager());
		this.canvasPanel = new VirtualScrollPane(canvas);
	}

	@Override
	public Component getGraphComponent() {
		return canvasPanel;
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
		if (o == null) {
			canvas.clearOffscreen();
		}
		canvas.repaint();
	}

	@Override
	protected void setZoomLevel(ZoomEffect effect) {
		canvas.zoom(effect);
	}

    public double getZoomLevel() {
        return canvas.getZoomLevel();
    }

	@Override
	public JMenu getViewMenu(JMenu layout) {
		JMenu menu = super.getViewMenu(layout);
		menu.add(new CanvasHelpAction(canvas));
		return menu;
	}
}
