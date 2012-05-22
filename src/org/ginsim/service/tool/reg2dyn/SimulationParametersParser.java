package org.ginsim.service.tool.reg2dyn;

import java.util.HashMap;
import java.util.List;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.xml.XMLHelper;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.GsInitialStateList;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateList;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateManager;
import org.ginsim.core.graph.regulatorygraph.mutant.MutantListManager;
import org.ginsim.core.graph.regulatorygraph.mutant.RegulatoryMutants;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.Reg2dynPriorityClass;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * parser for simulation parameters file
 */
public class SimulationParametersParser extends XMLHelper {

    private static final int POS_OUT = 0;
    private static final int POS_PARAM = 1;
    private static final int POS_PCLASS = 2;
    private static final int POS_INITSTATES = 3;
    private static final int POS_INPUTS = 4;
    
    SimulationParameterList paramLists;
    GsInitialStateList imanager = null;
    InitialStateList initList = null;
    InitialStateList inputList = null;
    RegulatoryGraph graph;
    List<RegulatoryNode> nodeOrder;
    String[] t_order;
    int pos = POS_OUT;
    int posback = POS_OUT;
    SimulationParameters param;
    boolean pclass_fine;
    boolean addparameter = false;
	private PriorityClassDefinition	pcdef;
    
    /**
     * @param graph expected node order
     */
    public SimulationParametersParser( RegulatoryGraph graph) {
    	this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
        paramLists = new SimulationParameterList(graph);
        imanager = (GsInitialStateList)  ObjectAssociationManager.getInstance().getObject( graph, InitialStateManager.KEY, true);
        initList = imanager.getInitialStates();
        inputList = imanager.getInputConfigs();
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        switch (pos) {
            case POS_OUT:
                if (qName.equals("simulationParameters")) {
                    t_order = attributes.getValue("nodeOrder").trim().split(" ");
                    if (t_order.length != nodeOrder.size()) {
                        throw new SAXException( new GsException( GsException.GRAVITY_ERROR, "STR_REG2DYN_BadNumberGenes"));
                    }
                    for (int i=0 ; i<t_order.length ; i++) {
                        if (!t_order[i].equals(nodeOrder.get(i).toString())) {
                            throw new SAXException(new GsException( GsException.GRAVITY_ERROR, "STR_InvalidNodeOrder"));
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
                	param = (SimulationParameters)paramLists.getElement(null, index);
                    param.name = attributes.getValue("name");
                    // read old mode description
                    String s = attributes.getValue("mode");
                    if (s != null) {
                    	if ("asynchrone_df".equals(s)) {
                    		param.breadthFirst = false;
                    		param.store.setObject(SimulationParameters.PCLASS, paramLists.pcmanager.getElement(null, 0));
                    	} else if ("asynchrone_bf".equals(s)) {
                    		param.store.setObject(SimulationParameters.PCLASS, paramLists.pcmanager.getElement(null, 0));
                    		param.breadthFirst = true;
                    	} else if ("synchrone".equals(s)) {
                    		param.store.setObject(SimulationParameters.PCLASS, paramLists.pcmanager.getElement(null, 1));
                    		param.breadthFirst = false;
                    	}
                    } else {
                        s = attributes.getValue("updating");
                        Object o = paramLists.pcmanager.getElement(s);
                        if (o == null) {
                        	o = paramLists.pcmanager.getElement(PriorityClassManager.ASYNCHRONOUS);
                        }
                        param.store.setObject(SimulationParameters.PCLASS, o);
                        param.breadthFirst = "true".equals(attributes.getValue("breadthFirst"));
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
                } else if (qName.equals("inputs")) {
                    pos = POS_INPUTS;
                    param.m_input = new HashMap();
                } else if (qName.equals("mutant")) {
                    String s = attributes.getValue("value");
                    if (!s.trim().equals("")) {
                    	RegulatoryMutants mutantList = (RegulatoryMutants)  ObjectAssociationManager.getInstance().getObject( graph, MutantListManager.KEY, true);
                    	Object mutant = mutantList.get(s);
                        param.store.setObject(SimulationParameters.MUTANT, mutantList.get(s));
                        if (mutant == null) {
                            // TODO: report mutant not found
                        	LogManager.error( "Mutant not found "+s+" ("+mutantList.getNbElements(null)+")");
                        }
                    }
                } else if (qName.equals("priorityClassList")) {
                	int index = paramLists.pcmanager.add();
                	pcdef = (PriorityClassDefinition)paramLists.pcmanager.getElement(null, index);
                	pcdef.v_data.clear();
                	pcdef.m_elt.clear();
                	param.store.setObject(SimulationParameters.PCLASS, pcdef);
                    pclass_fine = false;
                    posback = pos;
                    pos = POS_PCLASS;
                } else if (qName.equals("priorityClass")) {
                	param.store.setObject(SimulationParameters.PCLASS,
                			paramLists.pcmanager.getElement(attributes.getValue("ref")));
                }
                break;
            case POS_PCLASS:
                if ("class".equals(qName)) {
                    parseClass(attributes);
                }
                break;
            case POS_INITSTATES:
            case POS_INPUTS:
                if ("row".equals(qName)) {
                    String s = attributes.getValue("name");
                    if (s == null) {
                    	// old file, do some cleanup
                    	if (pos == POS_INITSTATES) {
                            int index = initList.add();
                            InitialState istate = (InitialState)initList.getElement(null, index);
                            istate.setData(attributes.getValue("value").trim().split(" "), nodeOrder);
                    	    param.m_initState.put(istate, null);
                    	} else {
                            int index = inputList.add();
                            InitialState istate = (InitialState)inputList.getElement(null, index);
                            istate.setData(attributes.getValue("value").trim().split(" "), nodeOrder);
                            param.m_input.put(istate, null);
                    	}
                    } else {
                        // associate with the existing object
                        if (pos == POS_INITSTATES) {
                            initList.addInitState(s, param.m_initState);
                        } else {
                            inputList.addInitState(s, param.m_input);
                        }
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
            case POS_INPUTS:
                if (qName.equals("inputs")) {
                    pos = POS_PARAM;
                }
        }
    }
    /**
     * @return the list of parameters read by this parser.
     */
	public Object getParameters() {
		return paramLists;
	}
	
    private void parseClass(Attributes attributes) {
        Reg2dynPriorityClass pc = new Reg2dynPriorityClass();
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
                    RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(j);
                    if (vertex.getId().equals(s_vertex)) {
                        Object oc = pcdef.m_elt.get(vertex);
                        Object[] t;
                        if (oc == null || oc instanceof Reg2dynPriorityClass) {
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
                    RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(j);
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
        	for (RegulatoryNode vertex: nodeOrder) {
        		Object oc = pcdef.m_elt.get(vertex);
        		Object[] t;
        		if (oc instanceof Reg2dynPriorityClass) {
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
