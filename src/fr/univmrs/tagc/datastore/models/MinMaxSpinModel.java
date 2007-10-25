package fr.univmrs.tagc.datastore.models;

import javax.swing.JSpinner;

/**
 * GsMinMaxSpinModel: model controlling min and max spinbuttons
 */
public interface MinMaxSpinModel {
    /**
     * try to increase the max value
     * @return the new max value (may have changed or not)
     */
    public abstract Object getNextMaxValue();

    /**
     * try to decrease the max value
     * @return the new max value (may have changed or not)
     */
    public abstract Object getPreviousMaxValue();

    /**
     * @return the current max value.
     */
    public abstract Object getMaxValue();

    /**
     * 
     * @param value
     */
    public abstract void setMaxValue(Object value);

    /**
     * try to increase the min value
     * @return the new min value (may have changed or not)
     */
    public abstract Object getNextMinValue();

    /**
     * try to decrease the min value
     * @return the new min value (may have changed or not)
     */
    public abstract Object getPreviousMinValue();

    /**
     * @return the current min value.
     */
    public abstract Object getMinValue();

    /**
     * set the min value.
     * @param value
     */
    public abstract void setMinValue(Object value);
    
    /**
     * @return a min spinner following this model
     */
    public abstract JSpinner getSMin();
    
    /**
     * @return a max spinner following this model
     */
    public abstract JSpinner getSMax();

	public abstract String getMinName();
	public abstract String getMaxName();

	public abstract void setEditedObject(Object rawValue);
}