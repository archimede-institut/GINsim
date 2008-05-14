package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
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

import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeString;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.GsFunctionsCreator;

public class GsLogicalFunctionTreePanel extends GsParameterPanel implements
    KeyListener, MouseListener,
    ActionListener, TreeSelectionListener {
  private static final long serialVersionUID = -8323666225199589729L;

  class GsTreeUI extends BasicTreeUI {
    protected void paintExpandControl(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds,
                                      TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded,
                                      boolean isLeaf) {
      Object value = path.getLastPathComponent();
      if (!isLeaf && (!hasBeenExpanded || treeModel.getChildCount(value) > 0)) {
        int middleXOfKnob = bounds.x - (getRightChildIndent() - 1);
        int middleYOfKnob = bounds.y + 10; //(bounds.height / 2);
        if (isExpanded) {
          Icon expandedIcon = getExpandedIcon();
          if (expandedIcon != null) {
            drawCentered(tree, g, expandedIcon, middleXOfKnob, middleYOfKnob);
          }
        }
        else {
          Icon collapsedIcon = getCollapsedIcon();
          if (collapsedIcon != null) {
            drawCentered(tree, g, collapsedIcon, middleXOfKnob, middleYOfKnob);
          }
        }
      }
    }

    protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds,
                                            TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded,
                                             boolean isLeaf) {
      if (!isLeaf) {
        return;
      }
      super.paintHorizontalPartOfLeg(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
    }

    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {
      int depth = path.getPathCount() - 1;
      if (depth == 0 && !getShowsRootHandles() && !isRootVisible()) {
        return;
      }
      int lineX = (depth + 1 + depthOffset) * totalChildIndent - getRightChildIndent() + insets.left;
      int clipLeft = clipBounds.x;
      int clipRight = clipBounds.x + clipBounds.width - 1;
      if (((GsTreeElement) path.getLastPathComponent()).getChildCount() == 0) {
        return;
      }
      boolean leaf = ((GsTreeElement) path.getLastPathComponent()).getChild(0).isLeaf();
      if (lineX >= clipLeft && lineX <= clipRight) {
        int clipTop = clipBounds.y;
        int clipBottom = clipBounds.y + clipBounds.height;
        Rectangle parentBounds = getPathBounds(tree, path);
        Rectangle lastChildBounds = getPathBounds(tree, getLastChildPath(path));
        if (lastChildBounds == null) {
          return;
        }

        int top;
        if (parentBounds == null) {
          top = Math.max(insets.top + getVerticalLegBuffer(), clipTop);
        }
        else {
          top = Math.max(parentBounds.y + parentBounds.height + getVerticalLegBuffer(), clipTop);
        }
        if (depth == 0 && !isRootVisible()) {
          TreeModel model = getModel();
          if (model != null) {
            Object root = model.getRoot();
            if (model.getChildCount(root) > 0) {
              parentBounds = getPathBounds(tree, path.pathByAddingChild(model.getChild(root, 0)));
              if (parentBounds != null) {
                top = Math.max(insets.top + getVerticalLegBuffer(), parentBounds.y + parentBounds.height / 2);
              }
            }
          }
        }
        int bottom = Math.min(lastChildBounds.y + lastChildBounds.height / 2, clipBottom);
        if (!leaf) {
          bottom = Math.min(lastChildBounds.y + 11, clipBottom);
        }
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

  public GsLogicalFunctionTreePanel(GsRegulatoryGraph graph) {
    super();
    setLayout(new BorderLayout());
    add(new JScrollPane(getJTree(graph)), BorderLayout.CENTER);
    this.graph = graph;
    new GsPanelFactory();
    menu = new GsTreeMenu(this);
    tree.addMouseListener(this);
    tree.getSelectionModel().addTreeSelectionListener(this);
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
      GsBooleanFunctionTreeRenderer cr = new GsBooleanFunctionTreeRenderer(getPreferredSize().width);
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
    if (enu != null) {
      while (enu.hasMoreElements()) {
        TreePath tp = (TreePath) enu.nextElement();
        tree.expandPath(tp);
      }
    }
  }

  public void keyPressed(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
		deleteSelection();
	}
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
          if (treeElement.toString().equals("")) {
            treeElement.getParent().setProperty("null function", new Boolean(false));
          }
        }
      }
      GsTreeInteractionsModel interactionsModel = (GsTreeInteractionsModel)tree.getModel();
      interactionsModel.refreshVertex();
      interactionsModel.setRootInfos();
      interactionsModel.fireTreeStructureChanged((GsTreeElement)interactionsModel.getRoot());
      while (enu.hasMoreElements()) {
        TreePath tp = (TreePath) enu.nextElement();
        if (!v.contains(tp.getLastPathComponent())) {
          tree.expandPath(tp);
        }
      }
    }
  }

  public void mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger()) {
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
    }
    else if (e.getActionCommand().equals(GsTreeMenu.CUT)) {
      transferable = current_transferable;
      deleteSelection();
    }
    else if (e.getActionCommand().equals(GsTreeMenu.PASTE)) {
      GsTreeElement node = (GsTreeElement) tree.getSelectionPath().getLastPathComponent();
      if (transferable != null) {
        if (transferable.getCurrentFlavor() == GsTransferable.FUNCTION_FLAVOR && node instanceof GsTreeValue) {
          pasteFunctionsInValue(transferable.getNodes(),
                                ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK),
                                (GsTreeValue) node);
        }
        else if (transferable.getCurrentFlavor() == GsTransferable.VALUE_FLAVOR && node instanceof GsTreeString) {
          pasteValuesInRoot(transferable.getNodes(), (GsTreeString) node);
        }
        /*else if (transferable.getCurrentFlavor() == GsTransferable.MANUAL_FLAVOR && node instanceof GsTreeValue) {
          pasteManualsInValue(transferable.getNodes(),
                              ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK),
                              (GsTreeValue) node);
        }*/
        else if (transferable.getCurrentFlavor() == GsTransferable.PARAM_FLAVOR && node instanceof GsTreeValue) {
          pasteParamsInValue(transferable.getNodes(),
                             ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK),
                             (GsTreeValue) node);
        }
      }
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
    for (int i = 0; i < tree.getSelectionCount(); i++) {
      nodes[i] = (GsTreeElement) selectedPaths[i].getLastPathComponent();
    }
    if (tree.getSelectionCount() != 0) {
      current_transferable = new GsTransferable(nodes);
    }
    else {
      current_transferable = null;
    }
    menu.setEnabled(GsTreeMenu.COPY, (tree.getSelectionCount() > 0));
    menu.setEnabled(GsTreeMenu.CUT, tree.getSelectionCount() > 0 &&
                    current_transferable.getCurrentFlavor() != GsTransferable.MIXED_FLAVOR);
    menu.setEnabled(GsTreeMenu.DELETE, (tree.getSelectionCount() > 0));
    if (tree.getSelectionCount() == 0) {
      menu.setEnabled(GsTreeMenu.CREATE_1_FUNCTION, false);
      menu.setEnabled(GsTreeMenu.CREATE_N_FUNCTIONS, false);
      menu.setEnabled(GsTreeMenu.PASTE, false);
    }
    else {
      if (current_transferable.getCurrentFlavor() == GsTransferable.PARAM_FLAVOR) {
        menu.setEnabled(GsTreeMenu.CREATE_1_FUNCTION, true);
        menu.setEnabled(GsTreeMenu.CREATE_N_FUNCTIONS, true);
      }
      else {
        menu.setEnabled(GsTreeMenu.CREATE_1_FUNCTION, false);
        menu.setEnabled(GsTreeMenu.CREATE_N_FUNCTIONS, false);
      }
      if (transferable != null && tree.getSelectionCount() == 1) {
        if (current_transferable.getCurrentFlavor() == GsTransferable.VALUE_FLAVOR) {
          if (transferable.getCurrentFlavor() == GsTransferable.FUNCTION_FLAVOR ||
              transferable.getCurrentFlavor() == GsTransferable.PARAM_FLAVOR) {
            menu.setEnabled(GsTreeMenu.PASTE, true);
          }
          else {
            menu.setEnabled(GsTreeMenu.PASTE, false);
          }
        }
        else if (tree.getSelectionPath().getLastPathComponent() instanceof GsTreeString &&
                   transferable.getCurrentFlavor() == GsTransferable.VALUE_FLAVOR) {
          menu.setEnabled(GsTreeMenu.PASTE, true);
        }
        else {
          menu.setEnabled(GsTreeMenu.PASTE, false);
        }
      }
    }
  }

  public void pasteFunctionsInValue(GsTreeElement[] functions, boolean remove, GsTreeValue value) {
    Enumeration enu;
    TreePath tp;
    try {
      enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      for (int i = 0; i < functions.length; i++) {
        if (((GsTreeValue) functions[i].getParent()).getValue() != value.getValue()) {
          interactionList.addExpression(tree, (short) value.getValue(), interactionList.getVertex(),
                                        functions[i].toString());
          if (remove) {
            functions[i].remove(false);
          }
          interactionList.removeNullFunction((short) value.getValue());
        }
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
      if (remove) {
        params[i].remove(false);
      }
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
    if (current_transferable.getCurrentFlavor() == GsTransferable.PARAM_FLAVOR) {
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
          for (int k = 0; k < o.length; k++) {
            te[k] = (GsTreeElement) o[k];
          }
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
    c = new GsFunctionsCreator((GsRegulatoryGraph)graph, v, interactionList.getVertex());

    Hashtable h = c.doIt();

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
            interactionList.addExpression(null, key.shortValue(), interactionList.getVertex(), s);
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
        while (enu2.hasMoreElements()) {
          s = s + " | (" + (String)enu2.nextElement() + ")";
        }
        try {
          interactionList.addExpression(null, key.shortValue(), interactionList.getVertex(), s);
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
