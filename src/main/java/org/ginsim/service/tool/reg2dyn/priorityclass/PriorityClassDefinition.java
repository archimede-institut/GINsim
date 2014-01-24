package org.ginsim.service.tool.reg2dyn.priorityclass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.ListenableNamedList;
import org.ginsim.core.utils.data.NamedObject;


public class PriorityClassDefinition extends ListenableNamedList<Reg2dynPriorityClass> implements NamedObject, XMLize {

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int NONE = 2;
    

	
	public Map<RegulatoryNode,Object> m_elt;
	String name;
	boolean locked;

	public PriorityClassDefinition(List<RegulatoryNode> elts, String name) {
/*
		canAdd = true;
		canRemove = true;
		canOrder = true;
		canEdit = true;
		prefix = "class_";
		nbcol = 3;
		addWithPosition = true;

		Class[] t = {Integer.class, Boolean.class, String.class};
		t_type = t;
*/
		setName(name);
		add();
		m_elt = new HashMap();
		Reg2dynPriorityClass newclass = get(0);
		for (RegulatoryNode v: elts) {
			m_elt.put(v, newclass);
		}
	}

	@Override
	public void setName(String name) {
		if (locked) {
			return;
		}
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
		if (locked) {
			return;
		}
		moveElement(j, pos);
	}

    private void moveElement(int j, int pos) {
        // TODO: implement or delegate moving elements
    }

    // TODO: move col names to the helper
	public String getColName(int col) {
		switch (col) {
			case 0:
				return "Rank";
			case 1:
				return "Sync";
			case 2:
				return "Name";
			default:
				return null;
		}
	}

    // TODO: call the proper remove method
	public boolean remove(int[] t_index) {
		if (locked || t_index.length >= size()) {
			return false;
		}
		for (int i = t_index.length - 1 ; i > -1 ; i--) {
			int index = t_index[i];
			
            Reg2dynPriorityClass c = remove(index);
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
            Reg2dynPriorityClass lastClass = get(size()-1);
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
		if (locked) {
			return false;
		}
        int[][] index = getMovingRows(UP, selection);
        if (index == null) {
            return false;
        }
        
        int reselect = 0;
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = start+diff;
            int pr = (get(start)).rank;
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
		if (locked) {
			return false;
		}
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
            Reg2dynPriorityClass pc = get(i);
            out.addAttr("name", pc.getName());
            out.addAttr("mode", ""+pc.getMode());
            out.addAttr("rank", ""+pc.rank);
			s_tmp = new StringBuffer();
            for (Entry<RegulatoryNode, Object> e: m_elt.entrySet()) {
            	RegulatoryNode v = e.getKey();
                Object oc = e.getValue();
                if (oc instanceof Reg2dynPriorityClass) {
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
        for (Reg2dynPriorityClass pc: this) {
            List<Integer> v_content;
            if (pc.getMode() == Reg2dynPriorityClass.ASYNCHRONOUS) {
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
                t[1] = Reg2dynPriorityClass.SYNCHRONOUS;
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

	public void lock() {
		this.locked = true;
		for (Reg2dynPriorityClass pc : this) {
			pc.lock();
		}
	}

    public boolean match(String filter) {
        return this.getName().toLowerCase().indexOf(filter.toLowerCase()) >= 0;
    }


    public void refresh() {
        // TODO: implement refresh
    }
    public int add() {
        return add(size());
    }

    public int add(int i) {
        int len = size();
        if (locked || i>len || i<0) {
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
        Reg2dynPriorityClass pc = new Reg2dynPriorityClass(priority+1, name);
        super.add(i, pc);

        int idx = i;

        // increase the rank of the next classes
        for ( i++; i<len ; i++) {
            (get(i)).rank++;
        }
        refresh();

        return idx;
    }
}
