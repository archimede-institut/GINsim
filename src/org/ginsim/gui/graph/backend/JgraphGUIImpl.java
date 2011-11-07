package org.ginsim.gui.graph.backend;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;
import org.ginsim.graph.backend.GraphViewBackend;
import org.ginsim.graph.backend.JgraphtBackendImpl;
import org.ginsim.gui.graph.EditActionManager;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.helper.GraphGUIHelper;
import org.ginsim.gui.shell.FrameActionManager;
import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraphSelectionChangeEvent;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraph;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphEdgeAttribute;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphVertexAttribute;

public class JgraphGUIImpl<G extends Graph<V,E>, V, E extends Edge<V>> implements GraphGUI<G,V, E>, GraphViewBackend {

	private final G graph;
	private final JgraphtBackendImpl<V, E> backend;
    private JGraphModelAdapter<V,E> m_jgAdapter;
    private GsJgraph jgraph;
    private final GraphGUIHelper<G,V,E> helper;
    private final EditActionManager editActionManager;

    Collection<E> sel_edges;
    Collection<V> sel_vertices;
    
	public JgraphGUIImpl(G g, JgraphtBackendImpl<V, E> backend, GraphGUIHelper<G,V,E> helper) {
		this.graph = g;
		this.backend = backend;
		this.m_jgAdapter = new JGraphModelAdapter<V, E>(backend);
		this.jgraph = new GsJgraph(m_jgAdapter);
		jgraph.setEdgeLabelDisplayed(false);
		this.helper = helper;
		backend.setGraphViewBackend(this);
		
		// create the action manager and marquee handler
		editActionManager = new EditActionManager(helper.getEditActions(graph));
		new MarqueeHandler(this);
	}
	
	public JGraph getJGraph() {
		return jgraph;
	}
	
	@Override
	public Component getGraphComponent() {
		return jgraph;
	}
	
	@Override
	public GsEdgeAttributesReader getEdgeAttributeReader() {
		return new GsJgraphEdgeAttribute(backend, m_jgAdapter, null);
	}

	@Override
	public GsVertexAttributesReader getVertexAttributeReader() {
		return new GsJgraphVertexAttribute(m_jgAdapter, null);
	}

	/**
	 * Change a Boolean property
	 * 
	 * @param property
	 * @param b
	 */
	protected void setProperty(GUIProperties property, boolean b) {
		switch (property) {
		case GRID:
			// FIXME: Grid visibility flag does not work
			jgraph.setGridVisible(b);
			break;
		case GRIDACTIVE:
			jgraph.setGridEnabled(b);
			break;
		}
	}

	/**
	 * Get the state of a Boolean property.
	 * 
	 * @param property
	 * @return true if this property is enabled
	 */
	protected boolean hasProperty(GUIProperties property) {
		switch (property) {
		case GRID:
			return jgraph.isGridVisible();
		case GRIDACTIVE:
			return jgraph.isGridEnabled();
		}
		return false;
	}

	/**
	 * Change the zoom level.
	 * 
	 * @param direction: zoom in if positive, out if negative and reset if 0
	 */
    protected void setZoomLevel(int direction) {
    	if (direction > 0) {
            jgraph.setScale(jgraph.getScale()+0.1);
    	} else if (direction < 0) {
    		jgraph.setScale(jgraph.getScale()-0.1);
    	} else {
    		jgraph.setScale(1);
    	}
    }

	@Override
	public JMenu getViewMenu(JMenu layout) {
		JMenu menu = new JMenu("View");
		
		menu.add(layout);

		menu.add(new ZoomAction(this, -1));
		menu.add(new ZoomAction(this, +1));
		menu.add(new ZoomAction(this, 0));
		
		menu.add(new JSeparator());
		
		for (GUIProperties property: GUIProperties.values()) {
			menu.add(new PropertySwitchAction(this, property));
		}			
		
		return menu;
	}

	@Override
	public Collection<V> getSelectedVertices() {
		return sel_vertices;
	}

	@Override
	public Collection<E> getSelectedEdges() {
		return sel_edges;
	}

	public void SelectionChanged(GsGraphSelectionChangeEvent event) {
		sel_edges = event.getV_edge();
		sel_vertices = event.getV_vertex();
		// TODO propagate selection change event
	}

	@Override
	public Graph<V, E> getGraph() {
		return graph;
	}

	@Override
	public GUIEditor<G> getMainEditionPanel() {
		return helper.getMainEditionPanel(graph);
	}

	@Override
	public String getEditingTabLabel() {
		return helper.getEditingTabLabel(graph);
	}

	@Override
	public GUIEditor<V> getNodeEditionPanel() {
		return helper.getNodeEditionPanel(graph);
	}

	@Override
	public GUIEditor<E> getEdgeEditionPanel() {
		return helper.getEdgeEditionPanel( graph);
	}

	@Override
	public JPanel getInfoPanel() {
		return helper.getInfoPanel( graph);
	}

	@Override
	public EditActionManager getEditActionManager() {
		return editActionManager;
	}
}

enum GUIProperties {
	
	GRID("Grid Visible"),
	GRIDACTIVE("Grid Active");
	
	public final String name;
	
	private GUIProperties(String name) {
		this.name = name;
	}
}


class PropertySwitchAction extends AbstractAction {

	private final JgraphGUIImpl<?, ?, ?> gui;
	private final GUIProperties property;
	
	public PropertySwitchAction(JgraphGUIImpl<?, ?, ?> gui, GUIProperties property) {
		super(property.name);
		this.gui = gui;
		this.property = property;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.setProperty(property, !gui.hasProperty(property));
	}
}

class ZoomAction extends AbstractAction {

	private final JgraphGUIImpl<?, ?, ?> gui;
	private final int direction;
	
	public ZoomAction(JgraphGUIImpl<?, ?, ?> gui, int direction) {
		this.gui = gui;
		this.direction = direction;
		
		if (direction < 0) {
			putValue(NAME, "Zoom out");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, FrameActionManager.MASK));
		} else if (direction > 0) {
			putValue(NAME, "Zoom in");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, FrameActionManager.MASK));
		} else {
			putValue(NAME, "Reset zoom level");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, FrameActionManager.MASK));
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.setZoomLevel(direction);
	}
}
