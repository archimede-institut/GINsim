package org.ginsim.gui.service.export.sbml;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ExportAction;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

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
		
		if (graph instanceof RegulatoryGraph) {
			//actions.add( new ExportSBMLAction( (RegulatoryGraph) graph));
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
class ExportSBMLAction extends ExportAction<RegulatoryGraph> {

	private static final GsFileFilter ffilter = new GsFileFilter(new String[] {"sbml"}, "SBML files");
	
	public ExportSBMLAction( RegulatoryGraph graph) {
		super( graph, "STR_SBML", "STR_SBML_descr");
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		SBMLExportService.export( graph, filename);
	}
}
