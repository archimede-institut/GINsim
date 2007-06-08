package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.ibdm.GINsim.util.widget.GsJButton;

public class GsFunctionPanel extends GsBooleanFunctionTreePanel implements ActionListener, KeyListener,
  PropertyChangeListener, MouseListener {
  private static final long serialVersionUID = 8900639275182677150L;
  private static final Color editColor = new Color(204, 255, 204);
  private JPanel buttonPanel;
  private JButton showButton, hideButton, editButton;
  private JTextArea textArea;
  private JScrollPane jsp;
  private JSplitPane splitPane = null;
  private GsUnselectedParamsPanel listPanel;
  private boolean toUpdate = false;

  public GsFunctionPanel(GsTreeElement value, JTree tree, boolean sel, int width, boolean edit) {
    super(value, tree, sel, width);
    setBackground(Color.white);
    editButton = new GsJButton("edit.png");
    editButton.addActionListener(this);
    showButton = new GsJButton("show.png");
    showButton.addActionListener(this);
    hideButton = new GsJButton("hide.png");
    hideButton.addActionListener(this);
    buttonPanel = new JPanel(new GridBagLayout());
    buttonPanel.setBackground(Color.white);
    buttonPanel.add(editButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                                      GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    if (((Boolean)value.getProperty("show unselected")).booleanValue())
      buttonPanel.add(hideButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    else
      buttonPanel.add(showButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
    textArea = new JTextArea(text);
    textArea.setFont(defaultFont);
    textArea.setEditable(true);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(false);
    jsp = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
    else {
      setBackground(Color.white);
      textArea.setBackground(Color.white);
      buttonPanel.setBackground(Color.white);
    }
    textArea.setForeground(treeElement.getForeground());
    textArea.addKeyListener(this);
    add(buttonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                            GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    Vector v = null;
    if (treeElement.containsUnselectChild()) {
      showButton.setEnabled(true);
      hideButton.setEnabled(true);
      v = treeElement.getUnselectChilds();
    }
    else {
      showButton.setEnabled(false);
      hideButton.setEnabled(false);
    }
    if (((Boolean)treeElement.getProperty("show unselected")).booleanValue()) {
      listPanel = new GsUnselectedParamsPanel(v, sel, tree, this, edit);
      JScrollPane jsp2 = new JScrollPane(listPanel);
      jsp2.setBorder(null);
      splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jsp, jsp2);
      splitPane.setBorder(null);
      splitPane.setContinuousLayout(true);
      splitPane.setDividerSize(3);
      add(splitPane, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                            GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      int lpWidth, lpHeight;
      lpHeight = listPanel.getPreferredSize().height;
      if (treeElement.getProperty("divider location") == null) {
        lpWidth = listPanel.getPreferredSize().width;
        if (lpWidth > (width / 2)) lpWidth = width / 2;
      }
      else
        lpWidth = (int)((1.0D - ((Double)treeElement.getProperty("divider location")).doubleValue()) * width);
      if (lpHeight > (4 * charHeight)) {
        lpHeight = 4 * charHeight;
        lpWidth += 2 * charWidth;
      }
      UIManager.put("ScrollBar.width", new Integer(2 * charWidth));
      jsp2.setPreferredSize(new Dimension(lpWidth, lpHeight));
      int nbCols = (width - lpWidth) /charWidth;
      if ((width >= 0) && (charWidth > 0)) {
        int nbRows = (text.length() + 1) / nbCols + 1;
        if (((text.length() + 1) % nbCols) == 0) nbRows--;
        if (nbRows > 4) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if (((text.length() + 1) % nbCols) == 0) nbRows--;
        }
        textArea.setColumns(nbCols);
        textArea.setRows(nbRows);
        if (nbRows > 4) nbRows = 4;
        int ps = nbRows * charHeight;
        jsp.setPreferredSize(new Dimension(width - lpWidth, ps));
      }
      if (treeElement.getProperty("divider location") == null)
        splitPane.setDividerLocation((double)(width - lpWidth) / width);
      else
        splitPane.setDividerLocation((int)(((Double)treeElement.getProperty("divider location")).doubleValue() * width));
      splitPane.addPropertyChangeListener("dividerLocation", this);
      (((BasicSplitPaneUI)splitPane.getUI()).getDivider()).addMouseListener(this);
    }
    else {
      add(jsp, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                      GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
      if ((width >= 0) && (charWidth > 0)) {
        int nbCols = width / charWidth;
        int nbRows = (text.length() + 1) / nbCols + 1;
        if (((text.length() + 1) % nbCols) == 0) nbRows--;
        if (nbRows > 4) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if (((text.length() + 1) % nbCols) == 0) nbRows--;
        }
        textArea.setColumns(nbCols);
        textArea.setRows(nbRows);
        if (nbRows > 4) nbRows = 4;
        int ps = nbRows * charHeight;
        jsp.setPreferredSize(new Dimension(width, ps));
      }
    }
    addMouseListener(this);
    //addMouseMotionListener(this);
    //buttonPanel.addMouseListener(this);
  }
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == showButton) {
      showButtonPressed();
    }
    else if (e.getSource() == hideButton) {
      hideButtonPressed();
    }
    else if (e.getSource() == editButton) {
      editButtonPressed();
    }
  }
  public void showButtonPressed() {
    treeElement.setProperty("show unselected", new Boolean(true));
    TreePath tp = tree.getEditingPath();
    Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    tree.stopEditing();
    ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged(treeElement);
    while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
    tree.getSelectionModel().addSelectionPath(tp);
    treeElement.setChecked(true);
  }
  public void hideButtonPressed() {
    treeElement.setProperty("show unselected", new Boolean(false));
    TreePath tp = tree.getEditingPath();
    Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    tree.stopEditing();
    ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged(treeElement);
    while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
    treeElement.setChecked(true);
    tree.getSelectionModel().addSelectionPath(tp);
  }
  public void editButtonPressed() {
    treeElement.setEditable(true);
  }
  public void keyPressed(KeyEvent e) {
  }
  public void keyReleased(KeyEvent e) {
  }
  public void keyTyped(KeyEvent e) {
    if (treeElement instanceof GsTreeExpression)
      if (System.getProperty("line.separator").charAt(0) == e.getKeyChar()) {
        try {
          String oldExpression = treeElement.toString();
          textArea.getDocument().remove(textArea.getCaretPosition() - 1, 1);
          Enumeration exp_path = tree.getExpandedDescendants(tree.getPathForRow(0));
          TreePath sel_path = tree.getEditingPath();
          tree.stopEditing();
          treeElement.setProperty("show unselected", new Boolean(false));
          ((GsTreeInteractionsModel)tree.getModel()).updateExpression(
            (short)((GsTreeValue)treeElement.getParent()).getValue(),
            (GsTreeExpression)treeElement, textArea.getText());
          ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
          ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
          while (exp_path.hasMoreElements()) tree.expandPath((TreePath)exp_path.nextElement());
          tree.setSelectionPath(sel_path);
          if (oldExpression.equals("") && !treeElement.toString().equals(""))
            treeElement.getParent().setProperty("null function", new Boolean(false));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      else {
        text = textArea.getText();
        int nbCols = width / charWidth;
        int nbRows = (text.length() + 1) / nbCols + 1;
        if (((text.length() + 1) % nbCols) == 0) nbRows--;
        if (nbRows > 4) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if (((text.length() + 1) % nbCols) == 0) nbRows--;
        }
        textArea.setRows(nbRows);
        textArea.setColumns(nbCols);
        if (nbRows > 4) nbRows = 4;
        int ps = nbRows * charHeight;
        jsp.setSize(new Dimension(width, ps));
        setSize(new Dimension(getWidth(), ps + 4));
      }
  }
  public void propertyChange(PropertyChangeEvent e) {
    if (toUpdate) {
      int nbCols = ((Integer) e.getNewValue()).intValue() / charWidth;
      if ((width >= 0) && (nbCols > 0)) {
        int nbRows = (text.length() + 1) / nbCols + 1;
        if (((text.length() + 1) % nbCols) == 0) nbRows--;
        if (nbRows > 4) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if (((text.length() + 1) % nbCols) == 0) nbRows--;
        }
        textArea.setColumns(nbCols);
        textArea.setRows(nbRows);
        if (nbRows > 4) nbRows = 4;
        int ps = nbRows * charHeight;
        jsp.setSize(new Dimension(width, ps));
        treeElement.setProperty("divider location", new Double(((double) ((Integer) e.getNewValue()).intValue() / width)));
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
