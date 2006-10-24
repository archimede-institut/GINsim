package fr.univmrs.ibdm.GINsim.modelChecker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.ibdm.GINsim.gui.GsList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;

public class GsModelCheckerAssociatedObjectManager implements GsGraphAssociatedObjectManager {

	public static String key = "mchecker";
	
	public void doOpen(InputStream is, GsGraph graph) {
		// TODO Auto-generated method stub
		
	}

	public void doSave(OutputStreamWriter os, GsGraph graph) {
		GsList l_test = (GsList)graph.getObject(key);
		GsRegulatoryMutants mutants = GsRegulatoryMutants.getMutants((GsRegulatoryGraph)graph);
		try {
			GsXMLWriter out = new GsXMLWriter(os, null);
			out.openTag("testList");
			for (int i=0 ; i<l_test.getNbElements() ; i++) {
				GsModelChecker mcheck = (GsModelChecker)l_test.getElement(i);
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
							out.addAttr(sk, o.toString());
						}
					}
				}
				out.closeTag();
			}
			out.closeTag();
			out.openTag("ExpectedList");
			for (int i=0 ; i<l_test.getNbElements() ; i++) {
				GsModelChecker mcheck = (GsModelChecker)l_test.getElement(i);
				for (int j=0 ; j<mutants.getNbElements() ; j++) {
					String mutant = mutants.getElement(j).toString();
					Object o = mcheck.getInfo(mutant);
					if (o != null) {
						out.openTag("expected");
						out.addAttr("test", mcheck.getName());
						out.addAttr("mutant", mutant);
						out.addAttr("value", o.toString());
						out.closeTag();
					}
				}
			}
			out.closeTag();
		} catch (IOException e) {
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		}
	}

	public String getObjectName() {
		return key;
	}

	public boolean needSaving(GsGraph graph) {
		Object o = graph.getObject(key);
		if (o == null || !(o instanceof GsList) || ((GsList)o).getNbElements() < 1) {
			return false;
		}
		return true;
	}

}
