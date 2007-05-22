package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.util.widget.GsJCheckBox;

public class GsParamPanel extends GsBooleanFunctionTreePanel implements ItemListener {
private static final long serialVersionUID = -7863256897019020183L;
private JCheckBox checkBox;

  public GsParamPanel(GsTreeElement value, JTree tree, boolean sel, int width) {
    super(value, tree, sel, width);
    checkBox = new GsJCheckBox(value.toString(), true);
    checkBox.setFont(defaultFont);
    checkBox.addItemListener(this);
    if (sel)
      checkBox.setBackground(Color.yellow);
    else if (value.toString().equals(""))
      checkBox.setBackground(Color.cyan);
    else
      checkBox.setBackground(Color.white);
    checkBox.setMargin(new Insets(0, 0, 0, 0));
    checkBox.setForeground(value.getForeground());
    add(checkBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                         GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
  }
  public void itemStateChanged(ItemEvent e) {
    checkBox.removeItemListener(this);
    treeElement.setChecked(false);
    if (tree != null) {
      Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
      ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
      tree.stopEditing();
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged(treeElement.getParent());
      while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
    }
    checkBox.addItemListener(this);
  }
}
