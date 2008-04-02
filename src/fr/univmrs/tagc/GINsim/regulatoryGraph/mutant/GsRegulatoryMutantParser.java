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
 * parser for simulation parameters file
 */
public class GsRegulatoryMutantParser extends XMLHelper {

	private static Map CALLMAP = new TreeMap();
	
	private static final int LIST = 0;
	private static final int MUTANT = 1;
	private static final int CHANGE = 2;
	private static final int COMMENT = 10;
	private static final int LINK = 10;
	
	static {
		addCall("mutantList", LIST, CALLMAP, STARTONLY, false);
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
    
    /**
     * @param graph expected node order
     */
    public GsRegulatoryMutantParser(GsRegulatoryGraph graph) {
    	this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
        this.m_call = CALLMAP;
    }

    protected void endElement(int id) {
    	if (id == COMMENT) {
            mutant.annotation.setComment(curval);
    	}
	}

	protected void startElement(int id, Attributes attributes) {
		switch (id) {
			case LIST:
	            mutantList = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
				break;
			case MUTANT:
	            mutant = new GsRegulatoryMutantDef();
	            mutant.name = attributes.getValue("name");
	            for (int i=0 ; i<mutantList.getNbElements(null) ; i++) {
	                if (mutantList.getElement(null, i).toString().equals(mutant.name)) {
	                    // TODO: report error: duplicate ID entry
	                    return;
	                }
	            }
	            mutantList.v_data.add(mutant);
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
	            mutant.annotation.addLink(attributes.getValue("xlink:href"), null);
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
