package fr.univmrs.tagc.datastore;

import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.univmrs.ibdm.GINsim.global.GsNamedObject;


public class SimpleGenericList extends GenericList {

	public Vector		v_data;
	protected String	prefix			= "name_";
	protected String	pattern			= "^[a-zA-Z0-9_-]+$";

	public boolean		enforceUnique	= true;
	public boolean		addWithPosition = false;

	public SimpleGenericList(Vector v_data) {
		setData(v_data);
	}

	public SimpleGenericList() {
		this.v_data = new Vector();
	}

	public void setData(Vector v_data) {
		this.v_data = v_data;
		refresh();
	}

	public int add(int position) {
		if (!canAdd) {
			return -1;
		}
		// find an unused name
		String s = null;
		boolean[] t = new boolean[getNbElements(null)];
		for (int j = 0 ; j < t.length ; j++) {
			t[j] = true;
		}
		if (v_data.size() > 0 && !(v_data.get(0) instanceof GsNamedObject)) {
			return triggerAdd(wrapCreate(s, position));
		}
		for (int j = 0 ; j < t.length ; j++) {
			GsNamedObject obj = (GsNamedObject)v_data.get(j);
			if (obj.getName().startsWith(prefix)) {
				try {
					int v = Integer.parseInt(obj.getName().substring(
							prefix.length()));
					if (v > 0 && v <= t.length) {
						t[v - 1] = false;
					}
				} catch (NumberFormatException e) {
				}
			}
		}
		for (int j = 0 ; j < t.length ; j++) {
			if (t[j]) {
				s = prefix + (j + 1);
				break;
			}
		}
		if (s == null) {
			s = prefix + (t.length + 1);
		}

		return triggerAdd(wrapCreate(s, position));
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
				((GenericListListener)it.next()).itemAdded(item, pos);
			}
		}
		return pos;
	}

	public void run(String filter, int row, int col) {
		if (nbAction < col) {
			return;
		}
		int index = getRealIndex(filter, row);
		doRun(index, col);
	}

	public int getRealIndex(String filter, int i) {
		if (filter == null) {
			return i;
		}
		int c = 0;
		for (int j = 0 ; j < v_data.size() ; j++) {
			if (match(filter, v_data.get(j))) {
				if (c == i) {
					return j;
				}
				c++;
			}
		}
		return -1;
	}

	public boolean edit(String filter, int pos, int col, Object o) {
		if (!canEdit) {
			return false;
		}
		int index = getRealIndex(filter, pos);
		if (index == v_data.size()) {
			if (!doInlineAddRemove || o == null || o == "") {
				return false;
			}
			Object newObj = wrapCreate((String)o, pos);
			if (newObj != null) {
				triggerAdd(newObj);
				return true;
			}
			return false;
		}
		if ((o == null || o.equals("")) && doInlineAddRemove) {
			int[] t = new int[1];
			t[0] = pos;
			remove(null, t);
			return true;
		}
		Object data = v_data.get(index);
		if (data instanceof MultiColObject) {
			return ((MultiColObject)data).setVal(col, o);
		}
		if (data.getClass() == o.getClass()) {
			v_data.setElementAt(o, index);
			return true;
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
		if (enforceUnique) {
			for (int i = 0 ; i < v_data.size() ; i++) {
				if (i != index
						&& ((GsNamedObject)v_data.get(i)).getName().equals(
								o.toString())) {
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

	public Object getElement(String filter, int i) {
		return v_data.get(getRealIndex(filter, i));
	}

	public int getNbElements(String filter) {
		if (filter == null) {
			return v_data.size();
		}
		int c = 0;
		for (int i = 0 ; i < v_data.size() ; i++) {
			if (match(filter, v_data.get(i))) {
				c++;
			}
		}
		return c;
	}

	public boolean move(int[] sel, int diff) {

		if (diff == 0 || sel == null || sel.length == 0 || 
				diff < 0 && sel[0] <= -(diff+1) ||
				diff > 0 && sel[sel.length-1] >= v_data.size() - diff) {
			return false;
		}
		if (diff > 0) {
			doMoveDown(sel, diff);
		} else {
			doMoveUp(sel, diff);
		}
	    return true;

	
	}
	
	protected void doMoveUp(int[] sel, int diff) {
	    for (int i=0 ; i<sel.length ; i++) {
		    int a = sel[i];
		    if (a >= diff) {
			    moveElement(a, a+diff);
			    sel[i] += diff;
		    }
	    }
	}
	protected void doMoveDown(int[] sel, int diff) {
	    for (int i=sel.length-1 ; i>=0 ; i--) {
		    int a = sel[i];
		    if (a < v_data.size()+diff) {
			    moveElement(a, a+diff);
			    sel[i] += diff;
		    }
	    }
	}
	
	protected boolean moveElement(int src, int dst) {
		if (!canOrder) {
			return false;
		}
		if (src < 0 || dst < 0 || src >= v_data.size() || dst >= v_data.size()) {
			return false;
		}
		Object o = v_data.remove(src);
		v_data.add(dst, o);
		return true;
	}

	public boolean remove(String filter, int[] t_index) {
		if (!canRemove) {
			return false;
		}
		for (int i = t_index.length - 1 ; i > -1 ; i--) {
			int index = getRealIndex(filter, t_index[i]);
			Object item = v_data.remove(index);
			if (v_listeners != null) {
				Iterator it = v_listeners.iterator();
				while (it.hasNext()) {
					((GenericListListener)it.next()).itemRemoved(item,
							t_index[i]);
				}
			}
		}
		return true;
	}

	public boolean match(String filter, Object o) {
		return o.toString().toLowerCase().contains(filter.toLowerCase());
	}

	protected Object doCreate(String name) {
		System.out.println("you should override this if you plan to use it");
		return null;
	}
	protected Object doCreate(String name, int pos) {
		System.out.println("you should override this if you plan to use it");
		return null;
	}
	protected Object wrapCreate(String name, int pos) {
		if (addWithPosition) {
			return doCreate(name, pos);
		}
		return doCreate(name);
	}

	protected void doRun(int row, int col) {
		System.out.println("you should override this if you plan to use it");
		return;
	}
	
	public Object getElement(String name) {
		Iterator it = v_data.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o != null && o instanceof GsNamedObject && name.equals(((GsNamedObject)o).getName())) {
				return o;
			}
		}
		return null;
	}

}
