package org.ginsim.servicegui.export.graphviz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.graphviz.GraphvizExportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.ExportAction;
import org.ginsim.servicegui.common.GUIFor;
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

	@Override
	public int getWeight() {
		return W_GENERIC + 1;
	}
}


/**
 * Export to GraphViz file Action
 * 
 * @author spinelli
 *
 */
class ExportGraphVizAction extends ExportAction {

	private static final FileFormatDescription FORMAT = new FileFormatDescription("graphviz", "dot");
	
	public ExportGraphVizAction( Graph graph) {
		super( graph, "STR_graphviz", "STR_graphviz_descr");
	}

	@Override
	protected void doExport(String filename) throws IOException {
		 
		GraphvizExportService service = ServiceManager.getManager().getService( GraphvizExportService.class);
		service.run( graph, null, null, filename);
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
	}
}

