package org.ginsim.gui.service.export.biolayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.export.biolayout.BioLayoutExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * Export service to biolayout format
 * 
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( BioLayoutExportService.class)
public class BiolayoutExportServiceGUI implements ServiceGUI {
	
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
class ExportBioLayoutAction extends ExportAction {

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
