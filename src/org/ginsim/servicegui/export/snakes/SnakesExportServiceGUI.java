package org.ginsim.servicegui.export.snakes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.export.snakes.SnakesExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * Export the logical functions from regulatory graphs to python for use with the Snakes python library.
 * http://lacl.univ-paris12.fr/pommereau/soft/snakes/ 
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(SnakesExportService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class SnakesExportServiceGUI extends AbstractServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new SnakesExportAction<Graph<?, ?>>( graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_EXPORT_SPECIFIC + 40;
	}
}


/**
 * Export to Snakes file Action
 *
 */
class SnakesExportAction<G extends Graph> extends ExportAction {
	private static final long serialVersionUID = 7934695744239100292L;
	private static final FileFormatDescription FORMAT = new FileFormatDescription("Python code", "py");
	
	public SnakesExportAction(G graph, ServiceGUI serviceGUI) {
		
		super(graph, "STR_snakes", "STR_snakes_descr", serviceGUI);
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		
		SnakesExportService service = ServiceManager.getManager().getService( SnakesExportService.class);
		
		if( service != null){
			service.run( (RegulatoryGraph)graph, filename);
		}
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
	}
}

