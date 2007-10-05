package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model.GsFunctionEditorModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.GsFunctionsCreator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree.GsParamTree;
import java.util.Iterator;
import java.util.Vector;
import java.awt.Point;

public class GsTreeExpression extends GsTreeElement {
  private String compactExpression, userExpression, dnfExpression;
  private TBooleanTreeNode root;
  private boolean showCompactExpression, showDNFExpression, normal;
  private GsFunctionEditorModel editorModel;
  private GsFunctionsCreator functionsCreator;
  private Point selection;

  public GsTreeExpression(GsTreeElement parent, TBooleanTreeNode root, GsFunctionsCreator fc) {
    super(parent);
    compactExpression = dnfExpression = userExpression = "";
    showCompactExpression = showDNFExpression = false;
    normal = true;
    if (root != null) {
      setRoot(root);
      userExpression = root.toString();
    }
    property.put("invalid", new Boolean(false));
    functionsCreator = fc;
    selection = null;
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
    compactExpression = root.toString();
  }
  public TBooleanTreeNode remove(GsRegulatoryMultiEdge multiEdge) {
    root = remove(multiEdge.getSource().getId(), root);
    if (root != null) {
      compactExpression = root.toString();
      //dnfExpression = userExpression = root.toDNF();
    }
    else {
      compactExpression = userExpression = dnfExpression = "";
    }
    return root;
  }
  public TBooleanTreeNode remove(GsRegulatoryMultiEdge multiEdge, int index) {
    root = remove(multiEdge.getSource().getId() + "#" + index, root);
    if (root != null) {
      shiftIndexes(root, multiEdge, index);
      compactExpression = root.toString();
      //dnfExpression = userExpression = root.toDNF();
    }
    return root;
  }
  private void shiftIndexes(TBooleanTreeNode node, GsRegulatoryMultiEdge multiEdge, int index) {
    String oldId;
    int oldIndex, i;

    try {
      if (node.isLeaf()) {
        i = ((GsBooleanGene)node).getVal().lastIndexOf("#");
        oldId = ((GsBooleanGene)node).getVal();
        oldIndex = -1;
        if (i >= 0) {
          oldIndex = Integer.parseInt(oldId.substring(i + 1));
          oldId = oldId.substring(0, i);
        }
        if (oldIndex > index) {
          oldIndex--;
          ((GsBooleanGene)node).setValue(oldId + "#" + oldIndex);
        }
      }
      else {
        shiftIndexes(((TBooleanOperator)node).getArgs()[0], multiEdge, index);
        if (((TBooleanOperator)node).getNbArgs() == 2) {
          shiftIndexes(((TBooleanOperator)node).getArgs()[1], multiEdge, index);
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
        if (testString.indexOf("#") == -1) {
          testString += "#";
        }
        if (!testString.startsWith(id)) {
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
    if (showCompactExpression)
      return compactExpression;
    else if (showDNFExpression)
      return dnfExpression;
    return userExpression;
  }
  private void makeDNFString() throws Exception {
    GsParamTree tr = functionsCreator.makeTree();
    GsBooleanParser parser = new GsBooleanParser(functionsCreator.getGraphManager().getIncomingEdges(functionsCreator.getCurrentVertex()));
    parser.compile(userExpression);
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
}

