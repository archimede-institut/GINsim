package org.ginsim.core.graph.regulatorygraph.logicalfunction.parser;

public abstract class TBinaryOperator extends TBooleanOperator {
  protected TBooleanTreeNode leftArg, rightArg;

  public TBinaryOperator() {
    super();
  }
  public void setArgs(TBooleanTreeNode leftNode, TBooleanTreeNode rightNode) {
    leftArg = leftNode;
    rightArg = rightNode;
  }
  public String toString(boolean par) {
  	boolean leftPar = true;
  	if (leftArg.isLeaf()) {
		leftPar = false;
	} else if (((TBooleanOperator)leftArg).getSymbol().equals(getSymbol())) {
		leftPar = false;
	}
  	boolean rightPar = true;
  	if (rightArg.isLeaf()) {
		rightPar = false;
	} else if (((TBooleanOperator)rightArg).getSymbol().equals(getSymbol())) {
		rightPar = false;
	}
  	String s = leftArg.toString(leftPar) + " " + getSymbol() + " " + rightArg.toString(rightPar);
  	if (par) {
		s = "(" + s + ")";
	}
    return s;
  }
  public int getNbArgs() {
    return 2;
  }
  public TBooleanTreeNode[] getArgs() {
    TBooleanTreeNode[] r = new TBooleanTreeNode[2];
    r[0] = leftArg;
    r[1] = rightArg;
    return r;
  }
}
