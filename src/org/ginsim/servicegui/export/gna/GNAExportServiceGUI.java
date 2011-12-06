package org.ginsim.servicegui.export.gna;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.gna.GNAExportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.ExportAction;
import org.ginsim.servicegui.common.GUIFor;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to export a GNA model description
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(GNAExportService.class)
public class GNAExportServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new GNAExportAction((RegulatoryGraph) graph));
			return actions;
		}
		return null;
	}
}

/**
 * Export to GNA file Action
 * 
 * @author Pedro T. Monteiro
 * 
 */
class GNAExportAction extends ExportAction<RegulatoryGraph> {

	private static final long serialVersionUID = 1184129217755724894L;
	private static final GsFileFilter ffilter = new GsFileFilter(
			new String[] { "gna" }, "gna (GNA) files");

	public GNAExportAction(RegulatoryGraph graph) {
		super(graph, "STR_GNA", "STR_GNA_descr");
	}

	@Override
	protected void doExport(String filename) throws IOException {

		GNAExportService service = ServiceManager.getManager().getService(
				GNAExportService.class);
		service.run(graph, filename);
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}
}