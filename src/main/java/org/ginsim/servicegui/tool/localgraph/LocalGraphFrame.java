package org.ginsim.servicegui.tool.localgraph;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationHolder;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationStore;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.graph.regulatorygraph.perturbation.PerturbationSelectionPanel;
import org.ginsim.gui.utils.data.SimpleStateListTableModel;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialog;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.service.tool.localgraph.LocalGraphCategory;
import org.ginsim.service.tool.localgraph.LocalGraphConfig;
import org.ginsim.service.tool.localgraph.LocalGraphService;
import org.ginsim.service.tool.localgraph.LocalGraphStyleProvider;

public class LocalGraphFrame extends StackDialog implements ActionListener,
		TableModelListener, ListSelectionListener {
	private static final long serialVersionUID = -9126723853606423085L;

	private final int THRESHOLD_AUTO_REFRESH = 15;
	private Container mainPanel;
	private JButton colorizeButton, addStatesButton, replaceStatesButton;

	private LocalGraphConfig config;
	private Map<RegulatoryMultiEdge, LocalGraphCategory> functionalityMap;
	private LocalGraphStyleProvider styleProvider;
	private final StyleManager<RegulatoryNode, RegulatoryMultiEdge> styleManager;

	private boolean isColorized = false;
	private PerturbationSelectionPanel mutantSelectionPanel;
	private PerturbationHolder mutantStore;
	private StateSelectorTable sst;
	private JCheckBox autoUpdateCheckbox;

	public LocalGraphFrame(LocalGraphConfig config) {
		super(config.getGraph(), "STR_localGraph", 420, 260);
		this.styleManager = config.getGraph().getStyleManager();
		this.config = config;
		initialize();
	}

	public void initialize() {
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
			sst.initPanel(config.getGraph(), "STR_localGraph_descr", true);
			sst.setState(new byte[config.getGraph().getNodeOrderSize()]);
			sst.table.getModel().addTableModelListener(this);
			sst.table.getSelectionModel().addListSelectionListener(this);
			mainPanel.add(sst, c);

			c.gridy++;
			c.gridx = 0;
			mutantStore = new PerturbationStore();
			mutantSelectionPanel = new PerturbationSelectionPanel(this,
					config.getGraph(), mutantStore);
			mainPanel.add(mutantSelectionPanel, c);

			c.gridy++;
			c.weightx = 0;
			c.weighty = 0;
			c.fill = GridBagConstraints.EAST;
			autoUpdateCheckbox = new JCheckBox(
					Txt.t("STR_localGraph_autoUpdate"));
			autoUpdateCheckbox
					.setSelected(config.getGraph().getNodeOrderSize() < THRESHOLD_AUTO_REFRESH);
			mainPanel.add(autoUpdateCheckbox, c);

			c.gridy++;
			colorizeButton = new JButton(
					Txt.t("STR_colorize_local"));
			colorizeButton.setEnabled(false);
			colorizeButton.addActionListener(this);
			mainPanel.add(colorizeButton, c);

			if (config.getDynamic() != null) {
				c.gridy++;
				addStatesButton = new JButton(
						Txt.t("STR_localGraph_addStates"));
				addStatesButton.addActionListener(this);
				mainPanel.add(addStatesButton, c);
				c.gridy++;
				replaceStatesButton = new JButton(
						Txt.t("STR_localGraph_getStates"));
				replaceStatesButton.addActionListener(this);
				mainPanel.add(replaceStatesButton, c);
			}
		}
		setMainPanel(mainPanel);
	}

	private void undoColorize() {
		styleManager.setStyleProvider(null);
		colorizeButton.setText(Txt.t("STR_colorize_local"));
		isColorized = false;
	}

	public void doClose() {
		if (isColorized) {
			this.undoColorize();
		}
		super.doClose();
	}

	protected void run() {
		if (isColorized) {
			this.undoColorize();
		}

		LocalGraphService service = ServiceManager.getManager().getService(
				LocalGraphService.class);

		try {
			functionalityMap = service.run(config.getGraph(), sst.getStates(),
					mutantStore.getPerturbation());
			doColorize();
			colorizeButton.setEnabled(true);
		} catch (GsException e) {
			LogManager.error(e);
			NotificationManager.publishError(config.getGraph(), "Local Graph: "
					+ e.getLocalizedMessage());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == colorizeButton) {
			if (functionalityMap != null) {
				if (isColorized) {
					this.undoColorize();
				} else {
					this.doColorize();
				}
			}
			return;
		}

		GraphGUI graphGUI = GUIManager.getInstance().getGraphGUI(
				config.getDynamic());
		if (graphGUI != null) {
			GraphSelection<DynamicNode, Edge<DynamicNode>> selection = graphGUI
					.getSelection();
			if (e.getSource() == addStatesButton) {
				List<DynamicNode> selected = selection.getSelectedNodes();
				if (selected != null) {
					List<byte[]> states = new ArrayList<byte[]>();
					for (DynamicNode state : selected) {
						states.add(state.state);
					}
					sst.setStates(states);
				}
			} else if (e.getSource() == replaceStatesButton) {
				List<DynamicNode> selected = selection.getSelectedNodes();
				if (selected != null) {
					sst.ssl.data.clear();
					List<byte[]> states = new ArrayList<byte[]>();
					for (DynamicNode state : selected) {
						states.add(state.state);
					}
					sst.setStates(states);
				}
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
		if (functionalityMap == null) {
			return;
		}

		styleProvider = new LocalGraphStyleProvider(styleManager, functionalityMap);
		styleManager.setStyleProvider(styleProvider);

		colorizeButton.setText(Txt.t("STR_undo_colorize"));
		isColorized = true;
	}

	public void cancel() {
		if (isColorized) {
			this.undoColorize();
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

	public void setStates(List<byte[]> states) {
		for (Iterator<byte[]> it = states.iterator(); it.hasNext();) {
			ssl.addState(it.next());
		}
	}

	public List<byte[]> getStates() {
		int selectedRowCount = table.getSelectedRowCount();
		int rowCount = table.getRowCount();
		List<byte[]> states = new ArrayList<byte[]>();

		if (rowCount <= 1
				|| (selectedRowCount == 1 && table.getSelectedRow() >= rowCount - 1))
			return null;

		if (selectedRowCount > 0) {
			int[] selectedRows = table.getSelectedRows();
			for (int i = 0; i < selectedRows.length; i++) {
				if (selectedRows[i] != rowCount - 1)
					states.add(ssl.getState(selectedRows[i]));
			}
			return states;
		} else if (rowCount > 0) {
			for (int i = 0; i < rowCount - 1; i++) {
				states.add(ssl.getState(i));
			}
			return states;
		}
		states.add(new byte[table.getColumnCount()]);
		return states;
	}

	public void setState(byte[] state) {
		if (ssl.data.size() > 0)
			ssl.data.remove(0);
		ssl.addState(state);
	}

	protected GridBagConstraints initPanel(RegulatoryGraph g, String desckey,
			boolean isEditable) {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 8;
		c.fill = GridBagConstraints.BOTH;
		add(new JLabel(Txt.t(desckey)), c);

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
