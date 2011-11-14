package org.ginsim.gui.service.export.svg;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsExportAction;
import org.ginsim.service.export.svg.SVGExportService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

/**
 * Export services to SVG file
 * 
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( SVGExportService.class)
public class SVGExportServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ExportSVGAction( graph));
		return actions;
	}
}


/**
 * Export to SVG File Action
 * 
 * @author spinelli
 *
 */
class ExportSVGAction extends GsExportAction {

	private final Graph graph;
	
	public ExportSVGAction( Graph graph) {
		
		super( "STR_SVG", "STR_SVG_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed( ActionEvent e) {
		
		String extension = ".svg";

		GsFileFilter ffilter = new GsFileFilter();
        ffilter.setExtensionList(new String[] {"svg"}, "SVG files");
	    
        // TODO : REFACTORING ACTION
        // TODO : change the GsOpenAction
	    String filename = null;
	    
		//filename = GsOpenAction.selectSaveFile(null, ffilter, null, extension);
		if (filename == null) {
			return;
		}
		SVGExportService.exportSVG(graph, null, null, filename);

	}
}

