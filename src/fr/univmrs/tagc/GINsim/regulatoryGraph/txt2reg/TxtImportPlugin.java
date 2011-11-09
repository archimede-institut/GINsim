package fr.univmrs.tagc.GINsim.regulatoryGraph.txt2reg;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsActions;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

@ProviderFor(GsServiceGUI.class)
public class TxtImportPlugin implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new TxtImportAction());
		return actions;
	}
}

class TxtImportAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GsFileFilter ffilter = new GsFileFilter();
		String extension = null;
		String filename;

		ffilter.setExtensionList(new String[] { "txt" }, "TXT files");
		extension = ".txt";

		// we should add a better way to select a file for import
		// FIXME: get the main frame for this graph
		filename = GsOpenAction.selectFileWithOpenDialog(frame);


		// TODO ...
		TruthTableParser parser = new TruthTableParser(filename);
		System.out.println(filename);
		//Graph newGraph = parser.getGraph();
	}

}