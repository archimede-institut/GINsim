package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.tree.*;

import fr.univmrs.tagc.GINsim.gui.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.*;

public class GsLogicalFunctionTreePanel extends GsParameterPanel implements KeyListener, MouseListener, ActionListener, TreeSelectionListener {
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
      if (((GsTreeElement) path.getLastPathComponent()).getChildCount() == 0) return;
      boolean leaf = ((GsTreeElement) path.getLastPathComponent()).getChild(0).isLeaf();
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
  private GsTreeInteractionsModel interactionList = null;
  private GsMotionAdapter motionAdapter;
  private GsComponentAdapter componentAdapter;
  private DragSource dragSource;
  private DragGestureListener dragGestureListener;
  private GsDragSourceListener dragSourceListener;
  private GsDropListener dropListener;
  private GsTreeMenu menu;
  private GsTransferable transferable = null, current_transferable = null;
	private GsPanelFactory panelFactory;

  public GsLogicalFunctionTreePanel(GsRegulatoryGraph graph, GsLogicalFunctionPanel p) {
    super();
		panelFactory = new GsPanelFactory(p);
    setLayout(new BorderLayout());
    add(new JScrollPane(getJTree(graph)), BorderLayout.CENTER);
    this.graph = graph;
    menu = new GsTreeMenu(this);
    tree.addMouseListener(this);
    tree.getSelectionModel().addTreeSelectionListener(this);
    tree.addMouseListener(p);
		tree.addKeyListener(p);
		tree.setFocusTraversalKeysEnabled(false);
  }

  public void setEditedObject(Object obj) {
    GsRegulatoryVertex vertex = (GsRegulatoryVertex) obj;
    interactionList = vertex.getInteractionsModel();
    interactionList.setNode(vertex);
    interactionList.setView(this);
    interactionList.setRootInfos();
    tree.stopEditing();
    tree.setModel(interactionList);
 }

