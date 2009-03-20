package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor;

import java.awt.Point;
import java.util.*;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import fr.univmrs.tagc.GINsim.regulatoryGraph.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.GsFunctionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.GsFunctionsCreator;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.qmc.QMCThread;
import javax.swing.JProgressBar;
import fr.univmrs.tagc.common.widgets.GsButton;

public class GsFunctionEditorControler {
	public static final int ADD_EMPTY_TERM = 1;
	public static final int GET_TERM = 2;
	public static final int GET_SELECTED_STRING = 3;
	public static final int MODIF_TERM_ADD = 4;
	public static final int SET_PARENTHESIS = 5;
	public static final int MODIF_TERM_REM = 6;
	public static final int MODIF_TERM_NOT = 7;
	public static final int SET_NOT = 8;
	public static final int SET_AND_MODE = 9;
	public static final int AND = 10;
	public static final int OR = 11;
	public static final int CLEAR = 12;
	public static final int PREVIOUS = 13;
	public static final int NEXT = 14;
	public static final int DELETE = 15;
	public static final int VALIDATE_EDIT = 16;
	public static final int CANCEL_EDIT = 17;
	public static final int COMPACT = 18;
	public static final int USER = 19;
	public static final int DNF = 20;
	public static final int VALIDATE_DISPLAY = 21;
	public static final int CANCEL_DISPLAY = 22;
	public static final int QUINE_DNF = 23;
	public static final int QUINE_CNF = 24;

	private GsFunctionPanel functionPanel;
	private GsFunctionEditorModel editorModel;
	private GsFunctionEditorEditPanel editPanel;
	private GsFunctionEditorDisplayPanel displayPanel;
	private GsRegulatoryVertex vertex;
	private GsRegulatoryGraph graph;
	private JTree tree;

