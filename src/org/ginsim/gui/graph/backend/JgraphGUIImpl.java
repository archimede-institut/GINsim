package org.ginsim.gui.graph.backend;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.backend.JgraphtBackendImpl;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.graph.BaseGraphGUI;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgrapht.ext.JGraphModelAdapter;



public class JgraphGUIImpl<G extends Graph<V,E>, V, E extends Edge<V>> extends BaseGraphGUI<G, V, E>
       implements GraphSelectionListener {

    private JGraphModelAdapter<V,E> m_jgAdapter;
    private GsJgraph jgraph;
    
	public JgraphGUIImpl(G g, JgraphtBackendImpl<V, E> backend, GraphGUIHelper<G,V,E> helper, boolean can_be_saved) {

		super(g, helper, can_be_saved);
		
		this.m_jgAdapter = new JGraphModelAdapter<V, E>(backend);
		this.jgraph = new GsJgraph(m_jgAdapter, g);
		
		jgraph.setEdgeLabelDisplayed(false);
		jgraph.addGraphSelectionListener(this);
		g.addViewListener(this);
		
		new MarqueeHandler(this);
		vertexToFront(true);
	}
	
	public JGraph getJGraph() {
		return jgraph;
	}
	
	@Override
	public Component getGraphComponent() {
		return jgraph;
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
			jgraph.setGridVisible(b);
			break;
		case GRIDACTIVE:
			jgraph.setGridEnabled(b);
			break;
		case VERTEXFRONT:
			vertexToFront(b);
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
		JMenu menu = super.getViewMenu(layout);
		
		menu.add(new JSeparator());
		
		for (GUIProperties property: GUIProperties.values()) {
			menu.add(new PropertySwitchAction(this, property));
		}			
		
		return menu;
	}

	

    public void vertexToFront(boolean b) {
        // move all vertex to front;
        Object[] t = new Object[graph.getNodeCount()];
        int i=0;
        for (V node: graph.getNodes()) {
            t[i++] = m_jgAdapter.getVertexCell(node);
        }
        if (b) {
            m_jgAdapter.toFront(t);
        } else {
            m_jgAdapter.toBack(t);
        }
    }

	@Override
	public void valueChanged(GraphSelectionEvent event) {
		List<E> edges = new ArrayList<E>();
		List<V> nodes = new ArrayList<V>();
		
		for (Object o: jgraph.getSelectionCells()) {
			if (o instanceof DefaultEdge) {
				edges.add((E)((DefaultEdge)o).getUserObject());
			} else if (o instanceof DefaultGraphCell) {
				nodes.add((V)((DefaultGraphCell)o).getUserObject());
			} else {
				LogManager.error("Could not detect the selection: " + o);
			}
		}

		updateSelection(nodes, edges);
	}

	@Override
	public void selectionChanged() {
		List<Object> new_selection = new ArrayList<Object>();
		List<V> nodes = selection.getSelectedNodes();
		if (nodes != null) {
			for (V n: nodes) {
				new_selection.add(m_jgAdapter.getVertexCell(n));
			}
		}
		List<E> edges = selection.getSelectedEdges();
		if (edges != null) {
			for (E e: edges) {
				new_selection.add(m_jgAdapter.getEdgeCell(e));
			}
		}
		jgraph.setSelectionCells(new_selection.toArray());
		updateSelection(nodes, edges);
	}

	@Override
	public void refresh(Object o) {
		Object cell = null;
		if (o instanceof Edge) {
			cell = m_jgAdapter.getEdgeCell((E)o);
		} else {
			cell = m_jgAdapter.getVertexCell(o);
		}
		
		if (cell != null) {
			m_jgAdapter.cellsChanged(new Object[] {cell});
		}
	}

	@Override
	public void repaint() {
		jgraph.clearOffscreen();
	}
}

enum GUIProperties {
	
	GRID("Grid Visible"),
	GRIDACTIVE("Grid Active"),
	VERTEXFRONT("Vertex to Front");
	
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
