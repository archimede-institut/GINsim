package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.qmc;

import java.util.Vector;
import java.util.Enumeration;

public class ParameterGroup {
	private Vector parameters;

	public ParameterGroup(int c, Vector p) {
		super();
		LogicalParameter lp;
		parameters = new Vector();
		for (Enumeration enu = p.elements(); enu.hasMoreElements(); ) {
			lp = (LogicalParameter)enu.nextElement();
			if (lp.getCardinality() == c) parameters.addElement(lp);
		}
	}
	private ParameterGroup(Vector v) {
		super();
		parameters = v;
	}
	public ParameterGroup compareWith(ParameterGroup group) {
		LogicalParameter p1, p2, p12;
		boolean premier;
		ParameterGroup pg = null;
		Vector v = new Vector();

		for (Enumeration enu = parameters.elements(); enu.hasMoreElements(); ) {
			p1 = (LogicalParameter)enu.nextElement();
			premier = true;
			for (Enumeration enu2 = group.getParameters().elements(); enu2.hasMoreElements(); ) {
				p2 = (LogicalParameter)enu2.nextElement();
				p12 = p1.compareTo(p2);
				if (p12 != null) {
					premier = false;
					if (!v.contains(p12)) v.addElement(p12);
				}
			}
			if (premier) p1.setPremier();
		}
		pg = new ParameterGroup(v);
		return pg;
	}
	public void setPremier() {
		LogicalParameter p;
		for (Enumeration enu = parameters.elements(); enu.hasMoreElements(); ) {
			p = (LogicalParameter)enu.nextElement();
			p.setPremier();
		}
	}
	public boolean isEmpty() {
		return parameters.isEmpty();
	}
	public Vector getParameters() {
		return parameters;
	}
	public Vector getPremierTerms() {
		Vector v = new Vector();
		LogicalParameter p;
		for (Enumeration enu = parameters.elements(); enu.hasMoreElements(); ) {
			p = (LogicalParameter)enu.nextElement();
			if (p.isPremier()) v.addElement(p);
		}
		return v;
	}
	public String getString() {
		LogicalParameter lp;
		String s = "";
		for (Enumeration enu = parameters.elements(); enu.hasMoreElements(); ) {
			lp = (LogicalParameter) enu.nextElement();
			s += lp.getString() + "\n";
		}
		return s;
	}
}
