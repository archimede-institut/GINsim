package org.ginsim.servicegui.tool.dynamicalhierarchicalsimplifier;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.service.tool.dynamicalhierarchicalsimplifier.DynamicalHierarchicalSimplifierService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@GUIFor( DynamicalHierarchicalSimplifierService.class)
public class DynamicalHierarchicalSimplifierServiceGUI implements ServiceGUI {
	
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

class DynamicalHierarchicalSimplifierAction extends ToolAction {

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
