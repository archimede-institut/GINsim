package org.ginsim.gui.utils.data.models;

import javax.swing.AbstractSpinnerModel;


/**
 * model for a max spinbutton, depends on a GsMinMaxSpinModel
 */
public class MaxSpinModel extends AbstractSpinnerModel {

    private MinMaxSpinModel minmax;
    /**
     * @param minmax the real model
     */
    public MaxSpinModel(MinMaxSpinModel minmax) {
        this.minmax = minmax;
    }

    public Object getNextValue() {
        Object ret = minmax.getNextMaxValue();
        return ret;
    }

    public Object getPreviousValue() {
        Object ret = minmax.getPreviousMaxValue();
        return ret;
    }

    public Object getValue() {
        Object ret = minmax.getMaxValue();
        return ret;
    }

    public void setValue(Object value) {
        minmax.setMaxValue(value);
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
