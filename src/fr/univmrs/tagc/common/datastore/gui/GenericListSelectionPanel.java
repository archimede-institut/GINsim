package fr.univmrs.tagc.common.datastore.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import fr.univmrs.tagc.common.datastore.GenericList;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

abstract public class GenericListSelectionPanel extends JPanel implements ActionListener {
	private static final long	serialVersionUID	= -1200453159340442013L;
	
	protected StackDialog dialog;
	GenericListCombo combo;
	JLabel label;
	
	public GenericListSelectionPanel(StackDialog dialog, GenericList list, String name, boolean hasEmptyChoice) {
		this.dialog = dialog;
		if (name != null) {
			setBorder(BorderFactory.createTitledBorder(name));
		}
		setLayout(new GridBagLayout());
		combo = new GenericListCombo(list, hasEmptyChoice);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		add(combo, c);
		label = new JLabel();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(label, c);
		if (dialog != null) {
	        JButton but_configure = new JButton(Translator.getString("STR_configure"));
	        but_configure.addActionListener(this);
	        c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 0;
	        add(but_configure, c);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		configure();
	}

	abstract protected void configure();

	public void refresh() {
		combo.refresh();
	}
	
	public void setStore(ObjectStore store) {
		setStore(store, 0);
	}
	public void setStore(ObjectStore store, int id) {
		combo.setStore(store, id);
	}
	public void addActionListener(ActionListener l) {
		combo.addActionListener(l);
	}
	public void setText(String text) {
		label.setText(text);
	}
    public void setFilter(String filter) {
    	combo.model.setFilter(filter);
    }
}

class GenericListCombo extends JComboBox {
	private static final long serialVersionUID = -7848606073222946763L;

	GenericListComboModel model;
	public GenericListCombo(GenericList list, boolean hasEmptyChoice) {
		model = new GenericListComboModel(list, hasEmptyChoice);
		setModel(model);
	}
	
	public void refresh() {
		model.refresh();
	}
	
	public void setStore(ObjectStore store, int id) {
		model.setStore(store, id);
	}
}


class GenericListComboModel extends DefaultComboBoxModel implements ComboBoxModel {
    private static final long serialVersionUID = 2348678706086666489L;
    
    GenericList list;
    ObjectStore store = null;
    int id;
    String filter = null;
    boolean hasEmptyChoice = true;
    
    public GenericListComboModel(GenericList list, boolean hasEmptyChoice) {
    	setMutantList(list);
    	this.hasEmptyChoice = hasEmptyChoice;
    	refresh();
    }
    protected void setFilter(String filter) {
    	this.filter = filter;
    	refresh();
    }
    public void setStore(ObjectStore store, int id) {
		this.store = store;
		this.id = id;
		refresh();
	}

	void setMutantList(GenericList list) {
    	this.list = list;
    }

    public void refresh() {
    	fireContentsChanged(this, 0, getSize());
    }
    
    public Object getSelectedItem() {
    	if (store == null) {
    		return "--";
    	}
    	Object o = store.getObject(id);
    	return o==null ? "--": o;
    }

    public void setSelectedItem(Object anItem) {
        if (list.indexOf(anItem) != -1) {
            store.setObject(id, anItem);
        } else {
        	store.setObject(id, null);
        }
        fireContentsChanged(this, 0, getSize());
    }

    public Object getElementAt(int index) {
        if (index == 0 && hasEmptyChoice || list == null) {
            return "--";
        }
        if (hasEmptyChoice) {
        	return list.getElement(filter, index-1);
        }
    	return list.getElement(filter, index);
    }

    public int getSize() {
        if (list == null) {
            return 1;
        }
        if (hasEmptyChoice) {
        	return list.getNbElements(filter)+1;
        }
    	return list.getNbElements(filter);
    }
}
