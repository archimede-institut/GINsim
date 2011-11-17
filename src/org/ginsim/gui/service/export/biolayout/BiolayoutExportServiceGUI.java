package org.ginsim.gui.service.export.biolayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
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

	static GsFileFilter ffilter = new GsFileFilter(new String[] {"layout"}, "biolayout files");
	
	public ExportBioLayoutAction( Graph graph) {
		
		super( graph, "STR_biolayout", "STR_biolayout_descr");
	}

	public GsFileFilter getFileFilter() {
		
		return ffilter;
	}

	@Override
	protected void doExport(String filename) throws GsException, IOException {
		BioLayoutExportService.encode(graph, null, filename);
	}
}
