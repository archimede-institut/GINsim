package fr.univmrs.ibdm.GINsim.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.*;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsEventDispatcher;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.global.GsOptions;
import fr.univmrs.ibdm.GINsim.graph.*;
import fr.univmrs.ibdm.GINsim.manageressources.ImageLoader;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsGlassPane;
import fr.univmrs.ibdm.GINsim.util.widget.GsFrame;
import fr.univmrs.tagc.datastore.ObjectEditor;
import fr.univmrs.tagc.datastore.gui.GenericPropertyEditorPanel;
import fr.univmrs.tagc.widgets.SplitPane;

/**
 * GINsim's main frame
 */
public class GsMainFrame extends GsFrame implements GraphChangeListener {

    private static final long serialVersionUID = -3626877344852342412L;

    private JDialog secondaryFrame = null;
	private JPanel jPanel = null;
	private JSplitPane jSplitPane = null;
	private JScrollPane graphScrollPane = null;
	private JPanel graphPanel = null;
	private JSplitPane jSplitPane1 = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel gsGraphMapPanel = null;
	private JPanel jPanel1 = null;
	private GsGraphicAttributePanel gsGraphicAttributePanel = null;
	private GsEventDispatcher eventDispatcher = new GsEventDispatcher(true);
    GsActions gsActions = new GsActions(this);
    private GsGraph graph = null;
    private CardLayout cards = new CardLayout();
    private JPanel emptyPanel = null;

    private JPanel graphParameterPanel = null;
    private ObjectEditor graphEditor = null;

    private JPanel notificationPanel = null;
    private JLabel notificationMessage = null;
    private JButton bcloseNotification = null;
    private JComboBox cNotificationAction = null;
    private JButton bNotificationAction = null;
    private JButton bNotificationAction2 = null;
    private GsGraphNotificationMessage notification = null;

    private ObjectEditor vertexEditor = null;
    private ObjectEditor edgeEditor = null;
    private GsParameterPanel vertexPanel = null;
    private GsParameterPanel edgePanel = null;

    private int selectedPanel = 0;

	private boolean alwaysForceClose = false;

	private Vector v_edge;
	private Vector v_vertex;
    private int mmapDivLocation = ((Integer)GsOptions.getOption("display.minimapSize", new Integer(100))).intValue();

