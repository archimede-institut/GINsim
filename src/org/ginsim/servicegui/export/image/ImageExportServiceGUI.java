package org.ginsim.servicegui.export.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.export.image.ImageExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * Export services to PNG Image
 *  
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( ImageExportService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class ImageExportServiceGUI extends AbstractServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ExportImageAction( graph, this));

		return actions;
	}

	@Override
	public int getInitialWeight() {
		return W_EXPORT_DOC + 10;
	}
}


/**
 * Export to PNG image Action
 * 
 * @author spinelli
 *
 */
class ExportImageAction extends ExportAction {

	private static final FileFormatDescription FORMAT = new FileFormatDescription("PNG image", "png");

	public ExportImageAction( Graph graph, ServiceGUI serviceGUI) {
		super( graph, "STR_Image", "STR_Image_descr", serviceGUI);
	}

	@Override
	public FileFormatDescription getFileFilter() {
		return FORMAT;
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
        ImageExportService.export(graph, false, filename);
	}
}

