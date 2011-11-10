package fr.univmrs.tagc.GINsim.stateInRegulatoryGraph;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.action.stablestates.StableTableModel;
import org.ginsim.service.action.stablestates.StableStatesService;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.datastore.gui.SimpleStateListTableModel;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.EnhancedJTable;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * The main frame.
 * 
 * Display a JTabbedPane that contains different TabComponantProvidingAState having a method to get a state
 *
 */
public class GsStateInRegGraphFrame extends StackDialog implements ActionListener {
	private static final long serialVersionUID = -5576209151262677441L;

	//private JFrame frame;
	private GsRegulatoryGraph regGraph;

	private Container mainPanel;
	private JTabbedPane tabbedPane;

	private JButton colorizeButton;
	private boolean isColorized = false;

	private GsStateInRegGraph sirg;


	public GsStateInRegGraphFrame(JFrame frame, Graph graph) {
		super(frame, "stateInRegGraph", 420, 260);
		//this.frame = frame;
		this.regGraph = (GsRegulatoryGraph) graph;
		sirg = new GsStateInRegGraph(this.regGraph);
		setMainPanel(getMainPanel());
	}



	private Container getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new javax.swing.JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.BOTH;
			c.ipadx = 10;
			mainPanel.add(new JLabel(Translator.getString("STR_stateInRegGraph_descr")), c);

			c.gridy++;
			c.gridx = 0;
			c.ipadx = 0;
			c.ipady = 0;
			c.weightx = 1;
			c.weighty = 1;
			tabbedPane = new JTabbedPane();
			tabbedPane.add(Translator.getString("STR_stateInRegGraph_state"), new State(regGraph));
			tabbedPane.add(Translator.getString("STR_stateInRegGraph_stablestate"), new StableState(regGraph, this));
			tabbedPane.add(Translator.getString("STR_stateInRegGraph_maxvalues"), new MaxValues(regGraph));
			mainPanel.add(tabbedPane, c);

			c.gridy++;
			c.ipady = 20;
			c.weightx = 0;
			c.weighty = 0;
			mainPanel.add(new JLabel(""), c);

			c.gridy++;
			c.ipady = 0;
			c.fill = GridBagConstraints.CENTER;
			colorizeButton = new JButton(Translator.getString("STR_undo_colorize"));
			colorizeButton.setEnabled(false);
			mainPanel.add(colorizeButton, c);
			colorizeButton.addActionListener(this);
		}
		return mainPanel;
	}

	protected void run() {
		if (!isColorized) {
			isColorized = true;
			colorizeButton.setEnabled(true);
			sirg.restoreColorization();
		}
		sirg.colorizeGraph(((TabComponantProvidingAState)tabbedPane.getSelectedComponent()).getState());
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == colorizeButton) {
			if (isColorized) {
				undoColorize();
			}
		}
	}

	private void undoColorize() {
		if (sirg != null) {
			isColorized = false;
			colorizeButton.setEnabled(false);
			sirg.restoreColorization();
		}
	}

	public void cancel() {
		if (isColorized) {
			int res = JOptionPane.showConfirmDialog(this, Translator.getString("STR_sure_close_undo_colorize"));
			if (res == JOptionPane.OK_OPTION) sirg.restoreColorization();
			else if (res == JOptionPane.CANCEL_OPTION) return;			
		}
		super.cancel();
	}
}

/**
 * 
 * Provide proper initial layout and an abstract method returning a state.
 *
 */
abstract class TabComponantProvidingAState extends JPanel {
	private static final long serialVersionUID = -7502297761417113651L;
	protected SimpleStateListTableModel ssl;
	protected EnhancedJTable table;

	abstract public byte[] getState();

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

/**
 *  Return the state (1,1,...,1) and display the state (max_1, max_2,..., max_n) where max_i is the max value of the node i.
 */
class MaxValues extends TabComponantProvidingAState {
	private static final long serialVersionUID = -6227864741059321245L;
	private byte[] state1;

	public MaxValues(GsRegulatoryGraph g) {
		state1 = new byte[g.getNodeOrderSize()];
		for (int i = 0; i < g.getNodeOrderSize(); i++) {
			state1[i] = 1;
		}
		initPanel(g, "STR_stateInRegGraph_maxValuesdescr", false);
		ssl.addState(ssl.getMaxValues());
	}

	public byte[] getState() {
		return state1;
	}
}

/**
 * Provide a table to enter a state manually.
 *
 */
class State extends TabComponantProvidingAState {
	private static final long serialVersionUID = 918581816104803491L;

	public State(GsRegulatoryGraph g) {
		initPanel(g, "STR_stateInRegGraph_statedescr", true);
	}

	public byte[] getState() {
		return ssl.getState(table.getSelectedRow());
	}
}

/**
 * Return a state provided by the StableState plugin.
 */
class StableState extends TabComponantProvidingAState {
	private static final long serialVersionUID = 1301082532863004279L;
	
	JTable table;
	private MutantSelectionPanel mutantSelectionPanel;
	private ObjectStore mutantStore;
	private GsRegulatoryGraph g;
	private JButton computeStableStateButton;
	
	private StableStatesService sss;

	private StableTableModel tableModel;

	private GsStateInRegGraphFrame stateInRegGraphFrame;

	public StableState(GsRegulatoryGraph g, GsStateInRegGraphFrame stateInRegGraphFrame) {
		this.g = g;
		this.stateInRegGraphFrame = stateInRegGraphFrame;
		setMainPanel();
	}

	private void setMainPanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 8;
		c.fill = GridBagConstraints.BOTH;
		add(new JLabel(Translator.getString("STR_stateInRegGraph_statedescr")), c);	

		c.gridy++;
		c.ipady = 0;
		mutantStore = new ObjectStore();
		mutantSelectionPanel = new MutantSelectionPanel(stateInRegGraphFrame, g, mutantStore);
		add(mutantSelectionPanel, c);

		c.gridy++;
		c.weightx = 1;
		c.weighty = 1;
		c.ipady = 0;
		tableModel = new StableTableModel(g.getNodeOrder(), false);
        table = new EnhancedJTable(tableModel);
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					stateInRegGraphFrame.run(); //Colorize the graph when the selection change
				}				
			}
        });
		add(new JScrollPane(table), c);	
		
		c.gridy++;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.CENTER;
		computeStableStateButton = new JButton(Translator.getString("STR_stateInRegGraph_computeStableState"));
		add(computeStableStateButton, c);
		computeStableStateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run(); //call run() when the button is clicked
			}
		});
	}

	protected void run() {
		sss = new StableStatesService(g, g.getNodeOrder(), (GsRegulatoryMutantDef) mutantStore.getObject(0));
		OmddNode stable = sss.getStable();
		tableModel.setResult(stable, g);
	}

	public byte[] getState() {
		return tableModel.getState(table.getSelectedRow());
	}
}
