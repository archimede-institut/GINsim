package org.ginsim.service.tool.reg2dyn;

import java.util.HashMap;
import java.util.List;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.xml.XMLHelper;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateList;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesHandler;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesManager;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationManager;
import org.ginsim.core.graph.regulatorygraph.perturbation.ListOfPerturbations;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClass;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetList;
import org.ginsim.service.tool.reg2dyn.updater.UpdaterDefinition;
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
    NamedStatesHandler imanager = null;
    NamedStateList initList = null;
    NamedStateList inputList = null;
    RegulatoryGraph graph;
    List<RegulatoryNode> nodeOrder;
    String[] t_order;
    int pos = POS_OUT;
    int posback = POS_OUT;
    SimulationParameters param;
    boolean pclass_fine;
    boolean addparameter = false;
	private PrioritySetDefinition pcdef;
    
    /**
     * @param graph expected node order
     */
    public SimulationParametersParser( RegulatoryGraph graph) {
    	this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
        paramLists = new SimulationParameterList(graph);
        imanager = (NamedStatesHandler)  ObjectAssociationManager.getInstance().getObject( graph, NamedStatesManager.KEY, true);
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
                    if (addparameter) {
                    	param = paramLists.add();
                    } else {
                    	addparameter = true;
                        param = paramLists.get(0);
                    }
                    param.name = attributes.getValue("name");
                    // read old mode description
                    String s = attributes.getValue("mode");
                    if (s != null) {
                    	if ("asynchrone_df".equals(s)) {
                    		param.breadthFirst = false;
                    		param.setUpdatingMode(paramLists.pcmanager.get(0));
                    	} else if ("asynchrone_bf".equals(s)) {
                    		param.setUpdatingMode(paramLists.pcmanager.get(0));
                    		param.breadthFirst = true;
                    	} else if ("synchrone".equals(s)) {
                    		param.setUpdatingMode(paramLists.pcmanager.get(1));
                    		param.breadthFirst = false;
                    	}
                    } else {
                        s = attributes.getValue("updating");
                        UpdaterDefinition o = paramLists.pcmanager.getByName(s);
                        if (o == null) {
                        	o = paramLists.pcmanager.getByName(PrioritySetList.ASYNCHRONOUS);
                        }
                        param.setUpdatingMode(o);
                        param.breadthFirst = "true".equals(attributes.getValue("breadthFirst"));
                    }
                    s = attributes.getValue("maxdepth");
                    param.maxdepth = Integer.parseInt(s);
                    s = attributes.getValue("maxnodes");
                    param.maxnodes = Integer.parseInt(s);
                } else if (qName.equals("priorityClassList")) {
                	int index = paramLists.pcmanager.addDefinition(null);
                	pcdef = (PrioritySetDefinition)paramLists.pcmanager.get(index);
                	pcdef.clear();
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
                	
                	// compatibility code to restore the old perturbations
                  String s = attributes.getValue("value");
                  if (!s.trim().equals("")) {
                  	ListOfPerturbations perturbations = (ListOfPerturbations)  ObjectAssociationManager.getInstance().getObject( graph, PerturbationManager.KEY, true);
                  	Perturbation perturbation = perturbations.get(s);
                    if (perturbation == null) {
                    	LogManager.error( "Perturbation not found "+s+" ("+perturbations.size()+")");
                    } else {
                    	perturbations.usePerturbation("simulation::"+param.name, perturbation);
                    }
                  }
                	
                } else if (qName.equals("priorityClassList")) {
                	int index = paramLists.pcmanager.addDefinition(null);
                	pcdef = (PrioritySetDefinition)paramLists.pcmanager.get(index);
                	pcdef.clear();
                	pcdef.m_elt.clear();
                	param.setUpdatingMode(pcdef);
                    pclass_fine = false;
                    posback = pos;
                    pos = POS_PCLASS;
                } else if (qName.equals("priorityClass")) {
                	param.setUpdatingMode(paramLists.pcmanager.getByName(attributes.getValue("ref")));
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
                            NamedState istate = (NamedState)initList.get(index);
                            istate.setData(attributes.getValue("value").trim().split(" "), nodeOrder);
                    	    param.m_initState.put(istate, null);
                    	} else {
                            int index = inputList.add();
                            NamedState istate = (NamedState)inputList.get(index);
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
	public SimulationParameterList getParameters() {
		return paramLists;
	}
	
    private void parseClass(Attributes attributes) {
        PriorityClass pc = new PriorityClass();
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
                    	PriorityClass[] oc = pcdef.m_elt.get(vertex);
                    	if (oc == null) {
                    		oc = new PriorityClass[2];
                            pcdef.associate(vertex, pc,pc);
                    	} else if (t_content[i].endsWith(",+")) {                         
                    		// t[0] --> + ; t[1] --> -
                            oc[0] = pc;
                        } else {
                            oc[1] = pc;
                        }
                        break;
                    }
                }
                // TODO: report errors, unknown vertex... ?
            } else { // not fine grained
                // find the corresponding gene
                for (int j=0 ; j<nodeOrder.size() ; j++) {
                    RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(j);
                    if (vertex.getId().equals(t_content[i])) {
                    	pcdef.associate(vertex, pc);
                        break;
                    }
                }
                // TODO: report errors, unknown vertex... ?
            }
        }
        pcdef.add(pc);
    }
    
    private void closeClass() {
        // some consistency checking
    	PriorityClass cl = pcdef.get(0);
        if (pclass_fine) {
        	for (RegulatoryNode vertex: nodeOrder) {
        		PriorityClass[] oc = pcdef.m_elt.get(vertex);
        		if (oc == null) {
        			pcdef.associate(vertex, cl,cl);        			
        		} else {
	            	if (oc[0] == null) {
	            		oc[0] = cl;
	            	}
	            	if (oc[1] == null) {
	            		oc[1] = cl;
	            	}
        		}
        	}
        } else {
        	for (RegulatoryNode vertex: nodeOrder) {
        		PriorityClass[] oc = pcdef.m_elt.get(vertex);
        		if (oc == null) {
        			pcdef.associate(vertex, cl);
        		}
        	}        	
        }
    }
}
