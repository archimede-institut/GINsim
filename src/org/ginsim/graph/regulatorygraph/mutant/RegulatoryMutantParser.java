package org.ginsim.graph.regulatorygraph.mutant;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantListManager;
import org.ginsim.gui.graph.regulatorygraph.mutant.RegulatoryMutants;
import org.xml.sax.Attributes;

import fr.univmrs.tagc.common.xml.XMLHelper;

/**
 * parser for mutants definition file
 */
public class RegulatoryMutantParser extends XMLHelper {

	private static Map CALLMAP = new TreeMap();
	
	private static final int MUTANT = 1;
	private static final int CHANGE = 2;
	private static final int COMMENT = 11;
	private static final int LINK = 12;
	
	static {
		addCall("mutant", MUTANT, CALLMAP, STARTONLY, false);
		addCall("change", CHANGE, CALLMAP, STARTONLY, false);
		addCall("link", LINK, CALLMAP, STARTONLY, false);
		addCall("comment", COMMENT, CALLMAP, ENDONLY, true);
	}
	
    RegulatoryMutants mutantList = null;
    RegulatoryGraph graph;
    List nodeOrder;
    String[] t_order;
    RegulatoryMutantDef mutant;
    Map mutants_names = new TreeMap();
    
    /**
     * @param graph
     */
    public RegulatoryMutantParser(RegulatoryGraph graph) {
    	this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
        this.m_call = CALLMAP;
        mutantList = (RegulatoryMutants) ObjectAssociationManager.getInstance().getObject(graph, MutantListManager.key, true);
    }

    protected void endElement(int id) {
    	if (id == COMMENT) {
            mutant.annotation.setComment(curval);
    	}
	}

	protected void startElement(int id, Attributes attributes) {
		switch (id) {
			case MUTANT:
	            mutant = new RegulatoryMutantDef();
	            mutant.name = attributes.getValue("name");
	            if (mutants_names.containsKey(mutant.name)) {
	            	// TODO: report duplicate entry
	            } else {
		            mutantList.v_data.add(mutant);
	            	mutants_names.put(mutant.name, null);
	            }
				break;

			case CHANGE:
	            String s_vertex = attributes.getValue("target");
	            for (int i=0 ; i<nodeOrder.size() ; i++) {
	                RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(i);
	                if (vertex.getId().equals(s_vertex)) {
	                    RegulatoryMutantChange change = new RegulatoryMutantChange(vertex);
	                    change.setMin((byte)Integer.parseInt(attributes.getValue("min")));
	                    change.setMax((byte)Integer.parseInt(attributes.getValue("max")));
	                    String condition = attributes.getValue("condition");
	                    if (condition != null && condition.trim().length() > 0) {
	                    	change.setCondition(condition, graph);
	                    }
	                    mutant.v_changes.add(change);
	                    break;
	                }
	            }
				break;
			case LINK:
				String lnk = attributes.getValue("xlink:href");
				if (lnk != null) {
			      		mutant.annotation.addLink(lnk, graph);
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