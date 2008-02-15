package fr.univmrs.tagc.common.datastore;

import java.util.List;

/**
 * Default implementation of <code>GsValueInList</code>.
 * This implementation uses a Vector to store the possible values.
 */
public class ValueList {
    private List v_values;
    private int selected;
    private String s_none;
    
    /**
     * @param v_values
     * @param selected
     */
    public ValueList(List v_values, int selected) {
        this(v_values, selected, "");
    }
    /**
     * @param v_values
     * @param s_none
     */
    public ValueList(List v_values, String s_none) {
        this(v_values, -1, s_none);
    }
    /**
     * @param v_values
     * @param selected
     * @param s_none
     */
    public ValueList(List v_values, int selected, String s_none) {
        this.v_values = v_values;
        this.selected = selected;
        this.s_none = s_none;
    }
    /**
     * @param v_values
     * @param selected
     * @param s_none
     */
    public void reset(List v_values, int selected, String s_none) {
        this.v_values = v_values;
        this.selected = selected;
        this.s_none = s_none;
    }
    
    public String toString() {
        if (selected == -1) {
            return s_none;
        }
        return v_values.get(selected).toString();
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
        return v_values.size();
    }
    /**
     * @param index
     * @return the corresponding value
     */
    public Object get(int index) {
        if (index < 0 || index >= v_values.size()) {
            return null;
        }
        return v_values.get(index);
    }
    
    public int indexOf(Object anObject) {
        return v_values.indexOf(anObject);
    }
}
