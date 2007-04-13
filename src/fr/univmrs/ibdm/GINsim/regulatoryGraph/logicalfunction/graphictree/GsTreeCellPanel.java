package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.FlowLayout;
import javax.swing.JTree;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import fr.univmrs.ibdm.GINsim.global.GsEnv;

public class GsTreeCellPanel extends JPanel implements ItemListener, ActionListener {
  private Font font = new Font("dialog", Font.BOLD, 10);
  private GsTreeElement treeElement;
  private JTree tree = null;
  private boolean selected;

  public GsTreeCellPanel(Object value, boolean leaf, int row, JTree tree, boolean sel, boolean check) {
    super();
    selected = sel;
    Icon icon = null;
    if (tree != null)
      icon = ((GsTreeInteractionsCellRenderer)tree.getCellRenderer()).getIcon();
    this.tree = tree;
    treeElement = (GsTreeElement)value;
    setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
    if (row > 0) {
      JCheckBox cb = new JCheckBox();
      cb.setSelected(check);
      cb.addItemListener(this);
      if (sel)
        cb.setBackground(Color.yellow);
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
    JLabel lab = new JLabel(value.toString(), icon, JLabel.HORIZONTAL);
    lab.setFont(font);
    if (sel) {
      setBackground(Color.yellow);
    }
    else {
      setBackground(Color.white);
    }
    lab.setForeground(treeElement.getForeground());
    setOpaque(true);
    add(lab);
  }
  public void paint(Graphics g) {
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