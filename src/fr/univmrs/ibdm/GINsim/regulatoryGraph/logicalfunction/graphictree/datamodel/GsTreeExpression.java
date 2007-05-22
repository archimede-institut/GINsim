package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanOperator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TUnaryOperator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBinaryOperator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsBooleanGene;

public class GsTreeExpression extends GsTreeElement {
  private String expression;
  private TBooleanTreeNode root;

  public GsTreeExpression(GsTreeElement parent, TBooleanTreeNode root) {
    super(parent);
    if (root != null)
      setRoot(root);
    else
      root = null;
    property.put("show unselected", new Boolean(false));
  }
  public void setRoot(TBooleanTreeNode root) {
    this.root = root;
    expression = root.toString();
  }
  public TBooleanTreeNode remove(GsRegulatoryMultiEdge multiEdge) {
    root = remove(multiEdge.getSource().getId(), root);
    if (root != null)
      expression = root.toString();
    else
      expression = null;
    return root;
  }
  public TBooleanTreeNode remove(GsRegulatoryMultiEdge multiEdge, int index) {
    root = remove(multiEdge.getSource().getId() + "#" + index, root);
    if (root != null) {
      shiftIndexes(root, multiEdge, index);
      expression = root.toString();
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
        if (((TBooleanOperator)node).getNbArgs() == 2)
          shiftIndexes(((TBooleanOperator)node).getArgs()[1], multiEdge, index);
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
        if (testString.indexOf("#") == -1) testString += "#";
        if (!testString.startsWith(id))
          return node;
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
          if ((tn1 != null) && (tn2 != null)) {
            ((TBinaryOperator)node).setArgs(tn1, tn2);
            return node;
          }
          else if (tn1 != null)
            return tn1;
          else if (tn2 != null)
            return tn2;
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }
  public int getAllChildCount() {
    return childs.size();
  }
  public GsTreeElement getAllChild(int index) {
    if (index < childs.size())
      return (GsTreeElement)childs.elementAt(index);
    return null;
  }

  public String toString() {
    if (expression == null) return "";
    return expression;
  }
  public boolean isLeaf() {
    return false;
  }
  public TBooleanTreeNode getRoot() {
    return root;
  }
}

