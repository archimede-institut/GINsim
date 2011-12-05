package org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeElement;
import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeValue;
import org.ginsim.gui.resource.ImageLoader;
import org.ginsim.gui.utils.widgets.GsButton;


public class RootPanel extends BooleanFunctionTreePanel implements ActionListener {
  private static final long serialVersionUID = -1866485315946504210L;
  private JLabel label;
  private GsButton button;

  public RootPanel(TreeElement value, JTree tree, boolean sel, int width) {
    super(value, tree, sel, width);
    label = new JLabel(value.toString());
    label.setFont(defaultFont);
    label.setPreferredSize(new Dimension(width, charHeight));
    button = new GsButton(ImageLoader.getImageIcon("add.png"));
    button.addActionListener(this);
    buttonPanel.add(button);
    if (sel) {
      label.setBackground(Color.yellow);
      setBackground(Color.yellow);
    }
    if (!((Boolean)treeElement.getProperty("add")).booleanValue()) button.setEnabled(false);
		add(buttonPanel, BorderLayout.WEST);
    add(label, BorderLayout.CENTER);
  }
  public void actionPerformed(ActionEvent e) {
    RegulatoryNode vertex;
    boolean ok = true;
    byte i;

    if (e.getSource() == button) {
      try {
        vertex = ((TreeInteractionsModel)tree.getModel()).getNode();
        Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
        for (i = 1 ; i <= vertex.getMaxValue(); i++) {
          ok = true;
          for (int k = 0; k < treeElement.getChildCount(); k++)
						if (((TreeValue)treeElement.getChild(k)).getValue() == i) {
              ok = false;
              break;
            }
					if (ok) break;
        }
        if (!ok) {
					i = 0;
					ok = true;
					for (int k = 0; k < treeElement.getChildCount(); k++)
						if (((TreeValue)treeElement.getChild(k)).getValue() == 0) {
							ok = false;
							break;
						}
				}
				if (ok) {
          ((TreeInteractionsModel)tree.getModel()).addValue(i);
          ((TreeInteractionsModel)tree.getModel()).addEmptyExpression(i, vertex);
          ((TreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((TreeElement)tree.getModel().getRoot());
					if (enu != null)
						while (enu.hasMoreElements())
							tree.expandPath((TreePath)enu.nextElement());
				}
			}
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
