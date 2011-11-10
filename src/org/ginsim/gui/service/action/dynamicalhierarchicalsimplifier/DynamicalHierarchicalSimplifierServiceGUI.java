package org.ginsim.gui.service.action.dynamicalhierarchicalsimplifier;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.graph.dynamicalhierarchicalgraph.GsDynamicalHierarchicalGraph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsActionAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.service.action.dynamicalhierarchicalsimplifier.DynamicalHierarchicalSimplifierService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( GsServiceGUI.class)
@GUIFor( DynamicalHierarchicalSimplifierService.class)
public class DynamicalHierarchicalSimplifierServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsDynamicalHierarchicalGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new DynamicalHierarchicalSimplifierAction( (GsDynamicalHierarchicalGraph)graph));
			return actions;
		}
		return null;
	}

}

class DynamicalHierarchicalSimplifierAction extends GsActionAction {

	private final GsDynamicalHierarchicalGraph graph;
	
	public DynamicalHierarchicalSimplifierAction( GsDynamicalHierarchicalGraph graph) {
		
		super( "STR_dynHier_simplify", "STR_dynHier_simplify_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        // TODO : REFACTORING ACTION
        // TODO: get the parent frame
		new GsDynamicalHierarchicalSimplifierFrame( null, graph);
	}
	
}
