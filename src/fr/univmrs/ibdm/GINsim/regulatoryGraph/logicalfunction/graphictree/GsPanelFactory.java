package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import javax.swing.JTree;
import java.awt.Component;

public class GsPanelFactory {
  private static Component glassPane = null;

  public GsPanelFactory(Component gp) {
    super();
    glassPane = gp;
  }
  public static GsBooleanFunctionTreePanel getPanel(GsTreeElement value, JTree tree, boolean sel, int width, boolean edit) {
    GsBooleanFunctionTreePanel panel = null;
    switch (value.getDepth()) {
      case 0 :
        panel = new GsRootPanel(value, tree, sel, width);
        break;
      case 1 :
        panel = new GsValuePanel(value, tree, sel, width, edit);
        break;
      case 2 :
        panel = new GsFunctionPanel(value, tree, sel, width, edit);
        break;
      case 3 :
        panel = new GsParamPanel(value, tree, sel, width);
        break;
    }
    return panel;
  }
}
