package fr.univmrs.tagc.GINsim.regulatoryGraph.SBML;

import javax.swing.JFrame;

import fr.univmrs.tagc.GINsim.global.GsWhatToDoFrame;
import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsActions;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.GsException;

public class SBMLImportPlugin implements GsPlugin, GsActionProvider {

	private static final int XML = 7; // last modif

	public void registerPlugin() {
		GsActions.registerImportProvider(this);
	}

	private GsPluggableActionDescriptor[] t_import = null;

	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
		if (actionType == ACTION_IMPORT) {
			if (graph instanceof GsRegulatoryGraph) {
				return new GsPluggableActionDescriptor[] { new GsPluggableActionDescriptor(
						"STR_SBML_L3_IMP", "STR_SBML_L3_IMP_descr", null, this, ACTION_IMPORT, XML), };
			}
			return new GsPluggableActionDescriptor[] {};
		}
		return null;
	}

	public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
		if (actionType != ACTION_IMPORT) {
			return;
		}

		GsFileFilter ffilter = new GsFileFilter();
		String extension = null;
		String filename;

		switch (ref) {
		// case SBML3:
		case XML:
			ffilter.setExtensionList(new String[] { "xml" }, "SBML files");
			// extension = ".sbml";
			extension = ".xml";
			break;
		default:
			return;
		}
		// we should add a better way to select a file for import
		filename = GsOpenAction.selectFile(frame);
		if (filename == null) {
			System.out.println("fichier d'entree null");
			return;
		}

		/*
		 * switch (ref) { case SBML3: new SbmlXpathParser(filename); break; }
		 */
		System.out.println("On lance le parseur");
		SBMLXpathParser parser = new SBMLXpathParser(filename);
		GsGraph newGraph = parser.getGraph();
		new GsWhatToDoFrame(frame, newGraph, true);
	}
}
