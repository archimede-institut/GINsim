package org.ginsim.servicegui.export.svg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.export.svg.SVGExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * Export services to SVG file
 * 
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( SVGExportService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class SVGExportServiceGUI implements ServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ExportSVGAction( graph));
		return actions;
	}

	@Override
	public int getWeight() {
		return W_GENERIC + 5;
	}
}


/**
 * Export to SVG File Action
 * 
 * @author spinelli
 *
 */
class ExportSVGAction extends ExportAction {

	private static final FileFormatDescription FORMAT = new FileFormatDescription("SVG image", "svg");
	
	public ExportSVGAction( Graph graph) {
		super( graph, "STR_SVG", "STR_SVG_descr");
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
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

