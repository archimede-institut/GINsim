package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.xml.XMLHelper;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.xml.sax.Attributes;


/**
 * parser for perturbation definition file
 */
public class PerturbationParser extends XMLHelper {

	private static Map CALLMAP = new TreeMap();
	
	private static final int MUTANT = 1;
	private static final int CHANGE = 2;
	private static final int REGULATOR_CHANGE = 3;
	private static final int COMMENT = 11;
	private static final int LINK = 12;

	private static final int PERTURBATION = 21;
	private static final int MULTIPLE = 22;

	private static final int USER = 30;

	static {
		addCall("mutant", MUTANT, CALLMAP, BOTH, false);
		addCall("change", CHANGE, CALLMAP, STARTONLY, false);
		addCall("regulatorChange", REGULATOR_CHANGE, CALLMAP, STARTONLY, false);
		addCall("link", LINK, CALLMAP, STARTONLY, false);
		addCall("comment", COMMENT, CALLMAP, ENDONLY, true);

		addCall("perturbation", PERTURBATION, CALLMAP, STARTONLY, false);
		addCall("multiple", MULTIPLE, CALLMAP, STARTONLY, false);

		addCall("user", USER, CALLMAP, STARTONLY, false);
	}
	
    private ListOfPerturbations mutantList = null;
    private RegulatoryGraph graph;
    private List nodeOrder;
    private String[] t_order;
    
    private List<Perturbation> perturbations;
    private String curname;
    
    private Map<String, Perturbation> m_names = new HashMap<String, Perturbation>();

    /**
     * @param graph
     */
    public PerturbationParser(RegulatoryGraph graph) {
    	this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
        this.m_call = CALLMAP;
        mutantList = (ListOfPerturbations) ObjectAssociationManager.getInstance().getObject(graph, PerturbationManager.KEY, true);
    }

    protected void endElement(int id) {
    	if (id == COMMENT) {
    		// FIXME
//            mutant.annotation.setComment(curval);
    	} else if (id == MUTANT) {
    		// add multiple mutants if needed and set aliases
			Perturbation p = perturbations.get(0);
    		if (perturbations.size() > 1) {
    			p = mutantList.addMultiplePerturbation(perturbations);
    		}
			m_names.put(curname, p);
			mutantList.setAliases(curname, p);
    	}
	}

	protected void startElement(int id, Attributes attributes) {
		switch (id) {
		case PERTURBATION:
			// TODO: parse single changes
			break;
		case MULTIPLE:
			// TODO: parse new multiple perturbations
			break;

		case MUTANT:
			perturbations = new ArrayList<Perturbation>();
			curname = attributes.getValue("name");
			break;

		case CHANGE:
			// FIXME
			String s_vertex = attributes.getValue("target");
        	int min = Integer.parseInt(attributes.getValue("min"));
        	int max = Integer.parseInt(attributes.getValue("max"));
            String condition = attributes.getValue("condition");
            if (condition != null && condition.trim().length() > 0) {
                LogManager.debug("Conditional perturbations not supported");
            }
            for (int i=0 ; i<nodeOrder.size() ; i++) {
                RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(i);
                if (vertex.getId().equals(s_vertex)) {
            		// add it to the main list if needed, and to the current list
                	Perturbation p = mutantList.addRangePerturbation(vertex.getNodeInfo(), min, max);
                	if (perturbations != null) {
                		perturbations.add(p);
                	}
                    return;
                }
            }
            LogManager.debug("Could not find a matching node for perturbation on: "+s_vertex);
			break;

		case REGULATOR_CHANGE:
			s_vertex = attributes.getValue("target");
			String s_regulator = attributes.getValue("regulator");
        	int value = Integer.parseInt(attributes.getValue("value"));
        	NodeInfo regulator = null;
        	NodeInfo target = null;
            for (int i=0 ; i<nodeOrder.size() ; i++) {
                RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(i);
                if (vertex.getId().equals(s_vertex)) {
                	target = vertex.getNodeInfo();
                	if (regulator != null) {
                		break;
                	}
                }
                if (vertex.getId().equals(s_regulator)) {
                	regulator = vertex.getNodeInfo();
                	if (target != null) {
                		break;
                	}
                }
            }

            if (regulator != null && target != null) {
            	Perturbation p = mutantList.addRegulatorPerturbation(regulator, target, value);
            	if (perturbations != null) {
            		perturbations.add(p);
            	}
            } else {
            	LogManager.debug("Could not find a matching node for perturbation on: "+s_vertex);
            }
            break;

		case LINK:
			String lnk = attributes.getValue("xlink:href");
			if (lnk != null) {
				// FIXME
//	      		mutant.annotation.addLink(lnk, graph);
			}
			break;
		case USER:
			String s_key = attributes.getValue("key");
			String s_value = attributes.getValue("value");
			mutantList.usePerturbation(s_key, m_names.get(s_value));
			break;
		}
	}

	/**
     * @return the list of parameters read by this parser.
     */
	public Object getParameters() {
		return mutantList;
	}
}
