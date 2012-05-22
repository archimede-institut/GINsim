package org.ginsim.gui.shell;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.PriorityQueue;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.ginsim.common.application.OptionStore;
import org.ginsim.common.application.Translator;
import org.ginsim.commongui.utils.ImageLoader;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.notification.Notification;
import org.ginsim.core.notification.NotificationListener;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphGUIListener;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd.GlassPane;
import org.ginsim.gui.notifications.NotificationPanel;
import org.ginsim.gui.notifications.NotificationSource;
import org.ginsim.gui.shell.editpanel.EditPanel;
import org.ginsim.gui.shell.editpanel.EditTab;
import org.ginsim.gui.utils.widgets.Frame;
import org.ginsim.gui.utils.widgets.SplitPane;


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



public class MainFrame extends Frame implements NotificationSource, NotificationListener, GraphGUIListener {
	private static final long serialVersionUID = 3002680535567580439L;
	
	private static final int SECONDARY_SPLIT_PANEL_MIN_HEIGHT = 250;
	
    private JDialog secondaryFrame = null;
	private JPanel contentPanel = null;
	private JSplitPane mainSplitPane = null;
	private JScrollPane graphScrollPane = null;
	private JPanel graphPanel = null;
	private EditPanel editTabbedPane = null;

	private JMenuBar menubar = new JMenuBar();
	private JToolBar toolbar = new JToolBar();
	
    private int mmapDivLocation = ((Integer)OptionStore.getOption("display.minimapSize", new Integer(100))).intValue();

	private NotificationPanel notificationPanel = new NotificationPanel(this);
	private final PriorityQueue<Notification> notificationList = new PriorityQueue<Notification>();  

	private final FrameActionManager actionManager = new MainFrameActionManager();

	private final GraphGUI graphGUI;
	
	private static final boolean alwaysForceClose = false;

