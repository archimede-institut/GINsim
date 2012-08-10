package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.xml.XMLHelper;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.xml.sax.Attributes;


/**
 * parser for perturbation definition file
 */
public class RegulatoryMutantParser extends XMLHelper {

	private static Map CALLMAP = new TreeMap();
	
	private static final int MUTANT = 1;
	private static final int CHANGE = 2;
	private static final int COMMENT = 11;
	private static final int LINK = 12;

	private static final int PERTURBATION = 21;
	private static final int MULTIPLE = 22;

	static {
		addCall("mutant", MUTANT, CALLMAP, BOTH, false);
		addCall("change", CHANGE, CALLMAP, STARTONLY, false);
		addCall("link", LINK, CALLMAP, STARTONLY, false);
		addCall("comment", COMMENT, CALLMAP, ENDONLY, true);

		addCall("perturbation", PERTURBATION, CALLMAP, STARTONLY, false);
		addCall("multiple", MULTIPLE, CALLMAP, STARTONLY, false);

	}
	
    RegulatoryMutants mutantList = null;
    RegulatoryGraph graph;
    List nodeOrder;
    String[] t_order;
    
    
    List<Perturbation> perturbations;
    String curname;

    /**
     * @param graph
     */
    public RegulatoryMutantParser(RegulatoryGraph graph) {
    	this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
        this.m_call = CALLMAP;
        mutantList = (RegulatoryMutants) ObjectAssociationManager.getInstance().getObject(graph, PerturbationManager.KEY, true);
    }

    protected void endElement(int id) {
    	if (id == COMMENT) {
    		// FIXME
//            mutant.annotation.setComment(curval);
    	} else if (id == MUTANT) {
    		// add multiple mutants if needed
    		if (perturbations.size() > 1) {
    			mutantList.addMultiplePerturbation(perturbations);
    		}
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

		case LINK:
			String lnk = attributes.getValue("xlink:href");
			if (lnk != null) {
				// FIXME
//	      		mutant.annotation.addLink(lnk, graph);
			}
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
