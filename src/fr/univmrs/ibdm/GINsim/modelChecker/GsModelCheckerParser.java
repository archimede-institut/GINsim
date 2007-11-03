package fr.univmrs.ibdm.GINsim.modelChecker;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.xml.GsXMLHelper;
import fr.univmrs.tagc.datastore.ValueList;

/**
 * parser for simulation parameters file
 */
public class GsModelCheckerParser extends GsXMLHelper {

    public GsGraph getGraph() {
        // doesn't create a graph!
        return null;
    }
    public String getFallBackDTD() {
        // doesn't use a DTD either
        return null;
    }

    private static final int POS_OUT = 0;
    private static final int POS_TEST = 1;
    
    GsRegulatoryGraph graph;
    int pos = POS_OUT;
    String cfg_name = null;
    GsModelChecker mcheck;
    Map m_attr = new HashMap();
    GsModelCheckerList l_tests;
    GsRegulatoryMutants l_mutant;
    
    /**
     * @param graph expected node order
     */
    public GsModelCheckerParser(GsRegulatoryGraph graph) {
    	this.graph = graph;
    	l_tests = new GsModelCheckerList(graph);
    	l_mutant = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        switch (pos) {
            case POS_OUT:
            	if ("test".equals(qName)) {
            		pos = POS_TEST;
            		String testName = attributes.getValue("name");
            		String testType = attributes.getValue("type");
            		int typeIndex = -1;
            		for (int i=0 ; i<GsModelCheckerPlugin.v_checker.size() ; i++) {
            			if (testType.equals( ((GsModelCheckerDescr)GsModelCheckerPlugin.v_checker.get(i)).getName()) ) {
            				typeIndex = i;
            				break;
            			}
            		}
            		if (typeIndex == -1) {
            			throw new SAXException(testType+" model checker is not available");
            		}
            		int ref = l_tests.add();
            		l_tests.edit(null, ref, testName);
            		mcheck = (GsModelChecker)l_tests.getElement(null, ref);
            		m_attr.clear();
            	} else if ("expected".equals(qName)) {
            		String s = attributes.getValue("test");
            		for (int i=0 ; i<l_tests.getNbElements(null) ; i++) {
            			GsModelChecker mc = (GsModelChecker)l_tests.getElement(null, i);
            			if (s.equals(mc.getName())) {
            				s = attributes.getValue("mutant");
            				if (s == null || s.equals("-")) {
        						ValueList vl = (ValueList)mc.getInfo("-");
        						s = attributes.getValue("value");
        						if ("Yes".equals(s)) {
        							vl.setSelectedIndex(1);
        						} else if ("No".equals(s)) {
        							vl.setSelectedIndex(2);
        						} else {
        							vl.setSelectedIndex(0);
        						}
            				} else {
								for (int j=0 ; j<l_mutant.getNbElements(null) ; j++) {
									GsRegulatoryMutantDef mutant = (GsRegulatoryMutantDef)l_mutant.getElement(null, j);
									if (s.equals(mutant.toString())) {
										ValueList vl = (ValueList)mc.getInfo(mutant);
										s = attributes.getValue("value");
										if ("Yes".equals(s)) {
											vl.setSelectedIndex(1);
										} else if ("No".equals(s)) {
											vl.setSelectedIndex(2);
										} else {
											vl.setSelectedIndex(0);
										}
										break;
									}
								}
							}
            				break;
            			}
            		}
            	}
                break;
            case POS_TEST:
            	if ("config".equals(qName)) {
            		cfg_name = attributes.getValue("name");
            		curval = "";
            	}
            	break;
        }
    }
    
    public void endElement (String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        switch (pos) {
        case POS_TEST:
        	if ("test".equals(qName)) {
        		pos = POS_OUT;
        		mcheck.setCfg(m_attr);
        	} else if ("config".equals(qName)) {
        		m_attr.put(cfg_name, curval);
        		curval = null;
        	}
        	break;
        }
    }
    /**
     * @return the list of parameters read by this parser.
     */
	public Object getParameters() {
		return l_tests;
	}
}