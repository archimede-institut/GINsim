package org.ginsim.servicegui.export.svg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.svg.SVGExportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.ExportAction;
import org.ginsim.servicegui.common.GUIFor;
import org.mangosdk.spi.ProviderFor;


/**
 * Export services to SVG file
 * 
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( SVGExportService.class)
public class SVGExportServiceGUI implements ServiceGUI {
	
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
		
		SVGExportService service = ServiceManager.getManager().getService( SVGExportService.class);
		
		if( service != null){
			service.run(graph, null, null, filename);
		}
		else{
			throw new GsException( GsException.GRAVITY_ERROR, "No SVGExportService service available");
		}
	}
}
