package org.ginsim.gui.utils.data;

import javax.swing.JLabel;

import org.ginsim.gui.utils.data.models.MinMaxSpinModel;


public class MinMaxEditor implements ObjectPropertyEditorUI {

	MinMaxSpinModel model;
	GenericPropertyInfo pinfo;
	
	public void apply() {
	}

	public void refresh(boolean force) {
		model.setEditedObject(pinfo.getRawValue());
	}

	public void setEditedProperty(GenericPropertyInfo pinfo,
			GenericPropertyHolder panel) {
		this.model = (MinMaxSpinModel)pinfo.data;
		this.pinfo = pinfo;
		panel.addField(new JLabel(model.getMinName()), pinfo, 0);
		panel.addField(model.getSMin(), pinfo, 1);
		panel.addField(new JLabel(model.getMaxName()), pinfo, 2);
		panel.addField(model.getSMax(), pinfo, 3);
	}
}
