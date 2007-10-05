package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model;

import java.awt.Point;
import java.util.List;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.jgraph.GsJgraphDirectedEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.GsListInteraction;
import java.util.Enumeration;

public class GsFunctionEditorModel {
  private GsTreeInteractionsModel model;
  private Vector interactions;
  private Vector terms;
  private int currentTerm;
  private boolean dnf, compact;

  public GsFunctionEditorModel(GsTreeInteractionsModel model, GsTreeExpression exp) {
    this.model = model;
    GsRegulatoryMultiEdge o;
    interactions = new Vector();
    List ed = model.getGraph().getGraphManager().getIncomingEdges(model.getVertex());
    for (int i = 0; i < ed.size(); i++) {
      o = (GsRegulatoryMultiEdge)((GsJgraphDirectedEdge)ed.get(i)).getUserObject();
      for (int j = -1; j < o.getEdgeCount(); j++)
        interactions.addElement(new GsListInteraction(o, j));
    }
    parseExpression(exp.toString());
    //addTerm(null, false, 0);
    dnf = compact = false;
    exp.setEditorModel(this);
  }

  public void not() {
    getCurrentTerm().toggleNot();
  }
  public Vector getInteractions() {
    return interactions;
  }
  public void addTerm(Vector le, boolean not, int op) {
    insertTerm(le, not, op, ++currentTerm);
  }
  public void insertTerm(Vector le, boolean not, int op, int pos) {
    GsFunctionNF t = new GsFunctionNF(le, not, op);
    terms.insertElementAt(t, pos);
  }
  public GsFunctionTerm getTerm(int i) {
    return (GsFunctionTerm)terms.elementAt(i);
  }
  public GsFunctionTerm getCurrentTerm() {
    return (GsFunctionTerm)terms.elementAt(currentTerm);
  }
  public int getCurrentTermIndex() {
    return currentTerm;
  }
  public void setCurrentTerm(int ct) {
    if ((ct < terms.size()) && (ct >= 0)) currentTerm = ct;
  }
  public void deleteCurrentTerm() {
    terms.removeElementAt(currentTerm);
    if ((currentTerm == 0) && (terms.isEmpty())) currentTerm = -1;
  }

  private void parseExpression(String s) {
    int i0, i, j, j0, par, op;
    char c;
    GsFunctionTerm t;
    Vector v;
    String s0 = new String(s);
    s = s.replace(" ", "");
    String ss = "";

    terms = new Vector();
    currentTerm = -1;
    op = i0 = i = j = j0 = par = 0;
    while (i < s.length()) {
      c = s.charAt(i);
      while (s0.charAt(j) != c) j++;
      if (c == '(')
        par++;
      else if (c == ')')
        par--;
      else if (((c == '&') || (c == '|')) && (par == 0)) {
        v = getInteractionList(s.substring(i0, i));
        if (v.size() > 0)
          t = new GsFunctionNF(v, (s.charAt(i0) == '!'), op, GsFunctionTerm.getOperator(op) + s0.substring(j0, j));
        else
          t = new GsFunctionUF(GsFunctionTerm.getOperator(op) + s0.substring(j0, j), op);
        //System.err.print(t.stringValue());
        if (c == '&')
          op = GsFunctionTerm.AND;
        else if (c == '|')
          op = GsFunctionTerm.OR;
        else
          op = 0;
        if (t != null) terms.insertElementAt(t, ++currentTerm);
        i0 = i + 1;
        j0 = j + 1;
      }
      i++;
    }
    if (i != i0) {
      v = getInteractionList(s.substring(i0, i));
      if (v.size() > 0)
        t = new GsFunctionNF(v, (s.charAt(i0) == '!'), op, GsFunctionTerm.getOperator(op) + s0.substring(j0, s0.length()));
      else
        t = new GsFunctionUF(GsFunctionTerm.getOperator(op) + s0.substring(j0, s0.length()), op);
      //System.err.println(t.stringValue());
      if (t != null) terms.insertElementAt(t, ++currentTerm);
    }
    if (terms.size() == 0) addTerm(null, false, 0);
  }
  private Vector getInteractionList(String s) {
    Vector le = new Vector();
    GsListInteraction li;

    boolean not = (s.charAt(0) == '!');
    if (not) s = s.substring(1, s.length());
    if ((s.charAt(0) == '(') && (s.charAt(s.length() - 1) == ')'))
      s = s.substring(1, s.length() - 1);
    String[] t = s.split("&");
    for (int i = 0; i < t.length; i++) {
      li = null;
      if ((!t[i].startsWith("!")) && (t[i].indexOf("#") <= 0)) t[i] += "#0";
      for (Enumeration enu = interactions.elements(); enu.hasMoreElements() && (li == null); ) {
        li = (GsListInteraction)enu.nextElement();
        if (!li.stringValue().equalsIgnoreCase(t[i])) li = null;
      }
      if (li != null)
        le.addElement(li);
      else {
        le.clear();
        break;
      }
    }
    return le;
  }

  public String getStringValue() {
    String s = "";
    GsFunctionTerm ft;
    if (terms != null) {
      if (terms.size() == 1)
        s = ((GsFunctionNF) terms.firstElement()).stringValue();
      else {
        for (int i = 0; i < terms.size(); i++) {
          ft = (GsFunctionTerm)terms.elementAt(i);
          s += ft.stringValue();
        }
      }
    }
    return s;
  }
  public Point getPosition(GsFunctionTerm t) {
    if (!terms.contains(t)) return new Point(0, 0);
    return getPosition(terms.indexOf(t));
  }
  public Point getCurrentPosition() {
    return getPosition(currentTerm);
  }

  public Point getPosition(int index) {
    System.err.println(index);
    int deb = 0;
    for (int i = 0; i < index; i++)
      deb += ((GsFunctionTerm)terms.elementAt(i)).stringValue().length();
    deb += (index > 0 ? 3 : 0);
    System.err.println("select : " + deb + " " + (((GsFunctionTerm)terms.elementAt(index)).stringValue().length() - (index > 0 ? 3 : 0)));
    return new Point(deb, ((GsFunctionTerm)terms.elementAt(index)).stringValue().length() - (index > 0 ? 3 : 0));
  }
}
