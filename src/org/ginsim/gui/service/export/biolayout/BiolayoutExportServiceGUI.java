package org.ginsim.gui.service.export.biolayout;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsExportAction;
import org.ginsim.service.export.biolayout.BioLayoutExportService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;

/**
 * Export service to biolayout format
 * 
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( BioLayoutExportService.class)
public class BiolayoutExportServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ExportBioLayoutAction( graph));
		return actions;
	}
}


/**
 * Export to Biolayout file Action
 * 
 * @author spinelli
 *
 */
class ExportBioLayoutAction extends GsExportAction {

	private final Graph graph;
	
	public ExportBioLayoutAction( Graph graph) {
		
		super( "STR_biolayout", "STR_biolayout_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed( ActionEvent e) {
		
		String extension = ".layout";
        
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"layout"}, "biolayout files");
	    
        // TODO : REFACTORING ACTION
        // TODO : change the GsOpenAction
	    String filename = null;
	    
		//filename = GsOpenAction.selectSaveFile(null, ffilter, null, extension);
		if (filename == null) {
			return;
		}
		BioLayoutExportService.encode(graph, null, filename);

	}
}
