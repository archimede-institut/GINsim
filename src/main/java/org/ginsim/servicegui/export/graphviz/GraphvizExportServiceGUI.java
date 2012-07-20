package org.ginsim.servicegui.export.graphviz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.export.graphviz.GraphvizExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * Export services to Graphviz format
 * 
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( GraphvizExportService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class GraphvizExportServiceGUI extends AbstractServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ExportGraphVizAction( graph, this));
		return actions;
	}

	@Override
	public int getInitialWeight() {
		return W_EXPORT_GENERIC + 10;
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
	
	public ExportGraphVizAction( Graph graph, ServiceGUI serviceGUI) {
		super( graph, "STR_graphviz", "STR_graphviz_descr", serviceGUI);
	}

	@Override
	protected void doExport(String filename) throws IOException {
		 
		GraphvizExportService service = ServiceManager.getManager().getService( GraphvizExportService.class);
		service.export( graph, null, null, filename);
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
	}
}

