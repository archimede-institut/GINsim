package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.GsFunctionEditor;
import fr.univmrs.ibdm.GINsim.util.widget.GsJButton;

public class GsFunctionPanel extends GsBooleanFunctionTreePanel implements ActionListener, KeyListener,
    PropertyChangeListener, MouseListener {
  private static final long serialVersionUID = 8900639275182677150L;
  private static final Color editColor = new Color(204, 255, 204);
  private JButton editButton;
  private JTextArea textArea;
  private JScrollPane jsp;
  private boolean toUpdate = false;

  public GsFunctionPanel(GsTreeElement value, JTree tree, boolean sel, int width, boolean edit) {
    super(value, tree, sel, width);
    setLayout(new BorderLayout(2, 2));
    setBackground(Color.white);
    editButton = new GsJButton("edit.png");
    editButton.addActionListener(this);
    buttonPanel.add(editButton);

    textArea = new JTextArea(text);
    textArea.setFont(defaultFont);
    textArea.setEditable(true);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(false);
    if (edit) {
      setBackground(editColor);
      textArea.setBackground(editColor);
      buttonPanel.setBackground(editColor);
    }
    else if (sel) {
      setBackground(Color.yellow);
      textArea.setBackground(Color.yellow);
      buttonPanel.setBackground(Color.yellow);
    }
    else if (((Boolean)value.getProperty("invalid")).booleanValue()) {
      setBackground(Color.red);
      textArea.setBackground(Color.red);
      buttonPanel.setBackground(Color.red);
    }
    else if (((Boolean)value.getProperty("autoedit")).booleanValue()) {
      setBackground(new Color(138, 181, 218));
      textArea.setBackground(new Color(138, 181, 218));
      buttonPanel.setBackground(new Color(138, 181, 218));
    }
    else {
      setBackground(Color.white);
      buttonPanel.setBackground(Color.white);
    }
    textArea.setForeground(treeElement.getForeground());
    textArea.addKeyListener(this);
    add(buttonPanel, BorderLayout.WEST);
    UIManager.put("ScrollBar.width", new Integer(2 * charWidth));
    jsp = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    if (width >= 0 && charWidth > 0) {
      int nbCols = width / charWidth;
      int nbRows = (text.length() + 1) / nbCols + 1;
      if ((text.length() + 1) % nbCols == 0) nbRows--;
      if (nbRows > 4) {
        nbCols -= 2;
        nbRows = (text.length() + 1) / nbCols + 1;
        if ((text.length() + 1) % nbCols == 0) nbRows--;
      }
      if (nbRows > 4) nbRows = 4;
      textArea.setColumns(nbCols);
      textArea.setRows(nbRows);
      int ps = nbRows * charHeight;
      //int www = width + 20;
      //jsp.setSize(new Dimension(width, ps));
      jsp.setPreferredSize(new Dimension(width, ps));
      //jsp.setMinimumSize(new Dimension(width, ps));
      //setSize(new Dimension(www, ps + 4));
      //setPreferredSize(new Dimension(www, ps + 4));
      //setMinimumSize(new Dimension(www, ps + 4));
      //jsp.revalidate();
    }
    jsp.setBorder(null);
    add(jsp, BorderLayout.CENTER);
    addMouseListener(this);
  }
  public void updateSize() {
    int ps = textArea.getRows() * charHeight;
    jsp.setPreferredSize(new Dimension(width, ps));
  }
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == editButton)
      if (!treeElement.isEditable())
        editButtonPressed();
      else
        editButtonPressed2();
  }
  public void editButtonPressed() {
    treeElement.setEditable(true);
  }
  public void editButtonPressed2() {
    treeElement.setProperty("autoedit", new Boolean(true));
    Enumeration exp_path = tree.getExpandedDescendants(tree.getPathForRow(0));
    TreePath sel_path = tree.getEditingPath();
    tree.stopEditing();
    ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)treeElement);
    while (exp_path.hasMoreElements()) tree.expandPath((TreePath)exp_path.nextElement());
    tree.setSelectionPath(sel_path);
    GsFunctionEditor functionEditor;
    GsTreeInteractionsModel model = (GsTreeInteractionsModel)tree.getModel();
    functionEditor = new GsFunctionEditor(model, this);
    Object[] path = new Object[3];
    path[0] = model.getRoot();
    path[1] = treeElement.getParent();
    path[2] = treeElement;
    TreePath treePath = new TreePath(path);
    Point p = tree.getPathBounds(treePath).getLocation();
    p.translate(0, - functionEditor.getWindow().getPreferredSize().height - 2);
    SwingUtilities.convertPointToScreen(p, tree);
    functionEditor.getWindow().setLocation(p);
    functionEditor.getWindow().setVisible(true);
    treeElement.setSelected(false);
    treeElement.setProperty("autoedit", new Boolean(false));
  }
  public GsTreeExpression getTreeExpression() {
    return (GsTreeExpression)treeElement;
  }
  public void selectText(Point pt, boolean norm) {
    Color col = null;
    if (norm)
      col = Color.cyan;
    else
      col = Color.green;
    try {
      textArea.getHighlighter().removeAllHighlights();
      textArea.getHighlighter().addHighlight(pt.x, pt.x + pt.y, new DefaultHighlightPainter(col));
      int r = pt.x / textArea.getColumns();
      int sbValue = jsp.getVerticalScrollBar().getValue();
      jsp.getVerticalScrollBar().setMaximum(charHeight * (textArea.getDocument().getLength() / textArea.getColumns() - 2));
      if (!((sbValue < (r * charHeight)) && ((r * charHeight) < (sbValue + 4 * charHeight))))
        jsp.getVerticalScrollBar().setValue(r * charHeight);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    textArea.repaint();
  }

  public void setText(String s) {
    Enumeration exp_path = tree.getExpandedDescendants(tree.getPathForRow(0));
    TreePath sel_path = tree.getEditingPath();
    tree.stopEditing();
    GsTreeInteractionsModel interactionsModel = (GsTreeInteractionsModel)tree.getModel();
    ((GsTreeExpression)treeElement).setText(s);
    if (((GsTreeExpression)treeElement).getEditorModel() != null)
      ((GsTreeExpression)treeElement).setSelection(((GsTreeExpression)treeElement).getEditorModel().getCurrentPosition(),
                                                   ((GsTreeExpression)treeElement).getEditorModel().getCurrentTerm().isNormal());
    ((GsTreeExpression)treeElement).setProperty("invalid", new Boolean(false));
    interactionsModel.fireTreeStructureChanged((GsTreeElement)interactionsModel.getRoot());
    while (exp_path.hasMoreElements()) tree.expandPath((TreePath)exp_path.nextElement());
    tree.setSelectionPath(sel_path);
  }

  public String getCompactString() {
    return ((GsTreeElement)((GsTreeInteractionsModel)tree.getModel()).getRoot()).toString();
  }

  public void validateText(String s) {
    GsTreeInteractionsModel interactionsModel;
    boolean ok;
    Enumeration exp_path = tree.getExpandedDescendants(tree.getPathForRow(0));
    TreePath sel_path = tree.getEditingPath();
    tree.stopEditing();
    interactionsModel = (GsTreeInteractionsModel)tree.getModel();
    String oldText = s;
    ok = interactionsModel.updateExpression((short)((GsTreeValue)treeElement.getParent()).getValue(),
                                            (GsTreeExpression)treeElement, s);
    if (((GsTreeExpression)treeElement).getEditorModel() != null)
      ((GsTreeExpression)treeElement).setSelection(((GsTreeExpression)treeElement).getEditorModel().getCurrentPosition(),
          ((GsTreeExpression)treeElement).getEditorModel().getCurrentTerm().isNormal());
    treeElement.getParent().setProperty("null function", new Boolean(treeElement.toString().equals("")));
    ((GsTreeExpression)treeElement).setText(oldText);
    treeElement.setProperty("invalid", new Boolean(!ok));
    if (!ok && !oldText.equals("")) tree.startEditingAtPath(sel_path);
    interactionsModel.setRootInfos();
    interactionsModel.fireTreeStructureChanged((GsTreeElement)interactionsModel.getRoot());
    interactionsModel.refreshVertex();
    while (exp_path.hasMoreElements()) tree.expandPath((TreePath)exp_path.nextElement());
    tree.setSelectionPath(sel_path);
  }
  public void keyPressed(KeyEvent e) {
  }
  public void keyReleased(KeyEvent e) {
  }
  public void keyTyped(KeyEvent e) {
    if (treeElement instanceof GsTreeExpression) {
      if ('\n' == e.getKeyChar()) {
        try {
          textArea.getDocument().remove(textArea.getCaretPosition() - 1, 1);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        validateText(textArea.getText());
        ((GsTreeExpression)treeElement).setEditorModel(null);
        ((GsTreeExpression)treeElement).setSelection(new Point(0,0), false);
      }
      else {
        text = textArea.getText();
        int nbCols = width / charWidth;
        int nbRows = (text.length() + 1) / nbCols + 1;
        if ((text.length() + 1) % nbCols == 0) nbRows--;
        if (nbRows > 4) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if ((text.length() + 1) % nbCols == 0) nbRows--;
        }
        textArea.setRows(nbRows);
        textArea.setColumns(nbCols);
        if (nbRows > 4) nbRows = 4;
        int ps = nbRows * charHeight;
        jsp.setSize(new Dimension(width, ps));
        setSize(new Dimension(getWidth(), ps + 4));
      }
    }
  }
  public void propertyChange(PropertyChangeEvent e) {
    if (toUpdate) {
      int nbCols = ((Integer) e.getNewValue()).intValue() / charWidth;
      if (width >= 0 && nbCols > 0) {
        int nbRows = (text.length() + 1) / nbCols + 1;
        if ((text.length() + 1) % nbCols == 0) nbRows--;
        if (nbRows > 4) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if ((text.length() + 1) % nbCols == 0) nbRows--;
        }
        textArea.setColumns(nbCols);
        textArea.setRows(nbRows);
        if (nbRows > 4) nbRows = 4;
        int ps = nbRows * charHeight;
        jsp.setSize(new Dimension(width, ps));
      }
    }
  }
  public void mouseClicked(MouseEvent e) {
  }
  public void mouseEntered(MouseEvent e) {
  }
  public void mouseExited(MouseEvent e) {
  }
  public void mousePressed(MouseEvent e) {
    toUpdate = true;
  }
  public void mouseReleased(MouseEvent e) {
    toUpdate = false;
  }
}
