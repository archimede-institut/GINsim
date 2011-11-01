package fr.univmrs.tagc.GINsim.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.ginsim.gui.notifications.NotificationPanel;
import org.ginsim.gui.notifications.NotificationSource;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.global.GsEventDispatcher;
import fr.univmrs.tagc.GINsim.graph.GraphChangeListener;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.Frame;
import fr.univmrs.tagc.common.widgets.SplitPane;

/**
 * GINsim's main frame shell.
 * 
 * It provides the general layout with menu, toolbar,
 * notifications and reserved space for the view and edit panels.
 * 
 * Main frames are counted, an event is emitted when the last one is closed.
 * 
 * FIXME: refactor in progress...
 */
abstract public class BaseMainFrame extends Frame implements NotificationSource {
	private static final long serialVersionUID = 3002680535567580439L;
	
    private JDialog secondaryFrame = null;
	private JPanel contentPanel = null;
	private JSplitPane mainSplitPane = null;
	private JScrollPane graphScrollPane = null;
	private JPanel graphPanel = null;
	private JSplitPane secondarySplitPanel = null;
	private JTabbedPane editTabbedPane = null;
	private JPanel miniMapPanel = null;

    private Map<String, Integer> m_tabs = new HashMap<String, Integer>();
    private int mmapDivLocation = ((Integer)OptionStore.getOption("display.minimapSize", new Integer(100))).intValue();

	private NotificationPanel notificationPanel;

	private static final boolean alwaysForceClose = false;

	// FIXME: to remove, only used by TB plugin
	public static final int FLAG_ANY = TabSelection.TAB_MULTIPLE.flag | TabSelection.TAB_SINGLE.flag | TabSelection.TAB_NONE.flag;
    
	public BaseMainFrame(String id, int w, int h) {
		super(id, w, h);
	}
	
