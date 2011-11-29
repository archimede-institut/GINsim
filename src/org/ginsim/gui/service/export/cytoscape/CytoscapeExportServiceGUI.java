package org.ginsim.gui.service.export.cytoscape;

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
import org.ginsim.service.export.cytoscape.CytoscapeExportService;
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
}


/**
 * Export to Cytoscape XGMMLfile Action
 *
 */
class CytoscapeExportAction<G extends Graph> extends ExportAction {
	private static final long serialVersionUID = 7934695744239100292L;
	private static final GsFileFilter ffilter = new GsFileFilter(new String[] {"dot"}, "dot (graphviz) files");
	
	public CytoscapeExportAction(G graph) {
		super( graph, "STR_cytoscape", "STR_cytoscape_descr");
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		CytoscapeExportService.encode((RegulatoryGraph)graph, filename);
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}
}

