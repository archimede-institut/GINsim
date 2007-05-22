package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTree;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import javax.swing.JCheckBox;
import java.awt.Insets;
import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

public class GsParamPanel extends GsBooleanFunctionTreePanel implements ItemListener {
  private JCheckBox checkBox;

  public GsParamPanel(GsTreeElement value, JTree tree, boolean sel, int width) {
    super(value, tree, sel, width);
    checkBox = new JCheckBox(value.toString(), true) {
      public Insets getInsets() {
        return new Insets(2, 2, 2, 2);
      }
    };
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
