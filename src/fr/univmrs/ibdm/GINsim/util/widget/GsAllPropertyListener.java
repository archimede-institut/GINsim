package fr.univmrs.ibdm.GINsim.util.widget;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class GsAllPropertyListener implements PropertyChangeListener {

	Map m_object = new HashMap();
	Map m_key = new HashMap();
	
	static GsAllPropertyListener proplistener = null;
	
	public static void addListenedObject(Component o, String key, String[] props) {
		if (proplistener == null) {
			proplistener = new GsAllPropertyListener();
		}
		proplistener.doAddListenedObject(o, key, props);
	}
	
	// no public constructor!
	private GsAllPropertyListener() {
		
	}
	
	private void doAddListenedObject(Component o, String key, String[] props) {
		m_object.put(o, key);
		if (!m_key.containsKey(key)) {
			m_key.put(key, props);
		}
		for (int i=0 ; i<props.length ; i+=2) {
			o.addPropertyChangeListener(props[i], this);
		}
	}
	
	public void propertyChange(PropertyChangeEvent change) {
		Object src = change.getSource();
		String key = (String)m_object.get(src);
		if (key == null) {
			return;
		}
		String[] props = (String[])m_key.get(key);
		if (props == null) {
			return;
		}
		String pname = change.getPropertyName();
		for (int i=0 ; i<props.length ; i+=2) {
			if (props[i].equals(pname)) {
				System.out.println("changed: "+key+": "+props[i+1]);
				break;
			}
		}

	}

}
