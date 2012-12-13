package org.ginsim.servicegui.export.petrinet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.io.petrinet.PNConfig;
import org.colomoto.logicalmodel.io.petrinet.PetriNetSubformats;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.format.PetriNetFormatService;
import org.ginsim.servicegui.tool.reg2dyn.PrioritySelectionPanel;

public class PetriNetExportAction extends ExportAction<RegulatoryGraph> implements InitialStateStore {

	static final String PNFORMAT = "export.petriNet.defaultFormat";

	private LogicalModel model = null;
	PetriNetSubformats format = null;
	PNConfig config = null;
	Map m_init = null;
	Map m_input = null;
	
	public PetriNetExportAction(RegulatoryGraph graph) {
		super(graph, "STR_PetriNet", "STR_PetriNet_descr", null);
	}

	protected void doExport( String filename) {
		if (format == null) {
			throw new RuntimeException("No selected format");
		}
		
		// retrieve the selected initial state if needed
		byte[] initialstate = null;
		Map<NodeInfo,List<Integer>> m_init_values = null;
		if (m_init != null && m_init.size() == 1) {
			InitialState istate = (InitialState)m_init.keySet().iterator().next();
			m_init_values = istate.getMap();
		}
		if (m_input != null && m_input.size() == 1) {
			InitialState istate = (InitialState)m_input.keySet().iterator().next();
			if (m_init_values == null) {
				m_init_values = istate.getMap();
			} else {
				// avoid headaches, merge the two maps
				m_init_values = new HashMap<NodeInfo, List<Integer>>(m_init_values);
				m_init_values.putAll(istate.getMap());
			}
		}
		
		if (m_init_values != null) {
			List<NodeInfo> nodes = model.getNodeOrder();
			initialstate = new byte[nodes.size()];
			int i=0;
			for (NodeInfo ni: nodes) {
				List<Integer> values = m_init_values.get(ni);
				if (values != null && values.size() > 0) {
					initialstate[i] = (byte)values.get(0).intValue();
				}
				i++;
			}
			config.setInitialState(initialstate);
		}

		// call the selected export method to do the job
		try {
			format.getEncoder( model).export(config, new FileOutputStream(filename));
		} catch (IOException e) {
			LogManager.error(e);
		}
	}

	@Override
	protected FileFormatDescription getFileFilter() {
		if (format == null) {
			return null;
		}
		return new FileFormatDescription(format.name(), format.getExtension());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		new PetriNetExportFrame(null, graph, this);
	}

	public void selectFile(LogicalModel model, PetriNetSubformats format, PNConfig config) {
		this.model = model;
		this.format = format;
		this.config = config;
		selectFile();
	}

	@Override
	public Map getInitialState() {
		if (m_init == null) {
			m_init = new HashMap();
		}
		return m_init;
	}

	@Override
	public Map getInputState() {
		if (m_input == null) {
			m_input = new HashMap();
		}
		return m_input;
	}
}

class PetriNetExportFrame extends LogicalModelActionDialog {

	private PetriNetFormatService service = ServiceManager.getManager().getService(PetriNetFormatService.class);
	private final PetriNetExportAction action;
	private PrioritySelectionPanel priorityPanel = null;
	private final JComboBox formatCombo;
	private InitialStatePanel initStatePanel = null;

	PNConfig config = new PNConfig();
	
	public PetriNetExportFrame(JFrame f, RegulatoryGraph lrg, PetriNetExportAction action) {
		super(lrg, f, "PNGUI", 600, 400);
		this.action = action;
		
    	JPanel mainPanel = new JPanel();
    	mainPanel.setLayout(new GridBagLayout());
    	
    	
    	initStatePanel = new InitialStatePanel(lrg, false);
    	initStatePanel.setParam(action);
    	GridBagConstraints cst = new GridBagConstraints();
    	cst.gridwidth = 2;
    	cst.weightx = 1;
    	cst.weighty = 1;
    	cst.fill = GridBagConstraints.BOTH;
    	mainPanel.add(initStatePanel, cst);

    	cst = new GridBagConstraints();
    	cst.gridx = 0;
    	cst.gridy = 1;
    	cst.fill = GridBagConstraints.HORIZONTAL;
    	mainPanel.add(new JLabel("Petri net format"), cst);

    	
    	cst = new GridBagConstraints();
    	cst.gridx = 1;
    	cst.gridy = 1;
    	cst.weightx = 1;
    	PetriNetSubformats[] formats = service.format.getSubformats();
    	formatCombo = new JComboBox(formats);
    	mainPanel.add(formatCombo, cst);
    	
    	setMainPanel(mainPanel);
	}

	@Override
	public void run(LogicalModel model) {
		PetriNetSubformats format = (PetriNetSubformats)formatCombo.getSelectedItem();
		// initStatePanel
		action.selectFile(model, format, config);
		cancel();
	}

}
