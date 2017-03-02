package org.ginsim.service.tool.reg2dyn;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.colomoto.biolqm.NodeInfo;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.utils.data.NamedObject;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitStateTableModel;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClass;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetList;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityDefinitionStore;


/**
 * remember, save and restore a simulation parameter.
 */
public class SimulationParameters implements XMLize, NamedObject, NamedStateStore, PriorityDefinitionStore {

    public static final int MUTANT = 0;

	public static final int STRATEGY_STG = 0;
	public static final int STRATEGY_SCCG = 1;
	public static final int STRATEGY_HTG = 2;

	public String name = "new_parameter";

    public int maxdepth;
    public int maxnodes;
    public boolean breadthFirst = false;
    public int simulationStrategy = STRATEGY_STG;

    public Map m_initState = new HashMap();
    public Map m_input = new HashMap();
    public SimulationParameterList param_list;

    private PrioritySetDefinition pcdef;

    /**
     * empty constructor for everyday use.
     * @param param_list
     */
    public SimulationParameters(SimulationParameterList param_list) {
    	this.param_list = param_list;
        setPriorityDefinition(param_list.pcmanager.get(0));
    }

    /**
     * empty constructor without parameter list.
     * @param graph
     */
    public SimulationParameters(RegulatoryGraph graph) {
    	this(new SimulationParameterList(graph));
    }

    public String toString() {
        return name;
    }

    /**
     * Describe this parameter.
     * It shows the full content of this simulation parameter, used to add a useful comment to the state transition graph.
     * @return a human readable description of the parameter
     */
    public String getDescr(List<NodeInfo> nodeOrder) {
        String name = param_list.graph.getGraphName();
        String saveName = GSGraphManager.getInstance().getGraphPath( param_list.graph);
        if (saveName != null) {
            int pos = saveName.lastIndexOf(File.separatorChar);
            if (pos != -1) {
                saveName = saveName.substring(pos+1);
            }
            name += " ("+saveName+")";
        }
        String s = "construction parameters:\n";
        s += "    Regulatory graph: " + name + "\n";
        s += "    Simulation strategy: " + simulationStrategy + "\n";
        s += "    Updating policy: ";
		if (pcdef != null) {
	        if (pcdef.size() > 1) {
	            s += "by priority class\n";
	            int[][] pclass = getPriorityClassDefinition().getPclass(nodeOrder);
	            for (int i=0 ; i<pclass.length ; i++) {
	                int[] cl = pclass[i];
	                s += "        "+cl[0]+ (cl[1]==0?" sync":" async")+": ";
	                for (int j=2;j<cl.length ; j+=2) {
	                    if (j>2) {
	                        s += ", ";
	                    }
	                    s += nodeOrder.get(cl[j])+(cl[j+1]==0?"":cl[j+1]==1?"+":"-");
	                }
	                s += "\n";
	            }
	        } else {
	        	s += pcdef.toString()+"\n";
	        }
		}
        s += "    Initial states: ";
        if (m_initState == null || m_initState.size()==0) {
            s += "ALL\n";
        } else {
            Iterator it = m_initState.keySet().iterator();
            while (it.hasNext()) {
                Map m_init = ((NamedState)it.next()).getMap();
                s += "\n      ";
                for (int j=0 ; j<nodeOrder.size() ; j++) {
                    NodeInfo vertex = nodeOrder.get(j);
                    s += "  "+InitStateTableModel.showValue((List)m_init.get(vertex), vertex.getMax());
                }
            }
            s += "\n";
        }
        // FIXME: proper input/initial states reporting
        s += "    Inputs: ";
        if (m_input == null || m_input.size()==0) {
            s += "ALL\n";
        } else {
            Iterator it = m_input.keySet().iterator();
            while (it.hasNext()) {
                NamedState init = (NamedState)it.next();
                if (init != null) {
                    Map m_init = init.getMap();
                    s += "\n      ";
                    for (int j=0 ; j<nodeOrder.size() ; j++) {
                        NodeInfo vertex = nodeOrder.get(j);
                        s += "  "+InitStateTableModel.showValue((List)m_init.get(vertex), vertex.getMax());
                    }
                }
            }
            s += "\n";
        }

        // TODO: get the real associated perturbation
        Perturbation perturbation = null;
        if (perturbation != null) {
            s += "    Perturbation: "+perturbation+"\n";
        }
        if (maxdepth != 0) {
            s += "    Max depth: "+maxdepth+"\n";
        }
        if (maxnodes != 0) {
            s += "    Max nodes: "+maxnodes+"\n";
        }
        return s;
    }

