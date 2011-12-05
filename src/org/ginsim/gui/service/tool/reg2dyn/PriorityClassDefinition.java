package org.ginsim.gui.service.tool.reg2dyn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.utils.data.GenericListListener;
import org.ginsim.utils.data.NamedObject;
import org.ginsim.utils.data.SimpleGenericList;



public class PriorityClassDefinition extends SimpleGenericList<Reg2dynPriorityClass> implements NamedObject, XMLize {

	public Map<RegulatoryNode,Object> m_elt;
	String name;
	boolean locked;

	public PriorityClassDefinition(List<RegulatoryNode> elts, String name) {
		canAdd = true;
		canRemove = true;
		canOrder = true;
		canEdit = true;
		prefix = "class_";
		nbcol = 3;
		addWithPosition = true;
		Class[] t = {Integer.class, Boolean.class, String.class};
		t_type = t;
		setName(name);
		add();
		m_elt = new HashMap();
		Reg2dynPriorityClass newclass = (Reg2dynPriorityClass)getElement(null, 0);
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
	@Override
	public Reg2dynPriorityClass doCreate(String name, int pos, int mode) {
		if (locked) {
			return null;
		}
		int priority;
		int i = pos;
		int len = getNbElements(null);
		if (pos<0 || pos >= len) {
			i = len == 0 ? 0 : len-1;
		}
		priority = len == 0 ? 0: ((Reg2dynPriorityClass)v_data.get(i)).rank;
        for ( ; i < len ; i++) {
            if (((Reg2dynPriorityClass)v_data.get(i)).rank != priority) {
                break;
            }
        }
        v_data.add(i, new Reg2dynPriorityClass(priority+1, name));
        for ( i++; i<len ; i++) {
            ((Reg2dynPriorityClass)v_data.get(i)).rank++;
        }
        refresh();
        return null;
	}
	
	@Override
	public String getColName(int col) {
		switch (col) {
			case 0:
				return "Rank";
			case 1:
				return "Sync";
			case 2:
				return "Name";
			default:
				return super.getColName(col);
		}
	}

	@Override
	public boolean remove(String filter, int[] t_index) {
		if (locked || t_index.length >= v_data.size()) {
			return false;
		}
		for (int i = t_index.length - 1 ; i > -1 ; i--) {
			int index = getRealIndex(filter, t_index[i]);
			
            Reg2dynPriorityClass c = (Reg2dynPriorityClass) v_data.remove(index);
            if (index < v_data.size()) {
            	// update rank of the next priority classes
            	if ( index == 0 || ((Reg2dynPriorityClass) v_data.get(index-1)).rank != c.rank) {
            		if (((Reg2dynPriorityClass) v_data.get(index)).rank != c.rank) {
            			for (int j=index ; j<v_data.size() ; j++) {
            				((Reg2dynPriorityClass) v_data.get(j)).rank--;
            			}
            		}
            	}
            }
            Set<RegulatoryNode> elts = m_elt.keySet();
            Reg2dynPriorityClass lastClass = v_data.get(v_data.size()-1);
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
			
			if (v_listeners != null) {
				for (GenericListListener l: v_listeners) {
					l.itemRemoved(c, t_index[i]);
				}
			}
		}
		return true;
	}

	
    /**
     * move the whole selection up.
     * if some selected class are part of a group, the whole group will move with it.
     */
	@Override
    protected void doMoveUp(int[] selection, int diff) {
		if (locked) {
			return;
		}
        int[][] index = getMovingRows(Reg2dynPriorityClassConfig.UP, selection);
        if (index == null) {
            return;
        }
        
        int reselect = 0;
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = start+diff;
            int pr = ((Reg2dynPriorityClass)v_data.get(start)).rank;
            int prTarget = ((Reg2dynPriorityClass)v_data.get(target)).rank;
            target--;
            while (target >= 0 && ((Reg2dynPriorityClass)v_data.get(target)).rank == prTarget) {
                target--;
            }
            target++;
            for (int j=target ; j<start ; j++) {
                ((Reg2dynPriorityClass)v_data.get(j)).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
            	((Reg2dynPriorityClass)v_data.get(start+j)).rank = prTarget;
            	moveElement(start+j, target+j);
            	if (reselect < selection.length && selection[reselect] == start+j) {
            		selection[reselect++] = target+j;
                }
            }
        }
        refresh();
    }

