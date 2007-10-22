package fr.univmrs.ibdm.GINsim.gui;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.univmrs.ibdm.GINsim.global.GsNamedObject;

abstract public class GsListAbstract implements GsList {

	public Vector v_data = new Vector();
	protected String prefix = "name_";
	protected String pattern = "^[a-zA-Z0-9_-]+$";

	protected Vector v_listeners;
	
	protected boolean canAdd = false;
	protected boolean canCopy = false;
	protected boolean canRemove = false;
	protected boolean canEdit = false;
	protected boolean canOrder = false;
	protected boolean canFilter = true;
	
	protected String filter = null;
	
	public void addListListener(GsListListener l) {
		if (v_listeners == null) {
			v_listeners = new Vector();
		}
		v_listeners.add(l);
	}
	public void removeListListener(GsListListener l) {
		if (v_listeners == null) {
			return;
		}
		v_listeners.remove(l);
	}
	
	public int add(int i, int type) {
		if (!canAdd) {
			return -1;
		}
        // find an unused name
        String s = null;
        boolean[] t = new boolean[getNbElements()];
        for (int j=0 ; j<t.length ; j++) {
            t[j] = true;
        }
        for (int j=0 ; j<t.length ; j++) {
            GsNamedObject obj = (GsNamedObject)v_data.get(j);
            if (obj.getName().startsWith(prefix)) {
                try {
                    int v = Integer.parseInt(obj.getName().substring(prefix.length()));
                    if (v > 0 && v <= t.length) {
                        t[v-1] = false;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        for (int j=0 ; j<t.length ; j++) {
            if (t[j]) {
                s = prefix+(j+1);
                break;
            }
        }
        if (s == null) {
            s = prefix+(t.length+1);
        }

		Object item = doCreate(s, type);
		v_data.add(item);
		if (v_listeners != null) {
			for (int l=0 ; l<v_listeners.size() ; l++) {
				((GsListListener)v_listeners.get(l)).ItemAdded(item);
			}
		}
		return v_data.indexOf(item);
	}
	
	protected abstract Object doCreate(String name, int type);

	public boolean canAdd() {
		return canAdd;
	}

	public boolean canCopy() {
		return canCopy;
	}

	public boolean canEdit() {
		return canEdit;
	}

	public boolean canOrder() {
		return canOrder;
	}

	public boolean canRemove() {
		return canRemove;
	}

	public boolean canFilter() {
		return canFilter;
	}

	public void setFilter(String filter) {
		if (!canFilter) {
			return;
		}
		this.filter = filter;
	}
	public Vector getObjectType() {
		return null;
	}
	public int copy(int i) {
		if (!canCopy) {
			return -1;
		}
		// TODO: add generic copy
		return -1;
	}

	public boolean edit(int index, Object o) {
		if (!canEdit) {
			return false;
		}
		GsNamedObject obj = (GsNamedObject)v_data.get(index);
		if (obj.getName().equals(o.toString())) {
			return false;
		}
		// check that the new name is valid
		Matcher matcher = Pattern.compile(pattern).matcher(o.toString());
		if (!matcher.find()) {
			return false;
		}
		for (int i=0 ; i<v_data.size() ; i++) {
			if (i != index && ((GsNamedObject)v_data.get(i)).getName().equals(o.toString())) {
				return false;
			}
		}
		obj.setName(o.toString());
		return true;
	}

	public Object getElement(int i) {
		if (filter == null) {
			return v_data.get(i);
		}
		for (int j=0, c=0 ; j<v_data.size() ; j++) {
			Object o = v_data.get(j);
			if (match(filter, o)) {
				if (c == i) {
					return o;
				}
				c++;
			}
		}
		return null;
	}
	
	public boolean match(String filter, Object o) {
		return o.toString().contains(filter);
	}

	public int getNbElements() {
		if (filter == null) {
			return v_data.size();
		}
		int c=0;
		for (int i=0 ; i<v_data.size() ; i++) {
			if (match(filter, v_data.get(i))) {
				c++;
			}
		}
		return c;
	}

    public boolean moveElement(int src, int dst) {
    	if (!canOrder) {
    		return false;
    	}
        if (src<0 || dst<0 || src >= v_data.size() || dst>=v_data.size()) {
            return false;
        }
        Object o = v_data.remove(src);
        v_data.add(dst, o);
        return true;
    }

	public boolean remove(int[] t_index) {
		if (!canRemove) {
			return false;
		}
		for (int i=t_index.length-1 ; i>-1 ; i--) {
			Object item = v_data.get(t_index[i]);
			v_data.remove(t_index[i]);
			if (v_listeners != null) {
				for (int l=0 ; l<v_listeners.size() ; l++) {
					((GsListListener)v_listeners.get(l)).itemRemoved(item);
				}
			}
		}
		return true;
	}
}
