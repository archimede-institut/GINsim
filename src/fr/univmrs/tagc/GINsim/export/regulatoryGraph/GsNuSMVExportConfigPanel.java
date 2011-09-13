package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.tagc.GINsim.graph.GsExtensibleConfig;
import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParameterList;
import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParametersManager;
import fr.univmrs.tagc.GINsim.reg2dyn.PriorityClassDefinition;
import fr.univmrs.tagc.GINsim.reg2dyn.PrioritySelectionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStatePanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class GsNuSMVExportConfigPanel extends JPanel {
	private static final long serialVersionUID = -7398674287463858306L;

	private GsNuSMVConfig cfg;

	private PrioritySelectionPanel priorityPanel = null;
	private MutantSelectionPanel mutantPanel = null;
	private GsInitialStatePanel initPanel;

	JButton butCfgMutant = null;
	JTextArea area;
	private GsNuSMVConfigModel model;
	private GsNuSMVMutantModel mutantModel;

	private JRadioButton jrbType2;

	private StackDialog dialog;

	public GsNuSMVExportConfigPanel(GsExtensibleConfig config,
			StackDialog dialog) {
		super();
		this.dialog = dialog;
		if (config.getSpecificConfig() == null) {
			config.setSpecificConfig(new GsNuSMVConfig(
					(GsRegulatoryGraph) config.getGraph()));
		}
		this.cfg = (GsNuSMVConfig) config.getSpecificConfig();
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		JPanel jpTmp = new JPanel(new GridBagLayout());
		mutantPanel = new MutantSelectionPanel(dialog, cfg.graph, cfg.store);
		GridBagConstraints cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 0;
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.weightx = 0.5;
		jpTmp.add(mutantPanel, cst);

		GsSimulationParameterList paramList = (GsSimulationParameterList) this.cfg.graph
				.getObject(GsSimulationParametersManager.key, true);
		priorityPanel = new PrioritySelectionPanel(dialog, paramList.pcmanager);
		priorityPanel.setStore(cfg.store, 1);
		cst = new GridBagConstraints();
		cst.gridx = 1;
		cst.gridy = 0;
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.weightx = 0.5;
		jpTmp.add(priorityPanel, cst);

		add(jpTmp, BorderLayout.NORTH);

		priorityPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeUpdatePolicy();
			}
		});

		initPanel = new GsInitialStatePanel(dialog, cfg.graph, false);
		initPanel.setParam(cfg);
		add(initPanel, BorderLayout.CENTER);
		this.setMinimumSize(new Dimension(450, 320));

		JPanel jpExportType = new JPanel();
		jpExportType
				.setLayout(new BoxLayout(jpExportType, BoxLayout.PAGE_AXIS));
		jpExportType.setBorder(BorderFactory.createTitledBorder(Translator
				.getString("STR_NuSMV_Type")));
		JRadioButton jrb1 = new JRadioButton(
				Translator.getString("STR_NuSMV_Type1"));
		jrb1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeExportType();
			}
		});
		jrbType2 = new JRadioButton(Translator.getString("STR_NuSMV_Type2"));
		jrbType2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeExportType();
			}
		});
		ButtonGroup group = new ButtonGroup();
		group.add(jrb1);
		group.add(jrbType2);
		jrb1.setSelected(true);
		jpExportType.add(jrb1);
		jpExportType.add(jrbType2);
		add(jpExportType, BorderLayout.SOUTH);
	}

	public void changeUpdatePolicy() {
		this.cfg.setUpdatePolicy();
	}

	public void changeExportType() {
		int type = (jrbType2.isSelected()) ? GsNuSMVConfig.CFG_INPUT_IVAR
				: GsNuSMVConfig.CFG_INPUT_FRONZEN;
		this.cfg.setExportType(type);
	}

	/**
	 * refresh the state blocking.
	 * 
	 * @param nodeOrder
	 */
	public void refresh(Vector<GsRegulatoryVertex> nodeOrder) {
		model.refresh(nodeOrder);
	}

	/**
	 * @return the selected mutant (can be null)
	 */
	public GsRegulatoryMutantDef getMutant() {
		if (mutantModel.getSelectedItem() instanceof GsRegulatoryMutantDef) {
			return (GsRegulatoryMutantDef) mutantModel.getSelectedItem();
		}
		return null;
	}
}

