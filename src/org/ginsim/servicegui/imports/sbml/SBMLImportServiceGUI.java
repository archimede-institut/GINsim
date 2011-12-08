package org.ginsim.servicegui.imports.sbml;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.gui.utils.widgets.Frame;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.imports.sbml.SBMLImportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ImportAction;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@GUIFor( SBMLImportService.class)
public class SBMLImportServiceGUI implements ServiceGUI {
	
	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		
		if( graph instanceof RegulatoryGraph){
			List<Action> actions = new ArrayList<Action>();
			actions.add(new SBMLImportAction( (RegulatoryGraph) graph));
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

	private final RegulatoryGraph graph;
	
	public SBMLImportAction( RegulatoryGraph graph) {
		super( "STR_SBML_L3_IMP", "STR_SBML_L3_IMP_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		GsFileFilter ffilter = new GsFileFilter();
		String filename;

		ffilter.setExtensionList(new String[] { "sbml" }, "SBML files");

		Frame frame = GUIManager.getInstance().getFrame( graph);
		
		// we should add a better way to select a file for import
		filename = FileSelectionHelper.selectOpenFilename( frame, ffilter);
		if (filename == null) {
			return;
		}
		
		SBMLImportService service = ServiceManager.getManager().getService( SBMLImportService.class);
		Graph new_graph = service.run( filename);
		
		GUIManager.getInstance().whatToDoWithGraph (new_graph, true);
	}
}
