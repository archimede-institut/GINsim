package org.ginsim.gui.graph.regulatorygraph.logicalfunction;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.GraphChangeType;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.LogicalFunctionView;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeElement;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeExpression;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeParam;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeString;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeValue;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.param2function.FunctionsCreator;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.BooleanFunctionTreeEditor;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.BooleanFunctionTreeRenderer;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.PanelFactory;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.TreeMenu;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd.ComponentAdapter;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd.DropListener;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd.GlassPane;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd.GsDragGestureListener;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd.GsDragSourceListener;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd.GsTransferable;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd.MotionAdapter;
import org.ginsim.gui.shell.MainFrame;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;


public class LogicalFunctionTreePanel extends AbstractParameterPanel implements LogicalFunctionView, KeyListener, MouseListener, ActionListener, TreeSelectionListener {
  private static final long serialVersionUID = -8323666225199589729L;

  class GsTreeUI extends BasicTreeUI {
    protected void paintExpandControl(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row,
                                      boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
      Object value = path.getLastPathComponent();
      if (!isLeaf && (!hasBeenExpanded || treeModel.getChildCount(value) > 0)) {
        int middleXOfKnob = bounds.x - (getRightChildIndent() - 1);
        int middleYOfKnob = bounds.y + 10; //(bounds.height / 2);
        if (isExpanded) {
          Icon expandedIcon = getExpandedIcon();
          if (expandedIcon != null) drawCentered(tree, g, expandedIcon, middleXOfKnob, middleYOfKnob);
        }
        else {
          Icon collapsedIcon = getCollapsedIcon();
          if (collapsedIcon != null) drawCentered(tree, g, collapsedIcon, middleXOfKnob, middleYOfKnob);
        }
      }
    }

    protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path,
                                            int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
      if (!isLeaf) return;
      super.paintHorizontalPartOfLeg(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
    }

    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {
      int depth = path.getPathCount() - 1;
      if (depth == 0 && !getShowsRootHandles() && !isRootVisible()) return;
      int lineX = (depth + 1 + depthOffset) * totalChildIndent - getRightChildIndent() + insets.left;
      int clipLeft = clipBounds.x;
      int clipRight = clipBounds.x + clipBounds.width - 1;
      if (((TreeElement) path.getLastPathComponent()).getChildCount() == 0) return;
      boolean leaf = ((TreeElement) path.getLastPathComponent()).getChild(0).isLeaf();
      if (lineX >= clipLeft && lineX <= clipRight) {
        int clipTop = clipBounds.y;
        int clipBottom = clipBounds.y + clipBounds.height;
        Rectangle parentBounds = getPathBounds(tree, path);
        Rectangle lastChildBounds = getPathBounds(tree, getLastChildPath(path));
        if (lastChildBounds == null) return;
        int top;
        if (parentBounds == null)
          top = Math.max(insets.top + getVerticalLegBuffer(), clipTop);
        else
          top = Math.max(parentBounds.y + parentBounds.height + getVerticalLegBuffer(), clipTop);
        if (depth == 0 && !isRootVisible()) {
          TreeModel model = getModel();
          if (model != null) {
            Object root = model.getRoot();
            if (model.getChildCount(root) > 0) {
              parentBounds = getPathBounds(tree, path.pathByAddingChild(model.getChild(root, 0)));
              if (parentBounds != null)
                top = Math.max(insets.top + getVerticalLegBuffer(), parentBounds.y + parentBounds.height / 2);
            }
          }
        }
        int bottom = Math.min(lastChildBounds.y + lastChildBounds.height / 2, clipBottom);
        if (!leaf) bottom = Math.min(lastChildBounds.y + 11, clipBottom);
        if (top <= bottom) {
          g.setColor(getHashColor());
          paintVerticalLine(g, tree, lineX, top, bottom);
        }
      }
    }
  }

  private JTree tree = null;
  private TreeInteractionsModel interactionList = null;
  private MotionAdapter motionAdapter;
  private ComponentAdapter componentAdapter;
  private DragSource dragSource;
  private DragGestureListener dragGestureListener;
  private GsDragSourceListener dragSourceListener;
  private DropListener dropListener;
  private TreeMenu menu;
  private GsTransferable transferable = null, current_transferable = null;
	private PanelFactory panelFactory;

