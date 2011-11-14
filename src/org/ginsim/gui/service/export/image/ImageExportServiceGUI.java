package org.ginsim.gui.service.export.image;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsExportAction;
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
class ExportImageAction extends GsExportAction {

	private final Graph graph;
	
	public ExportImageAction( Graph graph) {
		
		super( "STR_Image", "STR_Image_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed( ActionEvent e) {
		
		String extension = ".png";

		GsFileFilter ffilter = new GsFileFilter();
        ffilter.setExtensionList(new String[] {"png"}, "PNG files");
	    
        // TODO : REFACTORING ACTION
        // TODO : change the GsOpenAction
	    String filename = null;
	    
		//filename = GsOpenAction.selectSaveFile(null, ffilter, null, extension);
		if (filename == null) {
			return;
		}
        ImageExportService.exportImage(graph, false, filename);

	}
}

