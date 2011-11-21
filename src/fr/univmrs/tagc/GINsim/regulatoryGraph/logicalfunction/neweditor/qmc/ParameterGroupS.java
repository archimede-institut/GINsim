package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.qmc;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;

public class ParameterGroupS {
	private Vector groups;
	private Hashtable interactions;
	private Vector premierTerms;
	private int nbParameters;
	private QMCAlgo algo;

	public ParameterGroupS(Collection<RegulatoryMultiEdge> inters, List parameters, boolean cnf, QMCAlgo a) {
		Vector vi = new Vector(), vp = new Vector();
		algo = a;
		for (RegulatoryMultiEdge me: inters) {
			for (int k = 0; k < me.getEdgeCount(); k++)
				vi.addElement(me.getEdge(k).getShortInfo());
		}

	  for (int i = 0; i < parameters.size(); i++)
			vp.addElement(((GsTreeParam)parameters.get(i)).toString());
		interactions = new Hashtable();
		for (int i = 0; i < vi.size(); i++) interactions.put(vi.elementAt(i), new Integer(i));
		Vector params = new Vector();
		for (int i = 0; i < vp.size(); i++)
			params.addElement(new LogicalParameter(i, vp.size(), interactions, (String)vp.elementAt(i)));
		if (cnf) {
			Vector v = new Vector();
			LogicalParameter lp;
			String s;
			int j = 0;
			for (int i = 0; i < Math.pow(2.0, vi.size()); i++) {
				s = String.valueOf(i + 1);
				for (int k = 0; k < vi.size(); k++)
					if ((i & (int)Math.pow(2.0, k)) > 0) s += " " + vi.elementAt(k);
				if (s.equals(String.valueOf(i + 1))) s = String.valueOf(i + 1) + " basal value";
				lp = new LogicalParameter(i, (int)Math.pow(2.0, vi.size()) - vp.size(), interactions, s);
				if (!params.contains(lp)) {
					lp.invert();
					lp.setId(j++);
					v.addElement(lp);
				}
			}
			params = v;
		}
		premierTerms = new Vector();
		ParameterGroup pg;
		groups = new Vector();
		for (int i = 0; i <= interactions.size(); i++) {
			pg = new ParameterGroup(i, params);
			if (!pg.isEmpty()) groups.addElement(pg);
		}
		nbParameters = params.size();
	}
	public void nextGroupS() {
		Vector g = new Vector();
		ParameterGroup pg;
		for (int i = 0; i < (groups.size() - 1); i++) {
			pg = ((ParameterGroup)groups.elementAt(i)).compareWith((ParameterGroup)groups.elementAt(i + 1));
			g.addElement(pg);
			premierTerms.addAll(((ParameterGroup)groups.elementAt(i)).getPremierTerms());
			if (algo.shouldKill()) break;
		}
		if (!algo.shouldKill()) {
			((ParameterGroup)groups.lastElement()).compareWith((ParameterGroup)groups.elementAt(groups.size() - 2));
			premierTerms.addAll(((ParameterGroup)groups.lastElement()).getPremierTerms());
			groups = g;
		}
	}
	public int size() {
		return groups.size();
	}
	public Vector getPremierTerms() {
		return premierTerms;
	}
	public int getNbParameters() {
		return nbParameters;
	}
	public void setPremier() {
		ParameterGroup pg;
		for (Enumeration enu = groups.elements(); enu.hasMoreElements(); ) {
			pg = (ParameterGroup)enu.nextElement();
			pg.setPremier();
			premierTerms.addAll(pg.getPremierTerms());
		}
	}
	public String getString() {
		StringBuffer s = new StringBuffer("");
		ParameterGroup pg;
		for (Enumeration enu = groups.elements(); enu.hasMoreElements(); ) {
			pg = (ParameterGroup)enu.nextElement();
			s.append(pg.getString());
			s.append("-------------------------------------------------------\n");
		}
		s.append("-------------------------------------------------------\n");
		return s.toString();
	}
}
