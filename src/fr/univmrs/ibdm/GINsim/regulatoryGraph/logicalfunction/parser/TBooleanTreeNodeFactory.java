package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.Stack;
import java.util.Vector;

public class TBooleanTreeNodeFactory {
  private String[] operators = {TAndOperator.SYMBOL, TOrOperator.SYMBOL, TNotOperator.SYMBOL};
  private String operatorsAndParenthesis = "";
  private String returnClassName, operandClassName;
  private TBooleanParser parser;

  public TBooleanTreeNodeFactory(String className, String operandClassName, TBooleanParser parser) throws ClassNotFoundException {
    super();
    returnClassName = className;
    this.operandClassName = operandClassName;
    for (int i = 0; i <(int) operators.length; i++) operatorsAndParenthesis += "\\" + operators[i] + "|";
    operatorsAndParenthesis += "\\(|\\)| ";
    this.parser = parser;
  }
  public Vector getOperators() {
    Vector v = new Vector();
    for (int i = 0; i < operators.length; i++) v.addElement(operators[i]);
    v.addElement("(");
    v.addElement(")");
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
    if (value.equals(TAndOperator.SYMBOL))
      return TAndOperator.priority;
    else if (value.equals(TOrOperator.SYMBOL))
      return TOrOperator.priority;
    else if (value.equals(TNotOperator.SYMBOL))
      return TNotOperator.priority;
    return -1;
  }
  public String getOperatorsAndParenthesis() {
    return operatorsAndParenthesis;
  }
}
