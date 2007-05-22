package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.ibdm.GINsim.util.widget.GsJCheckBox;

public class GsTreeCellPanel extends JPanel implements ItemListener, ActionListener, KeyListener {
private static final long serialVersionUID = -2982270267576728776L;
private static Font defaultFont = new Font("monospaced", Font.PLAIN, 10);
  private GsTreeElement treeElement;
  private JTree tree = null;
  private boolean selected, leaf;
  private JTextArea textArea;
  private String text;
  private static int charWidth = 8, charHeight = 8;
  private int width = 0;

  public GsTreeCellPanel(Object value, boolean leaf, int row, JTree tree, boolean sel, boolean check, int w) {
    super();
    width = w;
    text = value.toString();
    selected = sel;
    this.leaf = leaf;
    this.tree = tree;
    treeElement = (GsTreeElement)value;
    setLayout(new GridBagLayout());
    if (row > 0) {
      JCheckBox cb = new GsJCheckBox();
      cb.setSelected(check);
      cb.addItemListener(this);
      if (sel)
        cb.setBackground(Color.yellow);
      else if (value.toString().equals(""))
        cb.setBackground(Color.cyan);
      else
        cb.setBackground(Color.white);
      cb.setMargin(new Insets(0, 0, 0, 0));
      this.add(cb, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                          GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      if (!leaf) {
        JButton but = new JButton("close.png");
        but.addActionListener(this);
        this.add(but, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                             GridBagConstraints.NONE, new Insets(2, 0, 0, 0), 0, 0));
      }
    }
    textArea = new JTextArea(text);
    textArea.setFont(defaultFont);
    textArea.setEditable(true);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(false);
    this.add(textArea, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                              GridBagConstraints.NONE, new Insets(2, 5, 0, 0), 0, 0));
    if (sel) {
      setBackground(Color.yellow);
      textArea.setBackground(Color.yellow);
    }
    else {
      setBackground(Color.white);
      textArea.setBackground(Color.white);
    }
    textArea.setForeground(treeElement.getForeground());

    if ((width >= 0) && (charWidth > 0)) {
      int nbCols = width / charWidth;
      int nbRows = (text.length() + 1) / nbCols + 1;
      if (((text.length() + 1) % nbCols) == 0) nbRows--;
      textArea.setColumns(nbCols);
      textArea.setRows(nbRows);
      int ps = nbRows * charHeight;
      textArea.setPreferredSize(new Dimension(nbCols * charWidth, ps));
    }
    textArea.addKeyListener(this);
  }
  public void paint(Graphics g) {
    charWidth = g.getFontMetrics(defaultFont).charWidth('A');
    charHeight = g.getFontMetrics(defaultFont).getHeight();
    super.paint(g);
    if (selected) {
      g.setColor(Color.blue);
      g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
  }
  public void itemStateChanged(ItemEvent e) {
    boolean b = ((JCheckBox)e.getSource()).isSelected();
    treeElement.setChecked(b);
    for (int i = 0; i < treeElement.getChildCount(); i++) {
      treeElement.getChild(i).setChecked(b);
      for (int j = 0; j < treeElement.getChild(i).getChildCount(); j++)
        treeElement.getChild(i).getChild(j).setChecked(b);
    }
    if (tree != null) {
      ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
      tree.stopEditing();
      tree.repaint();
    }
  }
  public void actionPerformed(ActionEvent e) {
    treeElement.remove();
    ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
    ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
  }
  public Insets getInsets() {
	  if (leaf) {
		  return new Insets(0, 0, 0, 0);
	  }
	  return new Insets(3, 3, 3, 3);
  }
  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {
    if (treeElement instanceof GsTreeExpression)
      if (System.getProperty("line.separator").charAt(0) == e.getKeyChar()) {
        try {
          textArea.getDocument().remove(textArea.getCaretPosition() - 1, 1);
          tree.stopEditing();
          ((GsTreeInteractionsModel)tree.getModel()).updateExpression((short)((GsTreeValue)treeElement.getParent()).getValue(), (GsTreeExpression)treeElement, textArea.getText());
          ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
          ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
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
        textArea.setRows(nbRows);
        int ps = nbRows * charHeight + 10;
        int ws = getSize().width;
        textArea.setPreferredSize(new Dimension(nbCols * charWidth, ps));
        setSize(new Dimension(ws, ps));
        textArea.repaint();
        invalidate();
        repaint();
      }
  }
}
