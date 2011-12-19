package org.ginsim.servicegui.export.cytoscape;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.cytoscape.CytoscapeExportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.ExportAction;
import org.ginsim.servicegui.common.GUIFor;
import org.mangosdk.spi.ProviderFor;


/**
 * CytoscapeExportServiceGUI is the GUI service exporting a regulatory graph into XGMML format.
 * 
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(CytoscapeExportService.class)
public class CytoscapeExportServiceGUI implements ServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new CytoscapeExportAction<Graph<?, ?>>( graph));
			return actions;
		}
		return null;
	}

	@Override
	public int getWeight() {
		return W_MANIPULATION + 7;
	}
}


/**
 * Export to Cytoscape XGMMLfile Action
 *
 */
class CytoscapeExportAction<G extends Graph> extends ExportAction {
	private static final long serialVersionUID = 7934695744239100292L;
	private static final FileFormatDescription FORMAT = new FileFormatDescription("cytoscape network", "xgmml");
	
	public CytoscapeExportAction(G graph) {
		super( graph, "STR_cytoscape", "STR_cytoscape_descr");
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		
		CytoscapeExportService service = ServiceManager.getManager().getService( CytoscapeExportService.class);
		
		if( service != null){
			service.run( (RegulatoryGraph)graph, filename);
		}
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
	}
}

