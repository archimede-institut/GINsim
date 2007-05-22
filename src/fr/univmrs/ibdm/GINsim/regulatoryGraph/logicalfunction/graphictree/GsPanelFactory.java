package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class GsPanelFactory {
  public GsPanelFactory() {
  }
  public static GsBooleanFunctionTreePanel getPanel(GsTreeElement value, JTree tree, boolean sel, int width, TreePath[] sp) {
    GsBooleanFunctionTreePanel panel = null;
    switch (value.getDepth()) {
      case 0 :
        panel = new GsRootPanel(value, tree, sel, width);
        break;
      case 1 :
        panel = new GsValuePanel(value, tree, sel, width);
        break;
      case 2 :
        panel = new GsFunctionPanel(value, tree, sel, width, sp);
        break;
      case 3 :
        panel = new GsParamPanel(value, tree, sel, width);
        break;
    }
    return panel;
  }
}
