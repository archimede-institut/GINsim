package org.ginsim.gui.graph.backend;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.ginsim.common.OptionStore;
import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.backend.GraphViewListener;
import org.ginsim.core.graph.backend.JgraphtBackendImpl;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.EditActionManager;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphGUIHelper;
import org.ginsim.gui.graph.GraphGUIListener;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.shell.FrameActionManager;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.widgets.Frame;
import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgrapht.ext.JGraphModelAdapter;



public class JgraphGUIImpl<G extends Graph<V,E>, V, E extends Edge<V>> implements GraphGUI<G,V, E>, GraphSelectionListener, GraphViewListener {

	private final G graph;
	private final JgraphtBackendImpl<V, E> backend;
    private JGraphModelAdapter<V,E> m_jgAdapter;
    private GsJgraph jgraph;
    private final GraphGUIHelper<G,V,E> helper;
    private final EditActionManager editActionManager;
    
    private final GraphSelection<V, E> selection = new GraphSelection<V, E>(this);
    
    private final List<GraphGUIListener<G, V, E>> listeners = new ArrayList<GraphGUIListener<G,V,E>>();
    
    // saving memory
    // FIXME: listen for graph changes and set it as false when needed
    private boolean isSaved = false;
    
	public JgraphGUIImpl(G g, JgraphtBackendImpl<V, E> backend, GraphGUIHelper<G,V,E> helper) {
		this.graph = g;
		this.backend = backend;
		this.m_jgAdapter = new JGraphModelAdapter<V, E>(backend);
		this.jgraph = new GsJgraph(m_jgAdapter, g);
		
		jgraph.setEdgeLabelDisplayed(false);
		jgraph.addGraphSelectionListener(this);
		this.helper = helper;
		g.addViewListener(this);
		//backend.setGraphViewBackend(this);
		
		// create the action manager and marquee handler
		editActionManager = new EditActionManager(helper.getEditActions(graph));
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
	
	@Override
	public boolean isSaved() {
		return isSaved;
	}
	
	@Override
	public void setSaved( boolean isSaved) {
		
		this.isSaved = isSaved;
		Frame main_frame = GUIManager.getInstance().getFrame( graph);
		if( main_frame != null){
			main_frame.setFrameTitle( graph, isSaved);
		}
	}

	@Override
	public boolean save() {
		
		String savePath = GraphManager.getInstance().getGraphPath( graph);
		
		if (savePath == null) {
			isSaved = false;
			saveAs();
			return isSaved();
		}
		
		try {
			graph.save( savePath);
			Frame main_frame = GUIManager.getInstance().getFrame( graph);
			if( main_frame != null){
				main_frame.setFrameTitle( graph, true);
			}
			OptionStore.addRecentFile(savePath);
			isSaved = true;
			return true;
		} catch (Exception e) {
			GUIMessageUtils.openErrorDialog( "Unable to save file. See logs for more details");
			LogManager.error( "Unable to save file : " + savePath);
			LogManager.error( e);
		}
		return false;
		
	}

	@Override
	public boolean saveAs() {
		Frame frame = GUIManager.getInstance().getFrame(graph);
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] { "zginml" }, "GINsim files");
		String filename = FileSelectionHelper.selectSaveFilename(frame, ffilter);
		if (filename != null) {
			String graph_name = (new File( filename)).getName();
			int dot_index = graph_name.indexOf( ".");
			if( dot_index > 0){
				graph_name = graph_name.substring( 0, dot_index);
			}
			try {
				graph.setGraphName( graph_name);
				Frame main_frame = GUIManager.getInstance().getFrame( graph);
				if( main_frame != null){
					main_frame.setFrameTitle( graph, true);
				}
			} catch (GsException gse) {
				LogManager.error( "Unable to set graph name: " + graph_name);
				LogManager.error( gse);
			}
			GraphManager.getInstance().registerGraph( graph, filename);
			return save();
		}
		
		return false;
	}

	@Override
	public void fireGraphClose() {
		for (GraphGUIListener<G, V, E> listener: listeners) {
			listener.graphGUIClosed(this);
		}
	}

	@Override
	public void addGraphGUIListener(GraphGUIListener<G, V, E> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeGraphGUIListener(GraphGUIListener<G, V, E> listener) {
		listeners.remove(listener);
	}

	@Override
	public boolean isEditAllowed() {
		
		return GUIManager.getInstance().isEditAllowed( graph);
	}

	@Override
	public void selectNode(V vertex) {
		jgraph.setSelectionCell(m_jgAdapter.getVertexCell(vertex));
	}
	@Override
	public void selectEdge(E edge) {
		jgraph.setSelectionCell(m_jgAdapter.getVertexCell(edge));
	}

    public void selectAll() {
        jgraph.setSelectionCells(jgraph.getRoots());
    }

    public void select(Collection<?> l) {
        jgraph.setSelectionCells( new Object[0]);
        addSelection(l);
    }
    
    public void addSelection(Collection<?> l) {
        if (l == null) {
            return;
        }
        for (Object o: l) {
            if (o instanceof Edge) {
                jgraph.addSelectionCell(m_jgAdapter.getEdgeCell((E)o));
            } else {
                jgraph.addSelectionCell(m_jgAdapter.getVertexCell((V)o));
            }
        }
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

    // TODO : defined in GraphGUI. Move the code to JgraphGUIImpl
    public void invertSelection() {
		Object[] selects = jgraph.getSelectionCells();
		Object roots[] = jgraph.getRoots();
		int len = roots.length;
		int nbsel = selects.length;
		Vector toselect = new Vector(len - nbsel);
		for (int i=0 ; i<len ; i++) {
			toselect.add(roots[i]);
		}
		
		for (int i=len-1 ; i>=0 ; i--) {
			Object cur = roots[i];
			for (int j=0 ; j<nbsel ; j++) {
				if (selects[j] == cur) {
					toselect.remove(i);
					break;
				}
			}
		}
		jgraph.setSelectionCells(toselect.toArray());

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

		selection.setSelection(nodes, edges);
		for (GraphGUIListener<G, V, E> listener: listeners) {
			listener.graphSelectionChanged(this);
		}
	}

	@Override
	public void selectionChanged() {
		// TODO: update jgraph selection based on user request
	}

	@Override
	public GraphSelection<V, E> getSelection() {
		return selection;
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
