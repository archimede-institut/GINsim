package fr.univmrs.tagc.GINsim.regulatoryGraph.initialState;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParameterList;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.EnhancedJTable;
import fr.univmrs.tagc.common.widgets.Label;
import fr.univmrs.tagc.common.widgets.StackDialog;
import fr.univmrs.tagc.common.widgets.StockButton;


public class GsInitialStatePanel extends JPanel {
    private static final long serialVersionUID = -572201856207494392L;
    
    private StateListPanel initPanel;
    private StateListPanel inputPanel;
    
	public GsInitialStatePanel(StackDialog dialog, GsInitialStateList imanager, boolean several) {
		
	    initPanel = new StateListPanel(dialog, imanager.getInitialStates(), several, Translator.getString("STR_Initial_state"));
	    inputPanel = new StateListPanel(dialog, imanager.getInputConfigs(), several, Translator.getString("STR_Fixed_inputs"));
	    setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.weightx = c.weighty = 1;
	    c.fill = GridBagConstraints.BOTH;
	    if (imanager.normalNodes.size() > 0) {
	        add(initPanel, c);
	    }
        c.gridy = 1;
        if (imanager.inputNodes.size() > 0) {
            add(inputPanel, c);
        }
	}
    public GsInitialStatePanel(StackDialog dialog, Graph graph, boolean several) {
        this(dialog, (GsInitialStateList)graph.getObject( GsInitialStateManager.key, true), several);
    }
    public void setParam(GsInitialStateStore currentParameter) {
        initPanel.setParam(currentParameter.getInitialState());
        inputPanel.setParam(currentParameter.getInputState());
    }
}

class StateListPanel extends JPanel {
    private static final long serialVersionUID = -7273296658666786369L;

    private JScrollPane jScrollPane = null;
    private EnhancedJTable tableInitStates = null;
    private GsInitStateTableModel model = null;
    private JButton buttonDelStateRow = null;
    private JButton buttonCopyStateRow = null;

    GsSimulationParameterList paramList;

    Insets topInset = new Insets(20,0,0,0);
	private InitialStateList stateList;
	private StackDialog dialog;
	private boolean several;
	
    public StateListPanel(StackDialog dialog, InitialStateList stateList, boolean several, String title) {
    	this.dialog = dialog;
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
        c.gridy = 2;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(getJScrollPane(), c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        add(getButtonDelStateRow(), c);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        add(getButtonCopyStateRow(), c);
	}
	
    /**
     * This method initializes tableInitStates
     * 
     * @return javax.swing.JTable
     */
    private javax.swing.JTable getTableInitStates() {
        if(tableInitStates == null) {
            tableInitStates = new EnhancedJTable();
            model = new GsInitStateTableModel(dialog, stateList, several);
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
        int i=-1;
        while (e_col.hasMoreElements()) {
            TableColumn col = (TableColumn)e_col.nextElement();
            i++;
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
    private javax.swing.JScrollPane getJScrollPane() {
        if(jScrollPane == null) {
            jScrollPane = new javax.swing.JScrollPane();
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
        	buttonCopyStateRow.setToolTipText(Translator.getString("STR_duplicate_rows"));
        	buttonCopyStateRow.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    copyStateRow();
                }
            });
        }
        return buttonCopyStateRow;
    }

	public void setParam(Map currentParameter) {
		model.setParam(currentParameter);
	}
}
