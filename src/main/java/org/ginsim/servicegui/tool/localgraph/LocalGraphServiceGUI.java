package org.ginsim.servicegui.tool.localgraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.tool.localgraph.LocalGraphConfig;
import org.ginsim.service.tool.localgraph.LocalGraphService;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(ServiceGUI.class)
@GUIFor(LocalGraphService.class)
@ServiceStatus(ServiceStatus.RELEASED)
public class LocalGraphServiceGUI extends AbstractServiceGUI {

	static {
		Selector.registerSelector(LocalGraphSelector.IDENTIFIER,
				LocalGraphSelector.class);
	}

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof RegulatoryGraph) {
			actions.add(new LocalGraphAction((RegulatoryGraph) graph, this));
		} else if (graph instanceof DynamicGraph) {
			try {
				actions.add(new LocalGraphAction((DynamicGraph) graph, this));
			} catch (GsException ge) {
				LogManager
						.debug("Unable to add action for this graph since its associated graph was not retrieved");
			}
		}
		return actions;
	}

	@Override
	public int getInitialWeight() {
		return W_GRAPH_COLORIZE + 30;
	}
}

class LocalGraphAction extends GenericGraphAction {
	private static final long serialVersionUID = -4679900718786692002L;

	private LocalGraphConfig config;

	protected LocalGraphAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		this(graph, null, serviceGUI);
	}

	protected LocalGraphAction(DynamicGraph graph, ServiceGUI serviceGUI)
			throws GsException {

		this(graph.getAssociatedGraph(), graph, serviceGUI);
	}

	protected LocalGraphAction(RegulatoryGraph graph, DynamicGraph dyn,
			ServiceGUI serviceGUI) {
		super(dyn, "STR_localGraph", null, "STR_localGraph_descr", null,
				serviceGUI);
		this.config = new LocalGraphConfig(graph, dyn);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new LocalGraphFrame(config);
	}
}
