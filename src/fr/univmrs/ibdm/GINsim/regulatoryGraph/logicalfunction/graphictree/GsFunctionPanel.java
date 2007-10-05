package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.ibdm.GINsim.util.widget.GsJButton;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.GsFunctionEditor;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

public class GsFunctionPanel extends GsBooleanFunctionTreePanel implements ActionListener, KeyListener,
    PropertyChangeListener, MouseListener {
  private static final long serialVersionUID = 8900639275182677150L;
  private static final Color editColor = new Color(204, 255, 204);
  private JPanel buttonPanel;
  private JButton editButton;
  private JTextArea textArea;
  private JScrollPane jsp;
  private boolean toUpdate = false;

  public GsFunctionPanel(GsTreeElement value, JTree tree, boolean sel, int width, boolean edit) {
    super(value, tree, sel, width);
    setBackground(Color.white);
    editButton = new GsJButton("edit.png");
    editButton.addActionListener(this);
    buttonPanel = new JPanel(new GridBagLayout());
    buttonPanel.setBackground(Color.white);
    buttonPanel.add(editButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
        GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    textArea = new JTextArea(text);

    textArea.setFont(defaultFont);
    textArea.setEditable(true);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(false);
    jsp = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jsp.setBorder(null);
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
    else {
      setBackground(Color.white);
      buttonPanel.setBackground(Color.white);
    }
    textArea.setForeground(treeElement.getForeground());
    textArea.addKeyListener(this);
    add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    UIManager.put("ScrollBar.width", new Integer(2 * charWidth));
    add(jsp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                    GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    if (width >= 0 && charWidth > 0) {
      int nbCols = width / charWidth;
      int nbRows = (text.length() + 1) / nbCols + 1;
      if ((text.length() + 1) % nbCols == 0) {
        nbRows--;
      }
      if (nbRows > 4) {
        nbCols -= 2;
        nbRows = (text.length() + 1) / nbCols + 1;
        if ((text.length() + 1) % nbCols == 0) {
          nbRows--;
        }
      }
      textArea.setColumns(nbCols);
      textArea.setRows(nbRows);
      if (nbRows > 4) {
        nbRows = 4;
      }
      int ps = nbRows * charHeight;
      jsp.setPreferredSize(new Dimension(width, ps));
    }
    addMouseListener(this);
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
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    textArea.repaint();
  }
  public void validateText(String s) {
    GsTreeInteractionsModel interactionsModel;
    boolean ok;
    String oldExpression = treeElement.toString();
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
    interactionsModel.setRootInfos();
    interactionsModel.fireTreeStructureChanged((GsTreeElement)interactionsModel.getRoot());
    interactionsModel.refreshVertex();
    while (exp_path.hasMoreElements()) {
      tree.expandPath((TreePath)exp_path.nextElement());
    }
    tree.setSelectionPath(sel_path);
    if (oldExpression.equals("") && !treeElement.toString().equals("")) {
      treeElement.getParent().setProperty("null function", new Boolean(false));
    }
    if (ok) {
      treeElement.setProperty("invalid", new Boolean(false));
      ((GsTreeExpression)treeElement).setText(oldText);
    }
    if (!ok && !oldText.equals("")) {
      tree.startEditingAtPath(sel_path);
      ((GsTreeExpression)treeElement).setText(oldText);
    }
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
      }
      else {
    	text = textArea.getText();
        int nbCols = width / charWidth;
        int nbRows = (text.length() + 1) / nbCols + 1;
        if ((text.length() + 1) % nbCols == 0) {
          nbRows--;
        }
        if (nbRows > 4) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if ((text.length() + 1) % nbCols == 0) {
            nbRows--;
          }
        }
        textArea.setRows(nbRows);
        textArea.setColumns(nbCols);
        if (nbRows > 4) {
          nbRows = 4;
        }
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
        if ((text.length() + 1) % nbCols == 0) {
          nbRows--;
        }
        if (nbRows > 4) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if ((text.length() + 1) % nbCols == 0) {
            nbRows--;
          }
        }
        textArea.setColumns(nbCols);
        textArea.setRows(nbRows);
        if (nbRows > 4) {
          nbRows = 4;
        }
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
