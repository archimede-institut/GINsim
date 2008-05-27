package fr.univmrs.tagc.common.datastore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleGenericList extends GenericList {
	public List		v_data;
	protected String	prefix			= "name_";
	protected String	pattern			= "^[a-zA-Z0-9_-]+$";

	public boolean		enforceUnique	= true;
	public boolean		addWithPosition = false;

	public SimpleGenericList(List v_data) {
		setData(v_data);
	}

	public SimpleGenericList() {
		this.v_data = new ArrayList();
	}

	public void setData(List v_data) {
		this.v_data = v_data;
		refresh();
	}

	public int add(int position, int mode) {
		if (!canAdd) {
			return -1;
		}
		// find an unused name
		String s = null;
		boolean[] t = new boolean[getNbElements(null,0)];
		for (int j = 0 ; j < t.length ; j++) {
			t[j] = true;
		}
		if (v_data.size() > 0 && !(v_data.get(0) instanceof NamedObject)) {
			return triggerAdd(wrapCreate(s, position, mode), position);
		}
		for (int j = 0 ; j < t.length ; j++) {
			NamedObject obj = (NamedObject)v_data.get(j);
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

		return triggerAdd(wrapCreate(s, position, mode), position);
	}

	private int triggerAdd(Object item, int position) {
		if (item == null) {
			return -1;
		}
		if (addWithPosition) {
			v_data.add(position, item);
		} else {
			v_data.add(item);
		}
		int pos = v_data.indexOf(item);
		if (v_listeners != null) {
			Iterator it = v_listeners.iterator();
			while (it.hasNext()) {
				((GenericListListener)it.next()).itemAdded(item, pos);
			}
		}
		return pos;
	}

	public void run(String filter, int startIndex, int row, int col) {
		if (nbAction < col) {
			return;
		}
		int index = getRealIndex(filter, startIndex, row);
		doRun(index, col);
	}

	public int getRealIndex(String filter, int startIndex, int i) {
		if (filter == null) {
			return i+startIndex;
		}
		int c = 0;
		for (int j = startIndex ; j < v_data.size() ; j++) {
			if (match(filter, v_data.get(j))) {
				if (c == i) {
					return j;
				}
				c++;
			}
		}
		return -1;
	}

	public boolean edit(String filter, int startIndex, int pos, int col, Object o) {
		if (!canEdit) {
			return false;
		}
		int index = getRealIndex(filter, startIndex, pos);
		if (index == v_data.size()) {
			if (!doInlineAddRemove || "".equals(o)) {
				return false;
			}
			Object newObj = wrapCreate((String)o, pos, -1);
			if (newObj != null) {
				triggerAdd(newObj, pos);
				return true;
			}
			return false;
		}
		if ("".equals(o) && doInlineAddRemove) {
			int[] t = new int[1];
			t[0] = pos;
			remove(null, 0, t);
			return true;
		}
		Object data = v_data.get(index);
		if (mcolHelper != null) {
			return mcolHelper.setVal(data, col, o);
		}
		if (data instanceof MultiColObject) {
			return ((MultiColObject)data).setVal(col, o);
		}
		if (data.getClass() == o.getClass()) {
			v_data.add(index, o);
			return true;
		}
		if (!(data instanceof NamedObject)) {
			return doEdit(data, o);
		}
		NamedObject obj = (NamedObject)data;
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
						&& ((NamedObject)v_data.get(i)).getName().equals(
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

	public Object getElement(String filter, int startIndex, int i) {
		return v_data.get(getRealIndex(filter, startIndex, i));
	}

	public int getNbElements(String filter, int startIndex) {
		if (filter == null) {
			return v_data.size()-startIndex;
		}
		int c = 0;
		for (int i = startIndex ; i < v_data.size() ; i++) {
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

	public boolean remove(String filter, int startIndex, int[] t_index) {
		if (!canRemove) {
			return false;
		}
		for (int i = t_index.length - 1 ; i > -1 ; i--) {
			int index = getRealIndex(filter, startIndex, t_index[i]);
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

	protected Object doCreate(String name, int mode) {
		System.out.println("you should override this if you plan to use it");
		new Exception().printStackTrace();
		return null;
	}
	protected Object doCreate(String name, int pos, int mode) {
		System.out.println("you should override this if you plan to use it");
		new Exception().printStackTrace();
		return null;
	}
	protected Object wrapCreate(String name, int pos, int mode) {
		if (addWithPosition) {
			return doCreate(name, pos, mode);
		}
		return doCreate(name, mode);
	}

	protected void doRun(int row, int col) {
		System.out.println("you should override this if you plan to use it");
		return;
	}
	
	public Object getElement(String name) {
		Iterator it = v_data.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (o != null && o instanceof NamedObject && name.equals(((NamedObject)o).getName())) {
				return o;
			}
		}
		return null;
	}
}
