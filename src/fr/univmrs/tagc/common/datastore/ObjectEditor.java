package fr.univmrs.tagc.common.datastore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An ObjectEditor acts as proxy between an object and the UI to edit its properties.
 * It knows the available properties, can check if a proposed value is correct
 * and can apply them.
 */
public abstract class ObjectEditor {

	protected List v_listener = null;
	protected List v_prop = new ArrayList();
	protected Object o;
	protected Object master;
	
	public Object getMasterObject() {
		return master;
	}
	
	abstract public String getStringValue(int prop);
	
	abstract public int getIntValue(int prop);
	
	abstract public boolean isValidValue(int prop, String value);
	abstract public boolean isValidValue(int prop, int value);
	
	abstract public boolean setValue(int prop, String value);
	abstract public boolean setValue(int prop, int value);
	
	public void setEditedObject(Object o) {
		this.o = o;
		refresh(true);
	}
	
	public void refresh(boolean force) {
		if (v_listener != null) {
			Iterator it = v_listener.iterator();
			while (it.hasNext()) {
				((ObjectPropertyEditorUI)it.next()).refresh(force);
			}
		}
	}
	
	public void addListener(ObjectPropertyEditorUI l) {
		if (v_listener == null) {
			v_listener = new ArrayList();
		}
		v_listener.add(l);
	}
	public void removeListener(ObjectPropertyEditorUI l) {
		if (v_listener != null) {
			v_listener.remove(l);
		}
	}
	
	public List getProperties() {
		return v_prop;
	}

	public abstract Object getRawValue(int prop);
	public void performAction(int prop) {
		System.out.println("override this!");
	}
}
