package org.ginsim.gui.graph.regulatorygraph.logicalfunction.neweditor;

import java.awt.Point;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.FunctionPanel;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;


public class FunctionEditorModel {
	private FunctionPanel functionPanel;
	private String oldExp;
	private Vector interactions, interactionList;
	private Point selectedArea;
	private FunctionEditorControler controler;
	private Vector allowedTerms;

  public FunctionEditorModel(FunctionEditorControler c) {
  	interactions = new Vector();
  	controler = c;
		selectedArea = new Point(-1, -2);
		allowedTerms = new Vector();
		oldExp = "";
  }
  public void init(TreeInteractionsModel m, FunctionPanel p) {
  	functionPanel = p;
  	oldExp = p.getCurrentText();
  	Collection<RegulatoryMultiEdge> ed = m.getGraph().getIncomingEdges(m.getNode());
    interactions.clear();
    for (RegulatoryMultiEdge me: ed) {
      interactions.addElement(me);
    }
    Vector v = new Vector();
    v.addElement(oldExp);
    v.addElement(new Integer(functionPanel.getCaretPosition()));
    selectedArea = (Point)controler.exec(FunctionEditorControler.GET_TERM, v);

		if (selectedArea.x != selectedArea.y)
			interactionList = getInteractionList(oldExp.substring(selectedArea.x, selectedArea.y + 1));
		else
			interactionList = new Vector();
  }
	public void refreshInteractionList() {
		if (selectedArea.x != selectedArea.y)
			interactionList = getInteractionList(functionPanel.getCurrentText().substring(selectedArea.x, selectedArea.y + 1));
		else
			interactionList = new Vector();
	}
  public void setCurrentTerm(Point p) {
  	selectedArea = p;
		if (p.x != -1)
			interactionList = getInteractionList(functionPanel.getCurrentText().substring(selectedArea.x, selectedArea.y + 1));
		String s0 = functionPanel.getCurrentText();
		functionPanel.setText(s0, 0);
  }
  public void reset() {
		if (selectedArea.x != -1) {
			String s = (String) controler.exec(FunctionEditorControler.GET_SELECTED_STRING, selectedArea);
			String s0 = functionPanel.getCurrentText();

			if (selectedArea.x == selectedArea.y) {
				s = s0.substring(0, selectedArea.x) + s0.substring(selectedArea.x + 1);
				functionPanel.setText(s, 0);
				selectedArea.x = -1;
			}
			else if (s.equals("( )")) {
				s = s0.substring(0, selectedArea.x) + s0.substring(selectedArea.x + 3);
				functionPanel.setText(s, 0);
				selectedArea.x = -1;
			}
		}
  }
  public Vector getInteractionList() {
  	return interactionList;
  }
	private Vector getInteractionList(String s) {
		Vector le = new Vector();
		RegulatoryMultiEdge ed = null;
		int k = -1;
		boolean not, found = false;

		if ((s.charAt(0) == '!') && (s.charAt(1) == '(')) s = s.substring(1, s.length());
		if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') s = s.substring(1, s.length() - 1);
		String[] t = s.split("[& |]+", -1);
		for (int i = 0; i < t.length; i++) {
			if (!t[i].equals("")) {
				not = t[i].startsWith("!");
				if (not) t[i] = t[i].substring(1);
				for (Enumeration enu = interactions.elements(); enu.hasMoreElements(); ) {
					ed = (RegulatoryMultiEdge)enu.nextElement();
					found = false;
					k = -1;
					if (t[i].equals(ed.getSource().toString()))
						found = true;
					else
						for (k = 0; k < ed.getEdgeCount(); k++) {
							if (t[i].equals(ed.getEdge(k).getShortInfo())) {
								found = true;
								break;
							}
						}
					if (found) break;
				}
				if (found)
					le.addElement(new ListInteraction(ed, k, not));
				else {
					le = null;
					break;
				}
			}
		}
		return le;
	}

