package org.ginsim.gui.service.export.snakes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.snakes.SnakesExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * Export the logical functions from regulatory graphs to python for use with the Snakes python library.
 * http://lacl.univ-paris12.fr/pommereau/soft/snakes/ 
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(SnakesExportService.class)
public class SnakesExportServiceGUI implements ServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new SnakesExportAction<Graph<?, ?>>( graph));
			return actions;
		}
		return null;
	}
}


/**
 * Export to Snakes file Action
 *
 */
class SnakesExportAction<G extends Graph> extends ExportAction {
	private static final long serialVersionUID = 7934695744239100292L;
	private static final GsFileFilter ffilter = new GsFileFilter(new String[] {"dot"}, "dot (graphviz) files");
	
	public SnakesExportAction(G graph) {
		
		super(graph, "STR_snakes", "STR_snakes_descr");
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		
		SnakesExportService service = ServiceManager.getManager().getService( SnakesExportService.class);
		
		if( service != null){
			service.run( (RegulatoryGraph)graph, filename);
		}
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}
}

