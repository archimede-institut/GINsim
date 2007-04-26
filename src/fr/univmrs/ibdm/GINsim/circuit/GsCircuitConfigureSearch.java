package fr.univmrs.ibdm.GINsim.circuit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsMaxSpinModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsMinMaxSpinModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsMinSpinModel;

/**
 * configure the circuit-search.
 */
public class GsCircuitConfigureSearch extends JPanel {
    private static final long serialVersionUID = 412805818821147248L;

    private GsCircuitSearchStoreConfig config;
    
    private JScrollPane jsp = null;
    private JTable jtable = null;
    private GsCircuitConfigModel model;
    private GsCircuitFrame circuitFrame;
    private GsCircuitSpinModel smodel;
    private JButton buttonReset;
    private Vector nodeOrder;
    
    /**
     * create the configuration window.
     * 
     * @param frame
     * @param config
     * @param nodeOrder 
     */
    public GsCircuitConfigureSearch(GsCircuitFrame frame, GsCircuitSearchStoreConfig config, Vector nodeOrder) {
        this.circuitFrame = frame;
        this.nodeOrder = nodeOrder;
        this.config = config;
        smodel = new GsCircuitSpinModel(config, frame);
        initialize();
        setVisible(true);
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel(Translator.getString("STR_min")), c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel(Translator.getString("STR_max")), c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.EAST;
        add(getButtonReset(), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(smodel.getSMin(), c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(smodel.getSMax(), c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(getJsp(), c);
    }
    
    
    private JScrollPane getJsp() {
        if (jsp == null) {
            jsp = new JScrollPane();
            jsp.setViewportView(getJTable());
        }
        return jsp;
    }
    
    private JTable getJTable() {
        if (jtable == null) {
            model = new GsCircuitConfigModel(circuitFrame, config.v_list, config.t_status, config.t_constraint);
            jtable = new GsJTable(model);
        }
        return jtable;
    }
    
    private JButton getButtonReset() {
        if (buttonReset == null) {
            buttonReset = new JButton(Translator.getString("STR_reset"));
            buttonReset.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    reset();
                }
            });
        }
        return buttonReset;
    }
    protected void reset() {
        for (int i=0 ; i<nodeOrder.size() ; i++) {
            GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);
            config.t_status[i] = 3;
            config.t_constraint[i][0] = 0;
            config.t_constraint[i][1] = vertex.getMaxValue();
        }
        model.fireTableRowsUpdated(0, nodeOrder.size());
    }

}


class GsCircuitConfigModel extends DefaultTableModel {
    private static final long serialVersionUID = -8900180159435512429L;
 
    private Vector v_list;
    private short[] t_status;
    private short[][] t_constraint;
    private GsCircuitFrame frame;
    
    /**
     * 
     * @param frame
     * @param v_list
     * @param t_status
     * @param t_constraint 
     */
    public GsCircuitConfigModel(GsCircuitFrame frame, Vector v_list, short[] t_status, short[][] t_constraint) {
        this.frame = frame;
        this.v_list = v_list;
        this.t_status = t_status;
        this.t_constraint = t_constraint;
    }

    public int getColumnCount() {
        return 6;
    }

    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "gene";
            case 1:
                return "must";
            case 2:
                return "must not";
            case 3:
                return "any";
            case 4:
                return "test min";
            case 5:
                return "max";
        }
        return super.getColumnName(column);
    }

    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
            case 2:
            case 3:
                return Boolean.class;
            case 4:
            case 5:
                return Integer.class;
        }
        return super.getColumnClass(columnIndex);
    }

    public int getRowCount() {
        if (t_status == null) {
            return 0;
        }
        return t_status.length;
    }

    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return v_list.get(row);
            case 1:
            case 2:
            case 3:
                return (t_status[row] == column) ? Boolean.TRUE : Boolean.FALSE;
            case 4:
                if (t_constraint[row][0] == 0) {
                    return "";
                }
                return ""+t_constraint[row][0];
            case 5:
                if (t_constraint[row][1] == ((GsRegulatoryVertex)v_list.get(row)).getMaxValue()) {
                    return "";
                }
                return ""+t_constraint[row][1];
        }
        return super.getValueAt(row, column);
    }
    
    public void setValueAt(Object aValue, int row, int column) {
        if (column > 0 && column <= 3) {
            t_status[row] = (short)column;
            fireTableRowsUpdated(row, row);
            frame.updateStatus(GsCircuitFrame.STATUS_NONE);
        } else if (column == 4) {
            if (aValue == null) {
                t_constraint[row][0] = 0;
            } else {
                short val = ((Integer)aValue).shortValue();
                if (val > 0 && val <= ((GsRegulatoryVertex)v_list.get(row)).getMaxValue()) {
                    t_constraint[row][0] = val;
                    if (t_constraint[row][0] > t_constraint[row][1]) {
                        t_constraint[row][1] = t_constraint[row][0];
                    }
                }
            }
            fireTableRowsUpdated(row, row);
            frame.updateStatus(GsCircuitFrame.STATUS_NONE);
        } else if (column == 5) {
            if (aValue == null) {
                t_constraint[row][1] = ((GsRegulatoryVertex)v_list.get(row)).getMaxValue();
            } else {
                short val = ((Integer)aValue).shortValue();
                if (val > 0 && val <= ((GsRegulatoryVertex)v_list.get(row)).getMaxValue()) {
                    t_constraint[row][1] = val;
                    if (t_constraint[row][0] > t_constraint[row][1]) {
                        t_constraint[row][0] = t_constraint[row][1];
                    }
                }
            }
            fireTableRowsUpdated(row, row);
            frame.updateStatus(GsCircuitFrame.STATUS_NONE);
        }
    }
}

