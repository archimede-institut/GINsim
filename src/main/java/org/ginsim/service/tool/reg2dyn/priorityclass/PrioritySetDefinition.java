package org.ginsim.service.tool.reg2dyn.priorityclass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.tool.simulation.updater.PriorityClasses;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.ListenableNamedList;
import org.ginsim.service.tool.reg2dyn.updater.BaseSimulationUpdater;
import org.ginsim.service.tool.reg2dyn.updater.SimulationUpdater;
import org.ginsim.service.tool.reg2dyn.updater.UpdaterDefinition;
import org.ginsim.service.tool.reg2dyn.updater.UpdaterDefinitionAsynchronous;
import org.ginsim.service.tool.reg2dyn.updater.UpdaterDefinitionSynchronous;

/**
 * Definition of a set of priority classes: store a list of classes
 * and assign all nodes to one of them.
 *
 * @author Aurelien Naldi
 */
public class PrioritySetDefinition extends ListenableNamedList<PriorityClass> implements UpdaterDefinition, XMLize {

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int NONE = 2;
    
	public Map<RegulatoryNode, Object> m_elt;
	private String name;

	public PrioritySetDefinition(List<RegulatoryNode> elts, String name) {
		setName(name);
		add();
		m_elt = new HashMap<RegulatoryNode, Object>();
		PriorityClass newclass = get(0);
		for (RegulatoryNode v: elts) {
			m_elt.put(v, newclass);
		}
	}

	@Override
	public SimulationUpdater getUpdater(LogicalModel model) {
		if (USE_BIOLQM_UPDATERS) {
			PriorityClasses pcs = null;
			// TODO: refactor the editing structure of priority classes to use bioLQM's updater 
//			MultipleSuccessorsUpdater lqmUpdater = new PriorityUpdater(model, pcs);
//			return new GenericSimulationUpdater(lqmUpdater);
		}
		return BaseSimulationUpdater.getInstance(model, this);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public void moveElementAt(int j, int pos) {
		moveElement(j, pos);
	}

    public void associate(RegulatoryNode node, PriorityClass cl) {
        m_elt.put(node, cl);
    }
    public void associate(RegulatoryNode node, PriorityClass clUp, PriorityClass clDown) {
        Object[] t = {clUp, clDown};
        m_elt.put(node, t);
    }

    private void moveElement(int j, int pos) {
        PriorityClass tmp = get(pos);
        set(pos, get(j));
        set(j, tmp);
    }

    @Override
    public PriorityClass remove(int idx) {
        if (idx < 0 || idx >= size()) {
            return null;
        }

        PriorityClass cl = get(idx);
        remove(new int[] {idx});
        return cl;
    }

    @Override
    public boolean remove(Object o) {
        int idx = indexOf(o);
        if (idx < 0) {
            return false;
        }

        remove(idx);
        return true;
    }

    /**
     * Remove a group of items
     *
     * @param t_index
     * @return
     */
    public boolean remove(int[] t_index) {
		if (t_index.length >= size()) {
			return false;
		}
		for (int i = t_index.length - 1 ; i > -1 ; i--) {
			int index = t_index[i];

            PriorityClass c = super.remove(index);
            if (index < size()) {
            	// update rank of the next priority classes
            	if ( index == 0 || ( get(index-1)).rank != c.rank) {
            		if (( get(index)).rank != c.rank) {
            			for (int j=index ; j<size() ; j++) {
            				( get(j)).rank--;
            			}
            		}
            	}
            }
            Set<RegulatoryNode> elts = m_elt.keySet();
            PriorityClass lastClass = get(size()-1);
            for (RegulatoryNode v: elts) {
                Object cl = m_elt.get(v); 
                if (cl == c) {
                    m_elt.put(v,lastClass);
                } else if (cl instanceof Object[]) {
                    Object[] t = (Object[])cl;
                    for (int j=0 ; j<t.length ; j++) {
                        if (t[j] == c) {
                            t[j] = lastClass;
                        }
                    }
                }
            }

            fireRemoved(c, t_index[i]);
		}
		
		return true;
	}

    public boolean moveSelection(int[] sel, int diff) {
        if (diff > 0) {
            return doMoveDown(sel, diff);
        } else {
            return doMoveUp(sel, diff);
        }
    }
	
    /**
     * move the whole selection up.
     * if some selected class are part of a group, the whole group will move with it.
     */
    private boolean doMoveUp(int[] selection, int diff) {
        int[][] index = getMovingRows(UP, selection);
        if (index == null) {
            return false;
        }
        
        int reselect = 0;
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = start+diff;
            int pr = get(start).rank;
            int prTarget = get(target).rank;
            target--;
            while (target >= 0 && get(target).rank == prTarget) {
                target--;
            }
            target++;
            for (int j=target ; j<start ; j++) {
                get(j).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
            	get(start+j).rank = prTarget;
            	moveElement(start+j, target+j);
            	if (reselect < selection.length && selection[reselect] == start+j) {
            		selection[reselect++] = target+j;
                }
            }
        }
        refresh();
        return true;
    }

