package org.ginsim.servicegui.imports.boolsim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ImportAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.imports.boolsim.BoolsimImportService;
import org.ginsim.service.imports.sbml.SBMLImportService;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(ServiceGUI.class)
@GUIFor(BoolsimImportService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class BoolsimImportServiceGUI extends AbstractServiceGUI {

	public static final FileFormatDescription FORMAT = new FileFormatDescription("Boolsim", "net");

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add(new BoolsimImportAction());
		return actions;
	}
	
	@Override
	public int getInitialWeight() {
		return 1;
	}
}

class BoolsimImportAction extends ImportAction {

	private static final long serialVersionUID = -4775775151225210628L;

	public BoolsimImportAction() {
		super("Boolsim", "Import Boolsim model");
	}

	@Override
	protected FileFormatDescription getFileFilter() {
		return BoolsimImportServiceGUI.FORMAT;
	}

	@Override
	protected void doImport(String filename) {
		if (filename == null) {
			return;
		}

		BoolsimImportService service = ServiceManager.getManager().getService(
				BoolsimImportService.class);
		if (service == null) {
			LogManager.error("BoolsimImportService service is not available");
			return;
		}
		
		RegulatoryGraph newGraph;
		try {
			newGraph = service.importFile(filename);
			GUIManager.getInstance().whatToDoWithGraph(newGraph, true);
		} catch (IOException e) {
			LogManager.error("Error in boolsim import");
		}
	}
}
