package org.ginsim.gui.utils.data.models;

import javax.swing.DefaultComboBoxModel;

import org.ginsim.core.utils.data.ValueList;



public class ValueListComboModel extends DefaultComboBoxModel {
    private static final long serialVersionUID = -8553547226168566527L;
    
    ValueList data;

    public ValueListComboModel() {
    }
    
    ValueListComboModel(ValueList data) {
        this.data = data;
    }
    
    public void setData(ValueList data) {
        this.data = data;
        fireContentsChanged(this, 0, getSize());
    }
    
    public Object getElementAt(int index) {
        if (data == null) {
            return null;
        }
        return data.get(index);
    }

    public int getIndexOf(Object anObject) {
        if (data == null) {
            return -1;
        }
        return data.indexOf(anObject);
    }

    public Object getSelectedItem() {
        if (data == null) {
            return null;
        }
        int sel = data.getSelectedIndex();
        sel = sel == -1 ? 0 : sel;
        return data.get(data.getSelectedIndex());
    }

    public int getSize() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public void setSelectedItem(Object anObject) {
        if (data == null) {
            return;
        }
        data.setSelectedIndex(getIndexOf(anObject));
    }
}  