	public Vector getInteractions() {
  	return interactions;
  }
  public String getOldExp() {
  	return oldExp;
  }
  public Point getSelectedArea() {
  	return selectedArea;
  }
  public FunctionPanel getFunctionPanel() {
  	return functionPanel;
  }
  public void insertEmptyTerm(int x) {
  	String s = functionPanel.getCurrentText().substring(0, x) + " " + functionPanel.getCurrentText().substring(x);
  	functionPanel.setText(s, 0);
  }
  public void setParenthesis(boolean b) {
  	String s0 = functionPanel.getCurrentText();
  	String s = "";
  	if (b) {
  		s = s0.substring(0, selectedArea.x) + "(" + s0.substring(selectedArea.x, selectedArea.y + 1) + ")" + s0.substring(selectedArea.y + 1);
  		selectedArea.y += 2;
  		functionPanel.setText(s, 0);
  	}
  	else if ((s0.charAt(selectedArea.x) == '(') && (s0.charAt(selectedArea.y) == ')')) {
  		s = s0.substring(0, selectedArea.x) + s0.substring(selectedArea.x + 1, selectedArea.y) + s0.substring(selectedArea.y + 1);
  		selectedArea.y -= 2;
  		functionPanel.setText(s, 0);
  	}
  }
  public void setNot(boolean b) {
  	String s = functionPanel.getCurrentText();
  	String s0 = s.substring(0, selectedArea.x);
  	String t = s.substring(selectedArea.x, selectedArea.y + 1);
  	String s1 = s.substring(selectedArea.y + 1);
  	functionPanel.setText(s0 + (t.startsWith("!") ? t.substring(1) : "!" + t) + s1, 0);
  	selectedArea.y += (t.startsWith("!") ? -1 : 1);
  }
	public void clearTerm(String op, boolean par, boolean not) {
		String s = functionPanel.getCurrentText();
		String s0 = s.substring(0, selectedArea.x);
		String s1 = s.substring(selectedArea.y + 1);
		functionPanel.setText(s0 + (not ? "!" : "") + (par ? "(" : "") + " " + (par ? ")" : "") + s1, 0);
		selectedArea.y = selectedArea.x + (not ? 1 : 0) + (par ? 2 : 0);
		if (selectedArea.x != selectedArea.y)
			interactionList = getInteractionList(oldExp.substring(selectedArea.x, selectedArea.y + 1));
		else
			interactionList = new Vector();
	}
	public void removeFromTerm(String i, String op, boolean par, boolean not) {
  	String s = functionPanel.getCurrentText();
  	String s0 = s.substring(0, selectedArea.x);
  	String t = s.substring(selectedArea.x, selectedArea.y + 1);
		if (not) t = t.substring(1);
  	if (par) t = t.substring(1, t.length() - 1);
  	String s1 = s.substring(selectedArea.y + 1);
  	int x = 0, x0, d = 0;
  	do {
  		x0 = x = t.indexOf(i, x);
  		if (x0 != -1) {
  			x += i.length();
  			if (x < t.length()) {
  				while (t.charAt(x) == ' ') x++;
  				if (t.charAt(x) == op.charAt(0)) {
  					x++;
  					while (t.charAt(x) == ' ') x++;
  				}
					else
						x0 = -1;
  			}
  			else if (x0 > 0) {
  				x0--;
  				while (t.charAt(x0) == ' ') {
  					x0--;
  					if (x0 == 0) break;
  				}
  				if (x0 > 0) {
  					if (t.charAt(x0) == op.charAt(0)) {
    					x0--;
    					while (t.charAt(x0) == ' ') {
    						x0--;
    						if (x0 == 0) break;
    					}
    					if (x0 > 0) x0++;
    				}
  				}
  			}
				if (x0 != -1) {
					t = t.substring(0, x0) + t.substring(x);
					d = x - x0;
					x = -1;
				}
  		}
  	} while (x != -1);
  	selectedArea.y -= d;
  	if (t.equals("")) {
  		t = " ";
  		selectedArea.y++;
  	}
  	functionPanel.setText(s0 + (not ? "!" : "") + (par ? "(" : "") + t + (par ? ")" : "") + s1, 0);
  }
  public void addToTerm(String i, String op, boolean par, boolean not) {
  	String s = functionPanel.getCurrentText();
  	String s0 = s.substring(0, selectedArea.x);
  	String t = s.substring(selectedArea.x, selectedArea.y + 1);
		if (not) t = t.substring(1);
  	if (par) t = t.substring(1, t.length() - 1);
  	String s1 = s.substring(selectedArea.y + 1);
  	if (t.equals(" ")) {
  		t = i;
  		selectedArea.y--;
  	}
  	else {
  		t = t + " " + op + " " + i;
  		selectedArea.y += op.length() + 2;
  	}
  	selectedArea.y += i.length();
  	functionPanel.setText(s0 + (not ? "!" : "") + (par ? "(" : "") + t + (par ? ")" : "") + s1, 0);
  }
  public void toggleNot(String i, String op, boolean par, boolean not) {
  	String s = functionPanel.getCurrentText();
  	String s0 = s.substring(0, selectedArea.x);
  	String t = s.substring(selectedArea.x, selectedArea.y + 1);
		if (not) t = t.substring(1);
  	if (par) t = t.substring(1, t.length() - 1);
  	String s1 = s.substring(selectedArea.y + 1);
  	int x = 0, x0;
  	char c;

  	do {
  		x = x0 = t.indexOf(i, x);
  		if (x != -1) {
  			c = ' ';
  			if (x > 0) c = t.charAt(x - 1);
  			if ((c == ' ') || (c == op.charAt(0)) || (c == '!')) {
  				x0 = x;
  				if (c == '!') x0--;
  				x += i.length();
  				c = ' ';
  				if (x < t.length()) c = t.charAt(x);
  				if ((c == ' ') || (c == op.charAt(0))) x = -1;
  			}
  		}
  	} while (x != -1);
  	if (x0 != -1) {
  		if (t.charAt(x0) == '!') {
  			t = t.substring(0, x0) + t.substring(x0 + 1);
  			selectedArea.y--;
  		}
  		else {
  			t = t.substring(0, x0) + "!" + t.substring(x0);
  			selectedArea.y++;
  		}
  	}
   	functionPanel.setText(s0 + (not ? "!" : "") + (par ? "(" : "") + t + (par ? ")" : "") + s1, 0);
  }
  public void setAndMode(boolean b) {
  	String s = functionPanel.getCurrentText();
  	String s0 = s.substring(0, selectedArea.x);
  	String t = s.substring(selectedArea.x, selectedArea.y + 1);
  	String s1 = s.substring(selectedArea.y + 1);

  	if (b)
  		t = t.replace('|', '&');
  	else
  		t = t.replace('&', '|');
  	functionPanel.setText(s0 + t + s1, 0);
  }
	public void addOpTerm(boolean and, Vector interactions, String operator, boolean par, boolean not) {
		String s = functionPanel.getCurrentText();
		String s0 = s.substring(0, selectedArea.x);
		String t = s.substring(selectedArea.x, selectedArea.y + 1);
		String s1 = s.substring(selectedArea.y + 1);
		ListInteraction interaction;
		String n;
		int index;

		functionPanel.setText(s0 + t + " " + (and ? "&" : "|") + " " + (par ? "( )" : " ") + s1, 0);
		selectedArea.x = selectedArea.y + 4;
		selectedArea.y = selectedArea.x + (par ? 2 : 0);

		for (int i = 0; i < interactions.size(); i++) {
			interaction = (ListInteraction)interactions.elementAt(i);
			index = interaction.getIndex();
			n = "";
			if (index >= 0)
				n = interaction.getEdge().getEdge(index).getShortInfo();
			else
				n = interaction.getGene();
			addToTerm((interaction.getNot() ? "!" : "") + n, operator, par, false);
		}
		if (not) setNot(true);
	}
	public int deleteTerm() {
		String s = functionPanel.getCurrentText();
		String s0 = s.substring(0, selectedArea.x);
		String t = s.substring(selectedArea.x, selectedArea.y + 1);
		String s1 = s.substring(selectedArea.y + 1);
		int i = 0;
		boolean ok = false;

		while ((i < s1.length()) && (s1.charAt(i) == ' ')) i++;
		if ((i < s1.length()) && ((s1.charAt(i) == '|') || (s1.charAt(i) == '&'))) {
			i++;
			while ((i < s1.length()) && (s1.charAt(i) == ' ')) i++;
			s1 = s1.substring(i);
			i = s0.length() + 1;
			ok = true;
		}
		else {
			i = s0.length() - 1;
			while ((i >= 0) && (s0.charAt(i) == ' ')) i--;
			if ((i >= 0) && ((s0.charAt(i) == '|') || (s0.charAt(i) == '&'))) {
				i--;
				while ((i >= 0) && (s0.charAt(i) == ' ')) i--;
				s0 = s0.substring(0, i + 1);
				i = -(s0.length() - 1);
				ok = true;
			}
			else
				i = 0;
		}
		if ((s0.length() + s1.length()) > 0){
			if (ok)
				functionPanel.setText(s0 + s1, 0);
			else {
				functionPanel.setText(s0 + s1, 0);
				i = s0.length();
			}
		}
		else {
			functionPanel.setText(" ", 0);
			i = 0;
		}
		return i;
	}
	public Point getClosestTermLeft(int p) {
		String s = functionPanel.getCurrentText();
		String s0 = s.substring(0, p);
		String s1 = s.substring(p);
		s = s0 + " " + "@@@" + " " + s1;
		String sr = s.replaceAll("[()&\\|]", " ").replaceAll(" +", " ");
		String[] w = sr.split(" ", -1);
		Hashtable h = new Hashtable();
		for (int i = 0; i < w.length; i++) {
			if (!w[i].equals("")) {
				h.put(w[i], new Integer(s.indexOf(w[i])));
			}
		}
		int i = 0;
		while (!w[i].equals("@@@")) i++;
		int j = i - 1;
		while (j >= 0) {

		}
		Point pt = new Point(j, -1);
		return pt;
	}
	public Point getClosestTermRight(int p) {
		return null;
	}
}