	/**
	 * This method initializes a new MainFrame
	 *
	 */
	public GsMainFrame() {
		super("display.mainFrame", 800, 600);
		initialize();
		eventDispatcher.addGraphChangedListener(this);

    setGlassPane(new GsGlassPane());
	}
	/**
	 * This method initializes this
	 */
	private void initialize() {
        this.setJMenuBar(gsActions.getMenuBar());
        this.setContentPane(getJPanel());

        // doesn't work on mac OSX ?
		this.setIconImage(ImageLoader.getImage("gs1.gif"));
		updateTitle();
		this.setVisible(true);
	}
	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());

            GridBagConstraints c_toolbar = new GridBagConstraints();
            GridBagConstraints c_split = new GridBagConstraints();

            c_toolbar.gridx = 0;
            c_toolbar.gridy = 0;
            c_toolbar.weightx = 1;
            c_toolbar.weighty = 0;
            c_toolbar.fill = GridBagConstraints.HORIZONTAL;
            c_toolbar.anchor = GridBagConstraints.WEST;
            c_split.gridx = 0;
            c_split.gridy = 1;
            c_split.weightx = 1;
            c_split.weighty = 1;
            c_split.fill = GridBagConstraints.BOTH;

			jPanel.add(getJSplitPane(), c_split);
			jPanel.add(gsActions.getToolBar(), c_toolbar);
		}
		return jPanel;
	}
	/**
	 * This method initializes jSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new SplitPane();
			jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setTopComponent(getGraphPanel());
			jSplitPane.setBottomComponent(getJSplitPane1());
			jSplitPane.setResizeWeight(1.0D);
			jSplitPane.setName("mainFrameSeparator");
			jSplitPane.setOneTouchExpandable(true);
		}
		return jSplitPane;
	}
	/**
	 * This method initializes gsGraphPanel
	 *
	 * @return fr.univmrs.ibdm.GINsim.gui.GsGraphPanel
	 */
	private JComponent getGraphPanel() {
		if (graphPanel == null) {
			graphPanel = new JPanel();

			graphPanel.setLayout(new GridBagLayout());

			graphScrollPane = new JScrollPane();

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			graphPanel.add(graphScrollPane, c);

			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 1;
			c.weightx = 1;
			c.weighty = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
			graphPanel.add(getNotificationPanel(), c);
		}
		return graphPanel;
	}

	private JPanel getNotificationPanel() {
		if (notificationPanel == null) {
			notificationPanel = new JPanel();
			if (graph != null) {
				notification = graph.getTopMessage();
			}
			notificationPanel.setVisible(notification != null);
			notificationPanel.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
            c.insets = new Insets(0,10,0,10);
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.BOTH;
			notificationMessage = new JLabel("no notification");
			notificationPanel.add(notificationMessage, c);

			c = new GridBagConstraints();
			c.gridx = 2;
			c.gridy = 0;
			c.anchor = GridBagConstraints.EAST;
            bNotificationAction = new JButton();
            notificationPanel.add(bNotificationAction, c);
            bNotificationAction.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    notificationAction(0);
                }
            });

            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            c.anchor = GridBagConstraints.EAST;
            bNotificationAction2 = new JButton();
            notificationPanel.add(bNotificationAction2, c);
            bNotificationAction2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    notificationAction(1);
                }
            });
            cNotificationAction = new JComboBox();
            notificationPanel.add(cNotificationAction, c);

			c = new GridBagConstraints();
			c.gridx = 3;
			c.gridy = 0;
            c.insets = new Insets(0,10,0,0);
			c.anchor = GridBagConstraints.EAST;
			bcloseNotification = new JButton("close");
			notificationPanel.add(bcloseNotification, c);
			bcloseNotification.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					closeNotification();
				}
			});
		}
		return notificationPanel;
	}

	protected void closeNotification() {
		graph.deleteAllNotificationMessage();
	}

	protected void notificationAction(int index) {
		if (notification != null) {
            if (index == 0) {
                if (cNotificationAction.isVisible()) {
                    notification.performAction(cNotificationAction.getSelectedIndex());
                    return;
                }
            }
            notification.performAction(index);
		}
	}

	/**
	 * This method initializes jSplitPane1
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane1() {
		if (jSplitPane1 == null) {
			jSplitPane1 = new SplitPane();
			jSplitPane1.setLeftComponent(getJTabbedPane());
			jSplitPane1.setRightComponent(getGsGraphMapPanel());
			jSplitPane1.setDividerSize(2);
			jSplitPane1.setResizeWeight(0.7);
			jSplitPane1.setName("mapSeparator");
		}
		return jSplitPane1;
	}
	/**
	 * This method initializes jTabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab(Translator.getString("STR_tab_selection"), null, getJPanel1(), null);
			jTabbedPane.addTab(Translator.getString("STR_tab_graphicAttributes"), null, getGsGraphicAttributePanel(), null);
		}
		return jTabbedPane;
	}
	/**
	 * This method initializes gsGraphMapPanel
	 *
	 * @return fr.univmrs.ibdm.GINsim.gui.GsGraphMapPanel
	 */
	private JPanel getGsGraphMapPanel() {
		if (gsGraphMapPanel == null) {
			gsGraphMapPanel = new JPanel();
		}
		return gsGraphMapPanel;
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
	 * @return fr.univmrs.ibdm.GINsim.gui.GsGraphicAttributePanel
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
        jTabbedPane.setTitleAt(0, Translator.getString(graph.getTabLabel()));
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
        	jTabbedPane.remove(graphParameterPanel);
        }
        graphEditor = graph.getGraphEditor();
        if (graphEditor != null) {
        	graphParameterPanel = new GenericPropertyEditorPanel(graphEditor);
        	graphEditor.refresh(true);
        } else {
        	graphParameterPanel = graph.getGraphParameterPanel();
        }
        if (graphParameterPanel != null) {
        	jTabbedPane.addTab(Translator.getString("STR_tab_graphParameter"), null, graphParameterPanel, null);
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

        jTabbedPane.setEnabledAt(0, false);
        jTabbedPane.setEnabledAt(1, false);
        if (jTabbedPane.getTabCount() >= 3) {
            jTabbedPane.setEnabledAt(2, true);
            if (jTabbedPane.getSelectedIndex() != 2) {
                jTabbedPane.setSelectedIndex(2);
            }
        }

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
                jSplitPane.getHeight()-((Integer)GsOptions.getOption("display.dividersize", new Integer(80))).intValue());

        updateTitle();
        updateGraphNotificationMessage(graph);
    }
    /**
     * @return an empty jPanel (to be displayed when nothing is selected or when no parameter panel is avaible)
     */
    private JPanel getEmptyPanel() {
        if (emptyPanel == null) {
            emptyPanel = new JPanel();
        }
        return emptyPanel;
    }
    /**
     * @return this mainFrame's event dispatcher
     */
    public GsEventDispatcher getEventDispatcher() {
        return eventDispatcher;
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
    	if (confirmCloseGraph()) {
            if (gsGraphMapPanel.isVisible()) {
                mmapDivLocation = jSplitPane1.getWidth() - jSplitPane1.getDividerLocation();
            }
            GsOptions.setOption("display.minimapsize", new Integer(mmapDivLocation));
            if (secondaryFrame == null) {
                GsOptions.setOption("display.dividersize", new Integer(jSplitPane.getHeight()-jSplitPane.getDividerLocation()));
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
     * change the attribute panel position.
     *
     * @param b : if true the panel will be in a separate window
     */
    public void divideWindow(boolean b) {
		//if it's not divided
		if (secondaryFrame==null && b) {
			//create second frame
			secondaryFrame=new JDialog(this);
			secondaryFrame.setTitle(Translator.getString("STR_Tools"));
			//detach component from SplitPane_H
			jSplitPane.setBottomComponent(null);
			//set tools in ContentPane
			secondaryFrame.setContentPane(jSplitPane1);
			secondaryFrame.setSize(800,300);
			secondaryFrame.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent evt) {
						gsActions.viewcallback.divideWindow(false);
					}
				});
			//show
			secondaryFrame.setVisible(true);
			secondaryFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		} else if (secondaryFrame!=null) {
			// re-attach tools
			jSplitPane.setBottomComponent(jSplitPane1);
			//destroy secondary frame
			secondaryFrame.setVisible(false);
			secondaryFrame.dispose();
			secondaryFrame=null;
			this.setSize(this.getSize().width+1,this.getSize().height);
		}
    }
    /**
     * show/hide the minimap
     *
     * @param b : if true the miniMap will be shown
     */
    public void showMiniMap(boolean b) {
        if (gsGraphMapPanel != null) {
            if (b) {
                gsGraphMapPanel.setVisible(b);
                jSplitPane1.setDividerLocation(jSplitPane1.getWidth() - mmapDivLocation);
            } else {
                mmapDivLocation = jSplitPane1.getWidth() - jSplitPane1.getDividerLocation();
                gsGraphMapPanel.setVisible(b);
            }
        }
    }
    /**
     * @see fr.univmrs.ibdm.GINsim.graph.GraphChangeListener#graphSelectionChanged(fr.univmrs.ibdm.GINsim.graph.GsGraphSelectionChangeEvent)
     *
     * tons of tests to activate/select the right tab depending on the context.
     * overview:
     *   - if nothing is selected (ie the background): edit graph properties if avaible
     *   - if ONE SINGLE node or edge is selected: edit this object's properties or graphical settings (let the user choose)
     *   - if several items of the same type are selected: edit graphical settings
     *   - if the selection is heterogenous: don't edit anything
     *   - to make it funnier: always come back to the right tab when only ONE item is selected
     */
    public void graphSelectionChanged(GsGraphSelectionChangeEvent event) {
        	v_edge = event.getV_edge();
        	v_vertex = event.getV_vertex();
        int selected = jTabbedPane.getSelectedIndex();

        if (event.getNbEdge() > 0 && event.getNbVertex() == 0) {
            jTabbedPane.setEnabledAt(1, true);
            if (jTabbedPane.getTabCount() >= 3) {
                jTabbedPane.setEnabledAt(2, false);
            }
            // if multi-selection: force it on the graphic attribute panel otherwise let it free
            if (event.getNbEdge() == 1) {
                jTabbedPane.setEnabledAt(0, true);
                cards.show(jPanel1, "edge");
                if (edgeEditor != null) {
                	edgeEditor.setEditedObject(v_edge.get(0));
                } else if (edgePanel != null) {
                    edgePanel.setEditedObject(v_edge.get(0));
                }
                gsGraphicAttributePanel.setEditedObject(v_edge.get(0));
                if (selectedPanel != -1) {
                    jTabbedPane.setSelectedIndex(selectedPanel);
                    selectedPanel = -1;
                }
            } else {
                jTabbedPane.setEnabledAt(0, false);
                cards.show(jPanel1, "empty");
                if (selectedPanel == -1) {
                    selectedPanel = selected;
                }
                jTabbedPane.setSelectedIndex(1);
                gsGraphicAttributePanel.setEditedObject(v_edge);
            }
        } else if (event.getNbEdge() == 0 && event.getNbVertex() > 0) {
            jTabbedPane.setEnabledAt(1, true);
            if (jTabbedPane.getTabCount() >= 3) {
                jTabbedPane.setEnabledAt(2, false);
            }
            if (event.getNbVertex() == 1) {
                jTabbedPane.setEnabledAt(0, true);
                cards.show(jPanel1, "vertex");
                if (vertexEditor != null) {
                	vertexEditor.setEditedObject(v_vertex.get(0));
                } else if (vertexPanel != null) {
                    vertexPanel.setEditedObject(v_vertex.get(0));
                }
                gsGraphicAttributePanel.setEditedObject(v_vertex.get(0));
                if (selectedPanel != -1) {
                    jTabbedPane.setSelectedIndex(selectedPanel);
                    selectedPanel = -1;
                }
            } else {
                jTabbedPane.setEnabledAt(0, false);
                cards.show(jPanel1, "empty");
                if (selectedPanel == -1) {
                    selectedPanel = selected;
                }
                jTabbedPane.setSelectedIndex(1);
                gsGraphicAttributePanel.setEditedObject(v_vertex);
            }
        } else {
            jTabbedPane.setEnabledAt(0, false);
            jTabbedPane.setEnabledAt(1, false);
            cards.show(jPanel1, "empty");
            gsGraphicAttributePanel.setEditedObject(null);
            if (event.getNbEdge() > 0) {
                if (jTabbedPane.getTabCount() >= 3) {
                    jTabbedPane.setEnabledAt(2, false);
                    if (selected == 2) {
                        jTabbedPane.setSelectedIndex(0);
                    }
                }
            } else {
                if (jTabbedPane.getTabCount() >= 3) {
                    jTabbedPane.setEnabledAt(2, true);
                    jTabbedPane.setSelectedIndex(2);
                    if (selectedPanel == -1) {
                        selectedPanel = selected;
                    }
                }
            }
        }
    }

    /**
     * @return the list of currently selected edges
     */
    public Vector getSelectedEdges() {
    	return v_edge;
    }

    /**
     * @return the list of currently selected vertices
     */
    public Vector getSelectedVertices() {
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
	public synchronized void updateGraphNotificationMessage(GsGraph graph) {
		notification = graph.getTopMessage();
		if (notification == null) {
			notificationPanel.setVisible(false);
		} else {
            switch (notification.getType()) {
            case GsGraphNotificationMessage.NOTIFICATION_INFO:
            case GsGraphNotificationMessage.NOTIFICATION_INFO_LONG:
                notificationPanel.setBackground(Color.CYAN);
                break;
            case GsGraphNotificationMessage.NOTIFICATION_WARNING:
            case GsGraphNotificationMessage.NOTIFICATION_WARNING_LONG:
                notificationPanel.setBackground(Color.ORANGE);
                break;
            case GsGraphNotificationMessage.NOTIFICATION_ERROR:
            case GsGraphNotificationMessage.NOTIFICATION_ERROR_LONG:
                notificationPanel.setBackground(Color.RED);
                break;

            default:
                notificationPanel.setBackground(null);
                break;
            }

			notificationPanel.setVisible(true);
			notificationMessage.setText(notification.toString());
            String[] t_text = notification.getActionText();
			if (t_text != null && t_text.length > 0) {
                bNotificationAction.setVisible(true);
                if ( t_text.length == 1) {
                    cNotificationAction.setVisible(false);
                    bNotificationAction2.setVisible(false);
                    bNotificationAction.setText(t_text[0]);
                    bNotificationAction.requestFocusInWindow();
                } else if ( t_text.length == 2) {
                    bNotificationAction.setText(t_text[0]);
                    bNotificationAction2.setText(t_text[1]);
                    bNotificationAction2.setVisible(true);
                    cNotificationAction.setVisible(false);
                    bNotificationAction2.requestFocusInWindow();
                } else {
                    cNotificationAction.setVisible(true);
                    bNotificationAction2.setVisible(false);
                    bNotificationAction.setText("OK");
                    cNotificationAction.setModel(new DefaultComboBoxModel(t_text));
                    cNotificationAction.requestFocusInWindow();
                }
			} else {
                bNotificationAction.setVisible(false);
                bNotificationAction2.setVisible(false);
                cNotificationAction.setVisible(false);
                bcloseNotification.requestFocusInWindow();
			}
		}
	}
   }
