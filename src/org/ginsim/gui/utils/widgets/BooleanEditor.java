package org.ginsim.gui.utils.widgets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ginsim.core.utils.data.GenericPropertyInfo;
import org.ginsim.core.utils.data.ObjectPropertyEditorUI;
import org.ginsim.gui.utils.data.GenericPropertyHolder;



public class BooleanEditor extends JCheckBox implements ObjectPropertyEditorUI, ChangeListener {
	private static final long	serialVersionUID	= 7188801384907165831L;

	GenericPropertyInfo pinfo = null;
	
	public BooleanEditor() {
	    this.addChangeListener(this);
	}
	
	public void apply() {
		if (pinfo == null) {
			return;
		}
		pinfo.setValue(isSelected() ? 1 : 0);
	}

	public void refresh(boolean force) {
		if (force) {
			setSelected(pinfo.getRawValue() == Boolean.TRUE);
		}
	}

	public void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel) {
		this.pinfo = pinfo;
		int pos = 0;
		if (pinfo.name != null) {
			panel.addField(new JLabel(pinfo.name), pinfo, 0);
			pos++;
		}
		if (pinfo.isEditable) {
			panel.addField(this, pinfo, pos);
		} else {
			setEnabled(false);
			panel.addField(this, pinfo, pos);
		}
	}

    public void stateChanged(ChangeEvent change) {
        pinfo.setValue(isSelected() ? 1 : 0);
    }
}
