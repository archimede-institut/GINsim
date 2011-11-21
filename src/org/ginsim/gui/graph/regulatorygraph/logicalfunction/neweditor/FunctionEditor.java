package org.ginsim.gui.graph.regulatorygraph.logicalfunction.neweditor;

import javax.swing.JTree;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeExpression;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.FunctionPanel;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;


public class FunctionEditor {
  private FunctionEditorEditPanel editPanel;
	private FunctionEditorDisplayPanel displayPanel;
  private FunctionEditorModel model;
  private FunctionEditorControler controler;

  public FunctionEditor() {
  	editPanel = new FunctionEditorEditPanel();
		displayPanel = new FunctionEditorDisplayPanel();
    controler = new FunctionEditorControler(editPanel, displayPanel);
    model = new FunctionEditorModel(controler);
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
  public void init(TreeInteractionsModel m, FunctionPanel p) {
  	model.init(m, p);
    controler.init(p, model);
  	editPanel.init(model);
	}
	public void init(TreeExpression e, RegulatoryVertex v, RegulatoryGraph graph, JTree t) {
		model.init((TreeInteractionsModel)t.getModel(), e.getGraphicPanel());
		controler.init(v, graph, t, model);
		displayPanel.init(e);
	}
	public FunctionEditorModel getModel() {
  	return model;
  }
}
