package org.ginsim.servicegui.tool.polytopesViz;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.service.EStatus;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.service.StandaloneGUI;
import org.ginsim.gui.shell.actions.ToolkitAction;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( EStatus.DEVELOPMENT)
public class PolytopesVizServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof DynamicGraph) {
			List<Action> actions = new ArrayList<>();
			actions.add(new PolytopesVizAction((DynamicGraph)graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 40;
	}


	class PolytopesVizAction extends ToolkitAction {
		private DynamicGraph graph;

		public PolytopesVizAction(DynamicGraph graph, ServiceGUI serviceGUI) {
			super("STR_polytopesViz", "STR_polytopesViz_descr", serviceGUI);
			this.graph = graph;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			new PolytopesVizFrame(graph);
		}

	}
}

