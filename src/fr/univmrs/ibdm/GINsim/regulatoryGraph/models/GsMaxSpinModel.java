package fr.univmrs.ibdm.GINsim.regulatoryGraph.models;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JComponent;
import javax.swing.JTextField;


/**
 * model for a max spinbutton, depends on a GsMinMaxSpinModel
 */
public class GsMaxSpinModel extends AbstractSpinnerModel {

    private GsMinMaxSpinModel minmax;
    private JTextField editor;
    /**
     * @param minmax the real model
     */
    public GsMaxSpinModel(GsMinMaxSpinModel minmax) {
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
        Object ret = minmax.getNextMaxValue();
        editor.setText(ret.toString());
        return ret;
    }

    public Object getPreviousValue() {
        Object ret = minmax.getPreviousMaxValue();
        editor.setText(ret.toString());
        return ret;
    }

    public Object getValue() {
        Object ret = minmax.getMaxValue();
        editor.setText(ret.toString());
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
        editor.setText(minmax.getMaxValue().toString());
        fireStateChanged();
    }

    protected void applyEditor() {
        minmax.setMaxValue(editor.getText());
        update();
    }

    /**
     * @return the associated editor.
     */
    public JComponent getEditor() {
        return editor;
    }
}