	protected void init() {
        setJMenuBar(getActions().getMenuBar());
        setContentPane(getContentPanel());
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(new GridBagLayout());

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

			contentPanel.add(getMainSplitPane(), c_split);
			contentPanel.add(getActions().getToolBar(), c_toolbar);
		}
		return contentPanel;
	}
	/**
	 * This method initializes jSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getMainSplitPane() {
		if (mainSplitPane == null) {
			mainSplitPane = new SplitPane();
			mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			mainSplitPane.setTopComponent(getGraphPanel());
			mainSplitPane.setBottomComponent(getSecondarySplitPanel());
			mainSplitPane.setResizeWeight(1.0);
			mainSplitPane.setName("mainFrameSeparator");
			mainSplitPane.setOneTouchExpandable(true);
		}
		return mainSplitPane;
	}
	
	protected void loadGraphPanel() {
        mainSplitPane.setTopComponent(getGraphPanel());

        // FIXME: restore memory for divider location
//        // replace jSplitPane, only if this is the first graph in this frame
//        if (event.getOldGraph() != null) {
//            int md = jSplitPane.getHeight()-jTabbedPane.getMinimumSize().height;
//            if (d == -1 || md < d) {
//                // without the (-5) it's sometimes strange...
//                d = md-5;
//            }
//            jSplitPane.setDividerLocation(d);
//        }
        getMainSplitPane().setDividerLocation(
                mainSplitPane.getHeight()-((Integer)OptionStore.getOption("display.dividersize", new Integer(80))).intValue());
	}

	/**
	 * This method initializes gsGraphPanel
	 *
	 * @return fr.univmrs.tagc.GINsim.gui.GsGraphPanel
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
	
	protected JScrollPane getGraphScrollPane() {
		return graphScrollPane;
	}

    protected void setMapPanel(JPanel graphMapPanel) {
        secondarySplitPanel.remove(miniMapPanel);
        miniMapPanel = graphMapPanel;
        if (miniMapPanel == null) {
            showMiniMap(false);
        } else {
        	secondarySplitPanel.setRightComponent(miniMapPanel);
        }
	}

    /**
     * change the label of the main tab.
     * 
     * @param label the new label
     */
	protected void setTabLabel(String label) {
        int cst = m_tabs.get(editTabbedPane.getTitleAt(0));
        editTabbedPane.setTitleAt(0, label);
        m_tabs.put(editTabbedPane.getTitleAt(0), cst);
	}

	private NotificationPanel getNotificationPanel() {
		if (notificationPanel == null) {
			notificationPanel = new NotificationPanel(this);
		}
		return notificationPanel;
	}

	public synchronized void updateNotificationMessage() {
		notificationPanel.updateNotificationMessage();
	}

	/**
	 * This method initializes jSplitPane1
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getSecondarySplitPanel() {
		if (secondarySplitPanel == null) {
			secondarySplitPanel = new SplitPane();
			secondarySplitPanel.setLeftComponent(getEditTabbedPane());
			secondarySplitPanel.setRightComponent(getMiniMapPanel());
			secondarySplitPanel.setDividerSize(2);
			secondarySplitPanel.setResizeWeight(0.7);
			secondarySplitPanel.setName("mapSeparator");
		}
		return secondarySplitPanel;
	}

	/**
	 * This method initializes editTabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getEditTabbedPane() {
	    if (editTabbedPane == null) {
			editTabbedPane = new JTabbedPane();
			editTabbedPane.setMinimumSize(new Dimension(0, 0));
		}
	    return editTabbedPane;
	}

    public void addTab(String name, JPanel panel, boolean enabled, int constraint) {
        if (m_tabs.containsKey(name)) {
            // TODO: error
            return;
        }
        editTabbedPane.addTab(name, null, panel, null);
        m_tabs.put(name, new Integer(constraint));
        if (panel instanceof GraphChangeListener) {
            getEventDispatcher().addGraphChangedListener((GraphChangeListener) panel);
        }
        updateTabs(TabSelection.TAB_CHECK);
    }
	public boolean hasTab(String name) {
        return m_tabs.containsKey(name);
    }
    
    
    protected abstract TabSelection getCurrentSelectionType();
    
    /**
     * enable/disable tabs depending on the constraint
     * if the selected tab becomes inactive, select another one
     */
    public void updateTabs(TabSelection constraint) {
    	TabSelection cst = constraint;
        if (constraint == TabSelection.TAB_CHECK) {
            cst = getCurrentSelectionType();
        }

        int selected = editTabbedPane.getSelectedIndex();
        boolean need_change = true;
        if (selected != -1) {
            Integer i_sel = (Integer)m_tabs.get(editTabbedPane.getTitleAt(selected));
            int sel = 0;
            if (i_sel != null) {
                sel = i_sel.intValue();
            }
            need_change = (sel & cst.flag) == 0;
        }
        int nbtabs = editTabbedPane.getTabCount();
        for (int i=0 ; i<nbtabs ; i++) {
            Integer curCst = (Integer)m_tabs.get(editTabbedPane.getTitleAt(i));
            int cur_cst = 0;
            if (curCst != null) {
                cur_cst = curCst.intValue();
            }

            if ((cur_cst & cst.flag) > 0) {
                editTabbedPane.setEnabledAt(i, true);
                if (need_change) {
                    editTabbedPane.setSelectedIndex(i);
                    need_change = false;
                }
            } else {
                editTabbedPane.setEnabledAt(i, false);
            }
        }
    }
    public boolean removeTab(String name) {
        int i = editTabbedPane.indexOfTab(name);
        if (i != -1) {
            Component c = editTabbedPane.getComponentAt(i);
            editTabbedPane.removeTabAt(i);
            if (c instanceof GraphChangeListener) {
                getEventDispatcher().removeGraphChangeListener((GraphChangeListener)c);
            }
            m_tabs.remove(name);
            updateTabs(TabSelection.TAB_CHECK);
            return true;
        }
        return false;
    }
	/**
	 * This method initializes miniMapPanel
	 *
	 * @return fr.univmrs.tagc.GINsim.gui.GsGraphMapPanel
	 */
	private JPanel getMiniMapPanel() {
		if (miniMapPanel == null) {
			miniMapPanel = new JPanel();
		}
		return miniMapPanel;
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
			mainSplitPane.setBottomComponent(null);
			//set tools in ContentPane
			secondaryFrame.setContentPane(secondarySplitPanel);
			secondaryFrame.setSize(800,300);
			secondaryFrame.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent evt) {
						getActions().viewcallback.divideWindow(false);
					}
				});
			//show
			secondaryFrame.setVisible(true);
			secondaryFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		} else if (secondaryFrame!=null) {
			// re-attach tools
			mainSplitPane.setBottomComponent(secondarySplitPanel);
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
        if (miniMapPanel != null) {
            if (b) {
                miniMapPanel.setVisible(b);
                secondarySplitPanel.setDividerLocation(secondarySplitPanel.getWidth() - mmapDivLocation);
            } else {
                mmapDivLocation = secondarySplitPanel.getWidth() - secondarySplitPanel.getDividerLocation();
                miniMapPanel.setVisible(b);
            }
        }
    }

    abstract protected boolean confirmCloseGraph();
    
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
     * close the window without exiting:
     *    ie close if it's not the last window
     * @param force
     */
    private void doClose(boolean force) {
        
        // FIXME: graph not always really closed
        // --> GsEnv.m_graphs pas vide
        
    	if (confirmCloseGraph()) {
            if (miniMapPanel.isVisible()) {
                mmapDivLocation = secondarySplitPanel.getWidth() - secondarySplitPanel.getDividerLocation();
            }
            OptionStore.setOption("display.minimapsize", new Integer(mmapDivLocation));
            if (secondaryFrame == null) {
                OptionStore.setOption("display.dividersize", new Integer(mainSplitPane.getHeight()-mainSplitPane.getDividerLocation()));
            }
		    if (force || GsEnv.getNbFrame() > 1) {
		        GsEnv.delFrame(this);
		        dispose();
		    } else {
		        GsEnv.newGraph(this);
		    }
    	}
    }

    protected void setGraphView(JComponent view) {
    	graphScrollPane.setViewportView(view);
    }

	public void updateRecentMenu() {
		getActions().updateRecentMenu();
	}
	abstract public GsActions getActions();
    abstract public GsEventDispatcher getEventDispatcher();


	enum TabSelection {
		TAB_CHECK(0), TAB_NONE(1), TAB_SINGLE(2), TAB_MULTIPLE(4);

		public final int flag;
		
		private TabSelection(int flag) {
			this.flag = flag;
		}
	}

}

