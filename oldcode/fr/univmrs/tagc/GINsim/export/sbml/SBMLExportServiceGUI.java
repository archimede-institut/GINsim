package org.ginsim.servicegui.export.sbml;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.ExportAction;
import org.ginsim.servicegui.common.GUIFor;
import org.mangosdk.spi.ProviderFor;


/**
 * Export service to SBML format
 * 
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( SBMLExportService.class)
public class SBMLExportServiceGUI extends AbstractServiceGUI {
	
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
