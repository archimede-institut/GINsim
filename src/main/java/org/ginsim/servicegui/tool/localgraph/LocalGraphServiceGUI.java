package org.ginsim.servicegui.tool.localgraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.localgraph.LocalGraphService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@GUIFor( LocalGraphService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class LocalGraphServiceGUI extends AbstractServiceGUI {

	static {
		Selector.registerSelector(LocalGraphSelector.IDENTIFIER, LocalGraphSelector.class);
	}
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof RegulatoryGraph) {
			actions.add(new LocalGraphAction((RegulatoryGraph)graph, this));
		} else if (graph instanceof DynamicGraph){
			try{
				actions.add( new LocalGraphAction((DynamicGraph)graph, this));
			}
			catch( GsException ge){
	    		GUIMessageUtils.openErrorDialog( "The Local Graph tool will not be available since the associated Regulatory graph was not found");
	    		LogManager.error( "Unable to add action for this graph since its associated graph was not retrieved");
	    		LogManager.error( ge);
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

	private final RegulatoryGraph graph;
	private final DynamicGraph dyn;
	
	protected LocalGraphAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		this( graph, null, serviceGUI);
	}
	
	protected LocalGraphAction(DynamicGraph graph, ServiceGUI serviceGUI) throws GsException{
		
		this( graph.getAssociatedGraph(), graph, serviceGUI);
	}
	
	protected LocalGraphAction(RegulatoryGraph graph, DynamicGraph dyn, ServiceGUI serviceGUI) {
		super(dyn, "STR_localGraph", null, "STR_localGraph_descr", null, serviceGUI);
		this.graph = graph;
		this.dyn = dyn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (dyn == null) {
				new LocalGraphFrame( GUIManager.getInstance().getFrame( graph), graph);
			} else {
				new LocalGraphFrame( GUIManager.getInstance().getFrame( graph), graph, dyn);
			}
			
		} catch (GsException ex) {
			GUIMessageUtils.openErrorDialog("STR_localGraph_errorMultivaluedModel");
		}
	}
}
