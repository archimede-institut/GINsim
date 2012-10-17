package org.ginsim.servicegui.export.boolsim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.export.boolsim.BoolsimExportService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to export model to the boolsim format
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(BoolsimExportService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class BoolsimExportServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new BoolsimExportAction((RegulatoryGraph) graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_EXPORT_SPECIFIC + 50;
	}
}

/**
 * Export to boolsim file Action
 * 
 * @author Aurelien Naldi
 * 
 */
class BoolsimExportAction extends ExportAction<RegulatoryGraph> {

	private static final FileFormatDescription FORMAT = new FileFormatDescription("Boolsim", "net");

	public BoolsimExportAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		super(graph, "Boolsim", "Export to Boolsim format", serviceGUI);
	}

	@Override
	protected void doExport(String filename) throws IOException {

		BoolsimExportService service = ServiceManager.getManager().getService(
				BoolsimExportService.class);
		service.export(graph.getModel(), filename);
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
	}
}