	public MainFrame(GraphGUI graph_gui) {
		
		super("MainFrame", 800, 700);
		setIconImage(ImageLoader.getImage("gs1.gif"));
		
        this.graphGUI = graph_gui;
        GUIManager.getInstance().registerGUI( graph_gui, this);
        graph_gui.addGraphGUIListener(this);
        
        setFrameTitle( graphGUI.getGraph(), graphGUI.isSaved());
        setJMenuBar(menubar);
        
		contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 0;
        cst.weightx = 1;
        cst.weighty = 0;
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.anchor = GridBagConstraints.WEST;
        toolbar.setFloatable(false);
		contentPanel.add(toolbar, cst);

		cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 1;
        cst.weightx = 1; 
        cst.weighty = 1;
        cst.fill = GridBagConstraints.BOTH;
		setGlassPane( new GlassPane());

        setContentPane(contentPanel);
		contentPanel.add(getMainSplitPane(), cst);

    	actionManager.buildActions( graphGUI, menubar, toolbar);
    	fillGraphPane( graphGUI.getGraphComponent());
    	
		addComponentListener( new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				mainSplitPane.setDividerLocation( e.getComponent().getHeight() - 100 - SECONDARY_SPLIT_PANEL_MIN_HEIGHT);
			}
		});
    	
        setVisible(true);
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
			mainSplitPane.setBottomComponent(getEditTabbedPane());
			//mainSplitPane.setResizeWeight(0);
			mainSplitPane.setDividerLocation( this.getHeight() - 100 - SECONDARY_SPLIT_PANEL_MIN_HEIGHT);
			mainSplitPane.setName("mainFrameSeparator");
			mainSplitPane.setOneTouchExpandable(true);
		}
		return mainSplitPane;
	}
	
	/**
	 * Create a scrollable panel with a notification area on bottom for use as main panel in the frame.
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
			graphPanel.add(notificationPanel, c);
		}
		return graphPanel;
	}
	
	/**
	 * This method initializes editTabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private EditPanel getEditTabbedPane() {
	    if (editTabbedPane == null) {
			editTabbedPane = new EditPanel( graphGUI);
			editTabbedPane.setMinimumSize(new Dimension(0, 0));
		}
	    return editTabbedPane;
	}

    /**
     * change the attribute panel position.
     *
     * @param b : if true the panel will be in a separate window
     */
    public void divideWindow(boolean b) {
    	if ((b && secondaryFrame!=null) || (!b && secondaryFrame==null)) {
    		// already in the desired state
    		return;
    	}
		//if it's not divided
		if (b) {
			//create second frame
			secondaryFrame=new JDialog(this);
			secondaryFrame.setTitle(Translator.getString("STR_Tools"));
			//detach component from SplitPane_H
			mainSplitPane.setBottomComponent(null);
			//set tools in ContentPane
			secondaryFrame.setContentPane(editTabbedPane);
			secondaryFrame.setSize(800,300);
			secondaryFrame.addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent evt) {
						divideWindow(false);
					}
				});
			//show
			secondaryFrame.setVisible(true);
			secondaryFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		} else  {
			// re-attach tools
			mainSplitPane.setBottomComponent(editTabbedPane);
			//destroy secondary frame
			secondaryFrame.setVisible(false);
			secondaryFrame.dispose();
			secondaryFrame = null;
			this.setSize(this.getSize().width+1,this.getSize().height);
		}
		// FIXME: update menu content
    }
    
    /**
     * close the window without exiting:
     *    ie close if it's not the last window
     */
    public void close() {
    	GUIManager.getInstance().close(graphGUI.getGraph());
    }
    
    /**
     * some things to do before destroying the window
     * TODO: check if this is actually needed
     */
    public void dispose() {
        OptionStore.setOption("display.minimapsize", new Integer(mmapDivLocation));
        if (secondaryFrame == null) {
            OptionStore.setOption("display.dividersize", new Integer(mainSplitPane.getHeight()-mainSplitPane.getDividerLocation()));
        }
        super.dispose();
    }

    
    private void fillGraphPane( Component view) {
    	
    	graphScrollPane.setViewportView( view);
    }

    /**
     * Return the most important notification (it is the head of the queue since the queue is ordered to 
     * have the most urgent notification on top).
     * 
     */
	@Override
	public Notification getTopNotification() {
		
		Notification top_notification;
		
		synchronized( notificationList){
			top_notification = notificationList.poll();
		}
		
		return top_notification;
	}

	/**
	 * Close the notification by set invisible the notification panel and ask it to test if a
	 * other Notification is in the queue
	 * 
	 */
	@Override
	public void closeNotification() {
		
		notificationPanel.setVisible( false);
		notificationPanel.updateNotificationMessage();
	}
	
	/**
	 * Receive a notification and add it to the notification queue
	 * 
	 * @param message the notification to add to the queue
	 */
	@Override
	public void receiveNotification( Notification message) {
		
		synchronized( notificationList){
			if( message != null){
				notificationList.add( message);
			}
		}
		notificationPanel.updateNotificationMessage();
	}
	
	/**
	 * Remove the given notification from the queue
	 * 
	 * @param message the notification to remove
	 */
	@Override
	public void deleteNotification( Notification message) {
	
		synchronized( notificationList){
			if( message != null){
				notificationList.remove( message);
			}
		}
		
	}

	@Override
	public void graphSelectionChanged(GraphGUI gui) {
	}

	@Override
	public void graphGUIClosed(GraphGUI gui) {
	}

	@Override
	public void graphChanged(Graph g, GraphChangeType type, Object data) {
		
		GraphGUI graph_gui = GUIManager.getInstance().getGraphGUI( g);
		
		if( graph_gui != null){
			if( graph_gui.canBeSaved()){
				graph_gui.setSaved( type == GraphChangeType.GRAPHSAVED);
			}
		}
	}
	
	/**
	 * Add a given EditTab to the edit panel
	 * @param tab a given EditTab to add to the edit panel
	 */
	public void addTabToEditPanel(EditTab tab) {
		getEditTabbedPane().addTab(tab);
	}
	
	/**
	 * Remove a given EditTab from the edit panel
	 * @param tab_name a given EditTab's name to remove from the edit panel
	 */
	public void removeTabToEditPanel(String tab_name) {
		getEditTabbedPane().removeTab(tab_name);
	}
}
