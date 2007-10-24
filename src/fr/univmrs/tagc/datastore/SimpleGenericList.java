package fr.univmrs.tagc.datastore;

import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.univmrs.ibdm.GINsim.global.GsNamedObject;

public class SimpleGenericList implements GenericList {

	public Vector v_data;
	protected String prefix = "name_";
	protected String pattern = "^[a-zA-Z0-9_-]+$";

	protected Vector v_listeners;
	
	protected boolean canAdd = false;
	protected boolean canCopy = false;
	protected boolean canRemove = false;
	protected boolean canEdit = false;
	protected boolean canOrder = false;
	protected boolean canFilter = true;
	protected boolean hasAction = true;
	
	protected String filter = null;
	
	public SimpleGenericList(Vector v_data) {
		setData(v_data);
	}
	public SimpleGenericList() {
		this.v_data = new Vector();
	}
	public void setData(Vector v_data) {
		this.v_data = v_data;
		if (v_listeners == null) {
			return;
		}
		Iterator it = v_listeners.iterator();
		while (it.hasNext()) {
			((GenericListListener)it.next()).StructureChanged();
		}
	}
	
	public void addListListener(GenericListListener l) {
		if (v_listeners == null) {
			v_listeners = new Vector();
		}
		v_listeners.add(l);
	}
	public void removeListListener(GenericListListener l) {
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
			Iterator it = v_listeners.iterator();
			while (it.hasNext()) {
				((GenericListListener)it.next()).ItemAdded(item);
			}
		}
		return v_data.indexOf(item);
	}
	
	protected Object doCreate(String name, int type) {
		System.out.println("you should override this if you plan to use it");
		return null;
	}

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

	public boolean hasAction() {
		return hasAction;
	}

	public void setFilter(String filter) {
		if (!canFilter) {
			return;
		}
		this.filter = filter;
	}
	public void run(int i) {
		// TODO: run
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

	private int getRealIndex(int i) {
		if (filter == null) {
			return i;
		}
		int c = 0;
		for (int j=0 ; j<v_data.size() ; j++) {
			if (match(filter, v_data.get(j))) {
				if (c == i) {
					return j;
				}
				c++;
			}
		}
		return -1;
	}
	public boolean edit(int pos, Object o) {
		if (!canEdit) {
			return false;
		}
		int index = getRealIndex(pos);
		Object data = v_data.get(index);
		if (!(data instanceof GsNamedObject)) {
			return doEdit(data, o);
		}
		GsNamedObject obj = (GsNamedObject)data;
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

	protected boolean doEdit(Object data, Object value) {
		return false;
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
		return o.toString().toLowerCase().contains(filter.toLowerCase());
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
				Iterator it = v_listeners.iterator();
				while (it.hasNext()) {
					((GenericListListener)it.next()).itemRemoved(item);
				}
			}
		}
		return true;
	}
}
