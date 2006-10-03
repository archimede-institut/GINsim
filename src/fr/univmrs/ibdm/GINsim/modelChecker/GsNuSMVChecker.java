package fr.univmrs.ibdm.GINsim.modelChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.univmrs.ibdm.GINsim.gui.GsValueList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;

/**
 * a model checker using NuSMV
 */
public class GsNuSMVChecker implements GsModelChecker {

    String name;
    String thetest;
    GsRegulatoryGraph graph;
    Map m_info = new HashMap();
    
    public GsNuSMVChecker(String name, GsRegulatoryGraph graph) {
        this.name = name;
        this.graph = graph;
    }


    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;;
    }
    
    public String toString() {
        return name;
    }
    
    public boolean[] run(GsRegulatoryMutants mutants) {
        boolean[] ret = new boolean[mutants.getNbElements()+1];
        try {
        	for (int i=-1 ; i<mutants.getNbElements() ; i++) {
	            Object m;
	            if (i==-1) {
	            	m = "-";
	            } else {
	            	 m = mutants.getElement(i);
	            }
	            Object o = m_info.get(m);
	            GsModelCheckerTestResult result = new GsModelCheckerTestResult();
	            if (o == null) {
	            	result.expected = 0;
	            } else if (o instanceof GsValueList) {
	            	result.expected = ((GsValueList)o).getSelectedIndex();
	            } else {
	            	System.out.println("should not come here: result based on previous result");
	            	result.expected = ((GsModelCheckerTestResult)o).expected;
	            }

	            Process p = Runtime.getRuntime().exec("NuSMV -h");
	            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

	            // TODO: really run a NuSMV test
	            
	            result.result = 2;
	            result.output = "test did _NOT_ really run";
	            m_info.put(m, result);
        	}
        } catch (IOException e) {
            return null;
        }
        return ret;
    }

	public Object getInfo(Object mutant) {
		Object o = m_info.get(mutant);
		if (o == null) {
			o = new GsValueList(GsModelCheckerPlugin.v_values, 0);
			m_info.put(mutant, o);
		}
		return o;
	}
	public void delMutant(Object mutant) {
		m_info.remove(mutant);
	}
	public void cleanup() {
		Iterator it = m_info.keySet().iterator();
		while (it.hasNext()) {
			Object k = it.next();
			Object r = m_info.get(k);
			if (r instanceof GsModelCheckerTestResult) {
				m_info.put(k, new GsValueList(GsModelCheckerPlugin.v_values, ((GsModelCheckerTestResult)r).expected));
			}
		}
	}
}
