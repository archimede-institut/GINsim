package org.ginsim.gui.service.tool.reg2dyn;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.graph.GraphManager;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitStateTableModel;

import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.datastore.NamedObject;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;

/**
 * remember, save and restore a simulation parameter.
 */
public class SimulationParameters implements XMLize, NamedObject, InitialStateStore {

	public static final int MUTANT = 0;
	public static final int PCLASS = 1;
		
	public static final int STRATEGY_STG = 0;
	public static final int STRATEGY_SCCG = 1;
	public static final int STRATEGY_HTG = 2;

	public String name = "new_parameter";
    public List nodeOrder;

    public int maxdepth;
    public int maxnodes;
    public boolean breadthFirst = false;
    public int simulationStrategy = STRATEGY_STG;

    public ObjectStore store = new ObjectStore(2);
    public Map m_initState = new HashMap();
    public Map m_input = new HashMap();
    public SimulationParameterList param_list;

    /**
     * empty constructor for everyday use.
     * @param param_list
     */
    public SimulationParameters(SimulationParameterList param_list) {
    	this.param_list = param_list;
        this.nodeOrder = param_list.graph.getNodeOrder();
        store.setObject(PCLASS, param_list.pcmanager.getElement(null, 0));
    }

    public String toString() {
        return name;
    }

    /**
     * get priority class.
     *
     * @return a list listing all priority classes.
     *
     * see also <code>getMelt</code> to get association between nodes and classes
     */
    public List getVclass() {
    	PriorityClassDefinition pcdef = getPriorityClassDefinition();
    	return pcdef.v_data;
    }

    /**
     * a not so simple toString method.
     * It shows the full content of this simu parameters, used to add a usefull comment to the state transition graph.
     * @return a human readable description of the parameter
     */

    public String getDescr() {
        String name = param_list.graph.getGraphName();
        String saveName = GraphManager.getInstance().getGraphPath( param_list.graph);
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
		PriorityClassDefinition pcdef = (PriorityClassDefinition)store.getObject(PCLASS);
		if (pcdef != null) {
	        if (pcdef.getNbElements(null) > 1) {
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
                Map m_init = ((InitialState)it.next()).getMap();
                s += "\n      ";
                for (int j=0 ; j<nodeOrder.size() ; j++) {
                    RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(j);
                    s += "  "+InitStateTableModel.showValue((List)m_init.get(vertex), vertex.getMaxValue());
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
                InitialState init = (InitialState)it.next();
                if (init != null) {
                    Map m_init = init.getMap();
                    s += "\n      ";
                    for (int j=0 ; j<nodeOrder.size() ; j++) {
                        RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(j);
                        s += "  "+InitStateTableModel.showValue((List)m_init.get(vertex), vertex.getMaxValue());
                    }
                }
            }
            s += "\n";
        }

        if (store.getObject(MUTANT) != null) {
            s += "    Mutant: "+store.getObject(MUTANT).toString()+"\n";
        }
        if (maxdepth != 0) {
            s += "    Max depth: "+maxdepth+"\n";
        }
        if (maxnodes != 0) {
            s += "    Max nodes: "+maxnodes+"\n";
        }
        return s;
    }

	public void toXML(XMLWriter out, Object param, int xmlmode) throws IOException {
		PriorityClassDefinition pcdef = (PriorityClassDefinition)store.getObject(PCLASS);
		out.openTag("parameter");
		out.addAttr("name", name);
		out.addAttr("updating", pcdef.name);
		out.addAttr("breadthFirst", ""+breadthFirst);
		out.addAttr("maxdepth", ""+maxdepth);
		out.addAttr("maxnodes", ""+maxnodes);
		out.addAttr("simulationStrategy", ""+simulationStrategy);

		if (pcdef.getNbElements(null) > 1) {
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
                out.addAttr("name", ((InitialState)it.next()).getName());
                out.closeTag();
            }
            out.closeTag();
        }
        if (m_input != null && m_input.keySet().size() > 0) {
            out.openTag("inputs");
            Iterator it = m_input.keySet().iterator();
            while(it.hasNext()) {
                out.openTag("row");
                out.addAttr("name", ((InitialState)it.next()).getName());
                out.closeTag();
            }
            out.closeTag();
        }
		if (store.getObject(MUTANT) != null) {
            out.openTag("mutant");
            out.addAttr("value", store.getObject(MUTANT).toString());
            out.closeTag();
		}
		out.closeTag();
	}

    public Object clone() {
    	SimulationParameters newp = new SimulationParameters(param_list);
    	newp.simulationStrategy = simulationStrategy;
    	newp.name = name;
    	newp.maxdepth = maxdepth;
    	newp.maxnodes = maxnodes;
    	newp.store.setObject(MUTANT, store.getObject(MUTANT));
    	newp.store.setObject(PCLASS, store.getObject(PCLASS));

    	if (m_initState != null) {
    		newp.m_initState = new HashMap();
    		Iterator it = m_initState.keySet().iterator();
    		while (it.hasNext()) {
    			newp.m_initState.put(it.next(), null);
    		}
    	}
    	return newp;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map getInitialState() {
		return m_initState;
	}
    public Map getInputState() {
        return m_input;
    }

	public PriorityClassDefinition getPriorityClassDefinition() {
		PriorityClassDefinition pcd = (PriorityClassDefinition)store.getObject(PCLASS);
		if (pcd == null) {
			pcd = (PriorityClassDefinition)param_list.pcmanager.getElement(null, 0);
			store.setObject(PCLASS, pcd);
		}
		return pcd;
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
        other.store.setObject(MUTANT, mapping.get(store.getObject(MUTANT)));
        PriorityClassDefinition pcdef = (PriorityClassDefinition)store.getObject(PCLASS);
        PriorityClassDefinition new_pcdef = (PriorityClassDefinition)mapping.get(pcdef);
        if (new_pcdef == null) {
            PriorityClassManager new_pcman = (PriorityClassManager)mapping.get("");
            if (pcdef.getNbElements(null) < 2) {
                Reg2dynPriorityClass pc = (Reg2dynPriorityClass)pcdef.getElement(null,0);
                if (pc.getMode() == Reg2dynPriorityClass.SYNCHRONOUS) {
                    new_pcdef = (PriorityClassDefinition)new_pcman.getElement(null, 1);
                } else {
                    new_pcdef = (PriorityClassDefinition)new_pcman.getElement(null, 0);
                }
            } else {
                Debugger.error( "[BUG] complex pcdef not transposed in the reduced model");
                new_pcdef = (PriorityClassDefinition)new_pcman.getElement(null, 0);
            }
        }
        other.store.setObject(PCLASS, new_pcdef);
    }
}
