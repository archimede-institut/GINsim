package org.ginsim.core.utils.data;

import java.util.List;

/**
 * Default implementation of <code>GsValueInList</code>.
 * This implementation uses a List to store the possible values.
 */
public class ValueList<T> {
    private List<T> values;
    private int selected;
    private String s_none;

    public ValueList(List<T> values) {
    	this(values, 0);
    }

    /**
     * @param v_values
     * @param selected
     */
    public ValueList(List<T> values, int selected) {
        this(values, selected, "");
    }
    /**
     * @param v_values
     * @param s_none
     */
    public ValueList(List<T> values, String s_none) {
        this(values, -1, s_none);
    }
    /**
     * @param v_values
     * @param selected
     * @param s_none
     */
    public ValueList(List<T> values, int selected, String s_none) {
        this.values = values;
        this.selected = selected;
        this.s_none = s_none;
    }
    /**
     * @param v_values
     * @param selected
     * @param s_none
     */
    public void reset(List<T> values, int selected, String s_none) {
        this.values = values;
        this.selected = selected;
        this.s_none = s_none;
    }
    
    public String toString() {
        if (selected == -1) {
            return s_none;
        }
        return values.get(selected).toString();
    }
    /**
     * @return the index of the selected value, or -1 if none/empty
     */
    public int getSelectedIndex() {
        return selected;
    }
    /**
     * @param index
     */
    public void setSelectedIndex(int index) {
        selected = index;
    }
    /**
     * @return the number of values
     */
    public int size() {
        return values.size();
    }
    /**
     * @param index
     * @return the corresponding value
     */
    public T get(int index) {
        if (index < 0 || index >= values.size()) {
            return null;
        }
        return values.get(index);
    }
    
    public int indexOf(Object anObject) {
        return values.indexOf(anObject);
    }
}
