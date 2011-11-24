package org.ginsim.gui.service.imports.txt;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ImportAction;
import org.ginsim.gui.shell.FileSelectionHelper;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.service.imports.txt.TxtImportService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;


@ProviderFor(ServiceGUI.class)
@GUIFor( TxtImportService.class)
public class TxtImportServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new TxtImportAction( graph));
		return actions;
	}
}

class TxtImportAction extends ImportAction {

	private final Graph<?,?> graph;
	
	public TxtImportAction( Graph<?,?> graph) {
		super( "STR_txtimport", "STR_txtimport_descr");
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		GsFileFilter ffilter = new GsFileFilter();
		String extension = null;
		String filename;

		ffilter.setExtensionList(new String[] { "txt" }, "TXT files");
		extension = ".txt";

		// we should add a better way to select a file for import
		filename = FileSelectionHelper.selectOpenFilename( GUIManager.getInstance().getFrame( graph));

		// TODO ...
		TruthTableParser parser = new TruthTableParser(filename);
		Debugger.error( "Action to perform here : " + filename);
		//Graph newGraph = parser.getGraph();
	}

}