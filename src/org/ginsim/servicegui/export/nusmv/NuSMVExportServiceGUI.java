package org.ginsim.servicegui.export.nusmv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.dialog.stackdialog.StackDialogHandler;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.nusmv.NuSMVConfig;
import org.ginsim.service.export.nusmv.NuSMVExportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.ExportAction;
import org.ginsim.servicegui.common.StandaloneGUI;
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

	@Override
	public int getWeight() {
		return W_MANIPULATION;
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
		return new NuSMVExportConfigPanel(config, this);
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
