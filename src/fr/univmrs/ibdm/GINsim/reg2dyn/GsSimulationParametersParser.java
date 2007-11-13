package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
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
    
    
    GsSimulationParameterList paramLists;
    GsInitialStateList initList = null;
    GsRegulatoryGraph graph;
    Vector nodeOrder;
    String[] t_order;
    int pos = POS_OUT;
    int posback = POS_OUT;
    GsSimulationParameters param;
    boolean pclass_fine;
    boolean addparameter = false;
	private PriorityClassDefinition	pcdef;
    
    /**
     * @param graph expected node order
     */
    public GsSimulationParametersParser(GsRegulatoryGraph graph) {
    	this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
        paramLists = new GsSimulationParameterList(graph);
        initList = (GsInitialStateList)graph.getObject(GsInitialStateManager.key, true);
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
                    int index = 0;
                    if (addparameter) {
                    	index = paramLists.add();
                    } else {
                    	addparameter = true;
                    }
                	param = (GsSimulationParameters)paramLists.getElement(null, index);
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
                } else if (qName.equals("priorityClassList")) {
                	int index = paramLists.pcmanager.add();
                	pcdef = (PriorityClassDefinition)paramLists.pcmanager.getElement(null, index);
                	pcdef.v_data.clear();
                	pcdef.m_elt.clear();
                	pcdef.setName(attributes.getValue("id"));
                    pclass_fine = false;
                    posback = pos;
                    pos = POS_PCLASS;
                }
                break;
            case POS_PARAM:
                if (qName.equals("initstates")) {
                    pos = POS_INITSTATES;
                    param.m_initState = new HashMap();
                } else if (qName.equals("mutant")) {
                    String s = attributes.getValue("value");
                    if (!s.trim().equals("")) {
                    	GsRegulatoryMutants mutantList = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
                    	Object mutant = mutantList.get(s);
                        param.store.setObject(GsSimulationParameters.MUTANT, mutantList.get(s));
                        if (mutant == null) {
                            // TODO: report mutant not found
                            System.out.println("mutant not found "+s+" ("+mutantList.getNbElements(null)+")");
                        }
                    }
                } else if (qName.equals("priorityClassList")) {
                	int index = paramLists.pcmanager.add();
                	pcdef = (PriorityClassDefinition)paramLists.pcmanager.getElement(null, index);
                	pcdef.v_data.clear();
                	pcdef.m_elt.clear();
                	param.store.setObject(GsSimulationParameters.PCLASS, pcdef);
                    pclass_fine = false;
                    posback = pos;
                    pos = POS_PCLASS;
                } else if (qName.equals("priorityClass")) {
                	param.store.setObject(GsSimulationParameters.PCLASS,
                			paramLists.pcmanager.getElement(attributes.getValue("ref")));
                }
                break;
            case POS_PCLASS:
                if ("class".equals(qName)) {
                    parseClass(attributes);
                }
                break;
            case POS_INITSTATES:
                if ("row".equals(qName)) {
                    String s = attributes.getValue("name");
                    if (s == null) {
                    	// old file, do some cleanup
                    	int index = initList.add();
                    	GsInitialState istate = (GsInitialState)initList.getElement(null, index);
                    	istate.setData(attributes.getValue("value").trim().split(" "), nodeOrder);
                    	param.m_initState.put(istate, null);
                    } else {
                    	// associate with the existing object
                    	param.m_initState.put(initList.getInitState(s), null);
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
                    param = null;
                }
                break;
            case POS_PCLASS:
                if (qName.equals("priorityClassList")) {
                    pos = posback;
                    closeClass();
                    pcdef = null;
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
	
    private void parseClass(Attributes attributes) {
        GsReg2dynPriorityClass pc = new GsReg2dynPriorityClass();
        pc.setName(attributes.getValue("name"));
        try {
        	pc.setMode(Integer.parseInt(attributes.getValue("mode")));
        } catch (NumberFormatException e) {
            // TODO: report error with mode
        }
        try {
            pc.rank = Integer.parseInt(attributes.getValue("rank"));
        } catch (NumberFormatException e) {
            // TODO: report error with rank
        }
        String[] t_content = attributes.getValue("content").split(" ");
        for (int i=0 ; i<t_content.length ; i++) {
            if (t_content[i].endsWith(",+") || t_content[i].endsWith(",-")) {
            	pclass_fine = true;
                String s_vertex = t_content[i].substring(0, t_content[i].length()-2);
                for (int j=0 ; j<nodeOrder.size() ; j++) {
                    GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(j);
                    if (vertex.getId().equals(s_vertex)) {
                        Object oc = pcdef.m_elt.get(vertex);
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
                        pcdef.m_elt.put(vertex, t);
                        break;
                    }
                }
                // TODO: report errors, unknown vertex... ?
            } else { // not fine grained
                // find the corresponding gene
                for (int j=0 ; j<nodeOrder.size() ; j++) {
                    GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(j);
                    if (vertex.getId().equals(t_content[i])) {
                    	pcdef.m_elt.put(vertex, pc);
                        break;
                    }
                }
                // TODO: report errors, unknown vertex... ?
            }
        }
        pcdef.v_data.add(pc);
    }
    
    private void closeClass() {
        // some consistency checking
        if (pclass_fine) {
        	Iterator it = nodeOrder.iterator();
        	while (it.hasNext()) {
        		Object vertex = it.next();
        		Object oc = pcdef.m_elt.get(vertex);
        		Object[] t;
        		if (oc instanceof GsReg2dynPriorityClass) {
        			// added to a single class, fix it
                    t = new Object[2];
                    t[0] = t[1] = oc;
                    pcdef.m_elt.put(vertex, t);
                } else if (oc instanceof Object[]) {
                	t = (Object[])oc;
                	if (t[0] == null) {
                		t[0] = pcdef.v_data.get(0);
                	}
                	if (t[1] == null) {
                		t[1] = pcdef.v_data.get(0);
                	}
                } else {
                    t = new Object[2];
                    t[0] = t[1] = pcdef.v_data.get(0);
                    pcdef.m_elt.put(vertex, t);
                }
        	}
        }
    }
}
