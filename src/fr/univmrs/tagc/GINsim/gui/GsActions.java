package fr.univmrs.tagc.GINsim.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.ginsim.graph.EditGroup;
import org.ginsim.graph.EditMode;
import org.ginsim.gui.shell.FrameActions;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GraphChangeListener;
import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphSelectionChangeEvent;
import fr.univmrs.tagc.GINsim.graph.GsNewGraphEvent;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.OSXAdapter;
import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.ImageLoader;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.AboutDialog;
import fr.univmrs.tagc.common.widgets.BaseAction;


/**
 * this class creates the menubar, the toolbar and the main actions for GINsim
 * GUI.
 */
public class GsActions implements GraphChangeListener, FrameActions {

	/** default edit mode: move/resize objects */
	public static final int		MODE_DEFAULT		= 0;
	/** edit mode: add points to existing edges */
	public static final int		MODE_ADD_EDGE_POINT	= 1;
	/** edit mode: add new edges */
	public static final int		MODE_ADD_EDGE		= 2;
	/** edit mode: add new vertices */
	public static final int		MODE_ADD_VERTEX		= 3;

	private int					mode				= MODE_DEFAULT;
	private int					submode				= 0;
	private boolean				locked				= false;
	private int					nbEditVertexSubmode	= 0;
	private int					nbEditEdgeSubmode	= 0;

	private JMenuBar			menuBar				= null;
	private JToolBar			toolBar				= null;
	private ButtonGroup			editGroup			= null;
	private Vector				v_editButtons		= new Vector();

	// mainframe
	private GsMainFrame			mainFrame			= null;

	// callbacks
	protected GsFileCallBack	filecallback;
	protected GsEditCallBack	editcallback;
	protected GsViewCallBack	viewcallback;

	// Menus
	private JMenu				fileMenu					= null;
	private JMenu				editMenu					= null;
	private JMenu				viewMenu					= null;
	private JMenu				helpMenu					= null;
	private JMenu				actionMenu					= null;
	private JMenu				exportMenu					= null;
	private JMenu 				importMenu 					= null;
	private JMenu				layoutMenu					= null;
	private JMenu				addEdgeMenu					= null;
	private JMenu				addVertexMenu				= null;
	private JMenu				recentMenu					= null;
	private JMenu				editSelectMenu				= null;
	private JMenu				editExtendSelectionMenu		= null;
	private JMenu				invertSelectionMenu			= null;
	private JMenu				selectAllMenu				= null;


	// Actions
	private AbstractAction		actionClose;
	private AbstractAction		actionSave;
	private AbstractAction		actionSaveAs;
	private AbstractAction		actionOpen;
	private AbstractAction		actionOpenAndDo;
	private AbstractAction		actionNew;
	private AbstractAction		actionSaveSubGraph;
	private AbstractAction		actionMergeGraph;
	private AbstractAction		actionQuit;
	private AbstractAction		actionHelp;
	private AbstractAction		actionAbout;
	private AbstractAction		actionCopy;
	private AbstractAction		actionPaste;
	private AbstractAction		actionSelectAll;
	private AbstractAction		actionSelectAllNodes;
	private AbstractAction		actionSelectAllEdges;
	private AbstractAction		actionInvertSelection;
	private AbstractAction		actionInvertEdgeSelection;
	private AbstractAction		actionInvertVertexSelection;
	private AbstractAction		actionDelete;
	private AbstractAction		actionUndo;
	private AbstractAction		actionRedo;
	private AbstractAction		actionSearchNode;
	private AbstractAction		actionZoomIn;
	private AbstractAction		actionZoomOut;
	private AbstractAction		actionNormalSize;
	private AbstractAction		actionDisplayEdgeName;
	private AbstractAction		actionVertexToFront;
	private AbstractAction		actionDivideWindow;
	private AbstractAction		actionDisplayGrid;
	private AbstractAction		actionGridActive;
	private AbstractAction		actionDisplayMiniMap;
	private AbstractAction		actionSelectIncomingArcs;
	private AbstractAction		actionSelectOutgoingArcs;
	private AbstractAction		actionSelectIncomingVertices;
	private AbstractAction		actionSelectOutgoingVertices;
	private AbstractAction		actionExtendSelectionToIncomingArcs;
	private AbstractAction		actionExtendSelectionToOutgoingArcs;
	private AbstractAction		actionExtendSelectionToIncomingVertices;
	private AbstractAction		actionExtendSelectionToOutgoingVertices;
	private AbstractAction      actionSimpleFunctionEdition;

	// Check Box Menu Item
	private JCheckBoxMenuItem	btt_divideWindow;
	private JCheckBoxMenuItem	btt_displayEdgeName;
	private JCheckBoxMenuItem	btt_vertextofront;
	private JCheckBoxMenuItem	btt_displayGrid;
	private JCheckBoxMenuItem	btt_gridActive;
	private JCheckBoxMenuItem	btt_displayMiniMap;
	private JCheckBoxMenuItem   btt_simpleFunctionEdition;
	
