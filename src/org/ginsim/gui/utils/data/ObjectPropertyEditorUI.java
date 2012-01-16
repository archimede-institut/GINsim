package org.ginsim.gui.utils.data;


public interface ObjectPropertyEditorUI {
	void refresh(boolean force);
	void apply();
	void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel);
	void release();
}
