package org.ginsim.gui.utils.data.models;

import javax.swing.AbstractSpinnerModel;

/**
 * model for a min spinbutton, depends on a GsMinMaxSpinModel
 */
public class MinSpinModel extends AbstractSpinnerModel {
    
    MinMaxSpinModel minmax;
    
    /**
     * @param minmax the real model
     */
    public MinSpinModel(MinMaxSpinModel minmax) {
        this.minmax = minmax;
    }

    public Object getNextValue() {
        Object ret = minmax.getNextMinValue();
        return ret;
    }

    public Object getPreviousValue() {
        Object ret = minmax.getPreviousMinValue();
        return ret;
    }

    public Object getValue() {
        Object ret = minmax.getMinValue();
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
        fireStateChanged();
    }
    
    protected void applyEditor() {
        update();
    }
}
