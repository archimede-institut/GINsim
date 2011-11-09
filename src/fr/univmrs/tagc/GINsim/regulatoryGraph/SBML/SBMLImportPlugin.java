package fr.univmrs.tagc.GINsim.regulatoryGraph.SBML;

import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.global.GsWhatToDoFrame;
import fr.univmrs.tagc.GINsim.gui.GsActions;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

public class SBMLImportPlugin implements GsPlugin, GsActionProvider {

	private static final int XML = 7; 

	public void registerPlugin() {
		GsActions.registerImportProvider(this);
	}

	private GsPluggableActionDescriptor[] t_import = null;

	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		if (actionType == ACTION_IMPORT) {
			if (graph instanceof GsRegulatoryGraph) {
				return new GsPluggableActionDescriptor[] { new GsPluggableActionDescriptor(
						"STR_SBML_L3_IMP", "STR_SBML_L3_IMP_descr", null, this, ACTION_IMPORT, XML), };
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
		filename = GsOpenAction.selectFileWithOpenDialog(frame);
		if (filename == null) {
			return;
		}
		SBMLXpathParser parser = new SBMLXpathParser(filename);
		Graph newGraph = parser.getGraph();
		new GsWhatToDoFrame(frame, newGraph, true);
	}
}
