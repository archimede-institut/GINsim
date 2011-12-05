package org.ginsim.utils.data;

import org.ginsim.gui.utils.data.GenericPropertyHolder;

public interface ObjectPropertyEditorUI {
	void refresh(boolean force);
	void apply();
	void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel);
}
