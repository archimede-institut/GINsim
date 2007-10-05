package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsFunctionPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model.GsFunctionEditorModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model.GsFunctionTerm;
import java.util.Vector;

public class GsFunctionEditorControler {
  public static final int ADD_TERM = 1;
  public static final int MODIF_TERM = 2;
  public static final int NOT = 3;
  public static final int ADD_EMPTY_TERM = 4;
  public static final int PREVIOUS_TERM = 5;
  public static final int NEXT_TERM = 6;
  public static final int CANCEL = 7;
  public static final int OK = 8;

  private GsFunctionPanel functionPanel;
  private GsFunctionEditorModel editorModel;

  public GsFunctionEditorControler(GsFunctionPanel p, GsFunctionEditorModel m) {
    functionPanel = p;
    editorModel = m;
  }
  public void exec(int action) {
    switch(action) {
      case NOT :
        editorModel.not();
        functionPanel.validateText(editorModel.getStringValue());
        break;
      case PREVIOUS_TERM :
        editorModel.setCurrentTerm(editorModel.getCurrentTermIndex() - 1);
        functionPanel.validateText(editorModel.getStringValue());
        break;
      case NEXT_TERM :
        editorModel.setCurrentTerm(editorModel.getCurrentTermIndex() + 1);
        functionPanel.validateText(editorModel.getStringValue());
        break;
      case CANCEL :
        functionPanel.getTreeExpression().setSelection(null, true);
        functionPanel.revalidate();
        break;
      case OK :
        functionPanel.getTreeExpression().setSelection(null, true);
        functionPanel.revalidate();
        break;
    }
  }
  public void exec(int action, Object p) {
    switch(action) {
      case ADD_TERM :

        break;
      case ADD_EMPTY_TERM :
        editorModel.addTerm(null, false, ((Integer)p).intValue());
        functionPanel.validateText(editorModel.getStringValue());
        break;
    }
  }
  public void exec(int action, Object p1, Object p2) {
    switch(action) {
      case MODIF_TERM :
        ((GsFunctionTerm)p2).update((Vector)p1);
        functionPanel.validateText(editorModel.getStringValue());
        break;
    }
  }
}
