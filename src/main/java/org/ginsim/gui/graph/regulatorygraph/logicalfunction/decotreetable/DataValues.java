package org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable;

import java.util.Vector;

public class DataValues {
	private Vector values;
	private Vector editable;

	public DataValues() {
		super();
		values = new Vector();
		editable = new Vector();
	}
	public int getSize() {
		return values.size();
	}
	public void addValue(Object v, boolean edit) {
		values.addElement(v);
		editable.addElement(new Boolean(edit));
	}
	public void removeValue(int i) {
		values.removeElementAt(i);
		editable.removeElementAt(i);
	}
	public void setValueAt(int i, Object v, boolean edit) {
		values.setElementAt(v, i);
		editable.setElementAt(new Boolean(edit), i);
	}
	public Object getValueAt(int i) {
		return values.elementAt(i);
	}
	public void setEditable(int i, boolean edit) {
		editable.setElementAt(new Boolean(edit), i);
	}
	public boolean isEditable(int i) {
		return ((Boolean)editable.elementAt(i)).booleanValue();
	}
	public Class getClass(int i) {
		return values.elementAt(i).getClass();
	}
}
