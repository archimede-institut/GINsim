package org.ginsim.core.graph.regulatorygraph.logicalfunction.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class TBooleanParser {
  private Stack operatorStack, operandStack;
  protected TBooleanTreeNode root;
  protected TBooleanTreeNodeFactory nodeFactory = null;
  protected List<Integer> allData;

  public TBooleanParser(String returnClassName, String operandClassName) throws ClassNotFoundException {
    super();
    nodeFactory = new TBooleanTreeNodeFactory(returnClassName, operandClassName, this);
    operatorStack = operandStack = null;
  }
  public abstract boolean verifOperandList(List list);
//  protected abstract void setAllData(List data);
  public boolean compile(String s) throws Exception {
    boolean ret = true;
    int i, j, k;
    String elem;
    String[] split = s.split(nodeFactory.getOperatorsAndParenthesis());
    List<String> operands = new ArrayList<String>();
    List<String> operators;
    TBooleanTreeNode tbtn;

    for (i = 0; i < split.length; i++) {
      if (!split[i].equals("")) {
        operands.add(split[i]);
      }
    }
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
          else if (operands.contains(elem)) {
            operandStack.push(nodeFactory.createOperand(elem));
          }
          else if (elem.equals("(")) {
            operatorStack.push(elem);
          }
          else if (elem.equals(")")) {
            while (!((String) operatorStack.peek()).equals("(")) {
              tbtn = nodeFactory.createOperator((String) operatorStack.pop(), operandStack);
              if (tbtn != null) {
                operandStack.push(tbtn);
              }
            }
            operatorStack.pop();
          }
          else if (operators.contains(elem)) {
            j = TBooleanTreeNodeFactory.getPriority(elem);
            while (!operatorStack.empty()) {
              k = TBooleanTreeNodeFactory.getPriority((String) operatorStack.peek());
              if (k < j) {
                break;
              }
              tbtn = nodeFactory.createOperator((String) operatorStack.pop(), operandStack);
              if (tbtn != null) {
                operandStack.push(tbtn);
              }
            }
            operatorStack.push(elem);
          }
          i = elem.length() + s.indexOf(elem, i);
        }
        while (!operatorStack.empty()) {
          tbtn = nodeFactory.createOperator((String) operatorStack.pop(), operandStack);
          if (tbtn != null) {
            operandStack.push(tbtn);
          }
          else {
            ret = false;
            break;
          }
        }
        root = (TBooleanTreeNode) operandStack.pop();
        ret = operandStack.isEmpty();
      }
      catch (Exception ex) {
       ret = false;
      }
    }
    else {
      ret = false;
    }
    return ret;
  }
  private String readElement(List<String> operators, List<String> operands, String s, int i) {
    String s2 = s.substring(i).trim(), ret = "";

    for (String tmp: operands) {
      if (s2.startsWith(tmp) && tmp.length() > ret.length()) {
        ret = tmp;
      }
    }
    if (ret.equals("")) {
      for (String tmp: operators) {
        if (s2.startsWith(tmp)) {
          ret = tmp;
          break;
        }
      }
    }
    if (ret.equals("")) {
      return null;
    }
    return ret;
  }
  public List<Integer> getAllData() {
    return allData;
  }
  public TBooleanData eval() throws Exception {
  	if (root == null) return null;
    return root.getValue();
  }
}

