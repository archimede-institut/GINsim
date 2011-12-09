package org.ginsim.servicegui.imports.sbml;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.imports.sbml.SBMLImportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ImportAction;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(ServiceGUI.class)
@GUIFor(SBMLImportService.class)
public class SBMLImportServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		
		if( graph instanceof RegulatoryGraph){
			List<Action> actions = new ArrayList<Action>();
			actions.add(new SBMLImportAction());
			return actions;
		}
		return null;
	}
	
	@Override
	public int getWeight() {
		return W_MANIPULATION + 1;
	}
}

class SBMLImportAction extends ImportAction {

	private static final long serialVersionUID = -4775775151225210628L;
	private static final GsFileFilter ffilter = new GsFileFilter(
			new String[] { "sbml" }, "sbml (SBML) files");

	public SBMLImportAction() {
		super("STR_SBML_L3_IMP", "STR_SBML_L3_IMP_descr");
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}

	@Override
	protected void doImport(String filename) {
		if (filename == null) {
			return;
		}

		SBMLImportService service = ServiceManager.getManager().getService(
				SBMLImportService.class);
		if (service == null) {
			LogManager
					.error("SBMLImportService service is not available");
			return;
		}
		
		RegulatoryGraph newGraph = service.run(filename);
		GUIManager.getInstance().whatToDoWithGraph(newGraph, true);
	}
}
