package fr.univmrs.tagc.datastore;

import fr.univmrs.tagc.datastore.gui.GenericPropertyHolder;

public interface ObjectPropertyEditorUI {
	void refresh(boolean force);
	void apply();
	void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel);
}
