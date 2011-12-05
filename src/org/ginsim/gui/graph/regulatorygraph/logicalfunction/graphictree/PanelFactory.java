package org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree;

import java.awt.Point;

import javax.swing.JTree;

import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeElement;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeExpression;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.LogicalFunctionPanel;


public class PanelFactory {
	private LogicalFunctionPanel panel;

	public PanelFactory(LogicalFunctionPanel p) {
    super();
    panel = p;
  }
  public BooleanFunctionTreePanel getPanel(TreeElement value, JTree tree, boolean sel, int width, boolean edit) {
    BooleanFunctionTreePanel p = null;
    switch (value.getDepth()) {
      case 0 :
        p = new RootPanel(value, tree, sel, width);
        break;
      case 1 :
        p = new ValuePanel(value, tree, sel, width, edit);
        break;
      case 2 :
        if (value instanceof TreeExpression) {
          p = new FunctionPanel(panel, value, tree, sel, width, edit);
					((TreeExpression)value).setGraphicPanel((FunctionPanel)p);
          Point pt =((TreeExpression)value).getSelection();
          if (pt != null) {
						((FunctionPanel)p).selectText(pt);
          }
        }
        break;
      case 3 :
        if (value.getParent() instanceof TreeExpression) {
			p = new ParamPanel(value, tree, sel, width);
		}
        break;
    }
    return p;
  }
}
