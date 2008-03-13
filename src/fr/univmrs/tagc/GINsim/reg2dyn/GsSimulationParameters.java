package fr.univmrs.tagc.GINsim.reg2dyn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitStateTableModel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.tagc.common.datastore.NamedObject;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;

/**
 * remember, save and restore a simulation parameter.
 */
public class GsSimulationParameters implements XMLize, NamedObject, GsInitialStateStore {

	static final int MUTANT = 0;
	static final int PCLASS = 1;
	
    String name = "new_parameter";
    List nodeOrder;

    int mode = Simulation.SEARCH_ASYNCHRONE_DF;

    int maxdepth;
    int maxnodes;
    boolean buildSTG = true;

    ObjectStore store = new ObjectStore(2);

    Map m_initState = new HashMap();
	GsSimulationParameterList param_list;

    /**
     * empty constructor for everyday use.
     * @param nodeOrder
     */
    public GsSimulationParameters(GsSimulationParameterList param_list) {
    	this.param_list = param_list;
        this.nodeOrder = param_list.graph.getNodeOrder();
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
    	PriorityClassDefinition pcdef = getPriorityClassDefinition(true);
    	return pcdef.v_data;
    }

    /**
     * a not so simple toString method.
     * It shows the full content of this simu parameters, used to add a usefull comment to the state transition graph.
     * @return a human readable description of the parameter
     */
    public String getDescr() {
        String s;
        switch (mode) {
            case Simulation.SEARCH_ASYNCHRONE_BF:
                s = "asynchronous (BF)\n";
                break;
            case Simulation.SEARCH_ASYNCHRONE_DF:
                s = "asynchronous (DF)\n";
                break;
            case Simulation.SEARCH_BYPRIORITYCLASS:
                s = "by priority class\n";
                int[][] pclass = getPriorityClassDefinition(true).getPclass(nodeOrder);
                for (int i=0 ; i<pclass.length ; i++) {
                    int[] cl = pclass[i];
                    s += "   "+cl[0]+ (cl[1]==0?" sync":" async")+": ";
                    for (int j=2;j<cl.length ; j+=2) {
                        if (j>2) {
                            s += ", ";
                        }
                        s += nodeOrder.get(cl[j])+(cl[j+1]==0?"":cl[j+1]==1?"+":"-");
                    }
                    s += "\n";
                }
                break;
            case Simulation.SEARCH_SYNCHRONE:
                s = "synchronous\n";
                break;
            default:
                s = "";
        }
        // FIXME: descr initial states
        if (m_initState == null || m_initState.size()==0) {
            s += "full graph";
        } else {
            s += "initial states:\n";
            Iterator it = m_initState.keySet().iterator();
            while (it.hasNext()) {
            	// FIXME: get REAL initstate
                Map m_init = ((GsInitialState)it.next()).getMap();
                for (int j=0 ; j<nodeOrder.size() ; j++) {
                    GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(j);
                    s += "  "+GsInitStateTableModel.showValue((List)m_init.get(vertex), vertex.getMaxValue());
                }
                s += "\n";
            }
        }

        if (store.getObject(MUTANT) != null) {
            s += "\nMutant:\n"+store.getObject(MUTANT).toString();
        }

        if (maxdepth != 0) {
            s += "max depth: "+maxdepth+"\n";
        }
        if (maxnodes != 0) {
            s += "max nodes: "+maxnodes+"\n";
        }
        return s;
    }

	public void toXML(XMLWriter out, Object param, int xmlmode) throws IOException {
		out.openTag("parameter");
		out.addAttr("name", name);
		out.addAttr("mode", Simulation.MODE_NAMES[mode]);
		out.addAttr("maxdepth", ""+maxdepth);
		out.addAttr("maxnodes", ""+maxnodes);

		if (mode == Simulation.SEARCH_BYPRIORITYCLASS) {
			out.openTag("priorityClass");
			out.addAttr("ref", ((NamedObject)store.getObject(PCLASS)).getName());
			out.closeTag();
		}
		if (m_initState != null && m_initState.keySet().size() > 0) {
			out.openTag("initstates");
			Iterator it = m_initState.keySet().iterator();
			while(it.hasNext()) {
				out.openTag("row");
				out.addAttr("name", ((GsInitialState)it.next()).getName());
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
    	GsSimulationParameters newp = new GsSimulationParameters(param_list);
    	newp.name = name;
    	newp.mode = mode;
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

	public PriorityClassDefinition getPriorityClassDefinition(boolean b) {
		PriorityClassDefinition pcd = (PriorityClassDefinition)store.getObject(PCLASS);
		if (b && pcd == null) {
			int index = param_list.pcmanager.add();
			pcd = (PriorityClassDefinition)param_list.pcmanager.getElement(null, index);
			store.setObject(PCLASS, pcd);
		}
		return pcd;
	}
}
