package org.ginsim.gui.service.tools.dynamicalhierarchicalsimplifier;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.tools.dynamicalhierarchicalsimplifier.DynamicalHierarchicalSimplifierService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GsServiceGUI.class)
@GUIFor( DynamicalHierarchicalSimplifierService.class)
public class DynamicalHierarchicalSimplifierServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof DynamicalHierarchicalGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new DynamicalHierarchicalSimplifierAction( (DynamicalHierarchicalGraph)graph));
			return actions;
		}
		return null;
	}

}

class DynamicalHierarchicalSimplifierAction extends GsToolsAction {

	private final DynamicalHierarchicalGraph graph;
	
	public DynamicalHierarchicalSimplifierAction( DynamicalHierarchicalGraph graph) {
		
		super( "STR_dynHier_simplify", "STR_dynHier_simplify_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		new DynamicalHierarchicalSimplifierFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}
