package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;

import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.GsFunctionEditorModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.GsFunctionsCreator;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.tree.GsParamTree;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.parser.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsFunctionPanel;

public class GsTreeExpression extends GsTreeElement {
  private String userExpression;
  private TBooleanTreeNode root;
  private boolean normal;
  private GsFunctionEditorModel editorModel;
  private GsFunctionsCreator functionsCreator;
  private Point selection;
	private GsFunctionPanel graphicPanel;

  public GsTreeExpression(GsTreeElement parent, TBooleanTreeNode root, GsFunctionsCreator fc) {
    super(parent);
    userExpression = "";
    normal = true;
    if (root != null) {
      setRoot(root);
    }
    property.put("invalid", new Boolean(false));
    property.put("autoedit", new Boolean(false));
    functionsCreator = fc;
    selection = null;
  }
  public GsTreeExpression(GsTreeElement parent, String s, GsFunctionsCreator fc) {
  	super(parent);
  	userExpression = s;
    normal = true;
    property.put("invalid", new Boolean(true));
    property.put("autoedit", new Boolean(false));
    functionsCreator = fc;
    selection = null;
  }
	public void setGraphicPanel(GsFunctionPanel p) {
		graphicPanel = p;
	}
	public GsFunctionPanel getGraphicPanel() {
		return graphicPanel;
	}
  public void setEditorModel(GsFunctionEditorModel em) {
    editorModel = em;
  }
  public void setSelection(Point p, boolean norm) {
    selection = p;
    normal = norm;
  }
  public Point getSelection() {
    return selection;
  }
  public GsFunctionEditorModel getEditorModel() {
    return editorModel;
  }
  public void setRoot(TBooleanTreeNode root) {
    this.root = root;
    refreshRoot();
  }
  public void refreshRoot() {
      if (root != null) {
          userExpression = root.toString(false);
      }
  }
  public TBooleanTreeNode remove(GsRegulatoryMultiEdge multiEdge) {
  	if (root != null) {
  		root = remove(multiEdge.getSource().getId(), root);
  		userExpression = "";
  	}
    return root;
  }
  public TBooleanTreeNode remove(GsRegulatoryMultiEdge multiEdge, int index) {
    root = remove(multiEdge.getSource().getId() + ":" + (index + 1), root);
    if (root != null) {
      decIndexes(root, multiEdge, index);
      userExpression = root.toString(false);
    }
    return root;
  }
  private void decIndexes(TBooleanTreeNode node, GsRegulatoryMultiEdge multiEdge, int index) {
    String oldId;
    int oldIndex, i;

    try {
      if (node.isLeaf()) {
        i = ((GsBooleanGene)node).getVal().lastIndexOf(":");
        oldId = ((GsBooleanGene)node).getVal();
        oldIndex = -1;
        if (i >= 0) {
          oldIndex = Integer.parseInt(oldId.substring(i + 1));
          oldId = oldId.substring(0, i);
        }
        if (oldIndex > index + 1) {
          oldIndex--;
          ((GsBooleanGene)node).setValue(oldId + ":" + oldIndex);
        }
      }
      else {
        decIndexes(((TBooleanOperator)node).getArgs()[0], multiEdge, index);
        if (((TBooleanOperator)node).getNbArgs() == 2) {
          decIndexes(((TBooleanOperator)node).getArgs()[1], multiEdge, index);
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  private TBooleanTreeNode remove(String id, TBooleanTreeNode node) {
    TBooleanTreeNode tn1, tn2;
    String testString;
    try {
      if (node.isLeaf()) {
        testString = ((GsBooleanGene)node).getVal();
        if (testString.indexOf(":") == -1) {
          testString += ":";
        }
        if (!id.startsWith(testString)) {
          return node;
        }
      }
      else {
        if (((TBooleanOperator)node).getNbArgs() == 1) {
          tn1 = remove(id, ((TBooleanOperator)node).getArgs()[0]);
          if (tn1 != null) {
            ((TUnaryOperator)node).setArg(tn1);
            return node;
          }
        }
        else if (((TBooleanOperator)node).getNbArgs() == 2) {
          tn1 = remove(id, ((TBooleanOperator)node).getArgs()[0]);
          tn2 = remove(id, ((TBooleanOperator)node).getArgs()[1]);
          if (tn1 != null && tn2 != null) {
            ((TBinaryOperator)node).setArgs(tn1, tn2);
            return node;
          }
          else if (tn1 != null) {
            return tn1;
          }
          else if (tn2 != null) {
            return tn2;
          }
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }
  public String toString() {
    return userExpression;
  }
  private void makeDNFString() throws Exception {
    GsParamTree tr = functionsCreator.makeTree(1234);
    GsBooleanParser parser = new GsBooleanParser(functionsCreator.getGraph().getIncomingEdges(functionsCreator.getCurrentVertex()));
    parser.compile(userExpression, functionsCreator.getGraph(), functionsCreator.getCurrentVertex());
    root = parser.getRoot();
    GsLogicalFunctionList functionList = (GsLogicalFunctionList)parser.eval();
    Vector params = parser.getParams(functionList.getData());
    Iterator it = params.iterator();
    while (it.hasNext()) {
      Iterator it2 = ((Vector)it.next()).iterator();
      Vector v = new Vector();
      while (it2.hasNext()) {
        GsLogicalFunctionListElement element = (GsLogicalFunctionListElement)it2.next();
        v.addElement(element.getEdge().getEdge(element.getIndex()));
      }
    }
  }
  public boolean isLeaf() {
    return false;
  }
  public TBooleanTreeNode getRoot() {
    return root;
  }
  public void drop(GsTreeElement element) {

  }
  public void setText(String s) {
    userExpression = s;
  }
  public boolean isNormal() {
    return normal;
  }
  public GsTreeElement addChild(GsTreeElement element, int index) {
    if (childs != null) {
		return super.addChild(element, index);
	}
    return null;
  }
}

