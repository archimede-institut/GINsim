package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.tree.TreePath;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.tagc.common.manageressources.ImageLoader;
import fr.univmrs.tagc.common.widgets.GsButton;

public class GsFunctionPanel extends GsBooleanFunctionTreePanel implements ActionListener, KeyListener, PropertyChangeListener, MouseListener {
  private static final long serialVersionUID = 8900639275182677150L;
  private static final Color editColor = new Color(204, 255, 204);
  private JButton editButton;
  private JTextArea textArea;
  private JScrollPane jsp;
  private boolean toUpdate = false;
  private GsLogicalFunctionPanel panel;

  public GsFunctionPanel(GsLogicalFunctionPanel p, GsTreeElement value, JTree tree, boolean sel, int width, boolean edit) {
    super(value, tree, sel, width);
    panel = p;
    setLayout(new BorderLayout(2, 2));
    setBackground(Color.white);
    editButton = new GsButton(ImageLoader.getImageIcon("edit.png"));
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
      setBackground(new Color(222, 239, 255));
      textArea.setBackground(new Color(222, 239, 255));
      buttonPanel.setBackground(new Color(222, 239, 255));
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
      if (nbRows > 40) {
        nbCols -= 2;
        nbRows = (text.length() + 1) / nbCols + 1;
        if ((text.length() + 1) % nbCols == 0) nbRows--;
      }
      if (nbRows > 40) nbRows = 40;
      textArea.setColumns(nbCols);
      textArea.setRows(nbRows);
      int ps = nbRows * charHeight;
      jsp.setPreferredSize(new Dimension(width, ps));
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
    if (e.getSource() == editButton) editButtonPressed();
  }
  public void editButtonPressed() {
  	((GsTreeExpression)treeElement).setSelection(null, true);
  	textArea.getHighlighter().removeAllHighlights();
    treeElement.setEditable(true);
  }
  public GsTreeExpression getTreeExpression() {
    return (GsTreeExpression)treeElement;
  }
  public void selectText(Point pt) {
		Color col = Color.green;
		if (!((GsTreeExpression)treeElement).isNormal()) col = Color.gray;
    try {
      textArea.getHighlighter().removeAllHighlights();
      textArea.getHighlighter().addHighlight(pt.x, pt.y + 1, new DefaultHighlightPainter(col));
			int r = 0;
			if (textArea.getColumns() > 0) r = pt.x / textArea.getColumns();
      int sbValue = jsp.getVerticalScrollBar().getValue();
      jsp.getVerticalScrollBar().setMaximum(charHeight * (textArea.getDocument().getLength() / textArea.getColumns() - 2));
      if (!(sbValue < r * charHeight && r * charHeight < sbValue + 40 * charHeight))
        jsp.getVerticalScrollBar().setValue(r * charHeight);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    textArea.repaint();
  }
		public String getCurrentText() {
			return textArea.getText();
		}
  public void setText(String s, int c) {
  	textArea.setText(s);
  	textArea.setCaretPosition(c);
  	Enumeration exp_path = tree.getExpandedDescendants(tree.getPathForRow(0));
    TreePath sel_path = tree.getEditingPath();
    tree.stopEditing();
    GsTreeInteractionsModel interactionsModel = (GsTreeInteractionsModel)tree.getModel();
    ((GsTreeExpression)treeElement).setText(s);
    if (((GsTreeExpression)treeElement).getEditorModel() != null)
      ((GsTreeExpression)treeElement).setSelection(((GsTreeExpression)treeElement).getEditorModel().getSelectedArea(), true);
    ((GsTreeExpression)treeElement).setProperty("invalid", new Boolean(false));
    interactionsModel.fireTreeStructureChanged((GsTreeElement)interactionsModel.getRoot());
    while (exp_path.hasMoreElements()) tree.expandPath((TreePath)exp_path.nextElement());
    tree.setSelectionPath(sel_path);
  }

  public void refresh() {
  	String s = textArea.getText().replaceAll(".", " ");
  	//String s0 = textArea.getText();
  	textArea.setText(s);
  	//textArea.setText(s0);
  	Enumeration exp_path = tree.getExpandedDescendants(tree.getPathForRow(0));
    TreePath sel_path = tree.getEditingPath();
    tree.stopEditing();
    GsTreeInteractionsModel interactionsModel = (GsTreeInteractionsModel)tree.getModel();
    if (((GsTreeExpression)treeElement).getEditorModel() != null)
      ((GsTreeExpression)treeElement).setSelection(((GsTreeExpression)treeElement).getEditorModel().getSelectedArea(), true);
    ((GsTreeExpression)treeElement).setProperty("invalid", new Boolean(false));
    interactionsModel.fireTreeStructureChanged((GsTreeElement)interactionsModel.getRoot());
    while (exp_path.hasMoreElements()) tree.expandPath((TreePath)exp_path.nextElement());
    tree.setSelectionPath(sel_path);
  }
  public String getCompactString() {
    return ((GsTreeExpression)((GsTreeInteractionsModel)tree.getModel()).getRoot()).toString();
  }

  public void validateText(String s) {
		s = s.trim();
    GsTreeInteractionsModel interactionsModel;
    boolean ok;
    Enumeration exp_path = tree.getExpandedDescendants(tree.getPathForRow(0));
    TreePath sel_path = tree.getEditingPath();
    tree.stopEditing();
    interactionsModel = (GsTreeInteractionsModel)tree.getModel();
    String oldText = s;
    ok = interactionsModel.updateExpression((byte)((GsTreeValue)treeElement.getParent()).getValue(),
                                            (GsTreeExpression)treeElement, s);
    if (((GsTreeExpression)treeElement).getEditorModel() != null)
      ((GsTreeExpression)treeElement).setSelection(((GsTreeExpression)treeElement).getEditorModel().getSelectedArea(), true);
    treeElement.getParent().setProperty("null function", new Boolean(treeElement.toString().equals("")));

    if (ok && ((GsTreeExpression)treeElement).getRoot() != null)
    	((GsTreeExpression)treeElement).setText(((GsTreeExpression)treeElement).getRoot().toString(false));
    else {
        //((GsTreeExpression)treeElement).setText(oldText);
        ((GsTreeExpression) treeElement).setText(s);
        ((GsTreeExpression) treeElement).setRoot(null);
    }
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
        ((GsTreeExpression)treeElement).setSelection(null, false);
      }
      else if ('\t' == e.getKeyChar()) {
      	/*try {
          textArea.getDocument().remove(textArea.getCaretPosition() - 1, 1);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        ((GsTreeExpression)treeElement).setEditorModel(panel.getFunctionEditor().getModel());
				panel.setEditEditorVisible(true);
				setText(textArea.getText(), textArea.getCaretPosition());
				tree.stopEditing();
			  GsTreeInteractionsModel model = (GsTreeInteractionsModel)tree.getModel();
        tree.setEditable(false);
        panel.initEditor(model, this);
				tree.setSelectionPath(null);;
        treeElement.setProperty("autoedit", new Boolean(false));*/
      }
      else {
        text = textArea.getText();
        int nbCols = width / charWidth;
        int nbRows = (text.length() + 1) / nbCols + 1;
        if ((text.length() + 1) % nbCols == 0) nbRows--;
        if (nbRows > 40) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if ((text.length() + 1) % nbCols == 0) nbRows--;
        }
        textArea.setRows(nbRows);
        textArea.setColumns(nbCols);
        if (nbRows > 40) nbRows = 40;
        int ps = nbRows * charHeight;
        jsp.setSize(new Dimension(width, ps));
        setSize(new Dimension(getWidth(), ps + 4));
      }
    }
  }
  public void setTreeEditable() {
  	tree.setEditable(true);
  }
	public int getCaretPosition() {
		return textArea.getCaretPosition();
	}
  public void propertyChange(PropertyChangeEvent e) {
    if (toUpdate) {
      int nbCols = ((Integer) e.getNewValue()).intValue() / charWidth;
      if (width >= 0 && nbCols > 0) {
        int nbRows = (text.length() + 1) / nbCols + 1;
        if ((text.length() + 1) % nbCols == 0) nbRows--;
        if (nbRows > 40) {
          nbCols -= 2;
          nbRows = (text.length() + 1) / nbCols + 1;
          if ((text.length() + 1) % nbCols == 0) nbRows--;
        }
        textArea.setColumns(nbCols);
        textArea.setRows(nbRows);
        if (nbRows > 40) nbRows = 40;
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
