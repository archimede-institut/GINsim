package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.io.IOException;
import java.util.*;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitStateTableModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.tagc.datastore.NamedObject;
import fr.univmrs.tagc.datastore.ObjectStore;
import fr.univmrs.tagc.xml.XMLWriter;
import fr.univmrs.tagc.xml.XMLize;

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
     *
     * @return a map giving associations between nodes (the keys) and priority classes
     * use <code>getVclass</code> to get the list of priority classes
     */
    public Map getMelt() {
    	PriorityClassDefinition pcdef = getPriorityClassDefinition(true);
    	return pcdef.m_elt;
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
                int[][] pclass = getPclass();
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

    /**
     * @return the compiled priority class.
     * in the form of an int[][]
     * each int[] represent a priority class:
     *  - the very first int is the class' priority
     *  - the second int is the class' mode (sync or async)
     *  - and all others are couples: index of vertex in the nodeOrder followed by transition filter.
     *    the "transition filter" is a bit hacky: add it to your transition (which should be either +1 or -1)
     *    and if the result is zero (0), then this transition shouldn't be followed.
     *
     * shortly: it is 0 for all transitions, 1 for negative transitions and -1 for positive ones
     */
    public int[][] getPclass() {

        Integer zaroo = new Integer(0);
        Integer one = new Integer(1);
        Integer minusOne = new Integer(-1);

        // it is done by browsing twice the list:
        //   - during the first pass asynchronous classes with the same priority are merged
        //   - then the real int[][] is created from the merged classes

		List v_class = getVclass();
		Map m_elt = getMelt();
		List v_vpclass = new ArrayList();
        for (int i=0 ; i<v_class.size() ; i++) {
            GsReg2dynPriorityClass pc = (GsReg2dynPriorityClass)v_class.get(i);
            List v_content;
            if (pc.getMode() == GsReg2dynPriorityClass.ASYNCHRONOUS) {
                v_content = new ArrayList();
                v_content.add(new Integer(pc.rank));
                v_content.add(new Integer(pc.getMode()));
                v_vpclass.add(v_content);
            } else {
                v_content = new ArrayList();
                v_content.add(new Integer(pc.rank));
                v_content.add(new Integer(pc.getMode()));
                v_vpclass.add(v_content);
            }
            for (int n=0 ; n<nodeOrder.size() ; n++) {
                Object k = nodeOrder.get(n);
                Object target = m_elt.get(k);
                // if +1 and -1 are separated, target is an Object[]
                if (target instanceof Object[]) {
                    Object[] t = (Object[])target;
                    if (t[0] == pc) {
                        // to do it right: if both +1 and -1 are in the same class, add the node only once :)
                        if (t[1] == pc) {
                            v_content.add(new Integer(n));
                            v_content.add(zaroo);
                        } else {
                            v_content.add(new Integer(n));
                            v_content.add(one);
                        }
                    } else if (t[1] == pc) {
                        v_content.add(new Integer(n));
                        v_content.add(minusOne);
                    }
                } else { // +1 and -1 aren't separated, always accept every transitions
                    if (target == pc) {
                        v_content.add(new Integer(n));
                        v_content.add(zaroo);
                    }
                }
            }
        }

        int[][] pclass = new int[v_vpclass.size()][];
        for (int i=0 ; i<pclass.length ; i++) {
            List v_content = (List)v_vpclass.get(i);
            int[] t = new int[v_content.size()];
            t[0] = ((Integer)v_content.get(0)).intValue();
            if (v_content.size() > 1) {
                t[1] = ((Integer)v_content.get(1)).intValue();
            } else {
                // if only one node in the class, async mode is useless!
                t[1] = GsReg2dynPriorityClass.SYNCHRONOUS;
            }
            for (int n=2 ; n<t.length ; n++) {
                t[n] = ((Integer)v_content.get(n)).intValue();
            }
            pclass[i] = t;
        }
        return pclass;
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
