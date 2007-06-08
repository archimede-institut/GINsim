package fr.univmrs.ibdm.GINsim.export.regulatoryGraph;

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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.graph.GsExtensibleConfig;
import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStatePanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;

public class GsSMVExportConfigPanel extends JPanel {
	private static final long serialVersionUID = -7398674287463858306L;

	private GsSMVexportConfig cfg;
	
    JRadioButton radioSync = null;
    JRadioButton radioAsync = null;
    MutantSelectionPanel mutantPanel = null;
    JButton butCfgMutant = null;
    JTextArea area;
    private GsInitialStatePanel initPanel;
    private GsSMVConfigModel model;
    private GsMutantModel mutantModel;
    
    private boolean mutant;
    private boolean test;
    private GsStackDialog dialog;

    public GsSMVExportConfigPanel(GsExtensibleConfig config, GsStackDialog dialog, boolean mutant, boolean test) {
    	this.mutant = mutant;
    	this.test = test;
		this.dialog = dialog;
		if (config.getSpecificConfig() == null) {
			config.setSpecificConfig(new GsSMVexportConfig((GsRegulatoryGraph)config.getGraph()));
		}
		this.cfg = (GsSMVexportConfig)config.getSpecificConfig();
    	initialize();
    	if (cfg.type == GsSMVexportConfig.CFG_ASYNC) {
    		radioAsync.setSelected(true);
    	} else {
    		radioSync.setSelected(true);
    	}
	}

	private void initialize() {
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
	        JSplitPane splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	        add(splitpane, cst);
         	area = new JTextArea();
        	area.setLineWrap(true);
	        area.addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent e) {
					applyTest();
				}
				public void focusGained(FocusEvent e) {
				}
			});
	        splitpane.setBottomComponent(area);
	        splitpane.setTopComponent(getInitPanel());
	        splitpane.setDividerLocation(130);
        } else {
        	add(getInitPanel(), cst);
        }
        
        cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 0;
        cst.anchor = GridBagConstraints.WEST;
        add(radioSync, cst);
        cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.gridy = 1;
        cst.anchor = GridBagConstraints.WEST;
        add(radioAsync, cst);
        
        radioSync.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				applyMode();
			}
		});
        
        if (mutant) {
	        mutantPanel = new MutantSelectionPanel(dialog, cfg.graph, cfg);
	        cst = new GridBagConstraints();
	        cst.gridx = 0;
	        cst.gridy = 2;
	        add(mutantPanel, cst);
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

	private GsInitialStatePanel getInitPanel() {
		if (initPanel == null) {
			initPanel = new GsInitialStatePanel(dialog, cfg.graph, false);
			initPanel.setParam(cfg);
		}
		return initPanel;
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