	private JSeparator          simpleFunctionEdition_separator = new JSeparator();			

	protected static final int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	/** list of available imports */
	private static List v_import;

	// other menuItems
	JMenuItem					mi_edit;
	JMenuItem					mi_delete;

	private JSeparator			sepEdit;
	private AbstractAction		actionEditDefault;

	/**
	 * create a new menu/toolbar manager.
	 *
	 * @param m the mainFrame for which we create it
	 */
	public GsActions(GsMainFrame m) {
		menuBar = new JMenuBar();
		toolBar = new JToolBar();
		mainFrame = m;
		mainFrame.getEventDispatcher().addGraphChangedListener(this);
		initialize();
	}

	/**
	 * This function initialize the applications menu / toolbar
	 */
	private void initialize() {
		// get the message translator
		editGroup = new ButtonGroup();

		// create the main menu
		fileMenu = new JMenu(Translator.getString("STR_File"));
		menuBar.add(fileMenu);
		editMenu = new JMenu(Translator.getString("STR_Edit"));
		menuBar.add(editMenu);
		viewMenu = new JMenu(Translator.getString("STR_View"));
		menuBar.add(viewMenu);
		actionMenu = new JMenu(Translator.getString("STR_Actions"));
		menuBar.add(actionMenu);
		helpMenu = new JMenu(Translator.getString("STR_Help"));
		menuBar.add(helpMenu);

		// additionnals menus:
		layoutMenu = new JMenu(Translator.getString("STR_layout"));
		exportMenu = new JMenu(Translator.getString("STR_export"));
		importMenu = new JMenu(Translator.getString("STR_import"));
		addVertexMenu = new JMenu(Translator.getString("STR_addVertex"));
		addEdgeMenu = new JMenu(Translator.getString("STR_addEdge"));
		editSelectMenu = new JMenu(Translator.getString("STR_editSelectMenu"));
		editExtendSelectionMenu = new JMenu(Translator.getString("STR_editExtendSelectionMenu"));
		invertSelectionMenu = new JMenu(Translator.getString("STR_invertSelectionMenu"));
		selectAllMenu = new JMenu(Translator.getString("STR_selectAllMenu"));

		// create calbacks
		filecallback = new GsFileCallBack(mainFrame);
		editcallback = new GsEditCallBack(mainFrame);
		viewcallback = new GsViewCallBack(mainFrame);

		actionClose = new BaseAction("STR_close", "window-close.png", "STR_close_descr", KeyStroke
				.getKeyStroke(KeyEvent.VK_W, mask),
				new Integer(KeyEvent.VK_C)) {

			private static final long	serialVersionUID	= 5310411143622306390L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				filecallback.close();
			}
		};

