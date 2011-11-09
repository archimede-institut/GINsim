package fr.univmrs.tagc.GINsim.gui;

import java.awt.CardLayout;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.global.GsEventDispatcher;
import fr.univmrs.tagc.GINsim.graph.GraphChangeListener;
import fr.univmrs.tagc.GINsim.graph.GsGraphSelectionChangeEvent;
import fr.univmrs.tagc.GINsim.graph.GsNewGraphEvent;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsGlassPane;
import fr.univmrs.tagc.common.datastore.ObjectEditor;
import fr.univmrs.tagc.common.datastore.gui.GenericPropertyEditorPanel;
import fr.univmrs.tagc.common.managerresources.ImageLoader;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * GINsim's main frame
 */
public class GsMainFrame extends BaseMainFrame implements GraphChangeListener {

    private static final long serialVersionUID = -3626877344852342412L;

	private GsEventDispatcher eventDispatcher = new GsEventDispatcher(true);
    
	private JPanel selectionEditPanel = null;
	private GsGraphicAttributePanel graphicAttributePanel = null;
	
	
    private GsActions gsActions = new GsActions(this);
    private Graph<?,?> graph = null;
    private CardLayout cards = new CardLayout();
    private JPanel emptyPanel = null;

    private JPanel graphParameterPanel = null;
    private ObjectEditor graphEditor = null;

    private ObjectEditor vertexEditor = null;
    private ObjectEditor edgeEditor = null;
    private GsParameterPanel vertexPanel = null;
    private GsParameterPanel edgePanel = null;

	private List v_edge;
	private List v_vertex;

	/**
	 * This method initializes a new MainFrame
	 */
	public GsMainFrame() {
		super("display.mainFrame", 800, 600);

		init();
		
        // doesn't work on mac OSX ?
		this.setIconImage(ImageLoader.getImage("gs1.gif"));
		updateTitle();
		addTab(Translator.getString("STR_tab_selection"), getJPanel1(), true, TabSelection.TAB_SINGLE.flag);
		addTab(Translator.getString("STR_tab_graphicAttributes"), getGsGraphicAttributePanel(), true, TabSelection.TAB_SINGLE.flag | TabSelection.TAB_MULTIPLE.flag);

		getEventDispatcher().addGraphChangedListener(this);
		setGlassPane(new GsGlassPane());
		this.setVisible(true);
	}

	@Override
	public void closeNotification() {
		graph.deleteAllNotificationMessage();
	}

	@Override
	protected TabSelection getCurrentSelectionType() {
        int nb_edges = v_edge == null ? 0 : v_edge.size();
        int nb_vertices = v_vertex == null ? 0 : v_vertex.size();
        if (nb_edges == 0) {
            switch (nb_vertices) {
                case 0:
                    return TabSelection.TAB_NONE;
                case 1:
                    return TabSelection.TAB_SINGLE;
                default:
                	return TabSelection.TAB_MULTIPLE;
            }
        }
        
        if (nb_vertices == 0) {
            if (nb_edges == 1) {
            	return TabSelection.TAB_SINGLE;
            }
            return TabSelection.TAB_MULTIPLE;
        }
        
        return TabSelection.TAB_MULTIPLE;
	}
	
