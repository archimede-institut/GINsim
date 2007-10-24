package fr.univmrs.tagc.datastore.gui;

import javax.swing.JLabel;

import fr.univmrs.tagc.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.datastore.MinMaxSpinModel;
import fr.univmrs.tagc.datastore.ObjectPropertyEditorUI;


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
