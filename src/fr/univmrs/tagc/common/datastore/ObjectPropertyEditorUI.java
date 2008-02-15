package fr.univmrs.tagc.common.datastore;

import fr.univmrs.tagc.common.datastore.gui.GenericPropertyHolder;

public interface ObjectPropertyEditorUI {
	void refresh(boolean force);
	void apply();
	void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel);
}