  private JTree getJTree(GsRegulatoryGraph graph) {
    if (tree == null) {
      interactionList = new GsTreeInteractionsModel(graph);
      tree = new JTree(interactionList);
      tree.setShowsRootHandles(true);
      GsBooleanFunctionTreeRenderer cr = new GsBooleanFunctionTreeRenderer(getPreferredSize().width, panelFactory);
      tree.setCellRenderer(cr);
      tree.setCellEditor(new GsBooleanFunctionTreeEditor(tree, cr));
      tree.setEditable(true);
      tree.addKeyListener(this);
      dragSource = DragSource.getDefaultDragSource();
      dropListener = new GsDropListener(this, (GsGlassPane) graph.getGraphManager().getMainFrame().getGlassPane());
      dragSourceListener = new GsDragSourceListener(tree, (GsGlassPane) graph.getGraphManager().getMainFrame().getGlassPane());
      dragGestureListener = new GsDragGestureListener(tree, dragSourceListener, dropListener);
      dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, dragGestureListener);
      new DropTarget(tree, DnDConstants.ACTION_COPY_OR_MOVE, dropListener, true);
      motionAdapter = new GsMotionAdapter((GsGlassPane) graph.getGraphManager().getMainFrame().getGlassPane());
      tree.addMouseMotionListener(motionAdapter);
      componentAdapter = new GsComponentAdapter((GsGlassPane) graph.getGraphManager().getMainFrame().getGlassPane(), "");
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

  public void refresh() {
    tree.stopEditing();
    Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    interactionList.fireTreeStructureChanged((GsTreeElement) interactionList.getRoot());
    interactionList.refreshVertex();
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
    GsTreeElement treeElement;

    TreePath[] selectedPaths = tree.getSelectionPaths();
    if (selectedPaths != null) {
      enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      tree.stopEditing();
      v = new Vector();
      tree.stopEditing();
      for (int i = 0; i < selectedPaths.length; i++) {
        treeElement = (GsTreeElement) selectedPaths[i].getLastPathComponent();
        if (!(treeElement instanceof GsTreeParam)) {
          treeElement.remove(false);
          v.addElement(treeElement);
          if (treeElement.toString().equals("")) treeElement.getParent().setProperty("null function", new Boolean(false));
        }
      }
      GsTreeInteractionsModel interactionsModel = (GsTreeInteractionsModel)tree.getModel();
      interactionsModel.refreshVertex();
      interactionsModel.setRootInfos();
      interactionsModel.fireTreeStructureChanged((GsTreeElement)interactionsModel.getRoot());
      while (enu.hasMoreElements()) {
        TreePath tp = (TreePath) enu.nextElement();
        if (!v.contains(tp.getLastPathComponent())) tree.expandPath(tp);
      }
    }
  }

	public GsTreeExpression getSelectedFunction () {
		TreePath[] selectedPaths = tree.getSelectionPaths();
		if (selectedPaths != null)
			if (selectedPaths.length == 1)
				if (selectedPaths[0].getLastPathComponent() instanceof GsTreeExpression)
					return (GsTreeExpression)selectedPaths[0].getLastPathComponent();
		return null;
	}
  public void mousePressed(MouseEvent e) {
  	boolean delete = false, copy = false, cut = false, paste = false;
  	GsTreeElement node = null;
  	if (e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger()) {
    	if (current_transferable != null) {
    		node = (GsTreeElement)tree.getSelectionPath().getLastPathComponent();
        delete = ((current_transferable.getCurrentFlavor() == GsTransferable.VALUE_FLAVOR) ||
      						(current_transferable.getCurrentFlavor() == GsTransferable.FUNCTION_FLAVOR) ||
      						((current_transferable.getCurrentFlavor() == GsTransferable.MIXED_FLAVOR) && 
      						 !current_transferable.containsRoot() && !current_transferable.containsParameter()));
      	cut = ((current_transferable.getCurrentFlavor() == GsTransferable.VALUE_FLAVOR) ||
						 	 (current_transferable.getCurrentFlavor() == GsTransferable.FUNCTION_FLAVOR));
      	copy = true;  
    	}
    	if ((transferable != null) && (node != null) && (tree.getSelectionCount() == 1)) {
    		if ((transferable.getCurrentFlavor() == GsTransferable.VALUE_FLAVOR) && node instanceof GsTreeString)
        	paste = true;
        else if ((transferable.getCurrentFlavor() == GsTransferable.FUNCTION_FLAVOR) && node instanceof GsTreeValue)
        	paste = true;
    	}
    	menu.setEnabled(GsTreeMenu.DELETE, delete);
    	menu.setEnabled(GsTreeMenu.COPY, copy);
    	menu.setEnabled(GsTreeMenu.CUT, cut);
    	menu.setEnabled(GsTreeMenu.PASTE, paste);
      
      if (tree.getSelectionCount() == 0) {
        menu.setEnabled(GsTreeMenu.CREATE_1_FUNCTION, false);
        menu.setEnabled(GsTreeMenu.CREATE_N_FUNCTIONS, false);
      }
      else {
      	if ((current_transferable.getCurrentFlavor() == GsTransferable.PARAM_FLAVOR) && current_transferable.isOneValue()) {
        	menu.setEnabled(GsTreeMenu.CREATE_1_FUNCTION, true);
          menu.setEnabled(GsTreeMenu.CREATE_N_FUNCTIONS, true);
        }
        else {
          menu.setEnabled(GsTreeMenu.CREATE_1_FUNCTION, false);
          menu.setEnabled(GsTreeMenu.CREATE_N_FUNCTIONS, false);
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
    if (e.getActionCommand().equals(GsTreeMenu.COPY)) {
    	transferable = current_transferable;
    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	GsTreeElement[] gste = current_transferable.getNodes();
    	String s = "";
    	for (int i = 0; i < gste.length; i++) s += gste[i].toString() + "\n";
    	StringSelection stringSelection = new StringSelection(s);
    	clipboard.setContents(stringSelection, new ClipboardOwner() {
        public void lostOwnership(Clipboard aClipboard, Transferable aContents) {}
      });
    }
    else if (e.getActionCommand().equals(GsTreeMenu.CUT)) {
      transferable = current_transferable;
      deleteSelection();
    }
    else if (e.getActionCommand().equals(GsTreeMenu.PASTE)) {
      GsTreeElement node = (GsTreeElement) tree.getSelectionPath().getLastPathComponent();
      if (transferable.getCurrentFlavor().equals(GsTransferable.FUNCTION_FLAVOR) && node instanceof GsTreeValue)
        pasteFunctionsInValue(transferable.getNodes(), ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK),
                              (GsTreeValue) node);
      else if (transferable.getCurrentFlavor().equals(GsTransferable.PARAM_FLAVOR) && node instanceof GsTreeValue)
        pasteParamsInValue(transferable.getNodes(), ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK),
                           (GsTreeValue) node);
      else if (transferable.getCurrentFlavor().equals(GsTransferable.VALUE_FLAVOR) && node instanceof GsTreeString)
        pasteValuesInRoot(transferable.getNodes(), (GsTreeString)node);
    }
    else if (e.getActionCommand().equals(GsTreeMenu.DELETE)) {
      deleteSelection();
      transferable = null;
    }
    else if (e.getActionCommand().equals(GsTreeMenu.CREATE_1_FUNCTION)) {
      createFunctions(true);
    }
    else if (e.getActionCommand().equals(GsTreeMenu.CREATE_N_FUNCTIONS)) {
      createFunctions(false);
    }
  }

  public void valueChanged(TreeSelectionEvent e) {
    TreePath[] selectedPaths = tree.getSelectionPaths();
    GsTreeElement[] nodes = new GsTreeElement[tree.getSelectionCount()];
    if (tree.getSelectionCount() != 0) {
      for (int i = 0; i < tree.getSelectionCount(); i++)
      	nodes[i] = (GsTreeElement) selectedPaths[i].getLastPathComponent();
      current_transferable = new GsTransferable(nodes);
    }
    else
      current_transferable = null;
  }

  public void pasteFunctionsInValue(GsTreeElement[] functions, boolean remove, GsTreeValue value) {
    Enumeration enu;
    TreePath tp;
    try {
      enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      for (int i = 0; i < functions.length; i++)
        if (((GsTreeValue) functions[i].getParent()).getValue() != value.getValue()) {
          interactionList.addExpression(tree, (byte) value.getValue(), interactionList.getVertex(), functions[i].toString());
          if (remove) functions[i].remove(false);
          interactionList.removeNullFunction((byte) value.getValue());
        }
      interactionList.fireTreeStructureChanged((GsTreeElement) tree.getPathForRow(0).getLastPathComponent());
      interactionList.refreshVertex();
      while (enu.hasMoreElements()) {
        tp = (TreePath) enu.nextElement();
        tree.expandPath(tp);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
	public void pasteExpressionsInExpression(GsTreeElement[] source, boolean move, GsTreeExpression target) {
		String s = "(" + target.toString() + ")";
		for (int i = 0; i < source.length; i++) {
			s += " | (" + source[i].toString() + ")";
			if (move) source[i].remove(false);
		}
		target.getGraphicPanel().validateText(s);
	}

  public void pasteValuesInRoot(GsTreeElement[] values, GsTreeString root) {
    Enumeration enu;
    TreePath tp;

    try {
      enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      for (int i = 0; i < values.length; i++) {
        values[i].remove(false);
        interactionList.addValue((GsTreeValue) values[i]);
      }
      interactionList.fireTreeStructureChanged((GsTreeElement) tree.getPathForRow(0).getLastPathComponent());
      interactionList.refreshVertex();
      while (enu.hasMoreElements()) {
        tp = (TreePath) enu.nextElement();
        tree.expandPath(tp);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

//  public void pasteFunctionsInManual(GsTreeElement[] functions, boolean remove, GsTreeManual manual) {
//    Enumeration enu, enu2;
//    TreePath tp;
//    GsTreeParam param;
//
//    try {
//      enu = tree.getExpandedDescendants(tree.getPathForRow(0));
//      for (int i = 0; i < functions.length; i++) {
//        enu2 = functions[i].getChilds().elements();
//        while (enu2.hasMoreElements()) {
//          param = (GsTreeParam) enu2.nextElement();
//          manual.addChild(new GsTreeParam(manual, param.getEdgeIndexes()), -1);
//        }
//        if (remove) {
//          functions[i].remove(false);
//        }
//      }
//      interactionList.fireTreeStructureChanged((GsTreeElement) tree.getPathForRow(0).getLastPathComponent());
//      interactionList.refreshVertex();
//      while (enu.hasMoreElements()) {
//        tp = (TreePath) enu.nextElement();
//        tree.expandPath(tp);
//      }
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//    }
//  }

/*  public void pasteManualsInValue(GsTreeElement[] manuals, boolean remove, GsTreeValue value) {
    Enumeration enu, enu2;
    GsTreeParam param;
    TreePath tp;

    enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    for (int i = 0; i < manuals.length; i++) {
      enu2 = manuals[i].getChilds().elements();
      while (enu2.hasMoreElements()) {
        param = (GsTreeParam) enu2.nextElement();
        value.getChild(0).addChild(new GsTreeParam(value.getChild(0), param.getEdgeIndexes()), -1);
      }
      if (remove) {
        manuals[i].clearChilds();
      }
    }
    interactionList.fireTreeStructureChanged((GsTreeElement) tree.getPathForRow(0).getLastPathComponent());
    interactionList.refreshVertex();
    while (enu.hasMoreElements()) {
      tp = (TreePath) enu.nextElement();
      tree.expandPath(tp);
    }
  }
*/
  public void pasteParamsInValue(GsTreeElement[] params, boolean remove, GsTreeValue value) {
    Enumeration enu;
    TreePath tp;

    enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    for (int i = 0; i < params.length; i++) {
      value.getChild(0).addChild(new GsTreeParam(value.getChild(0),((GsTreeParam) params[i]).getEdgeIndexes()), -1);
      if (remove) params[i].remove(false);
    }
    interactionList.fireTreeStructureChanged((GsTreeElement) tree.getPathForRow(0).getLastPathComponent());
    interactionList.refreshVertex();
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
      GsTreeElement[] manuals = current_transferable.getNodes();
      GsTreeElement[] te;
      Object[] o;
      Object[] path = new Object[3];
      for (int i = 0; i < manuals.length; i++) {
        o = manuals[i].getChilds().toArray();
        if (o.length > 0) {
          te = new GsTreeElement[o.length];
          for (int k = 0; k < o.length; k++) te[k] = (GsTreeElement) o[k];
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

  private boolean doChaos(GsTreeElement[] params, boolean oneFunction) {
    GsFunctionsCreator c = null;
    Vector v = new Vector();
    int value = ((GsTreeValue) params[0].getParent().getParent()).getValue();
    GsLogicalParameter lp;

    for (int i = 0; i < params.length; i++) {
      lp = new GsLogicalParameter(value);
      lp.setEdges(((GsTreeParam) params[i]).getEdgeIndexes());
      v.addElement(lp);
    }
    c = new GsFunctionsCreator((GsRegulatoryGraph) graph, v, interactionList.getVertex());

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
            interactionList.addExpression(null, key.byteValue(), interactionList.getVertex(), s);
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
          interactionList.addExpression(null, key.byteValue(), interactionList.getVertex(), s);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    interactionList.setRootInfos();
    interactionList.fireTreeStructureChanged((GsTreeElement) interactionList.getRoot());
    return !h.isEmpty();
  }
}