		actionSave = new BaseAction("STR_save", "document-save.png", "STR_save_descr", KeyStroke
				.getKeyStroke(KeyEvent.VK_S, mask),
				new Integer(KeyEvent.VK_S)) {

			private static final long	serialVersionUID	= -5505221251989246299L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				filecallback.save();
			}
		};

		actionSaveAs = new BaseAction("STR_saveAs", "document-save-as.png", "STR_saveAs_descr",
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK
						+ mask), null) {

			private static final long	serialVersionUID	= -7021395742832737524L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				filecallback.saveAs();
			}
		};

		actionSaveSubGraph = new BaseAction("STR_saveSubGraph", null,
				"STR_saveSubGraph_descr", null) {

			private static final long	serialVersionUID	= -4153506771396559120L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				filecallback.saveSubGraph();
			}
		};

		actionMergeGraph = new BaseAction("STR_mergeGraph", null,
				"STR_mergeGraph_descr", null) {

			private static final long	serialVersionUID	= -6093958446613777041L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				filecallback.mergeGraph();
			}
		};

		actionQuit = new BaseAction("STR_quit", "exit.png", "STR_quit_descr", KeyStroke.getKeyStroke(KeyEvent.VK_Q, GsActions.mask), new Integer(KeyEvent.VK_Q)) {
			private static final long	serialVersionUID	= 4215659230452329435L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				quit();
			}
			
		};
		actionHelp = new BaseAction("STR_help", "help-contents.png", "STR_help_descr", null,
				new Integer(KeyEvent.VK_H)) {

			private static final long	serialVersionUID	= 6430521053940787968L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				String path = GsEnv.getGinsimDir()+"Documentation/html/index.html";
				// TODO: better URL on the web site ?
				if (!Tools.openFile(path)) {
					Tools.openURI("http://gin.univ-mrs.fr/GINsim/doc.html");
				}
			}
		};

		actionAbout = new BaseAction("STR_about", "help-about.png", "STR_about_descr", null, new Integer(KeyEvent.VK_C)) {
			private static final long	serialVersionUID	= -4657616921932268806L;
			
			public void actionPerformed(java.awt.event.ActionEvent e) {
				about();
			}			
		};
		actionCopy = new BaseAction("STR_copy", "edit-copy.png", "STR_copy_descr",
				KeyStroke.getKeyStroke(KeyEvent.VK_C, mask), null) {

			private static final long	serialVersionUID	= -3723793530677676142L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.copy();
			}
		};

		actionPaste = new BaseAction("STR_paste", "edit-paste.png", "STR_paste_descr",
				KeyStroke.getKeyStroke(KeyEvent.VK_V, mask), null) {

			private static final long	serialVersionUID	= -5367472052019947472L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.paste();
			}
		};

		actionSelectAll = new BaseAction("STR_selectAll", null,
				"STR_selectAll_descr", KeyStroke.getKeyStroke(KeyEvent.VK_A,
						mask)) {

			private static final long	serialVersionUID	= 8225661783758969807L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.selectAll();
			}
		};

		actionSelectAllNodes = new BaseAction("STR_selectAllNodes", null,
				"STR_selectAllNodes_descr", null) {

			private static final long	serialVersionUID	= 8225661783758969807L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.selectAllNodes();
			}
		};

		actionSelectAllEdges = new BaseAction("STR_selectAllEdges", null,
				"STR_selectAllEdges_descr", null) {

			private static final long	serialVersionUID	= 8225661783758969807L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.selectAllEdges();
			}
		};

		actionInvertSelection = new BaseAction("STR_invertSelection", null,
				"STR_invertSelection_descr",  KeyStroke.getKeyStroke(KeyEvent.VK_A,
						mask | ActionEvent.SHIFT_MASK)) {

			private static final long	serialVersionUID	= -2792926833599149035L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.invertSelection();
			}
		};

		actionInvertEdgeSelection = new BaseAction("STR_invertEdgeSelection", null,
				"STR_invertEdgeSelection_descr",  null) {

			private static final long	serialVersionUID	= -2792926833599149035L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.invertEdgeSelection();
			}
		};

		actionInvertVertexSelection = new BaseAction("STR_invertVertexSelection", null,
				"STR_invertEdgeSelection_descr",  null) {

			private static final long	serialVersionUID	= -2792926833599149035L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.invertVertexSelection();
			}
		};

		actionUndo = new BaseAction("STR_undo", "undo.png", "STR_undo_descr", null, null) {

			private static final long	serialVersionUID	= 3241742636297115171L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.undo();
			}
		};

		actionRedo = new BaseAction("STR_redo", "redo.png", "STR_redo_descr", null, null) {

			private static final long	serialVersionUID	= 2613112142063032508L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.redo();
			}
		};

		actionDelete = new BaseAction("STR_delete", "edit-delete.png", "STR_delete_descr", KeyStroke.getKeyStroke(KeyEvent.VK_F,
				mask | ActionEvent.SHIFT_MASK), null) {

			private static final long	serialVersionUID	= -3283938108975661376L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.delete();
			}
		};
		mi_delete = new JMenuItem(actionDelete);
		

		actionSearchNode = new BaseAction("STR_searchNode", null,
				"STR_searchNode_descr", KeyStroke.getKeyStroke(KeyEvent.VK_F,
						mask | ActionEvent.SHIFT_MASK)) {

			private static final long serialVersionUID = 9114560394293685735L;
			
			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.searchNode();
			}
		};

		actionEditDefault = new GsEditSwitchAction("STR_edit", ImageLoader
				.getImageIcon("editmode.gif"), "STR_edit_descr", KeyStroke
				.getKeyStroke(KeyEvent.VK_ESCAPE, InputEvent.CTRL_MASK), this,
				GsActions.MODE_DEFAULT, 0);
		mi_edit = new JMenuItem(actionEditDefault);

		actionZoomIn = new BaseAction("STR_zoomIn", "zoom-in.png", "STR_zoomIn_descr",
				KeyStroke.getKeyStroke(KeyEvent.VK_ADD, mask), null) {

			private static final long	serialVersionUID	= 7767720724191732506L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				viewcallback.zoomIn();
			}
		};

		actionZoomOut = new BaseAction("STR_zoomOut", "zoom-out.png",
				"STR_zoomOut_descr", KeyStroke.getKeyStroke(
						KeyEvent.VK_SUBTRACT, mask), null) {

			private static final long	serialVersionUID	= 4121075965026762863L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				viewcallback.zoomOut();
			}
		};

		actionNormalSize = new BaseAction("STR_Normalsize", "zoom-original.png",
				"STR_NormalSize_descr", KeyStroke.getKeyStroke(
						KeyEvent.VK_EQUALS, mask), null) {

			private static final long	serialVersionUID	= 6710461629856502102L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				viewcallback.normalSize();
			}
		};

		actionVertexToFront = new BaseAction("STR_vertextofront", null,
				"STR_vertextofront_descr", null) {

			private static final long	serialVersionUID	= 1884358515751192901L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				viewcallback.vertexToFront(((JCheckBoxMenuItem)e.getSource())
						.getState());
			}
		};

		actionDisplayEdgeName = new BaseAction("STR_displayEdgeName", null,
				"STR_displayEdgeName_descr", null) {

			private static final long	serialVersionUID	= 2417319229337107021L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				viewcallback.displayEdgeName(((JCheckBoxMenuItem)e.getSource())
						.getState());
			}
		};

		actionDivideWindow = new BaseAction("STR_DivideWindow", null,
				"STR_DivideWindow_descr", null) {

			private static final long	serialVersionUID	= 5003089203724966295L;

			public void actionPerformed(java.awt.event.ActionEvent e) {

				if (e.getSource() instanceof JCheckBoxMenuItem) {
					viewcallback
							.divideWindow(((JCheckBoxMenuItem)e.getSource())
									.getState());
				}
			}
		};
		actionDisplayGrid = new BaseAction("STR_DisplayGrid", null,
				"STR_DisplayGrid_descr", null) {

			private static final long	serialVersionUID	= 6518755881749690697L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (e.getSource() instanceof JCheckBoxMenuItem) {
					viewcallback.displayGrid(((JCheckBoxMenuItem)e.getSource())
							.getState());
				}
			}
		};
		actionGridActive = new BaseAction("STR_gridActive", null,
				"STR_gridActive_descr", null) {

			private static final long	serialVersionUID	= 6518755881749690697L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (e.getSource() instanceof JCheckBoxMenuItem) {
					viewcallback.gridActive(((JCheckBoxMenuItem)e.getSource())
							.getState());
				}
			}
		};

		actionDisplayMiniMap = new BaseAction("STR_DisplayMiniMap", null,
				"STR_DisplayMiniMap_descr", null) {

			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (e.getSource() instanceof JCheckBoxMenuItem) {
					viewcallback.displayMiniMap(((JCheckBoxMenuItem)e
							.getSource()).getState());
				}
			}
		};

		actionSimpleFunctionEdition = new BaseAction("STR_simpleFunctionEdition", null,
				"STR_simpleFunctionEdition_descr", null) {
			
			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (e.getSource() instanceof JCheckBoxMenuItem) {
					editcallback.simpleFunctionEdition(((JCheckBoxMenuItem)e
							.getSource()).getState());
				}
			}
		};

		actionExtendSelectionToIncomingVertices = new BaseAction("STR_extendSelectionToIncomingVertices", null,
				"STR_extendSelectionToIncomingVertices_descr", null) {

			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.extendSelectionToIncomingVertices();
			}
		};
		actionExtendSelectionToOutgoingVertices = new BaseAction("STR_extendSelectionToOutgoingVertices", null,
				"STR_extendSelectionToOutgoingVertices_descr", null) {

			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.extendSelectionToOutgoingVertices();
			}
		};
		actionExtendSelectionToIncomingArcs = new BaseAction("STR_extendSelectionToIncomingArcs", null,
				"STR_extendSelectionToIncomingArcs_descr", null) {

			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.extendSelectionToIncomingArcs();
			}
		};
		actionExtendSelectionToOutgoingArcs = new BaseAction("STR_extendSelectionToOutgoingArcs", null,
				"STR_extendSelectionToOutgoingArcs_descr", null) {

			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.extendSelectionToOutgoingArcs();
			}
		};
		
		actionSelectIncomingVertices = new BaseAction("STR_selectIncomingVertices", null,
				"STR_selectIncomingVertices_descr", null) {

			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.selectIncomingVertices();
			}
		};
		actionSelectOutgoingVertices = new BaseAction("STR_selectOutgoingVertices", null,
				"STR_selectOutgoingVertices_descr", null) {

			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.selectOutgoingVertices();
			}
		};
		actionSelectIncomingArcs = new BaseAction("STR_selectIncomingArcs", null,
				"STR_selectIncomingArcs_descr", null) {

			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.selectIncomingArcs();
			}
		};
		actionSelectOutgoingArcs = new BaseAction("STR_selectOutgoingArcs", null,
				"STR_selectOutgoingArcs_descr", null) {

			private static final long	serialVersionUID	= 7152250065865101484L;

			public void actionPerformed(java.awt.event.ActionEvent e) {
				editcallback.selectOutgoingArcs();
			}
		};


		
		// we could have auto-generated open/new menu, but we don't want it now!
