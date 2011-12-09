package org.ginsim.servicegui.tool.localgraph;

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

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.data.SimpleStateListTableModel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.servicegui.tool.reg2dyn.SynchronousSimulationUpdater;


public class LocalGraphFrame extends StackDialog implements ActionListener, TableModelListener, ListSelectionListener {
	
	private final int THRESHOLD_AUTO_REFRESH = 15;
	private RegulatoryGraph regGraph;
	private DynamicGraph dynGraph;
	private Container mainPanel;
	private JButton colorizeButton, addStatesButton, replaceStatesButton;
	
	private LocalGraph lg;
	private boolean isColorized = false;
	private MutantSelectionPanel mutantSelectionPanel;
	private ObjectStore mutantStore;
	private Perturbation mutant;
	private StateSelectorTable sst;
	private JCheckBox autoUpdateCheckbox;
	
	private static final long serialVersionUID = -9126723853606423085L;

	public LocalGraphFrame(JFrame parent) {
		super(parent, "STR_localGraph", 420, 260);
	}

	public LocalGraphFrame(JFrame frame, Graph regGraph) {
		
		this(frame);
		this.regGraph = (RegulatoryGraph) regGraph;
		this.dynGraph = null;
		lg = new LocalGraph((RegulatoryGraph) regGraph);
        initialize();
    }

	public LocalGraphFrame(JFrame frame, Graph regulatoryGraph, Graph dynamicGraph) {
		
		this(frame);
		this.regGraph = (RegulatoryGraph) regulatoryGraph;
		this.dynGraph = (DynamicGraph) dynamicGraph;
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
		
		mutant = (Perturbation) mutantStore.getObject(0);
		lg.setUpdater(new SynchronousSimulationUpdater(regGraph, mutant));
		List states = sst.getStates();
		if (states == null) return;
		
		
		lg.setStates(states);
		lg.run();
		doColorize();
		colorizeButton.setEnabled(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		GraphSelection<DynamicNode, Edge<DynamicNode>> selection = GUIManager.getInstance().getGraphGUI(dynGraph).getSelection();
		if (e.getSource() == colorizeButton) {
			if (lg.getFunctionality() != null) {	
				if (isColorized) {
					undoColorize();
				} else {
					doColorize();
				}
			}
		} else if (e.getSource() == addStatesButton) {
			List<DynamicNode> selected = selection.getSelectedNodes();
			if (selected != null) {
				List states = new ArrayList();
				for (DynamicNode state: selected) {
					states.add(state.state);
				}
				lg.setStates(states);
				sst.setStates(states);
			}
		} else if (e.getSource() == replaceStatesButton) {
			List<DynamicNode> selected = selection.getSelectedNodes();
			if (selected != null) {
				sst.ssl.data.clear();
				List states = new ArrayList();
				for (DynamicNode state: selected) {
					states.add(state.state);
				}
				lg.setStates(states);
				sst.setStates(states);
			}
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

	protected GridBagConstraints initPanel(RegulatoryGraph g, String desckey, boolean isEditable) {
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
