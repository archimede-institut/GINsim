package org.ginsim.gui.service.export.gna;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.gna.GNAMLExportService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to export a GNAML model description
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(GNAMLExportService.class)
public class GNAMLExportServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new GNAMLExportAction((RegulatoryGraph) graph));
			return actions;
		}
		return null;
	}
}

/**
 * Export to GNAML file Action
 * 
 * @author Pedro T. Monteiro
 * 
 */
class GNAMLExportAction extends ExportAction<RegulatoryGraph> {

	private static final long serialVersionUID = 1184129217755724894L;
	private static final GsFileFilter ffilter = new GsFileFilter(
			new String[] { "gnaml" }, "gnaml (GNAML) files");

	public GNAMLExportAction(RegulatoryGraph graph) {
		super(graph, "STR_GNAML", "STR_GNAML_descr");
	}

	@Override
	protected void doExport(String filename) throws IOException {

		GNAMLExportService service = ServiceManager.getManager().getService(
				GNAMLExportService.class);
		service.run(graph, filename);
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}
}
