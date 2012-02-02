package org.ginsim.servicegui.export.sat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;
import org.ginsim.service.export.sat.SATConfig;
import org.ginsim.service.export.sat.SATExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * GUI Action to export a SAT model description
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus(ServiceStatus.RELEASED)
public class SATExportServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new SATExportAction((RegulatoryGraph) graph));
			return actions;
		}
		return null;
	}

	@Override
	public int getWeight() {
		return W_MANIPULATION;
	}
}

/**
 * Export to SAT file Action
 * 
 * @author Pedro T. Monteiro
 */
class SATExportAction extends ExportAction<RegulatoryGraph> {
	private static final long serialVersionUID = 9217593419394199024L;

	private static final FileFormatDescription FORMAT = new FileFormatDescription("SAT (cnf)", "cnf");
	
	private SATConfig config;

	public SATExportAction(RegulatoryGraph graph) {
		super(graph, "STR_SAT", "STR_SAT_descr");
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
	}

	@Override
	public StackDialogHandler getConfigPanel() {
		config = new SATConfig(graph);
		return new SATExportConfigPanel(config, this);
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		if (config == null) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"Nothing to export: SATConfig must be specified");
		}

		SATExportService service = ServiceManager.getManager().getService(
				SATExportService.class);
		if (service == null) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"SATExportService service is not available");
		}
		service.run(config, filename);
	}
}
