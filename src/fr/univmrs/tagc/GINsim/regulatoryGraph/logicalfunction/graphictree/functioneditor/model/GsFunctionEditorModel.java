package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.model;

import java.awt.Point;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.jgraph.GsJgraphDirectedEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor.GsListInteraction;

public class GsFunctionEditorModel {
	private GsTreeInteractionsModel model;
	private Vector interactions;
	private Vector terms;
	private int currentTerm;
	private String oldExp;
	private GsTreeExpression exp;

	public GsFunctionEditorModel(GsTreeInteractionsModel model, GsTreeExpression exp) {
		this.model = model;
		this.exp = exp;
		oldExp = exp.toString();
		GsRegulatoryMultiEdge o;
		interactions = new Vector();
		List ed = model.getGraph().getGraphManager().getIncomingEdges(model.getVertex());
		for (int i = 0; i < ed.size(); i++) {
			o = (GsRegulatoryMultiEdge)((GsJgraphDirectedEdge)ed.get(i)).getUserObject();
			interactions.addElement(new GsListInteraction(o, -1));
			if (o.getEdgeCount() > 1) {
				interactions.addElement(new GsListInteraction(o, 0));
			}
			for (int j = 1; j <= o.getEdgeCount(); j++) {
				interactions.addElement(new GsListInteraction(o, j));
			}
		}
		parseExpression(exp.toString());
		exp.setEditorModel(this);
		exp.setSelection(getCurrentPosition(), getCurrentTerm().isNormal());
	}
	public GsTreeInteractionsModel getTreeInteractionsModel() {
		return model;
	}
	public void update() {
		parseExpression(exp.toString());
	}
	public void clearSelection() {
		exp.setSelection(null, true);
	}
	public void not() {
		getCurrentTerm().toggleNot();
	}
	public String getOldExp() {
		return oldExp;
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
		if (ct < terms.size() && ct >= 0) {
			currentTerm = ct;
		}
	}
	public void deleteCurrentTerm() {
		terms.removeElementAt(currentTerm);
		if (currentTerm == terms.size()) {
			currentTerm--;
		}
		if (currentTerm == 0) {
			getCurrentTerm().operator = 0;
			getCurrentTerm().update();
		}
		if (terms.isEmpty()) {
			addTerm(null, false, 0);
		}
	}

	private void parseExpression(String s) {
		int i0, i, j, j0, par, op;
		char c;
		GsFunctionTerm t;
		Vector v;
		String s0 = new String(s);
		s = s.replaceAll(" ", "");
		String ss = "";

		terms = new Vector();
		currentTerm = -1;
		op = i0 = i = j = j0 = par = 0;
		while (i < s.length()) {
			c = s.charAt(i);
			while (s0.charAt(j) != c) {
				j++;
			}
			if (c == '(') {
				par++;
			} else if (c == ')') {
				par--;
			} else if ((c == '&' || c == '|') && par == 0) {
				v = getInteractionList(s.substring(i0, i));
				ss = "";
				if (op > 0) {
					ss = " ";
				}
				ss += GsFunctionTerm.getOperator(op) + s0.substring(j0, j);
				if (ss.endsWith(" ")) {
					ss = ss.substring(0, ss.length() - 1);
				}
				if (v.size() > 0) {
					t = new GsFunctionNF(v, (s.charAt(i0) == '!' && s.charAt(i0 + 1) == '('), op, ss);
				} else {
					t = new GsFunctionUF(ss, op);
				}
				if (c == '&') {
					op = GsFunctionTerm.AND;
				} else if (c == '|') {
					op = GsFunctionTerm.OR;
				} else {
					op = 0;
				}
				terms.insertElementAt(t, ++currentTerm);
				i0 = i + 1;
				j0 = j + 1;
			}
			i++;
		}
		if (i != i0) {
			v = getInteractionList(s.substring(i0, i));
			ss = "";
			if (op > 0) {
				ss = " ";
			}
			ss += GsFunctionTerm.getOperator(op) + s0.substring(j0, s0.length());
			if (v.size() > 0) {
				t = new GsFunctionNF(v, (s.charAt(i0) == '!' && s.charAt(i0 + 1) == '('), op, ss);
			} else {
				t = new GsFunctionUF(ss, op);
			}
			terms.insertElementAt(t, ++currentTerm);
		}
		if (terms.size() == 0) {
			addTerm(null, false, 0);
		}
	}
	public int getNbTerms() {
		return terms.size();
	}
	private Vector getInteractionList(String s) {
		Vector le = new Vector();
		GsListInteraction li;
		int ix;

		boolean not = s.charAt(0) == '!' && s.charAt(1) == '(';
		if (not) {
			s = s.substring(1, s.length());
		}
		if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
			s = s.substring(1, s.length() - 1);
		}
		String[] t = s.split("&");
		for (int i = 0; i < t.length; i++) {
			li = null;
			for (Enumeration enu = interactions.elements(); enu.hasMoreElements() && li == null; ) {
				li = (GsListInteraction)enu.nextElement();
				if (t[i].equals(li.stringValue())) {
					;
				} else {
					ix = t[i].indexOf('#');
					if (ix > -1 && t[i].substring(ix + 1, ix + 2).equals("1") && t[i].substring(0, ix).equals(li.stringValue())) {
						;
					} else {
						li = null;
					}
				}
			}
			if (li != null) {
				le.addElement(li);
			} else {
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
			if (terms.size() == 1) {
				s = ((GsFunctionTerm) terms.firstElement()).stringValue();
			} else {
				for (int i = 0; i < terms.size(); i++) {
					ft = (GsFunctionTerm)terms.elementAt(i);
					s += ft.stringValue();
				}
			}
		}
		return s;
	}
	public Point getPosition(GsFunctionTerm t) {
		if (!terms.contains(t)) {
			return new Point(0, 0);
		}
		return getPosition(terms.indexOf(t));
	}
	public Point getCurrentPosition() {
		if (currentTerm != -1) {
			return getPosition(currentTerm);
		}
		return new Point(0, 0);
	}

	private Point getPosition(int index) {
		String s = getStringValue();
		int i, j, j0, par, n = 0;
		char c;
		String s0 = new String(s);
		s = s.replaceAll(" ", "");

		i = j = j0 = par = 0;
		while (i < s.length()) {
			c = s.charAt(i);
			while (s0.charAt(j) != c) {
				j++;
			}
			if (c == '(') {
				par++;
			} else if (c == ')') {
				par--;
			} else if ((c == '&' || c == '|') && par == 0) {
				if (n == index) {
					n++;
					break;
				}
				n++;
				j0 = j + 1;
			}
			i++;
		}
		if (j0 < s0.length()) {
			while (s0.charAt(j0) == ' ') {
				j0++;
				if (j0 >= s0.length()) {
					break;
				}
			}
		}
		if (j - 1 < s0.length() && j - 1 >= 0) {
			while (s0.charAt(j - 1) == ' ') {
				j--;
			}
		}
		if (n == index) {
			j++;
		}
		return new Point(j0, j - j0);
	}
}