  public LogicalFunctionTreePanel(RegulatoryGraph graph, LogicalFunctionPanel p) {
    super(graph);
    panelFactory = new PanelFactory(p);
    setLayout(new BorderLayout());
    add( new JScrollPane(getJTree(graph)), BorderLayout.CENTER);
    menu = new TreeMenu(this);
    tree.addMouseListener(this);
    tree.getSelectionModel().addTreeSelectionListener(this);
    tree.addMouseListener(p);
		tree.addKeyListener(p);
		tree.setFocusTraversalKeysEnabled(false);
  }

  public void setEditedItem(Object obj) {
    RegulatoryNode vertex = (RegulatoryNode) obj;
    interactionList = vertex.getInteractionsModel();
    interactionList.setNode(vertex);
    interactionList.setView(this);
    interactionList.setRootInfos();
    tree.stopEditing();
    tree.setModel(interactionList);
 }

  private JTree getJTree(RegulatoryGraph graph){
    if (tree == null) {
      interactionList = new TreeInteractionsModel(graph);
      tree = new JTree(interactionList);
      tree.setShowsRootHandles(true);
      BooleanFunctionTreeRenderer cr = new BooleanFunctionTreeRenderer(getPreferredSize().width, panelFactory);
      tree.setCellRenderer(cr);
      tree.setCellEditor(new BooleanFunctionTreeEditor(tree, cr));
      tree.setEditable(true);
      tree.addKeyListener(this);
      dragSource = DragSource.getDefaultDragSource();
      GlassPane glasspanel = null;
      if (this.frame != null && this.frame instanceof MainFrame) {
    	  glasspanel = (GlassPane) ((MainFrame) this.frame).getGlassPane();
      }
      else{
    	  GUIMessageUtils.openErrorDialog( "STR_treeviewer_noMainFrame");
    	  return null;
      }
      dropListener = new DropListener(this, glasspanel);
      dragSourceListener = new GsDragSourceListener(tree, glasspanel);
      dragGestureListener = new GsDragGestureListener(tree, dragSourceListener, dropListener);
      dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, dragGestureListener);
      new DropTarget(tree, DnDConstants.ACTION_COPY_OR_MOVE, dropListener, true);
      motionAdapter = new MotionAdapter(glasspanel);
      tree.addMouseMotionListener(motionAdapter);
      componentAdapter = new ComponentAdapter(glasspanel, "");
      tree.addMouseListener(componentAdapter);
      if (System.getProperty("os.name").indexOf("Mac") < 0) {
        TreeUI ui = new GsTreeUI();
        tree.setUI(ui);
      }
      addComponentListener(cr);
    }
    return tree;
  }

  public JTree getTree() {
    return tree;
  }

  @Override
  public void refresh() {
    tree.stopEditing();
    Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    interactionList.fireTreeStructureChanged((TreeElement) interactionList.getRoot());
    interactionList.refreshNode(false);
    if (enu != null)
      while (enu.hasMoreElements()) {
        TreePath tp = (TreePath) enu.nextElement();
        tree.expandPath(tp);
      }
  }

  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) deleteSelection();
  }

  public void keyTyped(KeyEvent e) {
  }

  private void deleteSelection() {
    Enumeration enu;
    Vector v;
    TreeElement treeElement;

    TreePath[] selectedPaths = tree.getSelectionPaths();
    if (selectedPaths != null) {
      enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      tree.stopEditing();
      v = new Vector();
      tree.stopEditing();
      for (int i = 0; i < selectedPaths.length; i++) {
        treeElement = (TreeElement) selectedPaths[i].getLastPathComponent();
        if (!(treeElement instanceof TreeParam)) {
          treeElement.remove(false);
          v.addElement(treeElement);
          if (treeElement.toString().equals("")) treeElement.getParent().setProperty("null function", new Boolean(false));
        }
      }
      
      // TODO: this is not the proper way to signal changes
      graph.fireGraphChange(GraphChangeType.OTHERCHANGE, null);
      
      
      TreeInteractionsModel interactionsModel = (TreeInteractionsModel)tree.getModel();
      interactionsModel.refreshNode();
      interactionsModel.setRootInfos();
      interactionsModel.fireTreeStructureChanged((TreeElement)interactionsModel.getRoot());
      while (enu.hasMoreElements()) {
        TreePath tp = (TreePath) enu.nextElement();
        if (!v.contains(tp.getLastPathComponent())) tree.expandPath(tp);
      }
    }
  }

	public TreeExpression getSelectedFunction () {
		TreePath[] selectedPaths = tree.getSelectionPaths();
		if (selectedPaths != null)
			if (selectedPaths.length == 1)
				if (selectedPaths[0].getLastPathComponent() instanceof TreeExpression)
					return (TreeExpression)selectedPaths[0].getLastPathComponent();
		return null;
	}
  public void mousePressed(MouseEvent e) {
  	boolean delete = false, copy = false, cut = false, paste = false;
  	TreeElement node = null;
  	if (e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger()) {
    	if (current_transferable != null) {
    		node = (TreeElement)tree.getSelectionPath().getLastPathComponent();
        delete = ((current_transferable.getCurrentFlavor() == GsTransferable.VALUE_FLAVOR) ||
      						(current_transferable.getCurrentFlavor() == GsTransferable.FUNCTION_FLAVOR) ||
      						((current_transferable.getCurrentFlavor() == GsTransferable.MIXED_FLAVOR) && 
      						 !current_transferable.containsRoot() && !current_transferable.containsParameter()));
      	cut = ((current_transferable.getCurrentFlavor() == GsTransferable.VALUE_FLAVOR) ||
						 	 (current_transferable.getCurrentFlavor() == GsTransferable.FUNCTION_FLAVOR));
      	copy = true;  
    	}
    	if ((transferable != null) && (node != null) && (tree.getSelectionCount() == 1)) {
    		if ((transferable.getCurrentFlavor() == GsTransferable.VALUE_FLAVOR) && node instanceof TreeString)
        	paste = true;
        else if ((transferable.getCurrentFlavor() == GsTransferable.FUNCTION_FLAVOR) && node instanceof TreeValue)
        	paste = true;
    	}
    	menu.setEnabled(TreeMenu.DELETE, delete);
    	menu.setEnabled(TreeMenu.COPY, copy);
    	menu.setEnabled(TreeMenu.CUT, cut);
    	menu.setEnabled(TreeMenu.PASTE, paste);
      
      if (tree.getSelectionCount() == 0) {
        menu.setEnabled(TreeMenu.CREATE_1_FUNCTION, false);
        menu.setEnabled(TreeMenu.CREATE_N_FUNCTIONS, false);
      }
      else {
      	if ((current_transferable.getCurrentFlavor() == GsTransferable.PARAM_FLAVOR) && current_transferable.isOneValue()) {
        	menu.setEnabled(TreeMenu.CREATE_1_FUNCTION, true);
          menu.setEnabled(TreeMenu.CREATE_N_FUNCTIONS, true);
        }
        else {
          menu.setEnabled(TreeMenu.CREATE_1_FUNCTION, false);
          menu.setEnabled(TreeMenu.CREATE_N_FUNCTIONS, false);
        }
      }
      menu.show(tree, e.getX(), e.getY());
      e.consume();
    }
  }

  public void mouseReleased(MouseEvent e) {}

  public void mouseClicked(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(TreeMenu.COPY)) {
    	transferable = current_transferable;
    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	TreeElement[] gste = current_transferable.getNodes();
    	String s = "";
    	for (int i = 0; i < gste.length; i++) s += gste[i].toString() + "\n";
    	StringSelection stringSelection = new StringSelection(s);
    	clipboard.setContents(stringSelection, new ClipboardOwner() {
        public void lostOwnership(Clipboard aClipboard, Transferable aContents) {}
      });
    }
    else if (e.getActionCommand().equals(TreeMenu.CUT)) {
      transferable = current_transferable;
      deleteSelection();
    }
    else if (e.getActionCommand().equals(TreeMenu.PASTE)) {
      TreeElement node = (TreeElement) tree.getSelectionPath().getLastPathComponent();
      if (transferable.getCurrentFlavor().equals(GsTransferable.FUNCTION_FLAVOR) && node instanceof TreeValue)
        pasteFunctionsInValue(transferable.getNodes(), ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK),
                              (TreeValue) node);
      else if (transferable.getCurrentFlavor().equals(GsTransferable.PARAM_FLAVOR) && node instanceof TreeValue)
        pasteParamsInValue(transferable.getNodes(), ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK),
                           (TreeValue) node);
      else if (transferable.getCurrentFlavor().equals(GsTransferable.VALUE_FLAVOR) && node instanceof TreeString)
        pasteValuesInRoot(transferable.getNodes(), (TreeString)node);
    }
    else if (e.getActionCommand().equals(TreeMenu.DELETE)) {
      deleteSelection();
      transferable = null;
    }
    else if (e.getActionCommand().equals(TreeMenu.CREATE_1_FUNCTION)) {
      createFunctions(true);
    }
    else if (e.getActionCommand().equals(TreeMenu.CREATE_N_FUNCTIONS)) {
      createFunctions(false);
    }
  }

  public void valueChanged(TreeSelectionEvent e) {
    TreePath[] selectedPaths = tree.getSelectionPaths();
    TreeElement[] nodes = new TreeElement[tree.getSelectionCount()];
    if (tree.getSelectionCount() != 0) {
      for (int i = 0; i < tree.getSelectionCount(); i++)
      	nodes[i] = (TreeElement) selectedPaths[i].getLastPathComponent();
      current_transferable = new GsTransferable(nodes);
    }
    else
      current_transferable = null;
  }

  public void pasteFunctionsInValue(TreeElement[] functions, boolean remove, TreeValue value) {
    Enumeration enu;
    TreePath tp;
    try {
      enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      for (int i = 0; i < functions.length; i++)
        if (((TreeValue) functions[i].getParent()).getValue() != value.getValue()) {
          interactionList.addExpression(tree, (byte) value.getValue(), interactionList.getNode(), functions[i].toString());
          if (remove) functions[i].remove(false);
          interactionList.removeNullFunction((byte) value.getValue());
        }
      interactionList.fireTreeStructureChanged((TreeElement) tree.getPathForRow(0).getLastPathComponent());
      interactionList.refreshNode();
      while (enu.hasMoreElements()) {
        tp = (TreePath) enu.nextElement();
        tree.expandPath(tp);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
	public void pasteExpressionsInExpression(TreeElement[] source, boolean move, TreeExpression target) {
		String s = "(" + target.toString() + ")";
		for (int i = 0; i < source.length; i++) {
			s += " | (" + source[i].toString() + ")";
			if (move) source[i].remove(false);
		}
		target.getGraphicPanel().validateText(s);
	}

  public void pasteValuesInRoot(TreeElement[] values, TreeString root) {
    Enumeration enu;
    TreePath tp;

    try {
      enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      for (int i = 0; i < values.length; i++) {
        values[i].remove(false);
        interactionList.addValue((TreeValue) values[i]);
      }
      interactionList.fireTreeStructureChanged((TreeElement) tree.getPathForRow(0).getLastPathComponent());
      interactionList.refreshNode();
      while (enu.hasMoreElements()) {
        tp = (TreePath) enu.nextElement();
        tree.expandPath(tp);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

//  public void pasteFunctionsInManual(TreeElement[] functions, boolean remove, GsTreeManual manual) {
//    Enumeration enu, enu2;
//    TreePath tp;
//    TreeParam param;
//
//    try {
//      enu = tree.getExpandedDescendants(tree.getPathForRow(0));
//      for (int i = 0; i < functions.length; i++) {
//        enu2 = functions[i].getChilds().elements();
//        while (enu2.hasMoreElements()) {
//          param = (TreeParam) enu2.nextElement();
//          manual.addChild(new TreeParam(manual, param.getEdgeIndexes()), -1);
//        }
//        if (remove) {
//          functions[i].remove(false);
//        }
//      }
//      interactionList.fireTreeStructureChanged((TreeElement) tree.getPathForRow(0).getLastPathComponent());
//      interactionList.refreshNode();
//      while (enu.hasMoreElements()) {
//        tp = (TreePath) enu.nextElement();
//        tree.expandPath(tp);
//      }
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//    }
//  }

/*  public void pasteManualsInValue(TreeElement[] manuals, boolean remove, TreeValue value) {
    Enumeration enu, enu2;
    TreeParam param;
    TreePath tp;

    enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    for (int i = 0; i < manuals.length; i++) {
      enu2 = manuals[i].getChilds().elements();
      while (enu2.hasMoreElements()) {
        param = (TreeParam) enu2.nextElement();
        value.getChild(0).addChild(new TreeParam(value.getChild(0), param.getEdgeIndexes()), -1);
      }
      if (remove) {
        manuals[i].clearChilds();
      }
    }
    interactionList.fireTreeStructureChanged((TreeElement) tree.getPathForRow(0).getLastPathComponent());
    interactionList.refreshNode();
    while (enu.hasMoreElements()) {
      tp = (TreePath) enu.nextElement();
      tree.expandPath(tp);
    }
  }
*/
  public void pasteParamsInValue(TreeElement[] params, boolean remove, TreeValue value) {
    Enumeration enu;
    TreePath tp;

    enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    for (int i = 0; i < params.length; i++) {
      value.getChild(0).addChild(new TreeParam(value.getChild(0),((TreeParam) params[i]).getEdgeIndexes()), -1);
      if (remove) params[i].remove(false);
    }
    interactionList.fireTreeStructureChanged((TreeElement) tree.getPathForRow(0).getLastPathComponent());
    interactionList.refreshNode();
    while (enu.hasMoreElements()) {
      tp = (TreePath) enu.nextElement();
      tree.expandPath(tp);
    }
  }

  private void createFunctions(boolean oneFunction) {
    boolean res = false;
    tree.stopEditing();
    TreePath[] selectedPaths = tree.getSelectionPaths();
    if (current_transferable.getCurrentFlavor().equals(GsTransferable.PARAM_FLAVOR)) {
      res = doChaos(current_transferable.getNodes(), oneFunction);
      if (res) {
        tree.setSelectionPaths(selectedPaths);
        deleteSelection();
      }
    }
    else {
      TreeElement[] manuals = current_transferable.getNodes();
      TreeElement[] te;
      Object[] o;
      Object[] path = new Object[3];
      for (int i = 0; i < manuals.length; i++) {
        o = manuals[i].getChilds().toArray();
        if (o.length > 0) {
          te = new TreeElement[o.length];
          for (int k = 0; k < o.length; k++) te[k] = (TreeElement) o[k];
          res = doChaos(te, oneFunction);
          if (res) {
            path[0] = tree.getModel().getRoot();
            path[1] = manuals[i].getParent();
            path[2] = manuals[i];
            tree.setSelectionPath(new TreePath(path));
            deleteSelection();
          }
        }
      }
    }
  }

  private boolean doChaos(TreeElement[] params, boolean oneFunction) {
    FunctionsCreator c = null;
    Vector v = new Vector();
    int value = ((TreeValue) params[0].getParent().getParent()).getValue();
    LogicalParameter lp;

    for (int i = 0; i < params.length; i++) {
      lp = new LogicalParameter(value);
      lp.setEdges(((TreeParam) params[i]).getEdgeIndexes());
      v.addElement(lp);
    }
    c = new FunctionsCreator((RegulatoryGraph) graph, v, interactionList.getNode());

    Hashtable h = c.doIt(false);

    Enumeration enu = h.keys(), enu2;
    Integer key;
    String s;

    if (!oneFunction) {
      while (enu.hasMoreElements()) {
        key = (Integer) enu.nextElement();
        v = (Vector) h.get(key);
        for (enu2 = v.elements(); enu2.hasMoreElements(); ) {
          s = (String) enu2.nextElement();
          try {
            interactionList.addExpression(null, key.byteValue(), interactionList.getNode(), s);
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    }
    else {
      while (enu.hasMoreElements()) {
        key = (Integer) enu.nextElement();
        v = (Vector) h.get(key);
        enu2 = v.elements();
        s = (String) enu2.nextElement();
        while (enu2.hasMoreElements()) s = s + " | (" + (String)enu2.nextElement() + ")";
        try {
          interactionList.addExpression(null, key.byteValue(), interactionList.getNode(), s);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    interactionList.setRootInfos();
    interactionList.fireTreeStructureChanged((TreeElement) interactionList.getRoot());
    return !h.isEmpty();
  }
}
