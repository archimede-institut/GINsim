package fr.univmrs.tagc.GINsim.gui.tbclient.genetree;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JCheckBox;

public class TreeElementSelectable extends TreeElementDeco implements ActionListener {
  private GeneTreeModel model;

  private JCheckBox cb = new JCheckBox("", false) {
    private static final long serialVersionUID = 3441730857578215072L;
	public Insets getInsets() {
      return new Insets(0, 0, 0, 0);
    }
  };
  public TreeElementSelectable(AbstractTreeElement e, boolean sel, GeneTreeModel m) {
    super(e);
    cb.setForeground(fgColor);
    cb.setOpaque(false);
    cb.setSelected(sel);
    cb.addActionListener(this);
    model = m;
  }
  public TreeElementSelectable(AbstractTreeElement e, boolean sel, Object o, GeneTreeModel m) {
    this(e, sel, m);
    userObject = o;
  }
  public void check(boolean b) {
    cb.setSelected(b);
    model.fireTreeNodesChanged(this);
    super.check(b);
  }
  public Vector getGraphicComponents(boolean sel) {
    Vector v = treeElement.getGraphicComponents(sel);
    cb.setBackground(sel ? selColor : bgColor);
    v.insertElementAt(cb, 0);
    return v;
  }
  public void actionPerformed(ActionEvent e) {
    check(cb.isSelected());
  }
  public void setSelected(boolean b) {
  	cb.setSelected(b);
  }
  public boolean isSelected() {
  	return cb.isSelected();
  }
}
