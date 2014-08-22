package org.ginsim.gui.graph.regulatorygraph.initialstate;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.TableColumn;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateList;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesHandler;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesManager;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.gui.utils.widgets.Label;
import org.ginsim.gui.utils.widgets.StockButton;



public class InitialStatePanel extends JPanel {
    private static final long serialVersionUID = -572201856207494392L;
    
    private StateListPanel initPanel;
    private StateListPanel inputPanel;
    private JLabel messageLabel = new JLabel();
    
	public InitialStatePanel(NamedStatesHandler imanager, boolean several) {
		
	    initPanel = new StateListPanel(this, imanager.getInitialStates(), several, Txt.t("STR_Initial_state"));
	    inputPanel = new StateListPanel(this, imanager.getInputConfigs(), several, Txt.t("STR_Fixed_inputs"));
	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.weightx = 1;
	    c.fill = GridBagConstraints.HORIZONTAL;
	    add(messageLabel, c);
	    messageLabel.setForeground(Color.RED);
	    
	    c.weightx = c.weighty = 1;
        c.gridy = 1;
	    c.fill = GridBagConstraints.BOTH;
	    if (imanager.getNormalNodes().size() > 0) {
	        add(initPanel, c);
	    }
        c.gridy = 2;
        if (imanager.getInputNodes().size() > 0) {
            add(inputPanel, c);
        }
	}
    public InitialStatePanel(Graph graph, boolean several) {
        this((NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(graph, NamedStatesManager.KEY, true), several);
    }
    public void setParam(NamedStateStore currentParameter) {
        initPanel.setParam(currentParameter.getInitialState());
        inputPanel.setParam(currentParameter.getInputState());
    }
    
    public void setMessage(String message) {
    	this.messageLabel.setText(message);
    }
}

class StateListPanel extends JPanel {
    private static final long serialVersionUID = -7273296658666786369L;

    private JScrollPane jScrollPane = null;
    private EnhancedJTable tableInitStates = null;
    private InitStateTableModel model = null;
    
    private JButton buttonDelStateRow = null;
    private JButton buttonCopyStateRow = null;
    private JButton buttonUp = null;
    private JButton buttonDown = null;
    private JButton buttonSelect = null;

    Insets topInset = new Insets(20,0,0,0);
	private NamedStateList stateList;
	private InitialStatePanel panel;
	private boolean several;
	
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
     * 
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
     * the table's structure has changed, update it. 
     * note: don't try to implement tablemodellistener: when this one is called, 
     * the table structure change may not be applied yet in the column model
     */
    public void updateTable() {
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
     * 
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

    public void setParam(Map currentParameter) {
		model.setParam(currentParameter);
	}
}
