package fr.univmrs.tagc.datastore;

import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.univmrs.ibdm.GINsim.global.GsNamedObject;

public class SimpleGenericList implements GenericList {

	public Vector v_data;
	protected Vector v_listeners;
	protected String filter = null;
	
	protected String name = "";
	protected String prefix = "name_";
	protected String pattern = "^[a-zA-Z0-9_-]+$";
	
	public boolean canAdd = false;
	public boolean canCopy = false;
	public boolean canRemove = false;
	public boolean canEdit = false;
	public boolean canOrder = false;
	public boolean canFilter = true;
	public boolean hasAction = false;
	public boolean enforceUnique = true;
	public boolean inlineAddDel = false;
	
	
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
			((GenericListListener)it.next()).ContentChanged();
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
        if (v_data.size() > 0 && !(v_data.get(0) instanceof GsNamedObject)) {
        	return triggerAdd(doCreate(s, type));
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

		return triggerAdd(doCreate(s, type));
	}

	private int triggerAdd(Object item) {
		if (item == null) {
			return -1;
		}
		v_data.add(item);
		int pos = v_data.indexOf(item);
		if (v_listeners != null) {
			Iterator it = v_listeners.iterator();
			while (it.hasNext()) {
				((GenericListListener)it.next()).ItemAdded(item, getRealIndex(pos));
			}
		}
		return pos;
	}
	

	public void setFilter(String filter) {
		if (!canFilter) {
			return;
		}
		if (filter.length() == 0) {
			this.filter = null;
		} else {
			this.filter = filter;
		}
	}
	public void run(int i) {
		if (!hasAction) {
			return;
		}
		int index = getRealIndex(i);
		doRun(index);
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
		if (index == v_data.size()) {
			if (!inlineAddDel) {
				return false;
			}
			Object newObj = doCreate((String)o, 0);
			if (newObj != null) {
				triggerAdd(newObj);
				return true;
			}
			return false;
		}
		if ((o == null || o.equals("")) && inlineAddDel) {
			int[] t = new int[1];
			t[0] = pos;
			remove(t);
			return true;
		}
		Object data = v_data.get(index);
		if (data.getClass() == o.getClass()) {
			v_data.setElementAt(o, index);
		}
		if (!(data instanceof GsNamedObject)) {
			return doEdit(data, o);
		}
		GsNamedObject obj = (GsNamedObject)data;
		if (obj.getName().equals(o.toString())) {
			return false;
		}
		// check that the new name is valid
		if (pattern != null) {
			Matcher matcher = Pattern.compile(pattern).matcher(o.toString());
			if (!matcher.find()) {
				return false;
			}
		}
		if (enforceUnique ) {
			for (int i=0 ; i<v_data.size() ; i++) {
				if (i != index && ((GsNamedObject)v_data.get(i)).getName().equals(o.toString())) {
					return false;
				}
			}
		}
		obj.setName(o.toString());
		return true;
	}

	protected boolean doEdit(Object data, Object value) {
		return false;
	}
	
	public Object getElement(int i) {
		return v_data.get(getRealIndex(i));
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
    	if (!canOrder || filter != null) {
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
			int index = getRealIndex(t_index[i]);
			Object item = v_data.remove(index);
			if (v_listeners != null) {
				Iterator it = v_listeners.iterator();
				while (it.hasNext()) {
					((GenericListListener)it.next()).itemRemoved(item, index);
				}
			}
		}
		return true;
	}
	
	public boolean match(String filter, Object o) {
		return o.toString().toLowerCase().contains(filter.toLowerCase());
	}
	protected Object doCreate(String name, int type) {
		System.out.println("you should override this if you plan to use it");
		return null;
	}
	protected void doRun(int index) {
		System.out.println("you should override this if you plan to use it");
		return;
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
	public boolean doInlineAddRemove() {
		return inlineAddDel;
	}
	public boolean canFilter() {
		return canFilter;
	}
	public boolean hasAction() {
		return hasAction;
	}
	public Object getAction(int row) {
		return "->";
	}
	public String getName() {
		return name;
	}
}
