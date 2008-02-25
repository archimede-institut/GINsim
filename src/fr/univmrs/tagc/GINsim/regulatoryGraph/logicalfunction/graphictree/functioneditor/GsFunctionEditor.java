package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsFunctionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model.GsFunctionEditorModel;

public class GsFunctionEditor {
  private GsFunctionEditorWindow window;
  private GsFunctionEditorModel model;
  private GsFunctionEditorControler controler;

  public GsFunctionEditor(GsTreeInteractionsModel model, GsFunctionPanel p) {
    GsTreeExpression exp = p.getTreeExpression();
    this.model = new GsFunctionEditorModel(model, exp);
    controler = new GsFunctionEditorControler(p, this.model);
    window = new GsFunctionEditorWindow(controler, this.model);
  }
  public GsFunctionEditorWindow getWindow() {
    return window;
  }
}