    /**
     * move the whole selection down
     * if some selected class are part of a group, the whole group will move with it.
     */
    private boolean doMoveDown(int[] selection, int diff) {
        int[][] index = getMovingRows(DOWN, selection);
        if (index == null) {
            return false;
        }
        
        int reselect = 0;
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = stop+diff;
            int pr = get(start).rank;
            int prTarget = get(target).rank;
            target++;
            while (target < size() && get(target).rank == prTarget) {
                target++;
            }
            target--;
            for (int j=stop+1 ; j<=target ; j++) {
                get(j).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
                get(start).rank = prTarget;
                moveElement(start, target);
                if (reselect < selection.length && selection[reselect] == start+j) {
            		selection[reselect++] = target-stop+start+j;
                }
            }
        }
        refresh();
        return true;
    }
    
    /**
     * when moving a selection of class, they must move with other class of the same priority.
     * this checks the selection and compute a list of all really moving rows as ranges: start-stop for each selected clas
     * @param key
     * @param index 
     * @return moving ranges or null if nothing should move
     */
    public int[][] getMovingRows(int key, int[] index) {
        if (index == null) {
        	return null;
        }
        int end = size();
        int count = 0;
        int lastPriority = -1;
        for (int i=0 ; i<index.length ; i++) {
            int priority = get(index[i]).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && get(start).rank == priority) {
                    start--;
                }
                while(stop < end && get(stop).rank == priority) {
                    stop++;
                }
                start++;
                stop--;
                // if moving up and already on top or moving down and already on bottom: don't do anything
                if (key==UP && start == 0 || key==DOWN && stop == end-1) {
                    return null;
                }
                count++;
                lastPriority = priority;
            }
        }
        
        int[][] ret = new int[count][3];
        lastPriority = -1;
        count = 0;
        for (int i=0 ; i<index.length ; i++) {
            int priority = get(index[i]).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && get(start).rank == priority) {
                    start--;
                }
                while(stop < end && get(stop).rank == priority) {
                    stop++;
                }
                start++;
                stop--;
                ret[count][0] = start;
                ret[count][1] = stop;
                lastPriority = priority;
                count++;
            }
        }
        return ret;
    }

	@Override
	public void toXML(XMLWriter out) throws IOException {
		out.openTag("priorityClassList");
		out.addAttr("id", name);
		StringBuffer s_tmp;
		for (int i=0 ; i< size(); i++) {
			out.openTag("class");
            PriorityClass pc = get(i);
            out.addAttr("name", pc.getName());
            out.addAttr("mode", ""+pc.getMode());
            out.addAttr("rank", ""+pc.rank);
			s_tmp = new StringBuffer();
            for (Entry<RegulatoryNode, Object> e: m_elt.entrySet()) {
            	RegulatoryNode v = e.getKey();
                Object oc = e.getValue();
                if (oc instanceof PriorityClass) {
                    if (oc == pc) {
                        s_tmp.append(v+" ");
                    }
                } else if (oc instanceof Object[]) {
                    Object[] t = (Object[])oc;
                    for (int j=0 ; j<t.length ; j++) {
                        if (t[j] == pc) {
                            s_tmp.append(v+","+(j==0 ? "+" : "-")+" ");
                        }
                    }
                }
            }
			out.addAttr("content", s_tmp.toString());
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
     *  - and all others are couples: index of node in the nodeOrder followed by transition filter.
     *    the "transition filter" is a bit hacky: add it to your transition (which should be either +1 or -1)
     *    and if the result is zero (0), then this transition shouldn't be followed.
     *
     * shortly: it is 0 for all transitions, 1 for negative transitions and -1 for positive ones
     */
    public int[][] getPclass(List<NodeInfo> nodeOrder) {

        Integer zaroo = new Integer(0);
        Integer one = new Integer(1);
        Integer minusOne = new Integer(-1);

        // it is done by browsing twice the list:
        //   - during the first pass asynchronous classes with the same priority are merged
        //   - then the real int[][] is created from the merged classes
		List<List<Integer>> v_vpclass = new ArrayList<List<Integer>>();
        for (PriorityClass pc: this) {
            List<Integer> v_content;
            if (pc.getMode() == PriorityClass.ASYNCHRONOUS) {
                v_content = new ArrayList<Integer>();
                v_content.add(pc.rank);
                v_content.add(pc.getMode());
                v_vpclass.add(v_content);
            } else {
                v_content = new ArrayList<Integer>();
                v_content.add(pc.rank);
                v_content.add(pc.getMode());
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
                            v_content.add(n);
                            v_content.add(zaroo);
                        } else {
                            v_content.add(n);
                            v_content.add(one);
                        }
                    } else if (t[1] == pc) {
                        v_content.add(n);
                        v_content.add(minusOne);
                    }
                } else { // +1 and -1 aren't separated, always accept every transitions
                    if (target == pc) {
                        v_content.add(n);
                        v_content.add(zaroo);
                    }
                }
            }
        }

        int[][] pclass = new int[v_vpclass.size()][];
        for (int i=0 ; i<pclass.length ; i++) {
            List<Integer> v_content = v_vpclass.get(i);
            int[] t = new int[v_content.size()];
            t[0] = ((Integer)v_content.get(0)).intValue();
            if (v_content.size() > 1) {
                t[1] = ((Integer)v_content.get(1)).intValue();
            } else {
                // if only one node in the class, async mode is useless!
                t[1] = PriorityClass.SYNCHRONOUS;
            }
            for (int n=2 ; n<t.length ; n++) {
                t[n] = ((Integer)v_content.get(n)).intValue();
            }
            pclass[i] = t;
        }
        return pclass;
    }

	/**
     * @return the compiled priority class.
     * @see <code>getPclass</code>
     */
    public int[][] getPclassNew(List<NodeInfo> nodeInfos) {

    	Map<NodeInfo, RegulatoryNode> m_info2node = new HashMap<NodeInfo, RegulatoryNode>();
    	for (RegulatoryNode node: m_elt.keySet()) {
    		for (NodeInfo ni: nodeInfos) {
    			if (ni.equals(node)) {
    				m_info2node.put(ni, node);
    				break;
    			}
    		}
    	}

    	List<NodeInfo> nodeOrder = new ArrayList<NodeInfo>();
		for (NodeInfo ni: nodeInfos) {
			nodeOrder.add(ni);
			if (ni == null) {
				LogManager.debug("No matching RegulatoryNode for "+ni);
			}
		}

    	return getPclass(nodeOrder);
    }

    public boolean match(String filter) {
        return this.getName().toLowerCase().indexOf(filter.toLowerCase()) >= 0;
    }

    private void refresh() {
        fireChanged();
    }

    public int add() {
        return add(size());
    }

    public int add(int i) {
        int len = size();
        if (i>len || i<0) {
            return -1;
        }

        String name = findUniqueName("class ");
        int priority = 0;
        if (i == len) {
            if (len > 0) {
                priority = get(len-1).rank;
            }
        } else {
            priority = get(i).rank;
        }

        // move to the end of the group if needed
        for ( ; i < len ; i++) {
            if ((get(i)).rank != priority) {
                break;
            }
        }

        // create and insert the new class
        PriorityClass pc = new PriorityClass(priority+1, name);
        super.add(i, pc);

        int idx = i;

        // increase the rank of the next classes
        for ( i++; i<len ; i++) {
            (get(i)).rank++;
        }
        refresh();

        return idx;
    }

    /**
     * toggle the selection grouping:
     *    - if all selected items are part of the same group, it will be "ungrouped"
     *    - if selected items are part of several groups, they will be merged with the first one
     */
    public void groupToggle(int[] ts) {
        int[][] selExtended = getMovingRows(NONE, ts);
        // if class with different priorities are selected: give them all the same priority
        if (selExtended.length < 1) {
            return;
        }
        if (selExtended.length > 1) {
            if (ts == null || ts.length < 1) {
                return;
            }
            int pos = selExtended[0][1];
            int pr = get(pos).rank;
            for (int i=1 ; i<selExtended.length ; i++) {
                for (int j=selExtended[i-1][1]+1 ; j<selExtended[i][0] ; j++) {
                    get(j).rank -= i-1;
                }
                for (int j=selExtended[i][0] ; j<=selExtended[i][1] ; j++) {
                    pos++;
                    get(j).rank = pr;
                    moveElementAt(j, pos);
                }
            }
            int l = selExtended.length - 1;
            for (int j=selExtended[l][1]+1 ; j<size() ; j++) {
                get(j).rank -= l;
            }
        } else {
            if (selExtended[0][0] != selExtended[0][1]) {
                int i = selExtended[0][0];
                int inc = 1;
                for (i++ ; i<selExtended[0][1] ; i++) {
                    get(i).rank += inc;
                    inc++;
                }
                for ( ; i<size() ; i++) {
                    get(i).rank += inc;
                }
            }
        }
        refresh();
    }

	@Override
	public String summary(List<NodeInfo> nodeOrder) {
        if (size() == 1) {
        	int mode = get(0).getMode();
        	if (mode == PriorityClass.ASYNCHRONOUS) {
        		return UpdaterDefinitionAsynchronous.DEFINITION.summary(nodeOrder);
        	}
    		return UpdaterDefinitionSynchronous.DEFINITION.summary(nodeOrder);
        }
        String s = getName();
        s += "by priority class\n";
        int[][] pclass = getPclass(nodeOrder);
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

		return s;
	}
}
