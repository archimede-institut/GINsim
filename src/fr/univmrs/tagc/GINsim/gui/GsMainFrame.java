package fr.univmrs.tagc.GINsim.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GraphChangeListener;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.tagc.GINsim.graph.GsGraphSelectionChangeEvent;
import fr.univmrs.tagc.GINsim.graph.GsNewGraphEvent;
import fr.univmrs.tagc.GINsim.gui.BaseMainFrame.TabSelection;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsGlassPane;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.datastore.ObjectEditor;
import fr.univmrs.tagc.common.datastore.gui.GenericPropertyEditorPanel;
import fr.univmrs.tagc.common.managerresources.ImageLoader;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.SplitPane;

/**
 * GINsim's main frame
 */
public class GsMainFrame extends BaseMainFrame implements GraphChangeListener {

    private static final long serialVersionUID = -3626877344852342412L;

	private JPanel jPanel1 = null;
	private GsGraphicAttributePanel gsGraphicAttributePanel = null;
    private GsActions gsActions = new GsActions(this);
    private GsGraph graph = null;
    private CardLayout cards = new CardLayout();
    private JPanel emptyPanel = null;

    private JPanel graphParameterPanel = null;
    private ObjectEditor graphEditor = null;

    private ObjectEditor vertexEditor = null;
    private ObjectEditor edgeEditor = null;
    private GsParameterPanel vertexPanel = null;
    private GsParameterPanel edgePanel = null;

	private boolean alwaysForceClose = false;

	private List v_edge;
	private List v_vertex;

	/**
	 * This method initializes a new MainFrame
	 *
	 */
	public GsMainFrame() {
		super("display.mainFrame", 800, 600);
        this.setJMenuBar(gsActions.getMenuBar());

        // doesn't work on mac OSX ?
		this.setIconImage(ImageLoader.getImage("gs1.gif"));
		updateTitle();
		addTab(Translator.getString("STR_tab_selection"), getJPanel1(), true, FLAG_SINGLE);
		addTab(Translator.getString("STR_tab_graphicAttributes"), getGsGraphicAttributePanel(), true, FLAG_SELECTION);

		getEventDispatcher().addGraphChangedListener(this);
		setGlassPane(new GsGlassPane());
		this.setVisible(true);
	}

