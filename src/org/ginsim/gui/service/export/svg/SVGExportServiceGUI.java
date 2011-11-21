package org.ginsim.gui.service.export.svg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.export.svg.SVGExportService;
import org.mangosdk.spi.ProviderFor;


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
class ExportSVGAction extends ExportAction {

	private static final GsFileFilter ffilter = new GsFileFilter(new String[] {"svg"}, "SVG files");
	
	public ExportSVGAction( Graph graph) {
		super( graph, "STR_SVG", "STR_SVG_descr");
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		SVGExportService.exportSVG(graph, null, null, filename);
	}
}

