package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.*;

public abstract class TBooleanParser {
  private Stack operatorStack, operandStack;
  protected TBooleanTreeNode root;
  private TBooleanTreeNodeFactory nodeFactory = null;
  protected Vector allData;

  public TBooleanParser(String returnClassName, String operandClassName) throws ClassNotFoundException {
    super();
    nodeFactory = new TBooleanTreeNodeFactory(returnClassName, operandClassName, this);
    operatorStack = operandStack = null;
  }
  public abstract boolean verifOperandList(Vector list);
  protected abstract void setAllData(Vector list);
  public boolean compile(String s) {
    boolean ret = true;
    int i, j, k;
    String elem;
    String[] split = s.split(nodeFactory.getOperatorsAndParenthesis());
    Vector operands = new Vector();
    Vector operators;
    TBooleanTreeNode tbtn;

    for (i = 0; i < split.length; i++) if (!split[i].equals("")) operands.addElement(split[i]);
    if (verifOperandList(operands)) {
      operators = nodeFactory.getOperators();
      i = 0;
      operandStack = new Stack();
      operatorStack = new Stack();
      try {
        while (i != s.length()) {
          elem = readElement(operators, operands, s, i);
          if (elem == null) {
            ret = false;
            break;
          }
          else if (operands.contains(elem))
            operandStack.push(nodeFactory.createOperand(elem));
          else if (elem.equals("("))
            operatorStack.push(elem);
          else if (elem.equals(")")) {
            while (!((String) operatorStack.peek()).equals("(")) {
              tbtn = nodeFactory.createOperator((String) operatorStack.pop(), operandStack);
              if (tbtn != null) operandStack.push(tbtn);
            }
            operatorStack.pop();
          }
          else if (operators.contains(elem)) {
            j = TBooleanTreeNodeFactory.getPriority(elem);
            while (!operatorStack.empty()) {
              k = TBooleanTreeNodeFactory.getPriority((String) operatorStack.peek());
              if (k <= j)
                break;
              else {
                tbtn = nodeFactory.createOperator((String) operatorStack.pop(), operandStack);
                if (tbtn != null) operandStack.push(tbtn);
              }
            }
            operatorStack.push(elem);
          }
          i = elem.length() + s.indexOf(elem, i);
        }
        while (!operatorStack.empty()) {
          tbtn = nodeFactory.createOperator((String) operatorStack.pop(), operandStack);
          if (tbtn != null)
            operandStack.push(tbtn);
          else {
            ret = false;
            break;
          }
        }
        root = (TBooleanTreeNode) operandStack.pop();
      }
      catch (Exception ex) {
        ret = false;
      }
    }
    else
      ret = false;
    return ret;
  }
  private String readElement(Vector operators, Vector operands, String s, int i) {
    String s2 = s.substring(i).trim(), tmp, ret = null;

    for (Enumeration enu = operands.elements(); enu.hasMoreElements(); ) {
      tmp = (String)enu.nextElement();
      if (s2.startsWith(tmp)) {
        ret = tmp;
        break;
      }
    }
    for (Enumeration enu = operators.elements(); enu.hasMoreElements(); ) {
      tmp = (String)enu.nextElement();
      if (s2.startsWith(tmp)) {
        ret = tmp;
        break;
      }
    }
    return ret;
  }
  public Vector getAllData() {
    return allData;
  }
  public TBooleanData eval() throws Exception {
    return root.getValue();
  }
}

