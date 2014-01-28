package org.ginsim.gui.graph.regulatorygraph.logicalfunction.neweditor;

import javax.swing.JTree;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeExpression;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.FunctionPanelImpl;


public class FunctionEditor {
  private FunctionEditorEditPanel editPanel;
	private FunctionEditorDisplayPanel displayPanel;
  private FunctionEditorModelImpl model;
  private FunctionEditorControler controler;

  public FunctionEditor() {
  	editPanel = new FunctionEditorEditPanel();
		displayPanel = new FunctionEditorDisplayPanel();
    controler = new FunctionEditorControler(editPanel, displayPanel);
    model = new FunctionEditorModelImpl(controler);
  }
  public FunctionEditorEditPanel getEditPanel() {
    return editPanel;
  }
	public FunctionEditorDisplayPanel getDisplayPanel() {
		return displayPanel;
	}
	public void reset() {
		controler.reset();
	}
	public void validate() {
		controler.exec(FunctionEditorControler.VALIDATE_EDIT, null);
	}
	public void setVisible(boolean b) {
		editPanel.setVisible(b);
		displayPanel.setVisible(b);
	}
  public void init(TreeInteractionsModel m, FunctionPanelImpl p) {
  	model.init(m, p);
    controler.init(p, model);
  	editPanel.init(model);
	}
	public void init(TreeExpression e, RegulatoryNode v, RegulatoryGraph graph, JTree t) {
		model.init((TreeInteractionsModel)t.getModel(), e.getGraphicPanel());
		controler.init(v, graph, t, model);
		displayPanel.init(e);
	}
	public FunctionEditorModelImpl getModel() {
  	return model;
  }
}
