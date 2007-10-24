package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model;

import java.util.Vector;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.GsListInteraction;

public class GsFunctionNF extends GsFunctionTerm {
  private Vector interactions;

  public GsFunctionNF() {
    interactions = new Vector();
    not = false;
    operator = 0;
    makeStringValue();
  }
  public GsFunctionNF(Vector le, boolean n, int op) {
    if (le != null)
      interactions = new Vector(le);
    else
      interactions = new Vector();
    not = n;
    operator = op;
    makeStringValue();
  }
  public GsFunctionNF(Vector le, boolean n, int op, String s) {
    if (le != null)
      interactions = new Vector(le);
    else
      interactions = new Vector();
    not = n;
    operator = op;
    stringValue = s;
  }
  private void makeStringValue() {
    stringValue = "";
    if (operator == GsFunctionTerm.AND)
      stringValue = " & ";
    else if (operator == GsFunctionTerm.OR)
      stringValue = " | ";
    if (not) stringValue += "!";
    if (interactions.size() == 1)
      stringValue += ((GsListInteraction)interactions.elementAt(0)).stringValue();
    else if (interactions.size() > 1) {
      stringValue += "(";
      stringValue += ((GsListInteraction)interactions.elementAt(0)).stringValue();
      for (int i = 1; i < interactions.size(); i++)
        stringValue += " & " + ((GsListInteraction)interactions.elementAt(i)).stringValue();
      stringValue += ")";
    }
  }
  public Vector getInteractions() {
    return interactions;
  }
  public void update(Vector le) {
    if (le != null)
      interactions = new Vector(le);
    else
      interactions = new Vector();
    if (interactions.size() < 2) not = false;
    makeStringValue();
  }
  public void update() {
    if (interactions.size() < 2) not = false;
    makeStringValue();
  }
  public void toggleNot() {
    super.toggleNot();
    makeStringValue();
  }
  public boolean isNormal() {
    return true;
  }
}
