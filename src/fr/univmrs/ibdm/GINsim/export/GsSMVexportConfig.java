package fr.univmrs.ibdm.GINsim.export;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * Configure SMV export
 */
public class GsSMVexportConfig extends JPanel {

	private static final long serialVersionUID = -7398674287463858306L;
	private JTable blockTable;

    JRadioButton radioSync = null;
    JRadioButton radioAsync = null;
    private JScrollPane jsp;
    private GsSMVConfigModel model;
    short[] initstates;
    
	/**
	 * @param frame
	 * @param nodeOrder
	 */
	public GsSMVexportConfig(Vector nodeOrder) {
        initstates = new short[nodeOrder.size()];
        for (int i=0 ; i<initstates.length ; i++) {
            initstates[i] = -1;
        }
        model = new GsSMVConfigModel(nodeOrder, initstates);
		initialize();
	}

	private void initialize() {
		this.setSize(150, 250);
        setLayout(new GridBagLayout());
        ButtonGroup group = new ButtonGroup();
        radioSync = new JRadioButton("synchronous");
        radioAsync = new JRadioButton("asynchronous");
        group.add(radioAsync);
        group.add(radioSync);
        GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 2;
        cst.weightx = 1;
        cst.weighty = 1;
        cst.fill = GridBagConstraints.BOTH;
        add(getJsp(), cst);
        
        cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 0;
        add(radioSync, cst);
        cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 1;
        add(radioAsync, cst);
        
        radioAsync.setSelected(true);
	}
	
	private JTable getBlockTable() {
		if (blockTable == null) {
			blockTable = new GsJTable();
			blockTable.setModel(model);
		}
		return blockTable;
	}
	
    private JScrollPane getJsp() {
        if (jsp == null) {
            jsp = new JScrollPane();
            jsp.setViewportView(getBlockTable());
        }
        return jsp;
    }

    /**
     * refresh the state blocking.
     * @param nodeOrder
     */
    public void refresh(Vector nodeOrder) {
        model.refresh(nodeOrder);
    }

    /**
     * @return true if the "sync" option has been selected
     */
    public boolean isSync() {
        return radioSync.isSelected();
    }
    /**
     * @return an array giving desired initial states (-1 for no constraint)
     * TODO: share this initial state with simulation parameters ?
     */
    public short[] getInitStates() {
        return initstates;
    }
}


/**
 * tableModel to configure gene state blockers
 */
class GsSMVConfigModel extends AbstractTableModel {

    private static final long serialVersionUID = 864660594916225977L;
    private Vector nodeOrder;
    short[] initstates;

    
    /**
     * @param nodeOrder
     * @param t_min
     * @param t_max
     * @param initstates
     */
    public GsSMVConfigModel(Vector nodeOrder, short[] initstates) {
        this.nodeOrder = nodeOrder;
        this.initstates = initstates;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return nodeOrder.size();
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 4;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Translator.getString("STR_node");
        case 1:
            return Translator.getString("STR_initial");
        case 2:
            return Translator.getString("STR_min");
        case 3:
            return Translator.getString("STR_max");
        }
        return null;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex) {
        if (columnIndex > 0 && columnIndex < 4) {
            return String.class;
        }
        return Object.class;
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (rowIndex > getRowCount()) {
            return false;
        }
        switch (columnIndex) {
            case 1:
            case 2:
            case 3:
                return true;
        }
        return false;
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex > getRowCount()) {
            return null;
        }
        int value = -1;
        switch (columnIndex) {
            case 0:
                return nodeOrder.get(rowIndex);
            case 1:
                value = initstates[rowIndex];
                break;
            case 2:
                value = ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).getBlockMin();
                break;
            case 3:
                value = ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).getBlockMax();
                break;
            default:
                return null;
        }
        if (value == -1) {
            return "";
        }
        return ""+value;
    }

    /**
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount() || columnIndex < 1 || columnIndex > 3) {
            return;
        }
        
        if ("".equals(aValue) || "-".equals(aValue)) {
            switch(columnIndex) {
            case 1:
                initstates[rowIndex] = -1;
                fireTableCellUpdated(rowIndex, 1);
                break;
            case 2:
            case 3:
                ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMin((short)-1);
                ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMax((short)-1);
                fireTableCellUpdated(rowIndex, 2);
                fireTableCellUpdated(rowIndex, 3);
            }
            return;
        }
        
        int val;
        try {
            val = Integer.parseInt((String)aValue);
        } catch (Exception e) {
            return;
        }
        
        if (val == -1) {
            switch(columnIndex) {
            case 1:
                initstates[rowIndex] = -1;
                fireTableCellUpdated(rowIndex, 1);
                break;
            case 2:
            case 3:
                ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMin((short)-1);
                ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMax((short)-1);
                fireTableCellUpdated(rowIndex, 2);
                fireTableCellUpdated(rowIndex, 3);
            }
            return;
        }
        if (val < 0 || val > ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).getMaxValue()) {
            return;
        }
        switch (columnIndex) {
            case 1:
                initstates[rowIndex] = (short)val;
                break;
            case 2:
                ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMin((short)val);
                break;
            case 3:
                ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMax((short)val);
                break;
        }
        fireTableCellUpdated(rowIndex, 1);
        fireTableCellUpdated(rowIndex, 2);
        fireTableCellUpdated(rowIndex, 3);
    }
    /**
     * refresh the state blocking.
     * @param nodeOrder
     * @param minBlock
     * @param maxBlock
     */
    public void refresh(Vector nodeOrder) {
        this.nodeOrder = nodeOrder;
        fireTableStructureChanged();
    }
}

