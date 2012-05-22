package org.ginsim.gui.graph;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.OptionStore;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.backend.GraphViewListener;
import org.ginsim.core.graph.backend.JgraphtBackendImpl;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.backend.GsJgraph;
import org.ginsim.gui.graph.backend.JgraphGUIImpl;
import org.ginsim.gui.graph.backend.MarqueeHandler;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.shell.FrameActionManager;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.widgets.Frame;

/**
 * Abstract class providing generic parts of a Graph GUI.
 * 
 * @author Aurelien Naldi
 *
 * @param <G>
 * @param <V>
 * @param <E>
 */
public abstract class BaseGraphGUI<G extends Graph<V,E>, V, E extends Edge<V>>
       implements GraphGUI<G, V, E>, GraphViewListener, GraphListener<G> {

	private final GraphGUIHelper<G,V,E> helper;
	private final EditActionManager editActionManager;
    private final List<GraphGUIListener<G, V, E>> listeners = new ArrayList<GraphGUIListener<G,V,E>>();
    
	protected final G graph;
    protected final GraphSelection<V, E> selection = new GraphSelection<V, E>(this);
    
    private boolean isSaved = true;
    private boolean canBeSaved = true;

	public BaseGraphGUI(G g, GraphGUIHelper<G,V,E> helper, boolean can_be_saved) {
		
		this.graph = g;
		this.canBeSaved = can_be_saved;
		this.helper = helper;
		
		editActionManager = new EditActionManager(helper.getEditActions(graph));
		GraphManager.getInstance().addGraphListener(g, this);
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
	public JMenu getViewMenu(JMenu layout) {
		JMenu menu = new JMenu("View");
		
		menu.add(layout);

		menu.add(getZoomAction(this, -1));
		menu.add(getZoomAction(this, +1));
		menu.add(getZoomAction(this, 0));

		return menu;
	}

	@Override
	public boolean isSaved() {
		return isSaved;
	}

	@Override
	public boolean canBeSaved(){
		return canBeSaved;
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
		
		if( ! canBeSaved){
			GUIMessageUtils.openErrorDialog( "STR_graphTypeCannotBeSaved");
			return false;
		}
		
		String savePath = GraphManager.getInstance().getGraphPath( graph);
		
		if (savePath == null) {
			isSaved = false;
			saveAs();
			return isSaved();
		}
		
		try {
			graph.save( savePath);
			graphChanged(graph, GraphChangeType.GRAPHSAVED, null);
			OptionStore.addRecentFile(savePath);
			isSaved = true;
			return true;
		} catch (Exception e) {
			GUIMessageUtils.openErrorDialog( "STR_unableToOpen_SeeLogs");
			LogManager.error( "Unable to save file : " + savePath);
			LogManager.error( e);
		}
		return false;
		
	}

	@Override
	public boolean saveAs() {
		
		if( ! canBeSaved){
			GUIMessageUtils.openErrorDialog( "STR_graphTypeCannotBeSaved");
			return false;
		}
		
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
	public GraphSelection<V, E> getSelection() {
		return selection;
	}

	protected void updateSelection(List<V> nodes, List<E> edges) {
		selection.backendSelectionUpdated(nodes, edges);
		fireSelectionChange();
	}

	protected void fireSelectionChange() {
		for (GraphGUIListener<G, V, E> listener: listeners) {
			listener.graphSelectionChanged(this);
		}
	}

	
	@Override
	public GraphEventCascade graphChanged(G g, GraphChangeType type, Object data) {
		for (GraphGUIListener<G, V, E> listener: listeners) {
			listener.graphChanged(g, type, data);
		}
		return null;
	}

    protected abstract void setZoomLevel(int direction);

    protected Action getZoomAction(BaseGraphGUI<?, ?, ?> gui, int direction) {
    	return new ZoomAction(gui, direction);
    }
}

class ZoomAction extends AbstractAction {
	private static final long serialVersionUID = 8027606268716590825L;
	
	private final BaseGraphGUI<?, ?, ?> gui;
	private final int direction;
	
	public ZoomAction(BaseGraphGUI<?, ?, ?> gui, int direction) {
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
