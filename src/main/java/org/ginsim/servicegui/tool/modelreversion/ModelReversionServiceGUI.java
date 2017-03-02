package org.ginsim.servicegui.tool.modelreversion;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.modifier.ModelModifier;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.LogicalModel2RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.shell.actions.ToolAction;
import org.ginsim.service.tool.modelbooleanizer.ModelBooleanizerService;
import org.ginsim.service.tool.modelreversion.ModelReversionService;
import org.mangosdk.spi.ProviderFor;

/**
 * Main method for the model reversion plugin
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ModelReversionService.class)
@ServiceStatus(EStatus.DEVELOPMENT)
public class ModelReversionServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new ReversionAction((RegulatoryGraph) graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 50;
	}
}

class ReversionAction extends ToolAction {

	private static final long serialVersionUID = 4751364133411945974L;
	private final RegulatoryGraph graph;

	public ReversionAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		super("STR_reverse", "STR_reverse_descr", serviceGUI);
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		ModelReversionService revService = ServiceManager.getManager().getService(
				ModelReversionService.class);
		ModelBooleanizerService boolService = ServiceManager.getManager().getService(
				ModelBooleanizerService.class);
		
		LogicalModel origModel = this.graph.getModel();

		// Model reverser
		ModelModifier modelReverser = revService.getModelReverser(origModel);
		RegulatoryGraph gReversed = LogicalModel2RegulatoryGraph
				.importModel(modelReverser.getModifiedModel());

		// Copy all the (edge & node) styles from the original graph to the reversed one
		boolService.copyNodeStyles(this.graph, gReversed);

		// Show the reversed graph
		GUIManager.getInstance().whatToDoWithGraph(gReversed, true);
		if (!origModel.isBoolean()) {
			GUIMessageUtils.openErrorDialog("STR_reverse_multivalue");
		}

	}
}
