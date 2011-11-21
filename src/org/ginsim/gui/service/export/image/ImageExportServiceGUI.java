package org.ginsim.gui.service.export.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.service.export.image.ImageExportService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

/**
 * Export services to PNG Image
 *  
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( ImageExportService.class)
public class ImageExportServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ExportImageAction( graph));

		return actions;
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

