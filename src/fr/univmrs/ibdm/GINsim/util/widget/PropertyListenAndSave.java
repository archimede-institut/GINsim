package fr.univmrs.ibdm.GINsim.util.widget;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import fr.univmrs.ibdm.GINsim.global.GsOptions;

public class PropertyListenAndSave implements PropertyChangeListener {

	String key;
	
	public PropertyListenAndSave(String key) {
		this.key = key;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		GsOptions.setOption(key+"."+evt.getPropertyName(), evt.getNewValue());
	}

}