    /**
     * @return this mainFrame's event dispatcher
     */
    public GsEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (selectionEditPanel == null) {
			selectionEditPanel = new JPanel();
			selectionEditPanel.setLayout(cards);
		}
		return selectionEditPanel;
	}

	/**
	 * This method initializes gsGraphicAttributePanel
	 *
	 * @return fr.univmrs.tagc.GINsim.gui.GsGraphicAttributePanel
	 */
	private GsGraphicAttributePanel getGsGraphicAttributePanel() {
		if (graphicAttributePanel == null) {
			graphicAttributePanel = new GsGraphicAttributePanel();
			graphicAttributePanel.setMainFrame(this);
		}
		return graphicAttributePanel;
	}
	
    public void graphChanged(GsNewGraphEvent event) {
        if (event.getNewGraph() == null) {
            close();
            updateTitle();
            gsActions.setDefaults();
            updateGraphNotificationMessage(graph);
            return;
        }
        // FIXME: restore memory for divider location
//        // stupid default
//        int d = -1;
        if (graph != null) {
//            d = jSplitPane.getDividerLocation();
            graph.close();
        }
        graph = event.getNewGraph();
        // hack to update selection constraint for the tab after name change
        setTabLabel(Translator.getString(graph.getTabLabel()));
   
        graph.setMainFrame(this);
        setGraphView( graph.getGraphManager().getGraphPanel());
        selectionEditPanel.removeAll();
        selectionEditPanel.add(getEmptyPanel(), "empty");
        edgeEditor = graph.getEdgeEditor();
        if (edgeEditor == null) {
        	edgePanel = graph.getEdgeAttributePanel();
        }
        vertexEditor = graph.getVertexEditor();
        if (vertexEditor == null) {
        	vertexPanel = graph.getVertexAttributePanel();
        }
        setMapPanel(graph.getGraphManager().getGraphMapPanel(getGraphScrollPane()));

        if (graphParameterPanel != null) {
        	removeTab(Translator.getString("STR_tab_graphParameter"));
        }
        graphEditor = graph.getGraphEditor();
        if (graphEditor != null) {
        	graphParameterPanel = new GenericPropertyEditorPanel(graphEditor);
        	graphEditor.refresh(true);
        } else {
        	graphParameterPanel = graph.getGraphParameterPanel();
        }
        if (graphParameterPanel != null) {
        	addTab(Translator.getString("STR_tab_graphParameter"), graphParameterPanel, true, TabSelection.TAB_NONE.flag);
        }
        gsActions.setDefaults();

        if (edgeEditor != null) {
        	selectionEditPanel.add(new GenericPropertyEditorPanel(edgeEditor), "edge");
        } else if (edgePanel != null) {
        	edgePanel.setMainFrame(this);
            selectionEditPanel.add(edgePanel, "edge");
        } else {
            selectionEditPanel.add(emptyPanel, "edge");
        }
        if (vertexEditor != null) {
        	selectionEditPanel.add(new GenericPropertyEditorPanel(vertexEditor), "vertex");
        } else if (vertexPanel != null) {
            vertexPanel.setMainFrame(this);
            selectionEditPanel.add(vertexPanel, "vertex");
        } else {
            selectionEditPanel.add(emptyPanel, "vertex");
        }

        graphicAttributePanel.setMainFrame(this);
        
        loadGraphPanel();

        updateTitle();
        updateGraphNotificationMessage(graph);
        updateTabs(TabSelection.TAB_CHECK);
    }

	/**
     * @return an empty jPanel (to be displayed when nothing is selected or when no parameter panel is available)
     */
    private JPanel getEmptyPanel() {
        if (emptyPanel == null) {
            emptyPanel = new JPanel();
        }
        return emptyPanel;
    }
    /**
     * @return the graph currently associated with this frame
     */
    public Graph getGraph() {
    	
        return graph;
    }

    /**
     * confirm closing the current graph (to close the window or open another graph instead).
     * WARNING: if this confirms that the graph can be closed, it will be already closed, don't call it
     * just to see.
     *
     * @return true if the graph can be closed
     */
    public boolean confirmCloseGraph() {
        if (!graph.canClose()) {
            return false;
        }
        if (!graph.isSaved()) {
            // this _should_ avoid FUD when showing the save asking dialog but does it work at all ?
            this.toFront();
            String s_add = graph.getSaveFileName();
            if (s_add == null) {
                s_add = "unnamed";
            } else {
                s_add = s_add.substring(s_add.lastIndexOf(File.separator)+1);
            }
            
            int aw = JOptionPane.showConfirmDialog(this, Translator.getString("STR_saveQuestion1")+ s_add +Translator.getString("STR_saveQuestion2"),
                    Translator.getString("STR_closeConfirm"),
                    JOptionPane.YES_NO_CANCEL_OPTION);
            switch (aw) {
                case JOptionPane.CANCEL_OPTION:
                    return false;
                case JOptionPane.YES_OPTION:
                    try {
                        graph.save();
                    } catch (GsException e) {
                        GsEnv.error(e, this);
                        return false;
                    }
            }
        }
        return true;
    }

    /**
     * @see fr.univmrs.tagc.GINsim.graph.GraphChangeListener#graphSelectionChanged(fr.univmrs.tagc.GINsim.graph.GsGraphSelectionChangeEvent)
     *
     * tons of tests to activate/select the right tab depending on the context.
     * overview:
     *   - if nothing is selected (ie the background): edit graph properties if available
     *   - if ONE SINGLE node or edge is selected: edit this object's properties or graphical settings (let the user choose)
     *   - if several items of the same type are selected: edit graphical settings
     *   - if the selection is heterogeneous: don't edit anything
     *   - to make it funnier: always come back to the right tab when only ONE item is selected
     */
    public void graphSelectionChanged(GsGraphSelectionChangeEvent event) {
        v_edge = event.getV_edge();
        v_vertex = event.getV_vertex();

        if (event.getNbEdge() > 0 && event.getNbVertex() == 0) {
            // if multi-selection: force it on the graphic attribute panel otherwise let it free
            if (event.getNbEdge() == 1) {
                cards.show(selectionEditPanel, "edge");
                if (edgeEditor != null) {
                	edgeEditor.setEditedObject(v_edge.get(0));
                } else if (edgePanel != null) {
                    edgePanel.setEditedObject(v_edge.get(0));
                }
                graphicAttributePanel.setEditedObject(v_edge.get(0));
                updateTabs(TabSelection.TAB_SINGLE);
            } else {
                cards.show(selectionEditPanel, "empty");
                graphicAttributePanel.setEditedObject(v_edge);
                updateTabs(TabSelection.TAB_MULTIPLE);
            }
        } else if (event.getNbEdge() == 0 && event.getNbVertex() > 0) {
            if (event.getNbVertex() == 1) {
                cards.show(selectionEditPanel, "vertex");
                if (vertexEditor != null) {
                	vertexEditor.setEditedObject(v_vertex.get(0));
                } else if (vertexPanel != null) {
                    vertexPanel.setEditedObject(v_vertex.get(0));
                }
                graphicAttributePanel.setEditedObject(v_vertex.get(0));
                updateTabs(TabSelection.TAB_SINGLE);
            } else {
                cards.show(selectionEditPanel, "empty");
                graphicAttributePanel.setEditedObject(v_vertex);
                updateTabs(TabSelection.TAB_MULTIPLE);
            }
        } else {
            cards.show(selectionEditPanel, "empty");
            graphicAttributePanel.setEditedObject(null);
            updateTabs(TabSelection.TAB_NONE);
        }
    }

    /**
     * @return the list of currently selected edges
     */
    public List getSelectedEdges() {
    	return v_edge;
    }

    /**
     * @return the list of currently selected vertices
     */
    public List getSelectedVertices() {
    	return v_vertex;
    }
    
    @Override
    public GsActions getActions() {
    	return gsActions;
    }

    protected void error (GsException e) {
        graph.addNotificationMessage(new NotificationMessage(graph, e));
    }

    public void graphClosed(Graph graph) {
    }

    /**
     * refresh the title of the frame.
     * call it to mark it as (un)saved, update the filename...
     */
    public void updateTitle() {
        if (graph != null) {
	        String s_add = graph.getSaveFileName();
	        if (s_add == null) {
	            s_add = " (unnamed)";
	        } else {
	            s_add = s_add.substring(s_add.lastIndexOf(File.separator)+1);
	        }
	        if (!graph.isSaved()) {
	            s_add += " *";
	        }
	        setTitle("GINsim - "+s_add);
        } else {
            setTitle("GINsim");
        }
    }
	
	@Override
	public NotificationMessage getTopNotification() {
		if (graph != null) {
			return graph.getTopMessage();
		}
		return null;
	}

	@Override
	public void updateGraphNotificationMessage( Graph graph) {
		if (graph == this.graph) {
			super.updateNotificationMessage();
		}
	}
   }
