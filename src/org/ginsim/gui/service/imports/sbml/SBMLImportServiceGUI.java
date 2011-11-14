package org.ginsim.gui.service.imports.sbml;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsImportAction;
import org.ginsim.gui.service.tools.reg2dyn.GsBatchSimulationFrame;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParameterList;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParametersManager;
import org.ginsim.gui.service.tools.reg2dyn.GsSingleSimulationFrame;
import org.ginsim.service.imports.sbml.SBMLImportService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.Frame;

@ProviderFor( GsServiceGUI.class)
@GUIFor( SBMLImportService.class)
public class SBMLImportServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		
		if( graph instanceof GsRegulatoryGraph){
			List<Action> actions = new ArrayList<Action>();
			actions.add(new SBMLImportAction( (GsRegulatoryGraph) graph));
			return actions;
		}
		return null;
	}
}


class SBMLImportAction extends GsImportAction {

	private final GsRegulatoryGraph graph;
	
	public SBMLImportAction( GsRegulatoryGraph graph) {
		super( "STR_SBML_L3_IMP", "STR_SBML_L3_IMP_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		GsFileFilter ffilter = new GsFileFilter();
		String extension = null;
		String filename;

		ffilter.setExtensionList(new String[] { "xml" }, "SBML files");
		// extension = ".sbml";
		extension = ".xml";

		Frame frame = GUIManager.getInstance().getFrame( graph);
		
		// we should add a better way to select a file for import
		filename = GsOpenAction.selectFileWithOpenDialog( frame);
		if (filename == null) {
			return;
		}
		SBMLXpathParser parser = new SBMLXpathParser(filename);
		Graph newGraph = parser.getGraph();
		new GsWhatToDoFrame( frame, newGraph, true);
	}
}
