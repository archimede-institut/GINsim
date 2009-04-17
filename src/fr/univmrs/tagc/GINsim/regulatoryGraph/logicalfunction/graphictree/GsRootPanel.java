package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import fr.univmrs.tagc.GINsim.regulatoryGraph.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.tagc.common.manageressources.*;
import fr.univmrs.tagc.common.widgets.*;

public class GsRootPanel extends GsBooleanFunctionTreePanel implements ActionListener {
  private static final long serialVersionUID = -1866485315946504210L;
  private JLabel label;
  private GsButton button;

  public GsRootPanel(GsTreeElement value, JTree tree, boolean sel, int width) {
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
    GsRegulatoryVertex vertex;
    boolean ok = true;
    byte i;

    if (e.getSource() == button) {
      try {
        vertex = ((GsTreeInteractionsModel)tree.getModel()).getVertex();
        Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
        for (i = 1 ; i <= vertex.getMaxValue(); i++) {
          ok = true;
          for (int k = 0; k < treeElement.getChildCount(); k++)
						if (((GsTreeValue)treeElement.getChild(k)).getValue() == i) {
              ok = false;
              break;
            }
					if (ok) break;
        }
        if (!ok) {
					i = 0;
					ok = true;
					for (int k = 0; k < treeElement.getChildCount(); k++)
						if (((GsTreeValue)treeElement.getChild(k)).getValue() == 0) {
							ok = false;
							break;
						}
				}
				if (ok) {
          ((GsTreeInteractionsModel)tree.getModel()).addValue(i);
          ((GsTreeInteractionsModel)tree.getModel()).addEmptyExpression(i, vertex);
          ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
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
