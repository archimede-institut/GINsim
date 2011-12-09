package org.ginsim.servicegui.imports.txt;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.service.imports.txt.TxtImportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ImportAction;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@GUIFor( TxtImportService.class)
public class TxtImportServiceGUI implements ServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new TxtImportAction( graph));
		return actions;
	}
	@Override
	public int getWeight() {
		return W_MANIPULATION + 2;
	}

}

class TxtImportAction extends ImportAction {

	private static final FileFormatDescription FORMAT = new FileFormatDescription("text import", "txt");
	
	public TxtImportAction( Graph<?,?> graph) {
		super( graph, "STR_txtimport", "STR_txtimport_descr");
	}
	
	@Override
	public void doImport( String filename) {
		// TODO ...
		TruthTableParser parser = new TruthTableParser(filename);
		LogManager.error( "Action to perform here : " + filename);
		//Graph newGraph = parser.getGraph();
	}

	@Override
	public FileFormatDescription getFormat() {
		return FORMAT;
	}

}