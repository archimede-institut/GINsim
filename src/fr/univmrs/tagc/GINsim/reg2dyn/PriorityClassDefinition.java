package fr.univmrs.tagc.GINsim.reg2dyn;

import java.io.IOException;
import java.util.*;

import fr.univmrs.tagc.common.datastore.GenericListListener;
import fr.univmrs.tagc.common.datastore.NamedObject;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;
import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;


public class PriorityClassDefinition extends SimpleGenericList implements NamedObject, XMLize {

	public Map m_elt;
	String name;
	boolean locked;

	public PriorityClassDefinition(Iterator it, String name) {
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
		GsReg2dynPriorityClass newclass = (GsReg2dynPriorityClass)getElement(null, 0);
		while (it.hasNext()) {
			m_elt.put(it.next(), newclass);
		}
	}
	
	public void setName(String name) {
		if (locked) {
			return;
		}
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public String toString() {
		return name;
	}

	public void moveElementAt(int j, int pos) {
		if (locked) {
			return;
		}
		moveElement(j, pos);
	}
	public Object doCreate(String name, int pos, int mode) {
		if (locked) {
			return null;
		}
		int priority;
		int i = pos;
		int len = getNbElements(null);
		if (pos<0 || pos >= len) {
			i = len == 0 ? 0 : len-1;
		}
		priority = len == 0 ? 0: ((GsReg2dynPriorityClass)v_data.get(i)).rank;
        for ( ; i < len ; i++) {
            if (((GsReg2dynPriorityClass)v_data.get(i)).rank != priority) {
                break;
            }
        }
        v_data.add(i, new GsReg2dynPriorityClass(priority+1, name));
        for ( i++; i<len ; i++) {
            ((GsReg2dynPriorityClass)v_data.get(i)).rank++;
        }
        refresh();
        return null;
	}
	
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

	public boolean remove(String filter, int[] t_index) {
		if (locked || t_index.length >= v_data.size()) {
			return false;
		}
		for (int i = t_index.length - 1 ; i > -1 ; i--) {
			int index = getRealIndex(filter, t_index[i]);
			
            GsReg2dynPriorityClass c = (GsReg2dynPriorityClass) v_data.remove(index);
            if (index < v_data.size()) {
            	// update rank of the next priority classes
            	if ( index == 0 || ((GsReg2dynPriorityClass) v_data.get(index-1)).rank != c.rank) {
            		if (((GsReg2dynPriorityClass) v_data.get(index)).rank != c.rank) {
            			for (int j=index ; j<v_data.size() ; j++) {
            				((GsReg2dynPriorityClass) v_data.get(j)).rank--;
            			}
            		}
            	}
            }
            Iterator it = m_elt.keySet().iterator();
            Object lastClass = v_data.get(v_data.size()-1);
            while (it.hasNext()) {
                Object k = it.next();
                Object cl = m_elt.get(k); 
                if (cl == c) {
                    m_elt.put(k,lastClass);
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
				it = v_listeners.iterator();
				while (it.hasNext()) {
					((GenericListListener)it.next()).itemRemoved(c, t_index[i]);
				}
			}
		}
		return true;
	}

	
    /**
     * move the whole selection up.
     * if some selected class are part of a group, the whole group will move with it.
     */
    protected void doMoveUp(int[] selection, int diff) {
		if (locked) {
			return;
		}
        int[][] index = getMovingRows(GsReg2dynPriorityClassConfig.UP, selection);
        if (index == null) {
            return;
        }
        
        int reselect = 0;
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = start+diff;
            int pr = ((GsReg2dynPriorityClass)v_data.get(start)).rank;
            int prTarget = ((GsReg2dynPriorityClass)v_data.get(target)).rank;
            target--;
            while (target >= 0 && ((GsReg2dynPriorityClass)v_data.get(target)).rank == prTarget) {
                target--;
            }
            target++;
            for (int j=target ; j<start ; j++) {
                ((GsReg2dynPriorityClass)v_data.get(j)).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
            	((GsReg2dynPriorityClass)v_data.get(start+j)).rank = prTarget;
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
    protected void doMoveDown(int[] selection, int diff) {
		if (locked) {
			return;
		}
        int[][] index = getMovingRows(GsReg2dynPriorityClassConfig.DOWN, selection);
        if (index == null) {
            return;
        }
        
        int reselect = 0;
        for (int i=0 ; i<index.length ; i++) {
            int start = index[i][0];
            int stop = index[i][1];
            int target = stop+diff;
            int pr = ((GsReg2dynPriorityClass)v_data.get(start)).rank;
            int prTarget = ((GsReg2dynPriorityClass)v_data.get(target)).rank;
            target++;
            while (target < v_data.size() && ((GsReg2dynPriorityClass)v_data.get(target)).rank == prTarget) {
                target++;
            }
            target--;
            for (int j=stop+1 ; j<=target ; j++) {
                ((GsReg2dynPriorityClass)v_data.get(j)).rank = pr;
            }
            for (int j=0 ; j<=stop-start ; j++) {
                ((GsReg2dynPriorityClass)v_data.get(start)).rank = prTarget;
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
            int priority = ((GsReg2dynPriorityClass)v_data.get(index[i])).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && ((GsReg2dynPriorityClass)v_data.get(start)).rank == priority) {
                    start--;
                }
                while(stop < end && ((GsReg2dynPriorityClass)v_data.get(stop)).rank == priority) {
                    stop++;
                }
                start++;
                stop--;
                // if moving up and already on top or moving down and already on bottom: don't do anything
                if (key==GsReg2dynPriorityClassConfig.UP && start == 0 || key==GsReg2dynPriorityClassConfig.DOWN && stop == end-1) {
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
            int priority = ((GsReg2dynPriorityClass)v_data.get(index[i])).rank;
            if (priority != lastPriority) {
                int start = index[i]-1;
                int stop = index[i]+1;
                while(start >= 0 && ((GsReg2dynPriorityClass)v_data.get(start)).rank == priority) {
                    start--;
                }
                while(stop < end && ((GsReg2dynPriorityClass)v_data.get(stop)).rank == priority) {
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

	public void toXML(XMLWriter out, Object param, int mode)
			throws IOException {
		out.openTag("priorityClassList");
		out.addAttr("id", name);
		StringBuffer s_tmp;
		for (int i=0 ; i< v_data.size(); i++) {
			out.openTag("class");
            GsReg2dynPriorityClass pc = (GsReg2dynPriorityClass)v_data.get(i);
            out.addAttr("name", pc.getName());
            out.addAttr("mode", ""+pc.getMode());
            out.addAttr("rank", ""+pc.rank);
			s_tmp = new StringBuffer();
            Iterator it = m_elt.keySet().iterator();
            while (it.hasNext()) {
                Object o = it.next();
                Object oc = m_elt.get(o);
                if (oc instanceof GsReg2dynPriorityClass) {
                    if (m_elt.get(o) == pc) {
                        s_tmp.append(o+" ");
                    }
                } else if (oc instanceof Object[]) {
                    Object[] t = (Object[])oc;
                    for (int j=0 ; j<t.length ; j++) {
                        if (t[j] == pc) {
                            s_tmp.append(o+","+(j==0 ? "+" : "-")+" ");
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
     *  - and all others are couples: index of vertex in the nodeOrder followed by transition filter.
     *    the "transition filter" is a bit hacky: add it to your transition (which should be either +1 or -1)
     *    and if the result is zero (0), then this transition shouldn't be followed.
     *
     * shortly: it is 0 for all transitions, 1 for negative transitions and -1 for positive ones
     */
    public int[][] getPclass(List nodeOrder) {

        Integer zaroo = new Integer(0);
        Integer one = new Integer(1);
        Integer minusOne = new Integer(-1);

        // it is done by browsing twice the list:
        //   - during the first pass asynchronous classes with the same priority are merged
        //   - then the real int[][] is created from the merged classes
		List v_vpclass = new ArrayList();
        for (int i=0 ; i<v_data.size() ; i++) {
            GsReg2dynPriorityClass pc = (GsReg2dynPriorityClass)v_data.get(i);
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

	public void lock() {
		this.locked = true;
		for (Iterator it=v_data.iterator() ; it.hasNext() ;) {
			((GsReg2dynPriorityClass)it.next()).lock();
		}
	}
}
