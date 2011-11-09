package fr.univmrs.tagc.GINsim.regulatoryGraph.txt2reg;

import javax.swing.JFrame;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;

import fr.univmrs.tagc.GINsim.gui.GsActions;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.GsException;

public class TxtImportPlugin implements GsPlugin, GsActionProvider {

	private static final int TXT = 7; 

	public void registerPlugin() {
		GsActions.registerImportProvider(this);
	}

	private GsPluggableActionDescriptor[] t_import = null;

	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		if (actionType == ACTION_IMPORT) {
			if (graph instanceof GsRegulatoryGraph) {
				return new GsPluggableActionDescriptor[] { new GsPluggableActionDescriptor(
						"STR_TXT", "STR_TXT_descr", null, this, ACTION_IMPORT, TXT),};
			}
			return new GsPluggableActionDescriptor[] {};
		}
		return null;
	}

	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
		if (actionType != ACTION_IMPORT) {
			return;
		}

		GsFileFilter ffilter = new GsFileFilter();
		String extension = null;
		String filename;

		ffilter.setExtensionList(new String[] { "txt" }, "TXT files");
		extension = ".txt";

		// we should add a better way to select a file for import
		filename = GsOpenAction.selectFileWithOpenDialog(frame);


		// TODO ...
		TruthTableParser parser = new TruthTableParser(filename);
		System.out.println(filename);
		//Graph newGraph = parser.getGraph();
	}
}
