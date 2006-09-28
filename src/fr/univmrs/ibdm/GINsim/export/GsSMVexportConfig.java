package fr.univmrs.ibdm.GINsim.export;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutantDef;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * Configure SMV export
 */
public class GsSMVexportConfig extends JPanel {

	private static final long serialVersionUID = -7398674287463858306L;
	private JTable blockTable;

    GsRegulatoryGraph graph;
    
    JRadioButton radioSync = null;
    JRadioButton radioAsync = null;
    JComboBox comboMutant = null;
    JButton butCfgMutant = null;
    private JScrollPane jsp;
    private GsSMVConfigModel model;
    private GsMutantModel mutantModel;
    short[] initstates;
    
	/**
	 * @param graph
	 */
	public GsSMVexportConfig(GsRegulatoryGraph graph) {
        initstates = new short[graph.getNodeOrder().size()];
        for (int i=0 ; i<initstates.length ; i++) {
            initstates[i] = -1;
        }
        this.graph = graph;
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

        model = new GsSMVConfigModel(graph.getNodeOrder(), initstates);
        mutantModel = new GsMutantModel(GsRegulatoryMutants.getMutants(graph));
        comboMutant = new JComboBox(mutantModel);
        // TODO: add back config mutant button, when blocking is removed
//        butCfgMutant = new JButton(Translator.getString("STR_configure"));
//        butCfgMutant.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                GsRegulatoryMutants.editMutants(graph);
//            }
//        });
        
        GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 3;
        cst.weightx = 1;
        cst.weighty = 1;
        cst.fill = GridBagConstraints.BOTH;
        cst.gridwidth = 3;
        add(getJsp(), cst);
        
        cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 0;
        add(radioSync, cst);
        cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 1;
        add(radioAsync, cst);
        cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 2;
        add(comboMutant, cst);
//        cst = new GridBagConstraints();
//        cst.gridx = 1;
//        cst.gridy = 2;
//        add(butCfgMutant, cst);
        
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
     * @return the selected mutant (can be null)
     */
    public GsRegulatoryMutantDef getMutant() {
        if (mutantModel.getSelectedItem() instanceof GsRegulatoryMutantDef) {
            return (GsRegulatoryMutantDef)mutantModel.getSelectedItem();
        }
        return null;
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
        return 2;
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

class GsMutantModel extends DefaultComboBoxModel implements ComboBoxModel {
    private static final long serialVersionUID = 2348678706086666489L;
    
    GsRegulatoryMutants listMutants;
    GsRegulatoryMutantDef mutant = null;
    
    GsMutantModel(GsRegulatoryMutants listMutants) {
        this.listMutants = listMutants;
    }
    
    void setMutantList(GsRegulatoryMutants mutants) {
            this.listMutants = mutants;
            fireContentsChanged(this, 0, getSize());
    }

    public Object getSelectedItem() {
        if (mutant == null) {
            return "--";
        }
        return mutant;
    }

    public void setSelectedItem(Object anObject) {
        super.setSelectedItem(anObject);
        if (anObject instanceof GsRegulatoryMutantDef) {
            mutant = (GsRegulatoryMutantDef)anObject;
        } else {
            mutant = null;
        }
    }

    public Object getElementAt(int index) {
        if (index == 0 || listMutants == null) {
            return "--";
        }
        return listMutants.getElement(index-1);
    }

    public int getSize() {
        if (listMutants == null) {
            return 1;
        }
        return listMutants.getNbElements()+1;
    }
}