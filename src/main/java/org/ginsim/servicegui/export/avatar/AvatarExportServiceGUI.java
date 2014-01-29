package org.ginsim.servicegui.export.avatar;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.application.GsException;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.shell.actions.ExportAction;
import org.ginsim.gui.service.ServiceStatus;
import org.ginsim.gui.service.StandaloneGUI;
import org.ginsim.service.export.avatar.AvatarConfig;
import org.ginsim.service.export.avatar.AvatarExportService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to export a AVATAR model description
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus(ServiceStatus.RELEASED)
public class AvatarExportServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new AvatarExportAction((RegulatoryGraph) graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_EXPORT_SPECIFIC + 30;
	}
}

/**
 * Export to AVATAR file Action
 * 
 * @author Pedro T. Monteiro
 */
class AvatarExportAction extends ExportAction<RegulatoryGraph> {

	private static final long serialVersionUID = -5036736355808766593L;

	private static final FileFormatDescription FORMAT = new FileFormatDescription(
			"AVATAR", "avatar");

	private AvatarConfig config;

	public AvatarExportAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		super(graph, "STR_AVATAR", "STR_AVATAR_descr", serviceGUI);
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		config = new AvatarConfig(graph);
		new AvatarExportConfigPanel(config, this);
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		if (config == null) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"Nothing to export: AvatarConfig must be specified");
		}
		if (config.getGraph() == null
				|| config.getGraph().getNodes().size() == 0) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"Nothing to export: The graph is empty");
		}

		AvatarExportService service = ServiceManager.getManager().getService(
				AvatarExportService.class);
		if (service == null) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"AvatarExportService service is not available");
		}
		service.run(config, filename);
	}
}
