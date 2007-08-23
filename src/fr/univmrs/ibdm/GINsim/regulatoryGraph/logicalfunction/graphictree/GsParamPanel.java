package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JTree;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JLabel;
import java.awt.Dimension;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;

public class GsParamPanel extends GsBooleanFunctionTreePanel implements /*ItemListener,*/ MouseListener, MouseMotionListener {
  private static final long serialVersionUID = -7863256897019020183L;
  //private JCheckBox checkBox;
  private JLabel label;
  public GsParamPanel(GsTreeElement value, JTree tree, boolean sel, int width) {
    super(value, tree, sel, width);
    //checkBox = new GsJCheckBox(value.toString(), true);
    //checkBox.setFont(defaultFont);
    //checkBox.addItemListener(this);
    if (value.toString().equals(""))
      label = new JLabel("          ");
    else
      label = new JLabel(value.toString());
    label.setFont(defaultFont);
    label.setPreferredSize(new Dimension(width, charHeight));
    if (sel) {
      //checkBox.setBackground(Color.yellow);
      label.setBackground(Color.yellow);
      setBackground(Color.yellow);
    }
    else if (value.toString().equals("")) {
      //checkBox.setBackground(Color.cyan);
      if (((GsTreeInteractionsModel)tree.getModel()).getVertex().getBaseValue() == ((GsTreeValue)value.getParent().getParent()).getValue()) {
        label.setBackground(Color.cyan);
        setBackground(Color.cyan);
      }
      else {
        label.setBackground(Color.red);
        setBackground(Color.red);
      }
    }
    else {
      //checkBox.setBackground(Color.white);
      label.setBackground(Color.white);
      setBackground(Color.white);
    }
    //checkBox.setMargin(new Insets(0, 0, 0, 0));
    //checkBox.setForeground(value.getForeground());
    //add(checkBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
    //                                     GridBagConstraints.NONE, new Insets(2, 5, 2, 0), 0, 0));
    label.setForeground(value.getForeground());
    add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                      GridBagConstraints.NONE, new Insets(2, 5, 2, 0), 0, 0));
  }
  //public void itemStateChanged(ItemEvent e) {
    //checkBox.removeItemListener(this);
    //treeElement.setChecked(false);
    //if (tree != null) {
    //  Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    //  ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
    //  ((GsTreeInteractionsModel)tree.getModel()).setRootInfos();
    //  tree.stopEditing();
    //  ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged(treeElement.getParent());
    //  while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
    //}
    //checkBox.addItemListener(this);
  //}
  public void mouseClicked(MouseEvent e) {

  }
  public void mouseEntered(MouseEvent e) {

  }
  public void mouseExited(MouseEvent e) {

  }
  public void mousePressed(MouseEvent e) {
    e.setSource(this);
    getMouseListener().mousePressed(e);
  }
  public void mouseReleased(MouseEvent e) {
    e.setSource(this);
    getMouseListener().mouseReleased(e);
  }
  public void mouseMoved(MouseEvent e) {
    e.setSource(this);
    getMouseMotionListener().mouseMoved(e);
  }
  public void mouseDragged(MouseEvent e) {
    e.setSource(this);
    getMouseMotionListener().mouseDragged(e);
  }
}
