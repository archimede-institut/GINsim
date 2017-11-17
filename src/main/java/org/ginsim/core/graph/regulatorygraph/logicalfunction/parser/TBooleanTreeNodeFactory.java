package org.ginsim.core.graph.regulatorygraph.logicalfunction.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TBooleanTreeNodeFactory {
  private String[] operators = {TAndOperator.SYMBOL, TOrOperator.SYMBOL, TNotOperator.SYMBOL};
  private String operatorsAndParenthesis = "";
  private String returnClassName, operandClassName;
  private TBooleanParser parser;

  public TBooleanTreeNodeFactory(String className, String operandClassName, TBooleanParser parser) {
    super();
    returnClassName = className;
    this.operandClassName = operandClassName;
    for (int i = 0; i < operators.length; i++) {
		operatorsAndParenthesis += "\\" + operators[i] + "|";
	}
    operatorsAndParenthesis += "\\(|\\)| ";
    this.parser = parser;
  }
  public List<String> getOperators() {
    List<String> v = new ArrayList<String>();
    for (int i = 0; i < operators.length; i++) {
		v.add(operators[i]);
	}
    v.add("(");
    v.add(")");
    return v;
  }
  public TBooleanOperator createOperator(String value, Stack stack) {
    TBooleanOperator op = null;
    TBooleanTreeNode n1, n2;

    if (value.equals(TAndOperator.SYMBOL)) {
      op = new TAndOperator();
      op.setReturnClass(returnClassName);
      op.setParser(parser);
      n2 = (TBooleanTreeNode)stack.pop();
      n1 = (TBooleanTreeNode)stack.pop();
      ((TBinaryOperator)op).setArgs(n1, n2);
    }
    else if (value.equals(TOrOperator.SYMBOL)) {
      op = new TOrOperator();
      op.setReturnClass(returnClassName);
      op.setParser(parser);
      n2 = (TBooleanTreeNode)stack.pop();
      n1 = (TBooleanTreeNode)stack.pop();
      ((TBinaryOperator)op).setArgs(n1, n2);
    }
    else if (value.equals(TNotOperator.SYMBOL)) {
      op = new TNotOperator();
      op.setReturnClass(returnClassName);
      op.setParser(parser);
      n1 = (TBooleanTreeNode)stack.pop();
      ((TUnaryOperator)op).setArg(n1);
    }
    return op;
  }
  public TBooleanOperand createOperand(String value) throws Exception {
    TBooleanOperand bo = (TBooleanOperand)Class.forName(operandClassName).newInstance();
    bo.setValue(value);
    bo.setParser(parser);
    return bo;
  }

  public static int getPriority(String value) {
    if (value.equals(TAndOperator.SYMBOL)) {
		return TAndOperator.priority;
	} else if (value.equals(TOrOperator.SYMBOL)) {
		return TOrOperator.priority;
	} else if (value.equals(TNotOperator.SYMBOL)) {
		return TNotOperator.priority;
	}
    return -1;
  }
  public String getOperatorsAndParenthesis() {
    return operatorsAndParenthesis;
  }
}