	public GsFunctionEditorControler(GsFunctionEditorEditPanel p1, GsFunctionEditorDisplayPanel p2) {
		editPanel = p1;
		editPanel.setControler(this);
		displayPanel = p2;
		displayPanel.setControler(this);
	}
	public void init(GsFunctionPanel p, GsFunctionEditorModel m) {
  	functionPanel = p;
    editorModel = m;
	}
	public void init(GsRegulatoryVertex v, GsRegulatoryGraph g, JTree t, GsFunctionEditorModel m) {
		vertex = v;
		graph = g;
		tree = t;
		editorModel = m;
	}
	public void reset() {
		if (functionPanel != null) {
			editorModel.reset();
			functionPanel.getTreeExpression().setSelection(null, true);
			functionPanel.setTreeEditable();
		}
	}
	public Object exec(int action, Object p) {
		Object ret = null;
		boolean b, par, not;
		GsListInteraction interaction;
		Vector v, params;
		String op, n, s;
		int index, pos, value;
		Point pt;
		GsTreeExpression expression;
		GsFunctionsCreator c;
		GsLogicalParameter lp;
		TreePath sel_path;
		QMCThread t;

    switch(action) {
    	case ADD_EMPTY_TERM :
    		editorModel.insertEmptyTerm(((Point)p).x);
    		break;
    	case GET_TERM :
    		if (p == null)
    			ret = getTerm(functionPanel.getCurrentText(), functionPanel.getCaretPosition());
    		else
    			ret = getTerm((String)((Vector)p).firstElement(), ((Integer)((Vector)p).lastElement()).intValue());
    		break;
    	case GET_SELECTED_STRING :
    		ret = getSelectedString((Point)p);
    		break;
    	case MODIF_TERM_ADD :
    	case MODIF_TERM_REM :
    		interaction = (GsListInteraction)((Vector)p).firstElement();
    		par = ((Boolean)((Vector)p).lastElement()).booleanValue();
    		op = (String)((Vector)p).elementAt(1);
    		not = ((Boolean)((Vector)p).elementAt(2)).booleanValue();
    		index = interaction.getIndex();
    		n = "";
    		if (index >= 0)
    			n = interaction.getEdge().getEdge(index).getShortInfo();
    		else
    			n = interaction.getGene();
    		if (action == MODIF_TERM_ADD)
    			editorModel.addToTerm((interaction.getNot() ? "!" : "") + n, op, par, not);
    		else
    			editorModel.removeFromTerm((interaction.getNot() ? "!" : "") + n, op, par, not);
    		break;
    	case MODIF_TERM_NOT :
    		interaction = (GsListInteraction)((Vector)p).firstElement();
    		par = ((Boolean)((Vector)p).lastElement()).booleanValue();
    		op = (String)((Vector)p).elementAt(1);
				not = ((Boolean)((Vector)p).elementAt(2)).booleanValue();
    		index = interaction.getIndex();
    		n = "";
    		if (index >= 0)
    			n = interaction.getEdge().getEdge(index).getShortInfo();
    		else
    			n = interaction.getGene();
    		editorModel.toggleNot(n, op, par, not);
    		break;
    	case SET_PARENTHESIS :
    		b = ((Boolean)p).booleanValue();
    		editorModel.setParenthesis(b);
    		break;
    	case SET_NOT :
    		b = ((Boolean)p).booleanValue();
    		editorModel.setNot(b);
    		break;
    	case SET_AND_MODE :
    		b = ((Boolean)p).booleanValue();
    		editorModel.setAndMode(b);
    		break;
			case AND :
			case OR :
				s = getSelectedString(editorModel.getSelectedArea());
				if (!s.equals("( )") && !s.equals("") && !s.equals("!( )")) {
					v = (Vector)((Vector)p).firstElement();
					op = (String)((Vector)p).elementAt(1);
					par = ((Boolean)((Vector)p).lastElement()).booleanValue();
					not = ((Boolean)((Vector)p).elementAt(2)).booleanValue();
					editorModel.addOpTerm((action == AND), v, op, par, not);
					editorModel.refreshInteractionList();
					editPanel.updateSelectionTable();
				}
				break;
			case CLEAR :
				v = (Vector)p;
				Boolean andB = (Boolean)v.elementAt(1);
				Boolean notB = (Boolean)v.elementAt(2);
				Boolean parB = (Boolean)v.lastElement();
				editorModel.clearTerm(andB.booleanValue() ? "&" : "|", parB.booleanValue(), notB.booleanValue());
				editorModel.refreshInteractionList();
				editPanel.updateSelectionTable();
				editPanel.updateControls();
				break;
			case PREVIOUS :
				String s0 = getSelectedString(editorModel.getSelectedArea());
				if (s0.equals("( )") || s0.equals("") || s0.equals("!( )")) {
					deleteCurrentTerm();
				}
				s = editorModel.getFunctionPanel().getCurrentText();
				if ((editorModel.getSelectedArea().y != s.length() - 1) || (!s0.equals("( )") && !s0.equals("") && !s0.equals("!( )"))) {
					pos = editorModel.getSelectedArea().x - 1;
					pt = new Point(-1, -2);
					while (pos > 0) {
						pt = getTerm(s, pos);
						if ((pt.x > -1) && (pt.x != pt.y)) break;
						pos--;
					}
					if ((pt.x > -1) && (pt.x != pt.y)) {
						editorModel.setCurrentTerm(pt);
						editorModel.refreshInteractionList();
						editorModel.getFunctionPanel().getTreeExpression().setSelection(pt, (editorModel.getInteractionList() != null));

						editPanel.updateSelectionTable();
						editPanel.updateControls();
					}
				}
				break;
			case NEXT :
				s = getSelectedString(editorModel.getSelectedArea());
				if (s.equals("( )") || s.equals("") || s.equals("!( )")) {
					deleteCurrentTerm();
				}
				else {
					pos = editorModel.getSelectedArea().y + 2;
					s = editorModel.getFunctionPanel().getCurrentText();
					pt = new Point( -1, -2);
					while (pos < s.length()) {
						pt = getTerm(s, pos);
						if ((pt.x > -1) && (pt.x != pt.y)) break;
						pos++;
					}
					if ((pt.x > -1) && (pt.x != pt.y)) {
						editorModel.setCurrentTerm(pt);
						editorModel.refreshInteractionList();
						editorModel.getFunctionPanel().getTreeExpression().setSelection(pt, (editorModel.getInteractionList() != null));

						editPanel.updateSelectionTable();
						editPanel.updateControls();
					}
				}
				break;
			case DELETE :
				s = getSelectedString(editorModel.getSelectedArea());
				if (!s.equals("( )") && !s.equals("") && !s.equals("!( )"))
					deleteCurrentTerm();
				break;
			case VALIDATE_EDIT :
				editorModel.setCurrentTerm(new Point(-1, -1));
				functionPanel.validateText(editorModel.getFunctionPanel().getCurrentText());
				break;
			case CANCEL_EDIT :
				editorModel.setCurrentTerm(new Point(-1, -1));
				functionPanel.setText(editorModel.getOldExp(), 0);
				functionPanel.validateText(editorModel.getOldExp());
				break;
			case COMPACT :
				expression = (GsTreeExpression)p;
				params = expression.getChilds();
				v = new Vector();
				value = ((GsTreeValue)expression.getParent()).getValue();
				for (int i = 0; i < params.size(); i++) {
					lp = new GsLogicalParameter(value);
					lp.setEdges(((GsTreeParam) params.elementAt(i)).getEdgeIndexes());
					v.addElement(lp);
				}
				c = new GsFunctionsCreator(graph, v, vertex);
				Hashtable h = c.doIt(false);
				s = "";
				if (h.size() > 0) {
					v = (Vector)h.elements().nextElement();
					Enumeration enu = v.elements();
					s = (String)enu.nextElement();
					while (enu.hasMoreElements())
						s = s + " | (" + (String)enu.nextElement() + ")";
				}
				sel_path = tree.getLeadSelectionPath();
				expression.getGraphicPanel().setText(s, 0);
				expression.setSelected(true);
				tree.setSelectionPath(sel_path);
				break;
			case USER :
				expression = (GsTreeExpression)((Vector)p).firstElement();
				s = (String)((Vector)p).lastElement();
				sel_path = tree.getLeadSelectionPath();
				expression.getGraphicPanel().setText(s, 0);
				expression.setSelected(true);
				tree.setSelectionPath(sel_path);
				break;
			case DNF :
				expression = (GsTreeExpression)p;
				params = expression.getChilds();
				v = new Vector();
				value = ((GsTreeValue)expression.getParent()).getValue();
				for (int i = 0; i < params.size(); i++) {
					lp = new GsLogicalParameter(value);
					lp.setEdges(((GsTreeParam) params.elementAt(i)).getEdgeIndexes());
					v.addElement(lp);
				}
				c = new GsFunctionsCreator(graph, v, vertex);
				s = c.makeDNFExpression(value);
				sel_path = tree.getLeadSelectionPath();
				expression.getGraphicPanel().setText(s, 0);
				expression.setSelected(true);
				tree.setSelectionPath(sel_path);
				break;
			case CANCEL_DISPLAY :
				expression = (GsTreeExpression)p;
				sel_path = tree.getLeadSelectionPath();
				expression.getGraphicPanel().setText(editorModel.getOldExp(), 0);
				expression.setSelected(true);
				tree.setSelectionPath(sel_path);
				expression.getGraphicPanel().validateText(editorModel.getOldExp());
				break;
			case VALIDATE_DISPLAY :
				expression = (GsTreeExpression)p;
				expression.getGraphicPanel().validateText(expression.toString());
				break;
			case QUINE_DNF :
				v = (Vector)p;
				expression = (GsTreeExpression)v.firstElement();
				t = new QMCThread(false, graph, vertex, expression, tree, (JProgressBar)v.lastElement(), (GsButton)v.elementAt(1));
				t.start();
				break;
			case QUINE_CNF :
				v = (Vector)p;
				expression = (GsTreeExpression)v.firstElement();
				t = new QMCThread(true, graph, vertex, expression, tree, (JProgressBar)v.lastElement(), (GsButton)v.elementAt(1));
				t.start();
				break;
    }
    return ret;
	}
	private void deleteCurrentTerm() {
		Point pt;
		int pos = editorModel.deleteTerm();
		String s = editorModel.getFunctionPanel().getCurrentText();
		if (pos != 0) {
			if (pos > 0) {
				while(pos < s.length()) {
					pt = getTerm(s, pos);
					if ((pt.x != pt.y) && (pt.x != -1)) break;
					pos++;
				}
			}
			else {
				pos = -pos;
				while(pos >= 0) {
					pt = getTerm(s, pos);
					if ((pt.x != pt.y) && (pt.x != -1)) break;
					pos--;
				}
			}
			pt = getTerm(s, pos);
			editorModel.setCurrentTerm(pt);
			editorModel.refreshInteractionList();
			editorModel.getFunctionPanel().getTreeExpression().setSelection(pt, (editorModel.getInteractionList() != null));
			editPanel.updateSelectionTable();
			editPanel.updateControls();
		}
		else {
			editorModel.insertEmptyTerm(0);
			editorModel.setCurrentTerm(new Point(0, 0));
			editPanel.updateSelectionTable();
			editPanel.updateControls();
		}
	}
	private Point getTerm(String s, int p) {
		Point pt = null;
		char c, op = 'X';
		int i = p, j = p, k, p0 = p;
		boolean searchForTerm = false;

		if (p > 0) {
			c = s.charAt(p - 1);
			searchForTerm = (c != '(') && (c != ')') && (c != ' ') && (c != '&') && (c != '|');
			if (p == s.length()) {
				i--;
				j--;
			}
		}
		if (searchForTerm) {
			for (i = p - 1; i >= 0; i--) {
				c = s.charAt(i);
				if ((c == '(') || (c == ')'))
					break;
				else if ((c == '&') || (c == '|')) {
					if (op == 'X')
						op = c;
					else if (op != c)
						break;
				}
			}
			if (i == -1) i++;
			if (op != 'X') {
				k = i;
				while (s.charAt(k) != op) {
					if ((s.charAt(k) != '(') && (s.charAt(k) != ')') && (s.charAt(k) != ' ') && (s.charAt(k) != '&')
							&& (s.charAt(k) != '|') && (s.charAt(k) != '!')) break;
					k++;
				}
				if (s.charAt(k) == op) {
					op = 'X';
					i = k + 1;
				}
			}
			for (j = p; j < s.length(); j++) {
				c = s.charAt(j);
				if ((c == '(') || (c == ')'))
					break;
				else if ((c == '&') || (c == '|')) {
					if (op == 'X')
						op = c;
					else if (op != c)
						break;
				}
			}
			if (j == s.length()) j--;
			if (i == -1) i++;
			if ((s.charAt(i) != '(') || (s.charAt(j) != ')')) {
				for (p = i; p <= j; p++) {
					c = s.charAt(p);
					if ((c != ' ') && (c != '(') && (c != ')') && (c != '&') && (c != '|')) break;
				}
				i = p;
				for (p = j; p >= i; p--) {
					c = s.charAt(p);
					if ((c != ' ') && (c != '(') && (c != ')') && (c != '&') && (c != '|') && (c != '!')) break;
				}
				j = p;
			}
			if (i > 0) {
				p = i - 1;
				while (p >= 0) {
					if (s.charAt(p) != ' ') break;
					p--;
				}
				if ((p >= 0) && (s.charAt(p) == '!')) i = p;
			}
		}
		if ((p0 >= i) && (p0 <= j))
			pt = new Point(i, j);
		else
			pt = new Point(p0, p0);
		return pt;
	}
	public String getSelectedString(Point p) {
		if (p.x == p.y)
			return "";
		else if (p.y == (functionPanel.getCurrentText().length()))
			return functionPanel.getCurrentText().substring(p.x);
		return functionPanel.getCurrentText().substring(p.x, p.y + 1);
	}
}
