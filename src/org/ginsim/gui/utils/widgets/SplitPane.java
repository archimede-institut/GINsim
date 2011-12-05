package org.ginsim.gui.utils.widgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;

import org.ginsim.common.OptionStore;



public class SplitPane extends JSplitPane implements PropertyChangeListener {
	private static final long	serialVersionUID	= 5625943556046782994L;
	
	private String name = null;
	
	public void setName(String name) {
		super.setName(name);
		this.name = name;
		if (name != null) {
			Object option = OptionStore.getOption("display."+name+".divider");
			addPropertyChangeListener(DIVIDER_LOCATION_PROPERTY, this);
			if (option instanceof Integer) {
				setDividerLocation(((Integer)option).intValue());
			} else if (option instanceof String) {
				setDividerLocation(Integer.parseInt((String)option));
			}
			//this.setDivider
			//((BasicSplitPaneUI)getUI()).getDivider().
			
			setOneTouchExpandable(true);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (name != null) {
			OptionStore.setOption("display."+name+".divider", ""+getDividerLocation());
		}
	}
}