    /**
     * move the whole selection down
     * if some selected class are part of a group, the whole group will move with it.
     */
	@Override
    protected void doMoveDown(int[] selection, int diff) {
		if (locked) {
			return;
		}
        int[][] index = getMovingRows(Reg2dynPriorityClassConfig.DOWN, selection);
        if (index == null) {
            return;
        }
        
        int reselect = 0;
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = stop+diff;
            int pr = v_data.get(start).rank;
            int prTarget = v_data.get(target).rank;
            target++;
            while (target < v_data.size() && v_data.get(target).rank == prTarget) {
                target++;
            }
            target--;
            for (int j=stop+1 ; j<=target ; j++) {
                v_data.get(j).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
                v_data.get(start).rank = prTarget;
                moveElement(start, target);
                if (reselect < selection.length && selection[reselect] == start+j) {
            		selection[reselect++] = target-stop+start+j;
                }
            }
        }
        refresh();
    }
    
    /**
     * when moving a selection of class, they must move with other class of the same priority.
     * this checks the selection and compute a list of all really moving rows as ranges: start-stop for each selected clas
     * @param key
     * @param index 
     * @return moving ranges or null if nothing should move
     */
    int[][] getMovingRows(int key, int[] index) {
        if (index == null) {
        	return null;
        }
        int end = v_data.size();
        int count = 0;
        int lastPriority = -1;
        for (int i=0 ; i<index.length ; i++) {
            int priority = ((Reg2dynPriorityClass)v_data.get(index[i])).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && ((Reg2dynPriorityClass)v_data.get(start)).rank == priority) {
                    start--;
                }
                while(stop < end && ((Reg2dynPriorityClass)v_data.get(stop)).rank == priority) {
                    stop++;
                }
                start++;
                stop--;
                // if moving up and already on top or moving down and already on bottom: don't do anything
                if (key==Reg2dynPriorityClassConfig.UP && start == 0 || key==Reg2dynPriorityClassConfig.DOWN && stop == end-1) {
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
            int priority = ((Reg2dynPriorityClass)v_data.get(index[i])).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && ((Reg2dynPriorityClass)v_data.get(start)).rank == priority) {
                    start--;
                }
                while(stop < end && ((Reg2dynPriorityClass)v_data.get(stop)).rank == priority) {
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
	public void toXML(XMLWriter out, Object param, int mode)
			throws IOException {
		out.openTag("priorityClassList");
		out.addAttr("id", name);
		StringBuffer s_tmp;
		for (int i=0 ; i< v_data.size(); i++) {
			out.openTag("class");
            Reg2dynPriorityClass pc = (Reg2dynPriorityClass)v_data.get(i);
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
     * bytely: it is 0 for all transitions, 1 for negative transitions and -1 for positive ones
     */
    public int[][] getPclass(List<RegulatoryNode> nodeOrder) {

        Integer zaroo = new Integer(0);
        Integer one = new Integer(1);
        Integer minusOne = new Integer(-1);

        // it is done by browsing twice the list:
        //   - during the first pass asynchronous classes with the same priority are merged
        //   - then the real int[][] is created from the merged classes
		List<List<Integer>> v_vpclass = new ArrayList<List<Integer>>();
        for (int i=0 ; i<v_data.size() ; i++) {
            Reg2dynPriorityClass pc = (Reg2dynPriorityClass)v_data.get(i);
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

	public void lock() {
		this.locked = true;
		for (Reg2dynPriorityClass pc : v_data) {
			pc.lock();
		}
	}
}
