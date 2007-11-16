package fr.univmrs.tagc.datastore.gui;

import java.awt.Component;

import fr.univmrs.tagc.datastore.GenericPropertyInfo;

public interface GenericPropertyHolder {

	public abstract void addField(Component cmp, GenericPropertyInfo pinfo,
			int index);
}