class GsCircuitSpinModel implements GsMinMaxSpinModel {

    private GsCircuitSearchStoreConfig config;
    private GsMinSpinModel m_min;
    private GsMaxSpinModel m_max;

    private JSpinner smin = null;
    private JSpinner smax = null;
    
    GsCircuitFrame frame;
    
    /**
     * @param config
     * @param frame
     */
    public GsCircuitSpinModel(GsCircuitSearchStoreConfig config, GsCircuitFrame frame) {
        this.config = config;
        this.frame = frame;
        m_min = new GsMinSpinModel(this);
        m_max = new GsMaxSpinModel(this);
    }
    
    public Object getNextMaxValue() {
        if (config.maxlen < config.v_list.size()) {
            config.maxlen++;
        }
        return getMaxValue();
    }

    public Object getPreviousMaxValue() {
        if (config.maxlen > 0) {
            if (config.maxlen == config.minlen) {
                config.minlen--;
                updateMin();
            }
            config.maxlen--;
        }
        return getMaxValue();
    }

    public Object getMaxValue() {
        return ""+config.maxlen;
    }

    public void setMaxValue(Object value) {
        if (value instanceof String) {
            try {
                 int val = (short)Integer.parseInt(value.toString());
                 if (val >= 0 && val <= config.v_list.size()) {
                     config.maxlen = val;
                     if (val < config.minlen) {
                         config.minlen = val;
                         updateMin();
                     }
                 }
            } catch (NumberFormatException e) {}
        }    
        updateMax();
    }

    public Object getNextMinValue() {
        if (config.minlen == config.maxlen) {
            if (config.maxlen < config.v_list.size()) {
                config.maxlen++;
                config.minlen++;
                updateMax();
            }
        } else {
            config.minlen++;
        }
        return getMinValue();
    }

    public Object getPreviousMinValue() {
        if (config.minlen > 0) {
            config.minlen--;
        }
        return getMinValue();
    }

    public Object getMinValue() {
        return ""+config.minlen;
    }

    public void setMinValue(Object value) {
        if (value instanceof String) {
            try {
                 int val = (short)Integer.parseInt(value.toString());
                 if (val >= 0 && val <= config.v_list.size()) {
                     config.minlen = val;
                     if (val > config.maxlen) {
                         config.maxlen = val;
                         updateMax();
                     }
                 }
            } catch (NumberFormatException e) {}
        }    
        updateMin();
    }

    public JSpinner getSMin() {
        if (smin == null) {
            smin = new JSpinner(m_min);
            smin.setEditor(m_min.getEditor());
            smin.setSize(70, smin.getHeight());
        }
        return smin;
    }

    public JSpinner getSMax() {
        if (smax == null) {
            smax = new JSpinner(m_max);
            smax.setEditor(m_max.getEditor());
            smax.setSize(70, smax.getHeight());
        }
        return smax;
    }
    
    private void updateMin() {
        m_min.update();
        frame.updateStatus(GsCircuitFrame.STATUS_NONE);
    }
    private void updateMax() {
        m_max.update();
        frame.updateStatus(GsCircuitFrame.STATUS_NONE);
    }
}