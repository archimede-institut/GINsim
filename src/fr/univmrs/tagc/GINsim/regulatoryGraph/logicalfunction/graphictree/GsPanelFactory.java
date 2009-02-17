package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Point;

import javax.swing.JTree;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;

public class GsPanelFactory {
	private static GsLogicalFunctionPanel panel;

	public GsPanelFactory(GsLogicalFunctionPanel p) {
    super();
    panel = p;
  }
  public static GsBooleanFunctionTreePanel getPanel(GsTreeElement value, JTree tree, boolean sel, int width, boolean edit) {
    GsBooleanFunctionTreePanel p = null;
    switch (value.getDepth()) {
      case 0 :
        p = new GsRootPanel(value, tree, sel, width);
        break;
      case 1 :
        p = new GsValuePanel(value, tree, sel, width, edit);
        break;
      case 2 :
        if (value instanceof GsTreeExpression) {
          p = new GsFunctionPanel(panel, value, tree, sel, width, edit);
					((GsTreeExpression)value).setGraphicPanel((GsFunctionPanel)p);
          Point pt =((GsTreeExpression)value).getSelection();
          if (pt != null) {
            ((GsFunctionPanel)p).selectText(pt);
          }
        }
        break;
      case 3 :
        if (value.getParent() instanceof GsTreeExpression) {
			p = new GsParamPanel(value, tree, sel, width);
		}
        break;
    }
    return p;
  }
}
