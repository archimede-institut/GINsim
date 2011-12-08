package org.ginsim.gui.utils.data;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;




public class PropertySwitch extends JComboBox implements ObjectPropertyEditorUI, GenericPropertyHolder {
	private static final long	serialVersionUID	= 135698849165648064L;
	
	CardLayout cards = new CardLayout();
	JPanel stack = new JPanel();
	
	public void apply() {
	}

	public void refresh(boolean force) {
	}

	public void setEditedProperty(GenericPropertyInfo pinfo,
			GenericPropertyHolder panel) {
		GenericPropertyInfo[] t = (GenericPropertyInfo[])pinfo.data;
		setModel(new PropSwitchComboModel(t));
		panel.addField(this, pinfo, 0);
		stack.setLayout(cards);
		for (int i=0 ; i<t.length ; i++) {
			t[i].build(this);
		}
		if (pinfo.name != null) {
			setSelectedItem(pinfo.name);
		}
		panel.addField(stack, pinfo, 1);
	}

	protected void selectedItemChanged() {
		super.selectedItemChanged();
		cards.show(stack, (String)getSelectedItem());
	}

	public void addField(Component cmp, GenericPropertyInfo pinfo, int index) {
		stack.add(cmp, pinfo.name);
	}
}

class PropSwitchComboModel extends DefaultComboBoxModel {
	private static final long	serialVersionUID	= -1716419078930060713L;
	
	GenericPropertyInfo[] t_prop;
	int selected = 0;

	PropSwitchComboModel(GenericPropertyInfo[] t_prop) {
		this.t_prop = t_prop;
	}
	
	public Object getSelectedItem() {
		return t_prop[selected].name;
	}

	public void setSelectedItem(Object anItem) {
		for (int i=0 ; i<t_prop.length ; i++) {
			if (t_prop[i].name.equals(anItem)) {
				selected = i;
				return;
			}
		}
	}

	public Object getElementAt(int index) {
		return t_prop[index].name;
	}

	public int getSize() {
		return t_prop.length;
	}
}