/**
 * tableModel to configure gene state blockers
 */
class GsNuSMVConfigModel extends AbstractTableModel {

	private static final long serialVersionUID = 864660594916225977L;
	private Vector<GsRegulatoryVertex> nodeOrder;
	Map<GsRegulatoryVertex, Integer> m_initstates;

	/**
	 * @param nodeOrder
	 * @param t_min
	 * @param t_max
	 * @param initstates
	 */
	public GsNuSMVConfigModel(Vector<GsRegulatoryVertex> nodeOrder,
			Map<GsRegulatoryVertex, Integer> m_initstates) {
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
			switch (columnIndex) {
			case 1:
				m_initstates.remove(nodeOrder.get(rowIndex));
				fireTableCellUpdated(rowIndex, 1);
				break;
			}
			return;
		}

		int val;
		try {
			val = Integer.parseInt((String) aValue);
		} catch (Exception e) {
			return;
		}

		if (val == -1) {
			switch (columnIndex) {
			case 1:
				m_initstates.remove(nodeOrder.get(rowIndex));
				fireTableCellUpdated(rowIndex, 1);
				break;
			}
			return;
		}
		if (val < 0
				|| val > ((GsRegulatoryVertex) nodeOrder.get(rowIndex))
						.getMaxValue()) {
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
	 * 
	 * @param nodeOrder
	 * @param minBlock
	 * @param maxBlock
	 */
	public void refresh(Vector<GsRegulatoryVertex> nodeOrder) {
		this.nodeOrder = nodeOrder;
		fireTableStructureChanged();
	}
}

class GsNuSMVMutantModel extends DefaultComboBoxModel implements ComboBoxModel {
	private static final long serialVersionUID = 2348678706086666489L;

	GsRegulatoryMutants listMutants;
	GsNuSMVConfig cfg;

	GsNuSMVMutantModel(GsNuSMVConfig cfg) {
		this.cfg = cfg;
		this.listMutants = (GsRegulatoryMutants) cfg.graph.getObject(
				GsMutantListManager.key, true);
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
			cfg.mutant = (GsRegulatoryMutantDef) anObject;
		} else {
			cfg.mutant = null;
		}
	}

	public Object getElementAt(int index) {
		if (index == 0 || listMutants == null) {
			return "--";
		}
		return listMutants.getElement(null, index - 1);
	}

	public int getSize() {
		if (listMutants == null) {
			return 1;
		}
		return listMutants.getNbElements(null) + 1;
	}
}

class GsNuSMVConfig implements GsInitialStateStore {

	public static final int CFG_SYNC = 0;
	public static final int CFG_ASYNC = 1;
	public static final int CFG_PCLASS = 2;
	public static final int CFG_INPUT_FRONZEN = 10;
	public static final int CFG_INPUT_IVAR = 11;

	GsRegulatoryGraph graph;
	Map m_initStates;
	Map m_input;
	// Store has two objects: 0- Mutant & 1- PriorityClass
	ObjectStore store = new ObjectStore(2);
	public GsRegulatoryMutantDef mutant;
	private int updatePolicy;
	private int exportType;

	/**
	 * @param graph
	 */
	public GsNuSMVConfig(GsRegulatoryGraph graph) {
		m_initStates = new HashMap();
		this.graph = graph;
		updatePolicy = CFG_ASYNC; // Default update policy
		exportType = CFG_INPUT_FRONZEN; // Default export type
	}
	
	public void setUpdatePolicy() {
		PriorityClassDefinition priorities = (PriorityClassDefinition) store
				.getObject(1);
		if (priorities == null)
			updatePolicy = CFG_ASYNC;
		else if (priorities.getNbElements() == 1) {
			if (priorities.getPclass(graph.getNodeOrder())[0][1] == 0)
				updatePolicy = CFG_SYNC;
			else
				updatePolicy = CFG_ASYNC;
		} else
			updatePolicy = CFG_PCLASS;
	}

	public int getUpdatePolicy() {
		return updatePolicy;
	}

	public void setExportType(int type) {
		exportType = type;
	}

	public int getExportType() {
		return exportType;
	}

	public Map getInitialState() {
		return m_initStates;
	}

	public Map getInputState() {
		return m_input;
	}
}
