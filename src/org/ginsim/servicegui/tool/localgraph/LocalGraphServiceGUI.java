package org.ginsim.servicegui.tool.localgraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.service.tool.localgraph.LocalGraphService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;



@ProviderFor( ServiceGUI.class)
@GUIFor( LocalGraphService.class)
public class LocalGraphServiceGUI implements ServiceGUI {

	static {
		Selector.registerSelector(LocalGraphSelector.IDENTIFIER, LocalGraphSelector.class);
	}
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof RegulatoryGraph) {
			actions.add(new LocalGraphAction((RegulatoryGraph)graph));
		} else if (graph instanceof DynamicGraph){
			try{
				actions.add( new LocalGraphAction((DynamicGraph)graph));
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
	public int getWeight() {
		return W_INFO + 5;
	}
}

class LocalGraphAction extends ToolAction {

	private final RegulatoryGraph graph;
	private final DynamicGraph dyn;
	
	protected LocalGraphAction(RegulatoryGraph graph) {
		this( graph, null);
	}
	
	protected LocalGraphAction(DynamicGraph graph) throws GsException{
		
		this( graph.getAssociatedGraph(), graph);
	}
	
	protected LocalGraphAction(RegulatoryGraph graph, DynamicGraph dyn) {
		super("STR_localGraph", "STR_localGraph_descr");
		this.graph = graph;
		this.dyn = dyn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (dyn == null) {
			new LocalGraphFrame( GUIManager.getInstance().getFrame( graph), graph);
		} else {
			new LocalGraphFrame( GUIManager.getInstance().getFrame( graph), graph, dyn);
		}
	}
}
