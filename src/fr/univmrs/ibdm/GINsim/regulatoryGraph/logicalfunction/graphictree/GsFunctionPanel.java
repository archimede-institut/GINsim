package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JTree;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import javax.swing.JPanel;
import javax.swing.JButton;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Dimension;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import javax.swing.tree.TreePath;
import javax.swing.JSplitPane;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.UIManager;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class GsFunctionPanel extends GsBooleanFunctionTreePanel implements ActionListener, KeyListener, PropertyChangeListener, MouseListener {
  private JPanel buttonPanel;
  private JButton delButton, showButton, hideButton;
  private JTextArea textArea;
  private JScrollPane jsp;
  private JSplitPane splitPane = null;
  private GsUnselectedParamsPanel listPanel;
  private boolean toUpdate = false;
  private TreePath[] selectedPaths;

  public GsFunctionPanel(GsTreeElement value, JTree tree, boolean sel, int width, TreePath[] sp) {
    super(value, tree, sel, width);
    selectedPaths = sp;
    setBackground(Color.white);
    delButton = new JButton(GsEnv.getIcon("close.png")) {
      public Insets getInsets() {
        return new Insets(2, 2, 2, 2);
      }
    };
    delButton.addActionListener(this);
    showButton = new JButton(GsEnv.getIcon("show.png")) {
      public Insets getInsets() {
        return new Insets(2, 2, 2, 2);
      }
    };
    showButton.addActionListener(this);
    hideButton = new JButton(GsEnv.getIcon("hide.png")) {
      public Insets getInsets() {
        return new Insets(2, 2, 2, 2);
      }
    };
    hideButton.addActionListener(this);
    buttonPanel = new JPanel(new GridBagLayout());
    buttonPanel.setBackground(Color.white);
    buttonPanel.add(delButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
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
    if (sel) {
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
      listPanel = new GsUnselectedParamsPanel(v, sel, tree, this);
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
  }
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == delButton) {
      Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      tree.stopEditing();
      Vector v = new Vector();
      if (selectedPaths != null) {
        for (int i = 0; i < selectedPaths.length; i++) {
          ((GsTreeElement) selectedPaths[i].getLastPathComponent()).remove();
          v.addElement(selectedPaths[i].getLastPathComponent());
        }
        if (!v.contains(treeElement)) {
          treeElement.remove();
          v.addElement(treeElement);
        }
      }
      else {
        treeElement.remove();
        v.addElement(treeElement);
      }
      if (treeElement.toString().equals(""))
        treeElement.getParent().setProperty("null function", new Boolean(false));
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
      ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
      while (enu.hasMoreElements()) {
        TreePath tp = (TreePath)enu.nextElement();
        if (!v.contains(tp.getLastPathComponent())) tree.expandPath(tp);
      }
    }
    else if (e.getSource() == showButton) {
      showButtonPressed();
    }
    else if (e.getSource() == hideButton) {
      hideButtonPressed();
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
        jsp.setPreferredSize(new Dimension(width, ps));
        treeElement.setProperty("divider location",
                                new Double(((double) ((Integer) e.getNewValue()).intValue() / width)));
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
