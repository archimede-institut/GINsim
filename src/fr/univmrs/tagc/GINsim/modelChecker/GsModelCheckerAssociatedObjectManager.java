package fr.univmrs.tagc.GINsim.modelChecker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.datastore.GenericList;
import fr.univmrs.tagc.common.datastore.ValueList;
import fr.univmrs.tagc.common.xml.XMLWriter;

public class GsModelCheckerAssociatedObjectManager implements GsGraphAssociatedObjectManager {

	public static String key = "mchecker";
	
	public Object doOpen(InputStream is, GsGraph graph) {
        GsModelCheckerParser parser = new GsModelCheckerParser((GsRegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
	}

	public void doSave(OutputStreamWriter os, GsGraph graph) {
		GenericList l_test = (GenericList)graph.getObject(key, false);
		if (l_test == null) {
			return;
		}
		GsRegulatoryMutants mutants = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
		try {
			XMLWriter out = new XMLWriter(os, null);
			out.openTag("modelCheckerConfig");
			out.openTag("testList");
			for (int i=0 ; i<l_test.getNbElements(null) ; i++) {
				GsModelChecker mcheck = (GsModelChecker)l_test.getElement(null, i);
				out.openTag("test");
				out.addAttr("name", mcheck.getName());
				out.addAttr("type", mcheck.getType());
				Map m = mcheck.getAttrList();
				if (m != null) {
					Iterator it = m.keySet().iterator();
					while (it.hasNext()) {
						String sk = (String)it.next();
						Object o = m.get(sk);
						if (o != null) {
							out.openTag("config");
							out.addAttr("name", sk);
							out.addContent(o.toString());
							out.closeTag();
						}
					}
				}
				out.closeTag();
			}
			out.closeTag();
			out.openTag("ExpectedList");
			for (int i=0 ; i<l_test.getNbElements(null) ; i++) {
				GsModelChecker mcheck = (GsModelChecker)l_test.getElement(null, i);
				ValueList o = (ValueList)mcheck.getInfo("-");
				if (o != null) {
					out.openTag("expected");
					out.addAttr("test", mcheck.getName());
					out.addAttr("mutant", "-");
					out.addAttr("value", (String)o.get(o.getSelectedIndex()));
					out.closeTag();
				}
				for (int j=0 ; j<mutants.getNbElements(null) ; j++) {
					Object mutant = mutants.getElement(null, j);
					o = (ValueList)mcheck.getInfo(mutant);
					if (o != null) {
						out.openTag("expected");
						out.addAttr("test", mcheck.getName());
						out.addAttr("mutant", mutant.toString());
						out.addAttr("value", (String)o.get(o.getSelectedIndex()));
						out.closeTag();
					}
				}
			}
			out.closeTag();
			out.closeTag();
		} catch (IOException e) {
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		}
	}

	public String getObjectName() {
		return key;
	}

	public boolean needSaving(GsGraph graph) {
		Object o = graph.getObject(key, false);
		if (o == null || !(o instanceof GenericList) || ((GenericList)o).getNbElements(null) < 1) {
			return false;
		}
		return true;
	}

	public Object doCreate(GsGraph graph) {
		return new GsModelCheckerList((GsRegulatoryGraph)graph);
	}

}
