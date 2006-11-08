package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.xml.GsXMLHelper;

/**
 * parser for simulation parameters file
 */
public class GsSimulationParametersParser extends GsXMLHelper {

    public GsGraph getGraph() {
        // doesn't create a graph!
        return null;
    }
    public String getFallBackDTD() {
        // doesn't use a DTD either
        return null;
    }

    private static final int POS_OUT = 0;
    private static final int POS_PARAM = 1;
    private static final int POS_PCLASS = 2;
    private static final int POS_INITSTATES = 3;
    
    
    GsSimulationParameterList paramLists = null;
    GsRegulatoryGraph graph;
    Vector nodeOrder;
    String[] t_order;
    int pos = POS_OUT;
    GsSimulationParameters param;
    
    /**
     * @param graph expected node order
     */
    public GsSimulationParametersParser(GsRegulatoryGraph graph) {
    	this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        switch (pos) {
            case POS_OUT:
                if (qName.equals("simulationParameters")) {
                    t_order = attributes.getValue("nodeOrder").trim().split(" ");
                    if (t_order.length != nodeOrder.size()) {
                        throw new SAXException("bad number of genes");
                    }
                    for (int i=0 ; i<t_order.length ; i++) {
                        if (!t_order[i].equals(nodeOrder.get(i).toString())) {
                            throw new SAXException("wrong node order");
                        }
                    }
                } else if (qName.equals("parameter")) {
                    pos = POS_PARAM;
                    param = new GsSimulationParameters(nodeOrder);
                    param.name = attributes.getValue("name");
                    String s = attributes.getValue("mode");
                    for (int i=0 ; i<Simulation.MODE_NAMES.length ; i++) {
                        if (Simulation.MODE_NAMES[i].equals(s)) {
                            param.mode = i;
                            break;
                        }
                    }
                    s = attributes.getValue("maxdepth");
                    param.maxdepth = Integer.parseInt(s);
                    s = attributes.getValue("maxnodes");
                    param.maxnodes = Integer.parseInt(s);
                }
                break;
            case POS_PARAM:
                if (qName.equals("initstates")) {
                    pos = POS_INITSTATES;
                    param.m_initState = new HashMap();
                } else if (qName.equals("mutant")) {
                    String s = attributes.getValue("value");
                    if (!s.trim().equals("")) {
                        param.mutant = GsRegulatoryMutants.getMutants(graph).get(s);
                        if (param.mutant == null) {
                            // TODO: report mutant not found
                            System.out.println("mutant not found "+s+" ("+GsRegulatoryMutants.getMutants(graph).getNbElements()+")");
                        }
                    }
                } else if (qName.equals("priorityClassList")) {
                    pos = POS_PCLASS;
                }
                break;
            case POS_PCLASS:
                if ("class".equals(qName)) {
                    GsReg2dynPriorityClass pc = new GsReg2dynPriorityClass();
                    pc.setName(attributes.getValue("name"));
                    try {
                        pc.rank = Integer.parseInt(attributes.getValue("rank"));
                    } catch (NumberFormatException e) {
                        // TODO: report error with rank
                    }
                    if (param.v_class == null) {
                        param.v_class = new Vector();
                        param.m_elt = new HashMap();
                    }
                    String[] t_content = attributes.getValue("content").split(" ");
                    for (int i=0 ; i<t_content.length ; i++) {
                        if (t_content[i].endsWith(",+") || t_content[i].endsWith(",-")) {
                            String s_vertex = t_content[i].substring(0, t_content[i].length()-2);
                            for (int j=0 ; j<nodeOrder.size() ; j++) {
                                GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(j);
                                if (vertex.getId().equals(s_vertex)) {
                                    Object oc = param.m_elt.get(vertex);
                                    Object[] t;
                                    if (oc == null || oc instanceof GsReg2dynPriorityClass) {
                                        t = new Object[2];
                                        t[0] = t[1] = oc;
                                    } else { // should be an array 
                                        t = (Object[])oc;
                                    }
                                    // t[0] --> + ; t[1] --> -
                                    if (t_content[i].endsWith(",+")) {
                                        t[0] = pc;
                                    } else {
                                        t[1] = pc;
                                    }
                                    param.m_elt.put(vertex, t);
                                    break;
                                }
                            }
                            // TODO: report errors, unknown vertex... ?
                        } else { // not fine grained
                            // find the corresponding gene
                            for (int j=0 ; j<nodeOrder.size() ; j++) {
                                GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(j);
                                if (vertex.getId().equals(t_content[i])) {
                                    param.m_elt.put(vertex, pc);
                                    break;
                                }
                            }
                            // TODO: report errors, unknown vertex... ?
                        }
                    }
                    String[] t_s = attributes.getValue("content").split(" ");
                    for (int i=0 ; i<t_s.length ; i++) {
                    }
                    param.v_class.add(pc);
                }
                break;
            case POS_INITSTATES:
                if ("row".equals(qName)) {
                    String[] t_s = attributes.getValue("value").trim().split(" ");
                    Map m_row = new HashMap();
                    for (int i=0 ; i<t_s.length ; i++) {
                        GsRegulatoryVertex vertex = null;
                        String[] t_val = t_s[i].split(";");
                        if (t_val.length > 1) {
                            for (int j=0 ; j<nodeOrder.size() ; j++) {
                                if (((GsRegulatoryVertex)nodeOrder.get(j)).getId().equals(t_val[0])) {
                                    vertex = (GsRegulatoryVertex)nodeOrder.get(j);
                                    break;
                                }
                            }
                            if (vertex != null) {
                                Vector v_val = new Vector();
                                for (int j=1 ; j<t_val.length ; j++) {
                                    try {
                                        Integer val = new Integer(t_val[j]);
                                        if (val.intValue() >= 0 && val.intValue() <= vertex.getMaxValue()) {
                                            boolean ok = true;
                                            for (int k=0 ; k<v_val.size() ; k++) {
                                                if (v_val.get(k).equals(val)) {
                                                    ok = false;
                                                    break;
                                                }
                                            }
                                            if (ok) {
                                                v_val.add(val);
                                            }
                                        } else {
                                            // TODO: report error in file
                                        }
                                    } catch (NumberFormatException e) {
                                        // TODO: report error in file
                                    }
                                }
                                if (!v_val.isEmpty() && v_val.size() <= vertex.getMaxValue()) {
                                    m_row.put(vertex, v_val);
                                }
                            }
                        }
                    }
                    if (!m_row.isEmpty()) {
                    	// FIXME: not REALLY added here!
                        param.m_initState.put(m_row, null);
                    }
                }
                break;
        }
    }
    
    public void endElement (String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        switch (pos) {
            case POS_PARAM:
                if (qName.equals("parameter")) {
                    pos = POS_OUT;
                    if (paramLists == null) {
                    	paramLists = new GsSimulationParameterList(graph, param);
                    } else {
                    	paramLists.add(param);
                    }
                    param = null;
                }
                break;
            case POS_PCLASS:
                if (qName.equals("priorityClassList")) {
                    pos = POS_PARAM;
                }
                break;
            case POS_INITSTATES:
                if (qName.equals("initstates")) {
                    pos = POS_PARAM;
                }
                break;
        }
    }
    /**
     * @return the list of parameters read by this parser.
     */
	public Object getParameters() {
		return paramLists;
	}
}
