package fr.univmrs.tagc.common.datastore.gui;

import java.awt.Component;

import fr.univmrs.tagc.common.datastore.GenericPropertyInfo;

public interface GenericPropertyHolder {

	public abstract void addField(Component cmp, GenericPropertyInfo pinfo,
			int index);
}