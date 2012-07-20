package org.ginsim.servicegui.export.nusmv;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.mutant.MutantListManager;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.graph.regulatorygraph.mutant.RegulatoryMutants;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.utils.dialog.stackdialog.AbstractStackDialogHandler;
import org.ginsim.service.export.nusmv.NuSMVConfig;
import org.ginsim.service.tool.reg2dyn.SimulationParameterList;
import org.ginsim.service.tool.reg2dyn.SimulationParametersManager;
import org.ginsim.servicegui.tool.reg2dyn.PrioritySelectionPanel;

public class NuSMVExportConfigPanel extends AbstractStackDialogHandler {
	private static final long serialVersionUID = -7398674287463858306L;

	private final NuSMVConfig config;
	private final NuSMVExportAction action;

	private PrioritySelectionPanel priorityPanel = null;
	private MutantSelectionPanel mutantPanel = null;
	private InitialStatePanel initPanel;

	JButton butCfgMutant = null;
	JTextArea area;
	private GsNuSMVConfigModel model;
	private GsNuSMVMutantModel mutantModel;

	private JRadioButton jrbType2;

	public NuSMVExportConfigPanel(NuSMVConfig config, NuSMVExportAction action) {
		this.config = config;
		this.action = action;
	}

	@Override
	protected void init() {
		setLayout(new BorderLayout());

		JPanel jpTmp = new JPanel(new GridBagLayout());
		mutantPanel = new MutantSelectionPanel(stack, config.getGraph(),
				config.store);
		GridBagConstraints cst = new GridBagConstraints();
		cst.gridx = 0;
		cst.gridy = 0;
		cst.fill = GridBagConstraints.HORIZONTAL;
		cst.weightx = 0.5;
		jpTmp.add(mutantPanel, cst);

		SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager
				.getInstance().getObject(config.getGraph(),
						SimulationParametersManager.KEY, true);
		priorityPanel = new PrioritySelectionPanel(stack, paramList.pcmanager);
		priorityPanel.setStore(config.store, 1);
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

		initPanel = new InitialStatePanel( config.getGraph(), false);
		initPanel.setParam(config);
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
		this.config.setUpdatePolicy();
	}

	public void changeExportType() {
		int type = (jrbType2.isSelected()) ? NuSMVConfig.CFG_INPUT_IVAR
				: NuSMVConfig.CFG_INPUT_FRONZEN;
		this.config.setExportType(type);
	}

	/**
	 * refresh the state blocking.
	 * 
	 * @param nodeOrder
	 */
	public void refresh(Vector<RegulatoryNode> nodeOrder) {
		model.refresh(nodeOrder);
	}

	/**
	 * @return the selected mutant (can be null)
	 */
	public Perturbation getMutant() {
		if (mutantModel.getSelectedItem() instanceof Perturbation) {
			return (Perturbation) mutantModel.getSelectedItem();
		}
		return null;
	}

	@Override
	public boolean run() {
		action.selectFile();
		return true;
	}
}

/**
 * tableModel to configure gene state blockers
 */
class GsNuSMVConfigModel extends AbstractTableModel {

	private static final long serialVersionUID = 864660594916225977L;
	private Vector<RegulatoryNode> nodeOrder;
	Map<RegulatoryNode, Integer> m_initstates;

	/**
	 * @param nodeOrder
	 * @param t_min
	 * @param t_max
	 * @param initstates
	 */
	public GsNuSMVConfigModel(Vector<RegulatoryNode> nodeOrder,
			Map<RegulatoryNode, Integer> m_initstates) {
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
				|| val > ((RegulatoryNode) nodeOrder.get(rowIndex))
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
	public void refresh(Vector<RegulatoryNode> nodeOrder) {
		this.nodeOrder = nodeOrder;
		fireTableStructureChanged();
	}
}

class GsNuSMVMutantModel extends DefaultComboBoxModel implements ComboBoxModel {
	private static final long serialVersionUID = 2348678706086666489L;

	RegulatoryMutants listMutants;
	NuSMVConfig cfg;

	GsNuSMVMutantModel(NuSMVConfig cfg) {
		this.cfg = cfg;
		this.listMutants = (RegulatoryMutants) ObjectAssociationManager
				.getInstance().getObject(cfg.getGraph(), MutantListManager.KEY,
						true);
	}

	void setMutantList(RegulatoryMutants mutants) {
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
		if (anObject instanceof Perturbation) {
			cfg.mutant = (Perturbation) anObject;
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
