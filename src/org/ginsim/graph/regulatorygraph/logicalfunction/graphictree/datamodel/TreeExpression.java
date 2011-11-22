package org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel;

import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.logicalfunction.BooleanGene;
import org.ginsim.graph.regulatorygraph.logicalfunction.BooleanParser;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalFunctionList;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalFunctionListElement;
import org.ginsim.graph.regulatorygraph.logicalfunction.param2function.FunctionsCreator;
import org.ginsim.graph.regulatorygraph.logicalfunction.param2function.tree.ParamTree;
import org.ginsim.graph.regulatorygraph.logicalfunction.parser.TBinaryOperator;
import org.ginsim.graph.regulatorygraph.logicalfunction.parser.TBooleanOperator;
import org.ginsim.graph.regulatorygraph.logicalfunction.parser.TBooleanTreeNode;
import org.ginsim.graph.regulatorygraph.logicalfunction.parser.TUnaryOperator;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.FunctionPanel;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.neweditor.FunctionEditorModel;


public class TreeExpression extends TreeElement {
  private String userExpression;
  private TBooleanTreeNode root;
  private boolean normal;
  private FunctionEditorModel editorModel;
  private FunctionsCreator functionsCreator;
  private Point selection;
	private FunctionPanel graphicPanel;

  public TreeExpression(TreeElement parent, TBooleanTreeNode root, FunctionsCreator fc) {
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
  public TreeExpression(TreeElement parent, String s, FunctionsCreator fc) {
  	super(parent);
  	userExpression = s;
    normal = true;
    property.put("invalid", new Boolean(true));
    property.put("autoedit", new Boolean(false));
    functionsCreator = fc;
    selection = null;
  }
	public void setGraphicPanel(FunctionPanel p) {
		graphicPanel = p;
	}
	public FunctionPanel getGraphicPanel() {
		return graphicPanel;
	}
  public void setEditorModel(FunctionEditorModel em) {
    editorModel = em;
  }
  public void setSelection(Point p, boolean norm) {
    selection = p;
    normal = norm;
  }
  public Point getSelection() {
    return selection;
  }
  public FunctionEditorModel getEditorModel() {
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
  public TBooleanTreeNode remove(RegulatoryMultiEdge multiEdge) {
  	if (root != null) {
  		root = remove(multiEdge.getSource().getId(), root);
  		userExpression = "";
  	}
    return root;
  }
  public TBooleanTreeNode remove(RegulatoryMultiEdge multiEdge, int index) {
    root = remove(multiEdge.getSource().getId() + ":" + (index + 1), root);
    if (root != null) {
      decIndexes(root, multiEdge, index);
      userExpression = root.toString(false);
    }
    return root;
  }
  private void decIndexes(TBooleanTreeNode node, RegulatoryMultiEdge multiEdge, int index) {
    String oldId;
    int oldIndex, i;

    try {
      if (node.isLeaf()) {
        i = ((BooleanGene)node).getVal().lastIndexOf(":");
        oldId = ((BooleanGene)node).getVal();
        oldIndex = -1;
        if (i >= 0) {
          oldIndex = Integer.parseInt(oldId.substring(i + 1));
          oldId = oldId.substring(0, i);
        }
        if (oldIndex > index + 1) {
          oldIndex--;
          ((BooleanGene)node).setValue(oldId + ":" + oldIndex);
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
        testString = ((BooleanGene)node).getVal();
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
    ParamTree tr = functionsCreator.makeTree(1234);
    BooleanParser parser = new BooleanParser(functionsCreator.getGraph().getIncomingEdges(functionsCreator.getCurrentNode()));
    parser.compile(userExpression, functionsCreator.getGraph(), functionsCreator.getCurrentNode());
    root = parser.getRoot();
    LogicalFunctionList functionList = (LogicalFunctionList)parser.eval();
    Vector params = parser.getParams(functionList.getData());
    Iterator it = params.iterator();
    while (it.hasNext()) {
      Iterator it2 = ((Vector)it.next()).iterator();
      Vector v = new Vector();
      while (it2.hasNext()) {
        LogicalFunctionListElement element = (LogicalFunctionListElement)it2.next();
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
  public void drop(TreeElement element) {

  }
  public void setText(String s) {
    userExpression = s;
  }
  public boolean isNormal() {
    return normal;
  }
  public TreeElement addChild(TreeElement element, int index) {
    if (childs != null) {
		return super.addChild(element, index);
	}
    return null;
  }
}

