package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor;

import javax.swing.JTree;

import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsFunctionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;

public class GsFunctionEditor {
  private GsFunctionEditorEditPanel editPanel;
	private GsFunctionEditorDisplayPanel displayPanel;
  private GsFunctionEditorModel model;
  private GsFunctionEditorControler controler;

  public GsFunctionEditor() {
  	editPanel = new GsFunctionEditorEditPanel();
		displayPanel = new GsFunctionEditorDisplayPanel();
    controler = new GsFunctionEditorControler(editPanel, displayPanel);
    model = new GsFunctionEditorModel(controler);
  }
  public GsFunctionEditorEditPanel getEditPanel() {
    return editPanel;
  }
	public GsFunctionEditorDisplayPanel getDisplayPanel() {
		return displayPanel;
	}
	public void reset() {
		controler.reset();
	}
	public void validate() {
		controler.exec(GsFunctionEditorControler.VALIDATE_EDIT, null);
	}
	public void setVisible(boolean b) {
		editPanel.setVisible(b);
		displayPanel.setVisible(b);
	}
  public void init(GsTreeInteractionsModel m, GsFunctionPanel p) {
  	model.init(m, p);
    controler.init(p, model);
  	editPanel.init(model);
	}
	public void init(GsTreeExpression e, GsRegulatoryVertex v, GsRegulatoryGraph graph, JTree t) {
		model.init((GsTreeInteractionsModel)t.getModel(), e.getGraphicPanel());
		controler.init(v, graph, t, model);
		displayPanel.init(e);
	}
	public GsFunctionEditorModel getModel() {
  	return model;
  }
}
