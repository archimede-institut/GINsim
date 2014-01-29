package org.ginsim.servicegui.export.svg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.application.GsException;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.shell.actions.ExportAction;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.ServiceStatus;
import org.ginsim.service.export.svg.SVGExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * Export services to SVG file
 * 
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( SVGExportService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class SVGExportServiceGUI extends AbstractServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ExportSVGAction( graph, this));
		return actions;
	}

	@Override
	public int getInitialWeight() {
		return W_EXPORT_DOC + 20;
	}
}


/**
 * Export to SVG File Action
 * 
 * @author Lionel Spinelli
 *
 */
class ExportSVGAction extends ExportAction {

	private static final FileFormatDescription FORMAT = new FileFormatDescription("SVG image", "svg");
	
	public ExportSVGAction( Graph graph, ServiceGUI serviceGUI) {
		super( graph, "STR_SVG", "STR_SVG_descr", serviceGUI);
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		
		SVGExportService service = ServiceManager.getManager().getService( SVGExportService.class);
		
		if( service != null){
			service.export(graph, null, null, filename);
		}
		else{
			throw new GsException( GsException.GRAVITY_ERROR, "No SVGExportService service available");
		}
	}
}

