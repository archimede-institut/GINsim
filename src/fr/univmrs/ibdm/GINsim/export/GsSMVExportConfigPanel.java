package fr.univmrs.ibdm.GINsim.export;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Map;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutantDef;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class GsSMVExportConfigPanel extends JPanel {
	private static final long serialVersionUID = -7398674287463858306L;

	private GsSMVexportConfig cfg;
	
	private JTable blockTable;
    JRadioButton radioSync = null;
    JRadioButton radioAsync = null;
    JComboBox comboMutant = null;
    JButton butCfgMutant = null;
    JTextArea area;
    private JScrollPane jsp;
    private GsSMVConfigModel model;
    private GsMutantModel mutantModel;
    
    private boolean mutant;
    private boolean test;

    public GsSMVExportConfigPanel(boolean mutant, boolean test) {
    	this.mutant = mutant;
    	this.test = test;
    	initialize();
    }
    
    public void setCfg(GsSMVexportConfig cfg) {
    	applyTest();
    	this.cfg = cfg;
    	if (cfg == null) {
    		return;
    	}
		model = new GsSMVConfigModel(cfg.graph.getNodeOrder(), cfg.m_initStates);
		blockTable.setModel(model);
		if (cfg.isSync()) {
			radioSync.setSelected(true);
		} else {
			radioAsync.setSelected(true);
		}
		if (mutant) {
    		mutantModel = new GsMutantModel(cfg);
    		comboMutant.setModel(mutantModel);
    		comboMutant.setSelectedItem(cfg.mutant);
		}
		if (test) {
			area.setText(cfg.thetest);
		}
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
        cst.gridy = 3;
        cst.weightx = 1;
        cst.weighty = 1;
        cst.fill = GridBagConstraints.BOTH;
        if (test) {
	        JSplitPane splitpane = new JSplitPane();
	        add(splitpane, cst);
	        splitpane.setLeftComponent(getJsp());
	        
        	area = new JTextArea();
        	area.setLineWrap(true);
	        area.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					applyTest();
				}
				public void focusGained(FocusEvent e) {
				}
			});
	        splitpane.setRightComponent(area);
        } else {
        	add(getJsp(), cst);
        }
        
        cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 0;
        add(radioSync, cst);
        cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 1;
        add(radioAsync, cst);
        
        radioSync.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				applyMode();
			}
		});
        
        if (mutant) {
	        comboMutant = new JComboBox();
	        // TODO: add back config mutant button, when blocking is removed
//	        butCfgMutant = new JButton(Translator.getString("STR_configure"));
//	        butCfgMutant.addActionListener(new ActionListener() {
//	            public void actionPerformed(ActionEvent e) {
//	                GsRegulatoryMutants.editMutants(graph);
//	            }
//	        });
	        cst = new GridBagConstraints();
	        cst.gridx = 0;
	        cst.gridy = 2;
	        add(comboMutant, cst);
//	        cst = new GridBagConstraints();
//	        cst.gridx = 1;
//	        cst.gridy = 2;
//	        add(butCfgMutant, cst);
        }
	}
	
	protected void applyMode() {
		if (cfg == null) {
			return;
		}
		if (radioSync.isSelected()) {
			cfg.type = GsSMVexportConfig.CFG_SYNC;
		} else {
			cfg.type = GsSMVexportConfig.CFG_ASYNC;
		}
	}
	
	protected void applyTest() {
		if (cfg == null) {
			return;
		}
		cfg.setTest(area.getText());
	}
	
	private JTable getBlockTable() {
		if (blockTable == null) {
			blockTable = new GsJTable();
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
     * @return the selected mutant (can be null)
     */
    public GsRegulatoryMutantDef getMutant() {
        if (mutantModel.getSelectedItem() instanceof GsRegulatoryMutantDef) {
            return (GsRegulatoryMutantDef)mutantModel.getSelectedItem();
        }
        return null;
    }
}


/**
 * tableModel to configure gene state blockers
 */
class GsSMVConfigModel extends AbstractTableModel {

    private static final long serialVersionUID = 864660594916225977L;
    private Vector nodeOrder;
    Map m_initstates;

    
    /**
     * @param nodeOrder
     * @param t_min
     * @param t_max
     * @param initstates
     */
    public GsSMVConfigModel(Vector nodeOrder, Map m_initstates) {
        this.nodeOrder = nodeOrder;
        this.m_initstates = m_initstates;
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
        Object value = null;
        switch (columnIndex) {
            case 0:
                return nodeOrder.get(rowIndex);
            case 1:
                value = m_initstates.get(nodeOrder.get(rowIndex));
                break;
            default:
                return null;
        }
        if (value == null) {
            return "";
        }
        return value.toString();
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
                m_initstates.remove(nodeOrder.get(rowIndex));
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
            	m_initstates.remove(nodeOrder.get(rowIndex));
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
            	m_initstates.put(nodeOrder.get(rowIndex), new Integer(val));
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
    GsSMVexportConfig cfg;
    
    GsMutantModel(GsSMVexportConfig cfg) {
    	this.cfg = cfg;
        this.listMutants = (GsRegulatoryMutants)cfg.graph.getObject(GsMutantListManager.key, true);
    }
    
    void setMutantList(GsRegulatoryMutants mutants) {
            this.listMutants = mutants;
            fireContentsChanged(this, 0, getSize());
    }

    public Object getSelectedItem() {
        if (cfg.mutant == null) {
            return "--";
        }
        return cfg.mutant;
    }

    public void setSelectedItem(Object anObject) {
        super.setSelectedItem(anObject);
        if (anObject instanceof GsRegulatoryMutantDef) {
            cfg.mutant = (GsRegulatoryMutantDef)anObject;
        } else {
            cfg.mutant = null;
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