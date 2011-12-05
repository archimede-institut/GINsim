package org.ginsim.gui.utils.data.models;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * model for a min spinbutton, depends on a GsMinMaxSpinModel
 */
public class MinSpinModel extends AbstractSpinnerModel {
    
    MinMaxSpinModel minmax;
    private JTextField editor;
    
    /**
     * @param minmax the real model
     */
    public MinSpinModel(MinMaxSpinModel minmax) {
        this.minmax = minmax;
        editor = new JTextField();
        editor.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                applyEditor();
            }
            public void focusGained(FocusEvent e) {
            }
        });
    }

    public Object getNextValue() {
        Object ret = minmax.getNextMinValue();
        editor.setText(ret.toString());
        return ret;
    }

    public Object getPreviousValue() {
        Object ret = minmax.getPreviousMinValue();
        editor.setText(ret.toString());
        return ret;
    }

    public Object getValue() {
        Object ret = minmax.getMinValue();
        editor.setText(ret.toString());
        return ret;
    }

    public void setValue(Object value) {
        minmax.setMinValue(value);
        update();
    }

    /**
     * update the display
     */
    public void update() {
        editor.setText(minmax.getMinValue().toString());
        fireStateChanged();
    }
    
    protected void applyEditor() {
        minmax.setMinValue(editor.getText());
        update();
    }
    
    /**
     * @return the associated editor.
     */
    public JComponent getEditor() {
        return editor;
    }
}