	@Override
	protected void closeNotification() {
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
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(cards);
		}
		return jPanel1;
	}

        /**
	 * This method initializes gsGraphicAttributePanel
	 *
	 * @return fr.univmrs.tagc.GINsim.gui.GsGraphicAttributePanel
	 */
	private GsGraphicAttributePanel getGsGraphicAttributePanel() {
		if (gsGraphicAttributePanel == null) {
			gsGraphicAttributePanel = new GsGraphicAttributePanel();
			gsGraphicAttributePanel.setMainFrame(this);
		}
		return gsGraphicAttributePanel;
	}
    public void graphChanged(GsNewGraphEvent event) {
        if (event.getNewGraph() == null) {
            close();
            updateTitle();
            gsActions.setDefaults();
            updateGraphNotificationMessage(graph);
            return;
        }
        // stupid default
        int d = -1;
        if (graph != null) {
            d = jSplitPane.getDividerLocation();
            graph.close();
        }
        graph = event.getNewGraph();
        // hack to update selection constraint for the tab after name change
        Object cst = m_tabs.get(jTabbedPane.getTitleAt(0));
        jTabbedPane.setTitleAt(0, Translator.getString(graph.getTabLabel()));
        m_tabs.put(jTabbedPane.getTitleAt(0), cst);
        graph.setMainFrame(this);
        graphScrollPane.setViewportView(graph.getGraphManager().getGraphPanel());
        jPanel1.removeAll();
        jPanel1.add(getEmptyPanel(), "empty");
        edgeEditor = graph.getEdgeEditor();
        if (edgeEditor == null) {
        	edgePanel = graph.getEdgeAttributePanel();
        }
        vertexEditor = graph.getVertexEditor();
        if (vertexEditor == null) {
        	vertexPanel = graph.getVertexAttributePanel();
        }
        jSplitPane1.remove(gsGraphMapPanel);
        gsGraphMapPanel = graph.getGraphManager().getGraphMapPanel(graphScrollPane);
        jSplitPane1.setRightComponent(gsGraphMapPanel);

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
        	addTab(Translator.getString("STR_tab_graphParameter"), graphParameterPanel, true, FLAG_NONE);
        }
        gsActions.setDefaults();

        if (gsGraphMapPanel == null) {
            showMiniMap(false);
        }
        if (edgeEditor != null) {
        	jPanel1.add(new GenericPropertyEditorPanel(edgeEditor), "edge");
        } else if (edgePanel != null) {
        	edgePanel.setMainFrame(this);
            jPanel1.add(edgePanel, "edge");
        } else {
            jPanel1.add(emptyPanel, "edge");
        }
        if (vertexEditor != null) {
        	jPanel1.add(new GenericPropertyEditorPanel(vertexEditor), "vertex");
        } else if (vertexPanel != null) {
            vertexPanel.setMainFrame(this);
            jPanel1.add(vertexPanel, "vertex");
        } else {
            jPanel1.add(emptyPanel, "vertex");
        }

        gsGraphicAttributePanel.setMainFrame(this);
        jSplitPane.setTopComponent(getGraphPanel());

        // replace jSplitPane, only if this is the first graph in this frame
        if (event.getOldGraph() != null) {
            int md = jSplitPane.getHeight()-jTabbedPane.getMinimumSize().height;
            if (d == -1 || md < d) {
                // without the (-5) it's sometimes strange...
                d = md-5;
            }
            jSplitPane.setDividerLocation(d);
        }
        getJSplitPane().setDividerLocation(
                jSplitPane.getHeight()-((Integer)OptionStore.getOption("display.dividersize", new Integer(80))).intValue());

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
    public GsGraph getGraph() {
        return graph;
    }
    /**
     * close the current window
     * this will exit if it is the last window
     */
    public void doClose() {
        doClose(true);
    }
    /**
     * close the window without exiting:
     *    ie close if it's not the last window
     */
    public void close() {
        doClose(alwaysForceClose);
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
     * close the window without exiting:
     *    ie close if it's not the last window
     * @param force
     */
    private void doClose(boolean force) {
        
        // FIXME: graph not always really closed
        // --> GsEnv.m_graphs pas vide
        
    	if (confirmCloseGraph()) {
            if (gsGraphMapPanel.isVisible()) {
                mmapDivLocation = jSplitPane1.getWidth() - jSplitPane1.getDividerLocation();
            }
            OptionStore.setOption("display.minimapsize", new Integer(mmapDivLocation));
            if (secondaryFrame == null) {
                OptionStore.setOption("display.dividersize", new Integer(jSplitPane.getHeight()-jSplitPane.getDividerLocation()));
            }
		    if (force || GsEnv.getNbFrame() > 1) {
		        GsEnv.delFrame(this);
		        dispose();
		    } else {
		        GsEnv.newGraph(this);
		    }
    	}
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
                cards.show(jPanel1, "edge");
                if (edgeEditor != null) {
                	edgeEditor.setEditedObject(v_edge.get(0));
                } else if (edgePanel != null) {
                    edgePanel.setEditedObject(v_edge.get(0));
                }
                gsGraphicAttributePanel.setEditedObject(v_edge.get(0));
                updateTabs(TAB_SINGLE);
            } else {
                cards.show(jPanel1, "empty");
                gsGraphicAttributePanel.setEditedObject(v_edge);
                updateTabs(TAB_MULTIPLE);
            }
        } else if (event.getNbEdge() == 0 && event.getNbVertex() > 0) {
            if (event.getNbVertex() == 1) {
                cards.show(jPanel1, "vertex");
                if (vertexEditor != null) {
                	vertexEditor.setEditedObject(v_vertex.get(0));
                } else if (vertexPanel != null) {
                    vertexPanel.setEditedObject(v_vertex.get(0));
                }
                gsGraphicAttributePanel.setEditedObject(v_vertex.get(0));
                updateTabs(TAB_SINGLE);
            } else {
                cards.show(jPanel1, "empty");
                gsGraphicAttributePanel.setEditedObject(v_vertex);
                updateTabs(TAB_MULTIPLE);
            }
        } else {
            cards.show(jPanel1, "empty");
            gsGraphicAttributePanel.setEditedObject(null);
            updateTabs(TAB_NONE);
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
    /**
     * @return this frame's action manager.
     */
    public GsActions getGsAction() {
    	return gsActions;
    }

    protected void error (GsException e) {
        graph.addNotificationMessage(new GsGraphNotificationMessage(graph, e));
    }

    public void graphClosed(GsGraph graph) {
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
	public GsActions getActions() {
		return gsActions;
	}
	
	@Override
	public GsGraphNotificationMessage getTopNotification() {
		if (graph != null) {
			return graph.getTopMessage();
		}
		return null;
	}
   }
