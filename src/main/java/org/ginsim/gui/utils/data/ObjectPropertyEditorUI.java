package org.ginsim.gui.utils.data;


public interface ObjectPropertyEditorUI {
	void refresh(boolean force);
	void apply();

	/**
	 * Edit property setter
	 * @param pinfo  property info
	 * @param panel property holder
	 */
	void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel);
	void release();
}
