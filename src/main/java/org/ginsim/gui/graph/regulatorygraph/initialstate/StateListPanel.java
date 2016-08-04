package org.ginsim.gui.graph.regulatorygraph.initialstate;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateList;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.gui.utils.widgets.Label;
import org.ginsim.gui.utils.widgets.StockButton;

/**
 * Manager for the list of states in the initial state panel
 * @see CompleteStatePanel
 * @see InitialStatePanel
 * 
 * @version 2.0
 */
public class StateListPanel extends JPanel {

    private static final long serialVersionUID = -7273296658666786369L;

    private JScrollPane jScrollPane = null;
    private EnhancedJTable tableInitStates = null;
    private InitStateTableModel model = null;
    
    private JButton buttonDelStateRow = null;
    private JButton buttonCopyStateRow = null;
    private JButton buttonUp = null;
    private JButton buttonDown = null;
    private JButton buttonSelect = null;

    private Insets topInset = new Insets(20,0,0,0);
	private NamedStateList stateList;
	private InitialStatePanel panel;
	private boolean several;
	

	/***********************/
	/** SELECTION METHODS **/
	/***********************/

	/**
	 * Get the list of states
	 * @return list of named states
	 */
	public NamedStateList getStateList(){
		return stateList;
	}
		
	/**
	 * Get a list with the selected states
	 * @param input whether the states are from input or normal variables
	 * @return list of selected states
	 */
	public NamedStateList getSelectedStateList(boolean input){
		NamedStateList selected = new NamedStateList(stateList.getNodeOrder(),input); 
        for (int i=0 ; i<model.getRowCount() ; i++)
        	if((Boolean)model.getValueAt(i,1)) selected.add(stateList.get(i));
		return selected;
	}
	
	/**
	 * Get a list with the selected oracle-patterns
	 * @param input whether the oracles are from input or normal variables
	 * @return list of selected oracles
	 */
	public NamedStateList getSelectedOracleStateList(boolean input) {
		NamedStateList selected = new NamedStateList(stateList.getNodeOrder(),input); 
        for (int i=0 ; i<model.getRowCount() ; i++)
        	if((Boolean)model.getValueAt(i,2)) selected.add(stateList.get(i));
		return selected;
	}
	
	/**
	 * Get a boolean array defining the indexes of the selected oracles
	 * @return array with true entries on the indexes of the selected oracles
	 */
	public boolean[] getOracleSelection() {
		boolean[] res = new boolean[model.getRowCount()];
		for (int i=0 ; i<model.getRowCount() ; i++) 
			res[i]=(Boolean)model.getValueAt(i,2);
		return res;
	}
	
	/**
	 * Get a boolean array defining the indexes of the selected states
	 * @return array with true entries on the indexes of the selected states
	 */
	public boolean[] getSelection(){
		boolean[] res = new boolean[model.getRowCount()];
		for (int i=0 ; i<model.getRowCount() ; i++) 
			res[i]=(Boolean)model.getValueAt(i,1);
		return res;
	}

	/*****************/
	/**** SETTERS ****/
	/*****************/

    /**
     * Updates the current table given a map of the state patterns
     * @param currentParameter the states for populating the table
     * @param names the names of the states
     */
    public void setParam(Map currentParameter, List<String> names) {
		model.setParam(currentParameter);
        for (int i=0 ; i<model.getRowCount() ; i++) {
        	if(i==names.size()) model.setValueAt("states_", i, 0);
        	else model.setValueAt(names.get(i), i, 0);
        	model.setValueAt(Boolean.TRUE, i, 1);
            model.setValueAt(Boolean.FALSE, i, 2);
        }
	}
    
    /**
     * Updates the current table given a map of the state patterns
     * @param currentParameter the states for populating the table
     */
    public void setParam(Map currentParameter) {
		model.setParam(currentParameter);
        for (int i=0 ; i<model.getRowCount() ; i++) {
            model.setValueAt("states_"+i, i, 0);
            model.setValueAt(Boolean.TRUE, i, 1);
            model.setValueAt(Boolean.FALSE, i, 2);
        }
	}

	/**
	 * Sets the selected oracles based on a boolean array
	 * @param selection array with true entries on the indexes to be selected
	 */
	public void setOracleSelection(boolean[] selection){
		for (int i=0, l=model.getRowCount(); i<l; i++)
        	model.setValueAt(false,i,2);
		for (int i=0, l=Math.min(selection.length, model.getRowCount()); i<l; i++)
        	model.setValueAt(selection[i],i,2);
	}
	
	/**
	 * Sets the selected states based on a boolean array
	 * @param selection array with true entries on the indexes to be selected
	 */
	public void setSelection(boolean[] selection){
		for (int i=0, l=model.getRowCount(); i<l; i++)
        	model.setValueAt(false,i,1);
		for (int i=0, l=Math.min(selection.length, model.getRowCount()); i<l; i++)
        	model.setValueAt(selection[i],i,1);
	}

	public void setDisabledEdition(boolean[] selection) {
		model.setEdition(selection);
	}
	public boolean[] getDisabledEdition() {
		return model.getEdition();
	}

