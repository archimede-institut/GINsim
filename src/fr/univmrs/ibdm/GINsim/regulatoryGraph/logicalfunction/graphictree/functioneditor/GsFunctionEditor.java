package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model.*;

public class GsFunctionEditor {
  private GsFunctionEditorWindow window;
  private GsFunctionEditorModel model;
  private GsFunctionEditorControler controler;

  public GsFunctionEditor(GsTreeInteractionsModel model, GsFunctionPanel p) {
    GsTreeExpression exp = p.getTreeExpression();
    if (exp.getEditorModel() == null) {
      this.model = new GsFunctionEditorModel(model, exp);
    }
    else
      this.model = exp.getEditorModel();
    controler = new GsFunctionEditorControler(p, this.model);
    window = new GsFunctionEditorWindow(controler, this.model);
  }
  public GsFunctionEditorWindow getWindow() {
    return window;
  }
}
