package org.ginsim.servicegui.export.petrinet;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.io.petrinet.PNConfig;
import org.colomoto.logicalmodel.io.petrinet.PetriNetSubformats;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.format.PetriNetFormatService;
import org.ginsim.servicegui.tool.reg2dyn.PrioritySelectionPanel;

public class PetriNetExportAction extends ExportAction<RegulatoryGraph> {

	static final String PNFORMAT = "export.petriNet.defaultFormat";

	private LogicalModel model = null;
	PetriNetSubformats format = null;
	PNConfig config = null;
	
	public PetriNetExportAction(RegulatoryGraph graph) {
		super(graph, "STR_PetriNet", "STR_PetriNet_descr", null);
	}

	protected void doExport( String filename) {
		if (format == null) {
			throw new RuntimeException("No selected format");
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
}

class PetriNetExportFrame extends LogicalModelActionDialog {

	private PetriNetFormatService service = ServiceManager.getManager().getService(PetriNetFormatService.class);
	private final PetriNetExportAction action;
	private PrioritySelectionPanel priorityPanel = null;
	private final JComboBox formatCombo;

	PNConfig config = new PNConfig();
	
	public PetriNetExportFrame(JFrame f, RegulatoryGraph lrg, PetriNetExportAction action) {
		super(lrg, f, "PNGUI", 600, 400);
		this.action = action;
		
    	JPanel mainPanel = new JPanel();
    	mainPanel.setLayout(new GridBagLayout());

    	PetriNetSubformats[] formats = service.format.getSubformats();
    	formatCombo = new JComboBox(formats);
    	mainPanel.add(formatCombo);
    	
    	setMainPanel(mainPanel);
	}

	@Override
	public void run(LogicalModel model) {
		PetriNetSubformats format = (PetriNetSubformats)formatCombo.getSelectedItem();
		action.selectFile(model, format, config);
		cancel();
	}

}
