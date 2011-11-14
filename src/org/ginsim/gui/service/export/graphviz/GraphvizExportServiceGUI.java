package org.ginsim.gui.service.export.graphviz;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsExportAction;
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
class ExportGraphVizAction extends GsExportAction {

	private final Graph graph;
	
	public ExportGraphVizAction( Graph graph) {
		
		super( "STR_graphviz", "STR_graphviz_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed( ActionEvent e) {
		
		String extension = ".dot";
	    
		GsFileFilter ffilter = new GsFileFilter();
	    ffilter.setExtensionList(new String[] {"graphviz"}, "dot (graphviz) files");
	    
        // TODO : REFACTORING ACTION
        // TODO : change the GsOpenAction
	    String filename = null;
	    
		//filename = GsOpenAction.selectSaveFile(null, ffilter, null, extension);
		if (filename == null) {
			return;
		}
		GraphvizExportService.encode(graph, null, null, filename);

	}
}

