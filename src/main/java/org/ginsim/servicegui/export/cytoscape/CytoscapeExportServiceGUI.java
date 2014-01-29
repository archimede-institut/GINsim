package org.ginsim.servicegui.export.cytoscape;

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
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.ServiceStatus;
import org.ginsim.service.export.cytoscape.CytoscapeExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * CytoscapeExportServiceGUI is the GUI service exporting a regulatory graph into XGMML format.
 * 
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(CytoscapeExportService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class CytoscapeExportServiceGUI extends AbstractServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new CytoscapeExportAction<Graph<?, ?>>( graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_EXPORT_GENERIC + 10;
	}
}


/**
 * Export to Cytoscape XGMMLfile Action
 *
 */
class CytoscapeExportAction<G extends Graph> extends ExportAction {
	private static final long serialVersionUID = 7934695744239100292L;
	private static final FileFormatDescription FORMAT = new FileFormatDescription("cytoscape network", "xgmml");
	
	public CytoscapeExportAction(G graph, ServiceGUI serviceGUI) {
		super( graph, "STR_cytoscape", "STR_cytoscape_descr", serviceGUI);
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

