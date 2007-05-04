package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;

import fr.univmrs.ibdm.GINsim.global.GsEnv;

public class GsTreeCellPanel extends JPanel implements ItemListener, ActionListener {
  private Font font = new Font("Monospaced", Font.PLAIN, 10);
  private GsTreeElement treeElement;
  private JTree tree = null;
  private boolean selected;
  private JTextArea textArea;
  private String text;
  private int charWidth = 8, charHeight = 10;

  public GsTreeCellPanel(Object value, boolean leaf, int row, JTree tree, boolean sel, boolean check, int w) {
    super();
    selected = sel;
    this.tree = tree;
    treeElement = (GsTreeElement)value;
    setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
    if (row > 0) {
      JCheckBox cb = new JCheckBox();
      cb.setSelected(check);
      cb.addItemListener(this);
      if (sel)
        cb.setBackground(Color.yellow);
      else if (value.toString().equals(""))
        cb.setBackground(Color.cyan);
      else
        cb.setBackground(Color.white);
      add(cb);
      if (!leaf) {
        JButton but = new JButton(GsEnv.getIcon("close.png")) {
          public Insets getInsets() {
            return new Insets(2, 2, 2, 2);
          }
        };
        but.addActionListener(this);
        add(but);
      }
    }
    text = value.toString();
    JLabel lab = new JLabel(text);
    lab.setFont(font);
    //textArea = new JTextArea(text, 1, 50);
    //textArea.setFont(font);
    //textArea.setBorder(null);
    //textArea.setEditable(false);
    //textArea.setLineWrap(true);
    //textArea.setWrapStyleWord(true);
    //add(textArea);
    add(lab);
    if (sel) {
      setBackground(Color.yellow);
      //textArea.setBackground(Color.yellow);
      lab.setBackground(Color.yellow);
    }
    else {
      setBackground(Color.white);
      //textArea.setBackground(Color.white);
      lab.setBackground(Color.white);
    }
    //textArea.setForeground(treeElement.getForeground());
    lab.setForeground(treeElement.getForeground());
    setOpaque(true);
  }
  public void setWidth(int w) {
    //System.err.println("w = " + w + "   col = " + w / charWidth + "    row = " + w / (charWidth * text.length()) + 1);
    //textArea.setColumns(w / charWidth);
    //textArea.setRows((charWidth * text.length()) / w + 1);
    //int ps = textArea.getRows() * charHeight;
    //textArea.setSize(new Dimension(textArea.getColumns() * charWidth, ps));
    //setSize(new Dimension(w, ps));
    //textArea.repaint();
    //invalidate();
  }
  public void paint(Graphics g) {
    charWidth = g.getFontMetrics(font).charWidth('A');
    charHeight = g.getFontMetrics(font).getHeight();
    super.paint(g);
    if (selected) {
      g.setColor(Color.blue);
      g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
  }
  public void itemStateChanged(ItemEvent e) {
    boolean b = ((JCheckBox)e.getSource()).isSelected();
    treeElement.setSelected(b);
    for (int i = 0; i < treeElement.getChildCount(); i++) {
      treeElement.getChild(i).setSelected(b);
      for (int j = 0; j < treeElement.getChild(i).getChildCount(); j++)
        treeElement.getChild(i).getChild(j).setSelected(b);
    }
    if (tree != null) {
      ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
      tree.stopEditing();
      tree.repaint();
    }
  }
  public void actionPerformed(ActionEvent e) {
    treeElement.remove();
    tree.collapseRow(0);
    ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
    ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
  }
}
