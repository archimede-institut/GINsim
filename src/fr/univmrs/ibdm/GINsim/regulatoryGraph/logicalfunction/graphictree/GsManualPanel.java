package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.ibdm.GINsim.util.widget.GsJButton;

public class GsManualPanel extends GsBooleanFunctionTreePanel implements ActionListener {
  /**
	 * 
	 */
	private static final long	serialVersionUID	= -1746326107372469096L;
private JLabel label;
  private JButton button;

  public GsManualPanel(GsTreeElement value, JTree tree, boolean sel, int width) {
    super(value, tree, sel, width);
    label = new JLabel(value.toString());
    label.setFont(defaultFont);
    button = new GsJButton("add.png");
    button.addActionListener(this);
    if (sel) {
      setBackground(Color.yellow);
    }
    else {
      setBackground(Color.white);
    }

    add(button, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                       GridBagConstraints.NONE, new Insets(2, 2, 2, 0), 0, 0));
    add(label, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                      GridBagConstraints.NONE, new Insets(2, 5, 2, 2), 0, 0));
  }
  public void actionPerformed(ActionEvent e) {
    tree.stopEditing();
    addButtonAction();
  }
  public void addButtonAction() {
    GsParameterChoiceWindow choiceWindow;
    GsTreeInteractionsModel model = (GsTreeInteractionsModel)tree.getModel();
    Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    GsTreeParam param = null;
    Object[] path = new Object[4];
    TreePath treePath;

    try {
      param = model.addEmptyParameter((short)((GsTreeValue)treeElement.getParent()).getValue(), model.getVertex());
      model.fireTreeStructureChanged((GsTreeElement)model.getRoot());
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    while (enu.hasMoreElements()) {
    	tree.expandPath((TreePath)enu.nextElement());
    }
    path[0] = model.getRoot();
    path[3] = param;
    path[2] = param.getParent();
    path[1] = param.getParent().getParent();
    treePath = new TreePath(path);
    tree.scrollPathToVisible(treePath);
    tree.setSelectionPath(treePath);
    choiceWindow = new GsParameterChoiceWindow(tree);
    choiceWindow.init(model.getGraph().getGraphManager().getIncomingEdges(model.getVertex()), defaultFont, param,
      ((GsTreeValue)treeElement.getParent()).getValue());
    Point p = tree.getPathBounds(treePath).getLocation();
    p.translate(0, - choiceWindow.getPreferredSize().height - 2);
    SwingUtilities.convertPointToScreen(p, tree);
    choiceWindow.setLocation(p);
    choiceWindow.setVisible(true);
  }
}
