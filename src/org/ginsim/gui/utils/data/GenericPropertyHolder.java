package org.ginsim.gui.utils.data;

import java.awt.Component;

import org.ginsim.core.utils.data.GenericPropertyInfo;


public interface GenericPropertyHolder {

	public abstract void addField(Component cmp, GenericPropertyInfo pinfo,
			int index);
}