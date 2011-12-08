package org.ginsim.servicegui.export.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.export.image.ImageExportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.ExportAction;
import org.ginsim.servicegui.common.GUIFor;
import org.mangosdk.spi.ProviderFor;


/**
 * Export services to PNG Image
 *  
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( ImageExportService.class)
public class ImageExportServiceGUI implements ServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ExportImageAction( graph));

		return actions;
	}

	@Override
	public int getWeight() {
		return W_GENERIC + 4;
	}
}


/**
 * Export to PNG image Action
 * 
 * @author spinelli
 *
 */
class ExportImageAction extends ExportAction {

	private static final GsFileFilter ffilter = new GsFileFilter(new String[] {"png"}, "PNG files");

	public ExportImageAction( Graph graph) {
		super( graph, "STR_Image", "STR_Image_descr");
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
        ImageExportService.exportImage(graph, false, filename);
	}
}

