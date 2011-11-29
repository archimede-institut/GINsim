package org.ginsim.gui.service.export.graphviz;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.graphviz.GraphvizExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * Export services to Graphviz format
 * 
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( GraphvizExportService.class)
public class GraphvizExportServiceGUI implements ServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ExportGraphVizAction( graph));
		return actions;
	}
}


/**
 * Export to GraphViz file Action
 * 
 * @author spinelli
 *
 */
class ExportGraphVizAction extends ExportAction {

	private static final GsFileFilter ffilter = new GsFileFilter(new String[] {"dot"}, "dot (graphviz) files");
	
	public ExportGraphVizAction( Graph graph) {
		super( graph, "STR_graphviz", "STR_graphviz_descr");
	}

	@Override
	protected void doExport(String filename) throws IOException {
		 
		GraphvizExportService service = ServiceManager.getManager().getService( GraphvizExportService.class);
		service.run( graph, null, null, filename);
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}
}

