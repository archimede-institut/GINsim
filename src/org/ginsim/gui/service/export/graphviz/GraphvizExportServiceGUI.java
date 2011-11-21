package org.ginsim.gui.service.export.graphviz;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.service.export.graphviz.GraphvizExportService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

/**
 * Export services to Graphviz format
 * 
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( GraphvizExportService.class)
public class GraphvizExportServiceGUI implements GsServiceGUI {
	
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
	protected void doExport(String filename) throws GsException, IOException {
		GraphvizExportService.encode(graph, null, null, filename);
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}
}

