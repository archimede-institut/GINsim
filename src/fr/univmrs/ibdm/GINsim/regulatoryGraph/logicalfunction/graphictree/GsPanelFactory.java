package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import javax.swing.JTree;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsGlassPane;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsComponentAdapter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsMotionAdapter;
import javax.swing.JComponent;
import java.awt.Component;

public class GsPanelFactory {
  private static Component glassPane = null;

  public GsPanelFactory(Component gp) {
    super();
    glassPane = gp;
  }
  public static GsBooleanFunctionTreePanel getPanel(GsTreeElement value, JTree tree, boolean sel, int width, boolean editor) {
    GsBooleanFunctionTreePanel panel = null;
    //GsGlassPane glassPane = ((GsTreeInteractionsModel)tree.getModel()).getGlassPane();
    switch (value.getDepth()) {
      case 0 :
        panel = new GsRootPanel(value, tree, sel, width);
        break;
      case 1 :
        panel = new GsValuePanel(value, tree, sel, width);
        panel.setMouseListener(new GsComponentAdapter((GsGlassPane)glassPane, "value"));
        panel.setMouseMotionListener(new GsMotionAdapter((GsGlassPane)glassPane));
        break;
      case 2 :
        panel = new GsFunctionPanel(value, tree, sel, width, editor);
        panel.setMouseListener(new GsComponentAdapter((GsGlassPane)glassPane, "function"));
        panel.setMouseMotionListener(new GsMotionAdapter((GsGlassPane)glassPane));
        break;
      case 3 :
        panel = new GsParamPanel(value, tree, sel, width);
        panel.setMouseListener(new GsComponentAdapter((GsGlassPane)glassPane, "param"));
        panel.setMouseMotionListener(new GsMotionAdapter((GsGlassPane)glassPane));
        break;
    }
    return panel;
  }
}