	/**
	 * Disables the selection of the given set of patterns
	 * @param patterns to affect selection
	 */
	public void addDisabledEdition(List<NamedState> states) {
		model.disabledEdition(states);
	}
	
	
	/************************************/
	/** CONSTRUCTOR AND INITIALIZATION **/
	/************************************/
	
    /**
     * Initializes the table of states
     * @param panel the panel enclosing this table
     * @param stateList the list of states
     * @param several whether multiple states are allowed
     * @param title the title of the table
     */
    public StateListPanel(InitialStatePanel panel, NamedStateList stateList, boolean several, String title) {
    	this.panel = panel;
        this.several = several;
    	this.stateList = stateList;
        setBorder(BorderFactory.createTitledBorder(title));
    	initialize();
    }
	private void initialize() {
		setLayout(new GridBagLayout());
        GridBagConstraints c;
        if (!several) {
	        c = new GridBagConstraints();
	        c.gridx = 0;
	        c.gridy = 0;
	        c.gridwidth = 4;
	        c.fill = GridBagConstraints.BOTH;
	        c.weightx = 1;
	        add(new Label("STR_singleInit_descr", Label.MESSAGE_NORMAL), c);
        }
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        add(getButtonCopyStateRow(), c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        add(getButtonDelStateRow(), c);
        
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        add(getButtonUp(), c);
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        add(getButtonDown(), c);

        if (several) {
            c = new GridBagConstraints();
            c.gridx = 4;
            c.gridy = 1;
            c.anchor = GridBagConstraints.WEST;
            add(getButtonSelect(), c);
        }
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 5;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(getJScrollPane(), c);
	}
	
    /**
     * This method initializes tableInitStates
     * @return javax.swing.JTable
     */
    private JTable getTableInitStates() {
        if(tableInitStates == null) {
        	tableInitStates = new EnhancedJTable();
            model = new InitStateTableModel(panel, stateList, several);
            tableInitStates.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
            tableInitStates.setModel(model);
            tableInitStates.getTableHeader().setReorderingAllowed(false);
            tableInitStates.setRowSelectionAllowed(true);
            tableInitStates.setColumnSelectionAllowed(true);
            model.setTable(tableInitStates);
        }
        return tableInitStates;
    }
    
    /**
     * The table's structure has changed, update it. 
     * note: don't try to implement tablemodellistener: when this one is called, 
     * the table structure change may not be applied yet in the column model
     */
    public void updateTable() {
    	//System.out.println("Update tabble");
        Enumeration e_col = tableInitStates.getColumnModel().getColumns();
        while (e_col.hasMoreElements()) {
            TableColumn col = (TableColumn)e_col.nextElement();
            int w = 15+8*5;
            col.setPreferredWidth(w+10);
            col.setMinWidth(w);
        }
    }

    /**
     * This method initializes jScrollPane
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if(jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getTableInitStates());
        }
        return jScrollPane;
    }
    protected void deleteStateRow() {
    	int[] t = tableInitStates.getSelectedRows();
    	for (int i=t.length-1 ; i>=0 ; i--) {
    		model.deleteRow(t[i]);
    	}
    }
    
    protected void copyStateRow() {
    	int[] t = tableInitStates.getSelectedRows();
    	for (int i=0 ; i<t.length ; i++) {
    		model.copyLine(t[i]);
    	}
    }
    
    protected void move(int direction) {
    	int[] t = tableInitStates.getSelectedRows();
    	model.moveLine(t, direction);
    	
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)tableInitStates.getSelectionModel();
        selectionModel.clearSelection();
        int maxIndex = model.getRowCount()-1;
        for (int i=0 ; i<t.length ; i++) {
        	int index = t[i];
        	if (index < 0 || index >= maxIndex) {
        		LogManager.error("Incoherent selection after moving lines: "+index);
        	}
            selectionModel.addSelectionInterval(index, index);
        }
    }

    protected void selectAll() {
        model.toggleSelectAll();
    }
    
    private JButton getButtonDelStateRow() {
        if (buttonDelStateRow == null) {
            buttonDelStateRow = new StockButton("list-remove.png", true);
            buttonDelStateRow.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteStateRow();
                }
            });
        }
        return buttonDelStateRow;
    }
    private JButton getButtonCopyStateRow() {
        if (buttonCopyStateRow == null) {
        	buttonCopyStateRow = new StockButton("edit-copy.png", true);
        	buttonCopyStateRow.setToolTipText(Txt.t("STR_duplicate_rows"));
        	buttonCopyStateRow.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    copyStateRow();
                }
            });
        }
        return buttonCopyStateRow;
    }

    private JButton getButtonUp() {
        if (buttonUp == null) {
        	buttonUp = new StockButton("go-up.png", true);
        	buttonUp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    move(-1);
                }
            });
        }
        return buttonUp;
    }

    private JButton getButtonDown() {
        if (buttonDown == null) {
            buttonDown = new StockButton("go-down.png", true);
            buttonDown.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    move(1);
                }
            });
        }
        return buttonDown;
    }

    private JButton getButtonSelect() {
        if (buttonSelect == null) {
            buttonSelect = new StockButton("edit-select-all.png", true);
            buttonSelect.setToolTipText("(un)select all patterns");
            buttonSelect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectAll();
                }
            });
        }
        return buttonSelect;
    }

}
