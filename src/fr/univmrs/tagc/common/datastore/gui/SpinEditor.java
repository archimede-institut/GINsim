package fr.univmrs.tagc.common.datastore.gui;

import javax.swing.JLabel;
import javax.swing.JSpinner;

import fr.univmrs.tagc.common.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.common.datastore.ObjectPropertyEditorUI;
import fr.univmrs.tagc.common.datastore.models.SpinModel;

public class SpinEditor implements ObjectPropertyEditorUI {

	SpinModel model;
	GenericPropertyInfo pinfo;
	
	public void apply() {
	}

	public void refresh(boolean force) {
		model.setEditedObject(pinfo.getRawValue());
	}

	public void setEditedProperty(GenericPropertyInfo pinfo,
			GenericPropertyHolder panel) {
		this.model = (SpinModel)pinfo.data;
		this.pinfo = pinfo;
		panel.addField(new JLabel(pinfo.name), pinfo, 0);
		panel.addField(new JSpinner(model), pinfo, 1);
	}
}
