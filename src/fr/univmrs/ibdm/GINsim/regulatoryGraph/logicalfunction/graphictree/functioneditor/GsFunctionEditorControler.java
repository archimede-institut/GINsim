package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsFunctionPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model.GsFunctionEditorModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model.GsFunctionTerm;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.GsFunctionsCreator;

public class GsFunctionEditorControler {
  public static final int ADD_TERM = 1;
  public static final int MODIF_TERM = 2;
  public static final int NOT = 3;
  public static final int ADD_EMPTY_TERM = 4;
  public static final int PREVIOUS_TERM = 5;
  public static final int NEXT_TERM = 6;
  public static final int CANCEL = 7;
  public static final int OK = 8;
  public static final int DELETE = 9;
  public static final int COMPACT = 10;
  public static final int DNF = 11;

  private GsFunctionPanel functionPanel;
  private GsFunctionEditorModel editorModel;
  private String initString = "";

  public GsFunctionEditorControler(GsFunctionPanel p, GsFunctionEditorModel m) {
    functionPanel = p;
    editorModel = m;
    functionPanel.setText(editorModel.getOldExp());
  }
  public void exec(int action) {
    switch(action) {
      case NOT :
        editorModel.not();
        functionPanel.setText(editorModel.getStringValue());
        break;
      case PREVIOUS_TERM :
        editorModel.setCurrentTerm(editorModel.getCurrentTermIndex() - 1);
        functionPanel.setText(editorModel.getStringValue());
        break;
      case NEXT_TERM :
        editorModel.setCurrentTerm(editorModel.getCurrentTermIndex() + 1);
        functionPanel.setText(editorModel.getStringValue());
        break;
      case CANCEL :
        functionPanel.getTreeExpression().setSelection(null, true);
        functionPanel.setText(editorModel.getOldExp());
        editorModel.clearSelection();
        break;
      case OK :
        functionPanel.getTreeExpression().setSelection(null, true);
        functionPanel.validateText(editorModel.getStringValue());
        editorModel.clearSelection();
        break;
      case DELETE :
        editorModel.deleteCurrentTerm();
        functionPanel.validateText(editorModel.getStringValue());
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
      case COMPACT :
        if (((Boolean)p).booleanValue()) {
          if (initString.equals("")) {
			initString = editorModel.getStringValue();
		}
          functionPanel.validateText(editorModel.getStringValue());
          Vector params = functionPanel.getTreeExpression().getChilds();
          GsFunctionsCreator c = null;
          Vector v = new Vector();
          int value = ((GsTreeValue)functionPanel.getTreeExpression().getParent()).getValue();
          GsLogicalParameter lp;

          for (int i = 0; i < params.size(); i++) {
            lp = new GsLogicalParameter(value);
            lp.setEdges(((GsTreeParam) params.elementAt(i)).getEdgeIndexes());
            v.addElement(lp);
          }
          c = new GsFunctionsCreator(editorModel.getTreeInteractionsModel().getGraph().getGraphManager(),
                                     v, editorModel.getTreeInteractionsModel().getVertex());
          Hashtable h = c.doIt();
          v = (Vector)h.elements().nextElement();
          Enumeration enu = v.elements();
          String s = (String)enu.nextElement();
          while (enu.hasMoreElements()) {
			s = s + " | (" + (String) enu.nextElement() + ")";
		}
          functionPanel.setText(s);
          editorModel.update();
          functionPanel.getTreeExpression().setSelection(editorModel.getCurrentPosition(),
              editorModel.getCurrentTerm().isNormal());
        }
        else {
          functionPanel.setText(initString);
          editorModel.update();
          functionPanel.getTreeExpression().setSelection(editorModel.getCurrentPosition(),
              editorModel.getCurrentTerm().isNormal());
          initString = "";
        }
        break;
      case DNF :
        if (((Boolean)p).booleanValue()) {
          if (initString.equals("")) {
			initString = editorModel.getStringValue();
		}
          functionPanel.validateText(editorModel.getStringValue());
          Vector params = functionPanel.getTreeExpression().getChilds();
          GsFunctionsCreator c = null;
          Vector v = new Vector();
          int value = ((GsTreeValue)functionPanel.getTreeExpression().getParent()).getValue();
          GsLogicalParameter lp;

          for (int i = 0; i < params.size(); i++) {
            lp = new GsLogicalParameter(value);
            lp.setEdges(((GsTreeParam) params.elementAt(i)).getEdgeIndexes());
            v.addElement(lp);
          }
          c = new GsFunctionsCreator(editorModel.getTreeInteractionsModel().getGraph().getGraphManager(),
                                     v, editorModel.getTreeInteractionsModel().getVertex());
          String s = c.makeDNFExpression(value);
          functionPanel.setText(s);
          editorModel.update();
          functionPanel.getTreeExpression().setSelection(editorModel.getCurrentPosition(),
              editorModel.getCurrentTerm().isNormal());
        }
        else {
          functionPanel.setText(initString);
          editorModel.update();
          functionPanel.getTreeExpression().setSelection(editorModel.getCurrentPosition(),
              editorModel.getCurrentTerm().isNormal());
          initString = "";
        }
        break;
    }
  }
  public void exec(int action, Object p1, Object p2) {
    switch(action) {
      case MODIF_TERM :
        ((GsFunctionTerm)p2).update((Vector)p1);
        functionPanel.setText(editorModel.getStringValue());
        break;
    }
  }
}