    @Override
	public void toXML(XMLWriter out) throws IOException {
		out.openTag("parameter");
		out.addAttr("name", name);
		out.addAttr("updating", pcdef.getName());
		out.addAttr("breadthFirst", ""+breadthFirst);
		out.addAttr("maxdepth", ""+maxdepth);
		out.addAttr("maxnodes", ""+maxnodes);
		out.addAttr("simulationStrategy", ""+simulationStrategy);

		if (pcdef.size() > 1) {
			out.openTag("priorityClass");
			out.addAttr("ref", pcdef.getName());
			out.closeTag();
		} else {
			
		}
        if (m_initState != null && m_initState.keySet().size() > 0) {
            out.openTag("initstates");
            Iterator it = m_initState.keySet().iterator();
            while(it.hasNext()) {
                out.openTag("row");
                out.addAttr("name", ((NamedState)it.next()).getName());
                out.closeTag();
            }
            out.closeTag();
        }
        if (m_input != null && m_input.keySet().size() > 0) {
            out.openTag("inputs");
            Iterator it = m_input.keySet().iterator();
            while(it.hasNext()) {
                out.openTag("row");
                out.addAttr("name", ((NamedState)it.next()).getName());
                out.closeTag();
            }
            out.closeTag();
        }

        // TODO: get the real associated perturbation
        Perturbation perturbation = null;
		if (perturbation != null) {
            out.openTag("mutant");
            out.addAttr("value", perturbation.toString());
            out.closeTag();
		}
		out.closeTag();
	}

    @Override
    public SimulationParameters clone() {
    	SimulationParameters newp = new SimulationParameters(param_list);
    	newp.simulationStrategy = simulationStrategy;
    	newp.name = name;
    	newp.maxdepth = maxdepth;
    	newp.maxnodes = maxnodes;
        // TODO: get the real associated perturbation
        Perturbation perturbation = null;
//    	newp.perturbation = perturbation;
    	newp.setPriorityDefinition(pcdef);

    	if (m_initState != null) {
    		newp.m_initState = new HashMap();
    		Iterator it = m_initState.keySet().iterator();
    		while (it.hasNext()) {
    			newp.m_initState.put(it.next(), null);
    		}
    	}
    	return newp;
    }

    @Override
	public String getName() {
		return name;
	}

    @Override
	public void setName(String name) {
		ObjectAssociationManager.getInstance().fireUserUpdate(param_list.graph, Reg2DynService.KEY, this.name, name);
		this.name = name;
	}

    @Override
	public Map getInitialState() {
		return m_initState;
	}
    @Override
    public Map getInputState() {
        return m_input;
    }

	public PrioritySetDefinition getPriorityClassDefinition() {
        return getPriorityDefinition();
	}

    public void copy_to(SimulationParameters other, Map mapping) {
        other.simulationStrategy = this.simulationStrategy;
        other.breadthFirst = this.breadthFirst;
        other.maxdepth = this.maxdepth;
        other.maxnodes = this.maxnodes;
        other.name = this.name;
        Iterator it = m_initState.keySet().iterator();
        while (it.hasNext()) {
            other.m_initState.put(mapping.get(it.next()), null);
        }
        it = m_input.keySet().iterator();
        while (it.hasNext()) {
            other.m_input.put(mapping.get(it.next()), null);
        }

        // TODO: get the real associated perturbation
        Perturbation perturbation = null;
//        other.perturbation = (Perturbation)mapping.get(perturbation);
        PrioritySetDefinition new_pcdef = (PrioritySetDefinition)mapping.get(pcdef);
        if (new_pcdef == null) {
            PrioritySetList new_pcman = (PrioritySetList)mapping.get("");
            if (pcdef.size() < 2) {
                PriorityClass pc = (PriorityClass)pcdef.get(0);
                if (pc.getMode() == PriorityClass.SYNCHRONOUS) {
                    new_pcdef = (PrioritySetDefinition)new_pcman.get(1);
                } else {
                    new_pcdef = (PrioritySetDefinition)new_pcman.get(0);
                }
            } else {
                LogManager.error( "[BUG] complex pcdef not transposed in the reduced model");
                new_pcdef = (PrioritySetDefinition)new_pcman.get(0);
            }
        }
        other.setPriorityDefinition(new_pcdef);
    }

    @Override
    public PrioritySetDefinition getPriorityDefinition() {
        if (pcdef == null) {
            pcdef = param_list.pcmanager.get(0);
        }
        return pcdef;
    }

    @Override
    public void setPriorityDefinition(PrioritySetDefinition pcdef) {
        this.pcdef = pcdef;
    }

}
