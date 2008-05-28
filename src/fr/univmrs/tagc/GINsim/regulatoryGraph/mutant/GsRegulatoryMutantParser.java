package fr.univmrs.tagc.GINsim.regulatoryGraph.mutant;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.xml.sax.Attributes;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.xml.XMLHelper;

/**
 * parser for mutants definition file
 */
public class GsRegulatoryMutantParser extends XMLHelper {

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
	
    GsRegulatoryMutants mutantList = null;
    GsRegulatoryGraph graph;
    List nodeOrder;
    String[] t_order;
    GsRegulatoryMutantDef mutant;
    Map mutants_names = new TreeMap();
    
    /**
     * @param graph
     */
    public GsRegulatoryMutantParser(GsRegulatoryGraph graph) {
    	this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
        this.m_call = CALLMAP;
        mutantList = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
    }

    protected void endElement(int id) {
    	if (id == COMMENT) {
            mutant.annotation.setComment(curval);
    	}
	}

	protected void startElement(int id, Attributes attributes) {
		switch (id) {
			case MUTANT:
	            mutant = new GsRegulatoryMutantDef();
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
	                GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);
	                if (vertex.getId().equals(s_vertex)) {
	                    GsRegulatoryMutantChange change = new GsRegulatoryMutantChange(vertex);
	                    change.setMin((short)Integer.parseInt(attributes.getValue("min")));
	                    change.setMax((short)Integer.parseInt(attributes.getValue("max")));
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