//		if (false) {
//			// add menuentry / toolbar buttons for those actions
//			openMenu = new JMenu(Translator.getString("STR_open"));
//			newMenu = new JMenu(Translator.getString("STR_new"));
//			Vector v_graph = GsEnv.getGraphType();
//			for (int i = 0 ; i < v_graph.size() ; i++) {
//				GsGraphDescriptor gd = (GsGraphDescriptor)v_graph.get(i);
//				openMenu.add(new GsOpenAction(gd, GsOpenAction.MODE_OPEN,
//						mainFrame));
//				if (gd.canCreate()) {
//					newMenu.add(new GsOpenAction(gd, GsOpenAction.MODE_NEW,
//							mainFrame));
//				}
//			}
//			fileMenu.add(newMenu);
//			fileMenu.add(openMenu);
//		} else {
			GsGraphDescriptor gd = GsGinsimGraphDescriptor.getInstance();
			actionNew = new GsOpenAction(gd, GsOpenAction.MODE_NEW, mainFrame,
					"STR_new", "STR_new_descr", KeyStroke.getKeyStroke(
							KeyEvent.VK_N, mask));
			fileMenu.add(actionNew);
			actionOpen = new GsOpenAction(gd, GsOpenAction.MODE_OPEN,
					mainFrame, "STR_open", "STR_open_descr", KeyStroke
							.getKeyStroke(KeyEvent.VK_O, mask));
			fileMenu.add(actionOpen);
			actionOpenAndDo = new GsOpenAction(gd, GsOpenAction.MODE_OPEN_AND_DO,
					mainFrame, "STR_open_and_do", "STR_open_and_do_descr", KeyStroke
							.getKeyStroke(KeyEvent.VK_O, mask | KeyEvent.SHIFT_DOWN_MASK));
			fileMenu.add(actionOpenAndDo);
