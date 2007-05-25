package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import javax.swing.JTree;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsGlassPane;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsComponentAdapter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsMotionAdapter;

public class GsPanelFactory {
  public GsPanelFactory() {
  }
  public static GsBooleanFunctionTreePanel getPanel(GsTreeElement value, JTree tree, boolean sel, int width) {
    GsBooleanFunctionTreePanel panel = null;
    GsGlassPane glassPane = ((GsTreeInteractionsModel)tree.getModel()).getGlassPane();
    switch (value.getDepth()) {
      case 0 :
        panel = new GsRootPanel(value, tree, sel, width);
        break;
      case 1 :
        panel = new GsValuePanel(value, tree, sel, width);
        panel.setMouseListener(new GsComponentAdapter(glassPane, "value"));
        panel.setMouseMotionListener(new GsMotionAdapter(glassPane));
        break;
      case 2 :
        panel = new GsFunctionPanel(value, tree, sel, width);
        panel.setMouseListener(new GsComponentAdapter(glassPane, "function"));
        panel.setMouseMotionListener(new GsMotionAdapter(glassPane));
        break;
      case 3 :
        panel = new GsParamPanel(value, tree, sel, width);
        panel.setMouseListener(new GsComponentAdapter(glassPane, "param"));
        panel.setMouseMotionListener(new GsMotionAdapter(glassPane));
        break;
    }
    return panel;
  }
}
