package org.ginsim.gui.service.export.sbml;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsExportAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

/**
 * Export service to SBML format
 * 
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( SBMLExportService.class)
public class SBMLExportServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		
		if (graph instanceof GsRegulatoryGraph) {
			//actions.add( new ExportSBMLAction( (GsRegulatoryGraph) graph));
		}
		return actions;
	}
}


/**
 * Export to SBML Action
 * 
 * @author spinelli
 *
 */
class ExportSBMLAction extends GsExportAction {

	private final GsRegulatoryGraph graph;
	
	public ExportSBMLAction( GsRegulatoryGraph graph) {
		
		super( "STR_SBML", "STR_SBML_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed( ActionEvent e) {
		
		String extension = ".sbml";

		GsFileFilter ffilter = new GsFileFilter();
        ffilter.setExtensionList(new String[] {"sbml"}, "SBML files");
	    
        // TODO : REFACTORING ACTION
        // TODO : change the GsOpenAction
	    String filename = null;
	    
		//filename = GsOpenAction.selectSaveFile(null, ffilter, null, extension);
		if (filename == null) {
			return;
		}
        SBMLExportService.export( graph, filename);

	}
}
