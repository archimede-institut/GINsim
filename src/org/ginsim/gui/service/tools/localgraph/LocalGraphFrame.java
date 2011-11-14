package org.ginsim.gui.service.tools.localgraph;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.ginsim.graph.Graph;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.graph.dynamicgraph.GsDynamicNode;
import org.ginsim.gui.service.tools.reg2dyn.SynchronousSimulationUpdater;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.datastore.gui.SimpleStateListTableModel;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.EnhancedJTable;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class LocalGraphFrame extends StackDialog implements ActionListener, TableModelListener, ListSelectionListener {
	
	private final int THRESHOLD_AUTO_REFRESH = 15;
//	private JFrame frame;
	private GsRegulatoryGraph regGraph;
	private GsDynamicGraph dynGraph;
	private Container mainPanel;
	private JButton colorizeButton, addStatesButton, replaceStatesButton;
	
	private LocalGraph lg;
	private boolean isColorized = false;
	private MutantSelectionPanel mutantSelectionPanel;
	private ObjectStore mutantStore;
	private GsRegulatoryMutantDef mutant;
	private StateSelectorTable sst;
	private JCheckBox autoUpdateCheckbox;
	
	private static final long serialVersionUID = -9126723853606423085L;

	public LocalGraphFrame(JFrame parent, String id, int w, int h) {
		super(parent, id, w, h);
	}

	public LocalGraphFrame(JFrame frame, Graph regGraph) {
		
		super(frame, "localGraph", 420, 260);
		this.regGraph = (GsRegulatoryGraph) regGraph;
		this.dynGraph = null;
		lg = new LocalGraph((GsRegulatoryGraph) regGraph);
        initialize();
    }

	public LocalGraphFrame(JFrame frame, Graph regulatoryGraph, Graph dynamicGraph) {
		
		super(frame, "STR_localGraph", 420, 260);
		this.regGraph = (GsRegulatoryGraph) regulatoryGraph;
		this.dynGraph = (GsDynamicGraph) dynamicGraph;
		lg = new LocalGraph(regGraph);
        initialize();
	}

	public void initialize() {
		setMainPanel(getMainPanel());
	}
	
	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
		
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 2;
			c.weighty = 2;
			c.gridx = 0;
			c.gridy = 0;
			sst = new StateSelectorTable();
			sst.initPanel(regGraph, "STR_localGraph_descr", true);
			sst.setState(new byte[regGraph.getNodeOrderSize()]);
			sst.table.getModel().addTableModelListener(this);
		    sst.table.getSelectionModel().addListSelectionListener(this);
		    mainPanel.add(sst, c);
			
			c.gridy++;
			c.gridx = 0;
		    mutantStore = new ObjectStore();
			mutantSelectionPanel = new MutantSelectionPanel(this, regGraph, mutantStore);
			mainPanel.add(mutantSelectionPanel, c);
		    
			c.gridy++;
			c.weightx = 0;
			c.weighty = 0;
			c.fill = GridBagConstraints.EAST;
			autoUpdateCheckbox = new JCheckBox(Translator.getString("STR_localGraph_autoUpdate"));
			autoUpdateCheckbox.setSelected(regGraph.getNodeOrderSize() < THRESHOLD_AUTO_REFRESH);
			mainPanel.add(autoUpdateCheckbox, c);
			
			c.gridy++;
			colorizeButton = new JButton(Translator.getString("STR_do_colorize"));
		    colorizeButton.setEnabled(false);
		    colorizeButton.addActionListener(this);
		    mainPanel.add(colorizeButton, c);
		    
		    if (dynGraph != null) {
				c.gridy++;
			    addStatesButton = new JButton(Translator.getString("STR_localGraph_addStates"));
			    addStatesButton.addActionListener(this);
			    mainPanel.add(addStatesButton, c);
				c.gridy++;
			    replaceStatesButton = new JButton(Translator.getString("STR_localGraph_getStates"));
			    replaceStatesButton.addActionListener(this);
			    mainPanel.add(replaceStatesButton, c);
		    }		
		}
		return mainPanel;
	}

	protected void run() {
		if (isColorized) {
			lg.undoColorize();
			colorizeButton.setText(Translator.getString("STR_do_colorize"));
			isColorized = false;
		}
		
		mutant = (GsRegulatoryMutantDef) mutantStore.getObject(0);
		lg.setUpdater(new SynchronousSimulationUpdater(regGraph, mutant));
		List states = sst.getStates();
		if (states == null) return;
		
		
		lg.setStates(states);
		lg.run();
		doColorize();
		colorizeButton.setEnabled(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == colorizeButton) {
			if (lg.getFunctionality() != null) {	
				if (isColorized) {
					undoColorize();
				} else {
					doColorize();
				}
			}
		} else if (e.getSource() == addStatesButton) {
			List states = new ArrayList();
			for (Iterator it = dynGraph.getGraphManager().getSelectedVertexIterator(); it.hasNext();) {
				GsDynamicNode state = (GsDynamicNode) it.next();
				states.add(state.state);
			}
			lg.setStates(states);
			sst.setStates(states);
		} else if (e.getSource() == replaceStatesButton) {
				sst.ssl.data.clear();
				List states = new ArrayList();
				for (Iterator it = dynGraph.getGraphManager().getSelectedVertexIterator(); it.hasNext();) {
					GsDynamicNode state = (GsDynamicNode) it.next();
					states.add(state.state);
				}
				lg.setStates(states);
				sst.setStates(states);
			}

	}
	
	public void tableChanged(TableModelEvent e) {
		if (autoUpdateCheckbox.isSelected()) {
			run();
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if (autoUpdateCheckbox.isSelected()) {
			run();
		}		
	}

	private void doColorize() {
		if (lg != null) {
			lg.doColorize();
			colorizeButton.setText(Translator.getString("STR_undo_colorize"));
			isColorized = true;
		}
	}
	
	private void undoColorize() {
		if (lg != null) {
			lg.undoColorize();
			colorizeButton.setText(Translator.getString("STR_do_colorize"));
			isColorized = false;
		}
	}

	public void cancel() {
		if (isColorized) {
			int res = JOptionPane.showConfirmDialog(this, Translator.getString("STR_sure_close_undo_colorize"));
			if (res == JOptionPane.NO_OPTION) {
				super.cancel();
			} else if (res == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (res == JOptionPane.YES_OPTION) {
				undoColorize();
				super.cancel();
			}
		}
		super.cancel();
	}
}

class StateSelectorTable extends JPanel {
	private static final long serialVersionUID = -7502297761417113651L;
	protected SimpleStateListTableModel ssl;
	protected EnhancedJTable table;

	public byte[] getState() {
		return ssl.getState(0);
	}
	public void setStates(List states) {
		for (Iterator it = states.iterator(); it.hasNext();) {
			byte[] state = (byte[]) it.next();
			ssl.addState(state);
		}
	}
	public List getStates() {
		int selectedRowCount = table.getSelectedRowCount();
		int rowCount = table.getRowCount();
		List states = new ArrayList();

		if (rowCount <= 1 || (selectedRowCount == 1 && table.getSelectedRow() >= rowCount-1)) return null;

		if (selectedRowCount > 0) {
			int[] selectedRows = table.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {
				if (selectedRows[i] != rowCount-1) states.add(ssl.getState(selectedRows[i]));
			}
			return states;
		} else if (rowCount > 0) {
			for (int i = 0; i < rowCount-1; i++) {
				states.add(ssl.getState(i));
			}
			return states;
		}
		states.add(new byte[table.getColumnCount()]);
		return states;
	}
	
	public void setState(byte[] state) {
		if (ssl.data.size() > 0) ssl.data.remove(0);
		ssl.addState(state);
	}

	protected GridBagConstraints initPanel(GsRegulatoryGraph g, String desckey, boolean isEditable) {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 8;
		c.fill = GridBagConstraints.BOTH;
		add(new JLabel(Translator.getString(desckey)), c);	

		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.ipady = 0;
		ssl = new SimpleStateListTableModel(g, isEditable);
		table = new EnhancedJTable(ssl);
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		add(new JScrollPane(table), c);	
		return c;
	}

}