//		}

		btt_simpleFunctionEdition = new JCheckBoxMenuItem(actionSimpleFunctionEdition);

		recentMenu = new JMenu(Translator.getString("STR_openRecent"));
		fileMenu.add(recentMenu);
		updateRecentMenu();
		fileMenu.add(importMenu);

		fileMenu.add(actionMergeGraph);
		fileMenu.add(actionClose);
		fileMenu.add(new JSeparator());

		// separateur
		fileMenu.add(actionSave);
		fileMenu.add(actionSaveAs);
		fileMenu.add(actionSaveSubGraph);
		fileMenu.add(exportMenu);
		fileMenu.add(new JSeparator());

		fileMenu.add(actionQuit);

		helpMenu.add(actionHelp);
		helpMenu.add(actionAbout);

		editMenu.add(actionCopy);
		editMenu.add(actionPaste);
		sepEdit = new JSeparator();
		editMenu.add(sepEdit);
		editMenu.add(addVertexMenu);
		editMenu.add(addEdgeMenu);
		editMenu.add(new JSeparator());
		editMenu.add(actionSearchNode);
		editMenu.add(new JSeparator());
		editMenu.add(editSelectMenu);
		editMenu.add(selectAllMenu);
		editMenu.add(editExtendSelectionMenu);
		editMenu.add(invertSelectionMenu);
		editMenu.add(simpleFunctionEdition_separator);
		editMenu.add(btt_simpleFunctionEdition);

		invertSelectionMenu.add(actionInvertSelection);
		invertSelectionMenu.add(actionInvertVertexSelection);
		invertSelectionMenu.add(actionInvertEdgeSelection);

		selectAllMenu.add(actionSelectAll);
		selectAllMenu.add(actionSelectAllNodes);
		selectAllMenu.add(actionSelectAllEdges);

		editSelectMenu.add(actionSelectIncomingVertices);
		editSelectMenu.add(actionSelectOutgoingVertices);
		editSelectMenu.add(actionSelectIncomingArcs);
		editSelectMenu.add(actionSelectOutgoingArcs);
		
		editExtendSelectionMenu.add(actionExtendSelectionToIncomingVertices);
		editExtendSelectionMenu.add(actionExtendSelectionToOutgoingVertices);
		editExtendSelectionMenu.add(actionExtendSelectionToIncomingArcs);
		editExtendSelectionMenu.add(actionExtendSelectionToOutgoingArcs);
		
		actionMenu.add(layoutMenu);

		btt_divideWindow = new JCheckBoxMenuItem(actionDivideWindow);
		btt_displayEdgeName = new JCheckBoxMenuItem(actionDisplayEdgeName);
		btt_displayEdgeName = new JCheckBoxMenuItem(actionDisplayEdgeName);
		btt_displayGrid = new JCheckBoxMenuItem(actionDisplayGrid);
		btt_gridActive = new JCheckBoxMenuItem(actionGridActive);
		btt_displayMiniMap = new JCheckBoxMenuItem(actionDisplayMiniMap);
		btt_vertextofront = new JCheckBoxMenuItem(actionVertexToFront);
		btt_displayGrid.setSelected(true);
		btt_displayMiniMap.setSelected(true);
		viewMenu.add(actionZoomIn);
		viewMenu.add(actionZoomOut);
		viewMenu.add(actionNormalSize);
		viewMenu.add(btt_divideWindow);
		viewMenu.add(new JSeparator());
		viewMenu.add(btt_displayEdgeName);
		viewMenu.add(btt_vertextofront);
		viewMenu.add(btt_displayGrid);
		viewMenu.add(btt_gridActive);
		viewMenu.add(btt_displayMiniMap);
		
		registerForMacOSXEvents();
	}

	/**
	 * update the content of the "recent files" submenu
	 */
	public void updateRecentMenu() {
		recentMenu.removeAll();
		Vector v_recent = OptionStore.getRecent();
		GsGraphDescriptor gd = GsGinsimGraphDescriptor.getInstance();
		for (int i = v_recent.size() - 1 ; i >= 0 ; i--) {
			recentMenu.add(new GsOpenAction(gd, mainFrame, (String)v_recent
					.get(i)));
		}
		if (v_recent.size() == 0) {
			recentMenu.setEnabled(false);
		} else {
			recentMenu.setEnabled(true);
		}
	}

	/**
	 * apply saved and default values
	 */
	public void setDefaults() {
		btt_divideWindow.setState(((Boolean)OptionStore.getOption("display.dividewindow", Boolean.FALSE)).booleanValue());
		actionDivideWindow.actionPerformed(new ActionEvent(btt_divideWindow, 0, ""));

		btt_displayGrid.setState(((Boolean)OptionStore.getOption("display.grid", Boolean.TRUE)).booleanValue());
		actionDisplayGrid.actionPerformed(new ActionEvent(btt_displayGrid, 0, ""));

		btt_gridActive.setState(((Boolean)OptionStore.getOption("display.gridactive", Boolean.FALSE)).booleanValue());
		actionGridActive.actionPerformed(new ActionEvent(btt_gridActive, 0, ""));

		btt_displayMiniMap.setState(((Boolean)OptionStore.getOption("display.minimap", Boolean.TRUE)).booleanValue());
		actionDisplayMiniMap.actionPerformed(new ActionEvent(btt_displayMiniMap, 0, ""));
	
		btt_simpleFunctionEdition.setState(((Boolean)OptionStore.getOption("edit.simpleFunctionEdition", Boolean.FALSE)).booleanValue());
		actionSimpleFunctionEdition.actionPerformed(new ActionEvent(btt_simpleFunctionEdition, 0, ""));
	}

	private void initToolbar() {
		toolBar.removeAll();

		// if we have new/open action use them
		if (actionOpen != null) {
			toolBar.add(actionNew);
			toolBar.add(actionOpen);
		}
		toolBar.add(actionSave);

		((JComponent)toolBar.add(new JSeparator(SwingConstants.VERTICAL)))
				.setMaximumSize(new java.awt.Dimension(10, 30));
	}

	/**
	 * get the menu bar
	 *
	 * @return the main menubar
	 */
	public javax.swing.JMenuBar getMenuBar() {
		return menuBar;
	}

	/**
	 * get the tools bar
	 *
	 * @return the main toolbar
	 */
	public javax.swing.JToolBar getToolBar() {
		return toolBar;
	}

	/**
	 * Check/uncheck the divide windows menu
	 *
	 * @param b
	 */
	public void setSelectedDivideCheckBox(boolean b) {
		btt_divideWindow.setSelected(b);
	}

	/**
	 * get the file callback
	 *
	 * @return the file callback
	 */
	public GsFileCallBack getFilecallback() {
		return filecallback;
	}

	public void graphChanged(GsNewGraphEvent event) {

		// empty / reinitialize toolbar and edit group
		Enumeration elt = editGroup.getElements();
		while (elt.hasMoreElements()) {
			editGroup.remove((AbstractButton)elt.nextElement());
		}
		v_editButtons.clear();
		initToolbar();
		// cleanup menu
		editMenu.remove(mi_edit);
		editMenu.remove(mi_delete);
		editMenu.remove(sepEdit);
		editMenu.remove(addEdgeMenu);
		editMenu.remove(addVertexMenu);

		GsGraph graph = event.getNewGraph();
		if (graph == null) {
			toolBar.repaint();
			return;
		}
		GsGraphManager graphManager = graph.getGraphManager();

		if (graphManager.canUndo()) {
			toolBar.add(actionUndo);
			toolBar.add(actionRedo);
			((JComponent)toolBar.add(new JSeparator(SwingConstants.VERTICAL)))
					.setMaximumSize(new java.awt.Dimension(7, 30));
		}
		if (graph.canDelete()) {
			toolBar.add(actionDelete);
			((JComponent)toolBar.add(new JSeparator(SwingConstants.VERTICAL)))
					.setMaximumSize(new java.awt.Dimension(7, 30));
		}

		Vector v_modes = graph.getEditingModes();
		addVertexMenu.removeAll();
		addEdgeMenu.removeAll();
		if (v_modes != null) {
			// customize edit toolbar and edit submenus
			nbEditEdgeSubmode = 0;
			nbEditVertexSubmode = 0;

			JToggleButton bt = new JToggleButton(actionEditDefault);
			bt.setText("");
			toolBar.add(bt);
			editGroup.add(bt);
			v_editButtons.add(bt);
			for (int i = 0 ; i < v_modes.size() ; i++) {
				GsEditSwitchAction sa = new GsEditSwitchAction(
						(GsEditModeDescriptor)v_modes.get(i), this);

				bt = new JToggleButton(sa);
				bt.setText("");
				toolBar.add(bt);
				editGroup.add(bt);
				v_editButtons.add(bt);

				int mode = ((GsEditModeDescriptor)v_modes.get(i)).mode;
				if (mode == MODE_ADD_VERTEX) {
					nbEditVertexSubmode++;
					addVertexMenu.add(sa);
				} else if (mode == MODE_ADD_EDGE) {
					nbEditEdgeSubmode++;
					addEdgeMenu.add(sa);
				}
			}
			editMenu.add(sepEdit, 2);
			if (graph.canDelete()) {
				editMenu.add(mi_delete, 3);
			}
			editMenu.add(mi_edit, 4);
			editMenu.add(addEdgeMenu, 5);
			editMenu.add(addVertexMenu, 6);
		}

		// fill export menu
		exportMenu.removeAll();
		addToMenu(exportMenu, graph.getExport(),
				GsActionProvider.ACTION_EXPORT, graph);
		addToMenu(exportMenu, graph.getSpecificExport(),
				GsActionProvider.ACTION_EXPORT, graph);
		addToMenu(exportMenu, graphManager.getExport(),
				GsActionProvider.ACTION_EXPORT, graph);

		// fill import menu
		importMenu.removeAll();
		addToMenu(importMenu, getImport(), GsActionProvider.ACTION_IMPORT, graph);

		// fill layout menu
		layoutMenu.removeAll();
		addToMenu(layoutMenu, graph.getLayout(),
				GsActionProvider.ACTION_LAYOUT, graph);
		addToMenu(layoutMenu, graph.getSpecificLayout(),
				GsActionProvider.ACTION_LAYOUT, graph);
		addToMenu(layoutMenu, graphManager.getLayout(),
				GsActionProvider.ACTION_LAYOUT, graph);

		// fill action menu
		actionMenu.removeAll();
		actionMenu.add(layoutMenu);
		addToMenu(actionMenu, graph.getAction(),
				GsActionProvider.ACTION_ACTION, graph);
		actionMenu.add(new JSeparator());
		addToMenu(actionMenu, graph.getSpecificAction(),
				GsActionProvider.ACTION_ACTION, graph);
		addToMenu(actionMenu, graphManager.getAction(),
				GsActionProvider.ACTION_ACTION, graph);

		// ensure being on a valid mode by default
		setCurrentMode(MODE_DEFAULT, 0, true);

		btt_vertextofront.setSelected(false);
		btt_vertextofront.doClick();
		toolBar.repaint();

		btt_displayGrid.setSelected(graph.getGraphManager().isGridDisplayed());
		btt_gridActive.setSelected(graph.getGraphManager().isGridActive());
		if (graph instanceof GsRegulatoryGraph) {
			btt_simpleFunctionEdition.setVisible(true);
			simpleFunctionEdition_separator.setVisible(true);
		} else {
			btt_simpleFunctionEdition.setVisible(false);
			simpleFunctionEdition_separator.setVisible(false);
		}
		
	}

	private void addToMenu(JMenu menu, List v_actions, int actionCode,
			GsGraph graph) {
		if (v_actions != null) {
			for (int i = 0 ; i < v_actions.size() ; i++) {
				Object obj = v_actions.get(i);
				if (obj instanceof GsPluggableActionDescriptor) {
                  //if (!((GsPluggableActionDescriptor)obj).isCheckbox())
					menu.add(new GsPluggableAction(
							(GsPluggableActionDescriptor)obj, mainFrame));
       //else
         //menu.add(new JCheckBoxMenuItem(new GsPluggableAction((GsPluggableActionDescriptor)obj, mainFrame)));
				} else if (obj instanceof GsActionProvider) {
					GsPluggableActionDescriptor[] t_action = null;
					t_action = ((GsActionProvider)obj).getT_action(actionCode,
							graph);
					if (t_action != null) {
						for (int j = 0 ; j < t_action.length ; j++) {
                          ////if (!t_action[j].isCheckbox())
							menu.add(new GsPluggableAction(t_action[j],
									mainFrame));
                             // else
                                //menu.add(new JCheckBoxMenuItem(new GsPluggableAction(t_action[j],
				//					mainFrame)));
						}
					}
				}
			}
		}
	}

	public void graphSelectionChanged(GsGraphSelectionChangeEvent event) {
		// get ready
	}

	/**
	 * @param index the index of the selected edit mode
	 */
	public void setSelectedAction(int index) {
		if (v_editButtons != null && v_editButtons.size() > index) {
			((JToggleButton)v_editButtons.get(index)).setSelected(true);
		}
	}

	/**
	 * 
	 * @param import
	 */
	public static void registerImportProvider(GsActionProvider imports) {
		if (v_import == null) {
			v_import = new Vector();
		}
		v_import.add(imports);
	}

	/**
	 * @return a list of avaible actions.
	 */
	public List getImport() {
		return v_import;
	}

	/**
	 * choose the new mode and submode. the submode is an option that will be
	 * passed to newly created vertices/edges (different kinds of fully graph
	 * dependant objects just one click away).
	 *
	 * @param mode the new editing mode: should be on of MODE_DEFAULT,
	 *        MODE_ADD_VERTEX, MODE_ADD_EDGE or MODE_ADD_EDGE_POINT/
	 * @param submode the submode, ie option for the object.
	 * @param locked if true, the mode won't change when creating the next
	 *        object.
	 */
	public void setCurrentMode(int mode, int submode, boolean locked) {
		int selected = 0;
		switch (mode) {
			case MODE_ADD_VERTEX:
				this.mode = mode;
				if (submode >= nbEditVertexSubmode) {
					this.submode = 0;
				} else {
					this.submode = submode;
				}
				this.locked = locked;
				selected = 1 + this.submode;
				break;

			case MODE_ADD_EDGE:
				if (submode >= nbEditEdgeSubmode) {
					this.submode = 0;
				} else {
					this.submode = submode;
				}
				this.mode = mode;
				this.submode = submode;
				this.locked = locked;
				selected = 1 + nbEditVertexSubmode + this.submode;
				break;

			case MODE_ADD_EDGE_POINT:
				this.mode = mode;
				this.submode = 0;
				this.locked = locked;
				selected = 1 + nbEditVertexSubmode + nbEditEdgeSubmode;
				break;
			default:
				this.mode = mode;
				this.submode = 0;
				this.locked = true;
				selected = 0;
		}
		setSelectedAction(selected);
	}

	/**
	 * @return the current editing mode.
	 */
	public int getCurrentMode() {
		return mode;
	}

	/**
	 * @return the current editing option.
	 */
	public int getCurrentSubmode() {
		return submode;
	}

	/**
	 * change the editing mode if it's not locked. (according to last call to
	 * <code>setCurrentMode(int, int, boolean)</code>)
	 */
	public void changeModeIfUnlocked() {
		if (locked == false) {
			setCurrentMode(MODE_DEFAULT, 0, false);
		}
	}

	public void graphClosed(GsGraph graph) {
	}

	public void updateGraphNotificationMessage(GsGraph graph) {
	}
	
    // Generic registration with the Mac OS X application menu
    // Checks the platform, then attempts to register with the Apple EAWT
    // See OSXAdapter.java to see how this is done without directly referencing any Apple APIs
    public void registerForMacOSXEvents() {
        if (Tools.os == Tools.SYS_MACOSX) {
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(this, this.getClass().getDeclaredMethod("quit", (Class[])null));
                OSXAdapter.setAboutHandler(this, this.getClass().getDeclaredMethod("about", (Class[])null));
                OSXAdapter.setFileHandler(this, this.getClass().getDeclaredMethod("loadGINML", new Class[] { String.class }));
            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
    }
	public void about() {
		new AboutDialog().setVisible(true);
	}
	public Object quit() {
		this.filecallback.quit();
		return Boolean.FALSE;
	}
	public void loadGINML(String filename) {
		GsOpenAction.open(GsGinsimGraphDescriptor.getInstance(), GsEnv.newMainFrame(), null, filename);
	}

	public boolean shouldAutoAddNewElements() {
		return btt_simpleFunctionEdition.isSelected();
	}

	@Override
	public void setCurrentMode(EditMode mode, boolean lock) {
		// FIXME: temporary empty, waiting for a replacement
	}

	@Override
	public EditMode getCurrentEditMode() {
		// FIXME: temporary empty, waiting for a replacement
		return null;
	}

	@Override
	public EditGroup getCurrentGroup() {
		// FIXME: temporary empty, waiting for a replacement
		return null;
	}
}


