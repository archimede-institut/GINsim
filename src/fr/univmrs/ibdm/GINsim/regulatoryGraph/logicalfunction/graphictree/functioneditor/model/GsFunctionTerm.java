package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model;

import java.util.Vector;

public abstract class GsFunctionTerm {
  public final static int AND = 1;
  public final static int OR = 2;
  public final static char[] CHARS = { '&', '|' };

  protected String stringValue;
  protected boolean not;
  protected int operator;

  public Vector getInteractions() {
    return null;
  }
  public String stringValue() {
    return stringValue;
  }
  public boolean isNot() {
    return not;
  }
  public void setNot(boolean n) {
    not = n;
  }
  public void toggleNot() {
    not = !not;
  }
  public void update(Vector le) {

  }
  public void update(boolean n) {

  }
  public boolean isNormal() {
    return false;
  }
  public static String getOperator(int op) {
    switch (op) {
      case 1 :
        return String.valueOf(CHARS[0]);
      case 2 :
        return String.valueOf(CHARS[1]);
    }
    return "";
  }
}
