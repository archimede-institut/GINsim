package org.ginsim.gui.service.export.nusmv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.nusmv.NuSMVConfig;
import org.ginsim.service.export.nusmv.NuSMVExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * GUI Action to export a NuSMV model description
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
public class NuSMVExportServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new NuSMVExportAction((RegulatoryGraph) graph));
			return actions;
		}
		return null;
	}
}

/**
 * Export to NuSMV file Action
 * 
 * @author Pedro T. Monteiro
 */
class NuSMVExportAction extends ExportAction<RegulatoryGraph> {

	private static final long serialVersionUID = -3615904375655037276L;
	private static final GsFileFilter ffilter = new GsFileFilter(
			new String[] { "smv" }, "smv (NuSMV) files");
	private NuSMVConfig config;

	public NuSMVExportAction(RegulatoryGraph graph) {
		super(graph, "STR_NuSMV", "STR_NuSMV_descr");
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}

	@Override
	public StackDialogHandler getConfigPanel() {
		config = new NuSMVConfig(graph);
		return new NuSMVExportConfigPanel(config);
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		if (config == null) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"Nothing to export: NuSMVConfig must be specified");
		}

		NuSMVExportService service = ServiceManager.getManager().getService(
				NuSMVExportService.class);
		if (service == null) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"NuSMVExportService service is not available");
		}
		service.run(config, filename);
	}
}
