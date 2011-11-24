package org.ginsim.gui.service.tool.localgraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.view.css.Selector;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.localgraph.LocalGraphService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;

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
				actions.add(new LocalGraphAction((DynamicGraph)graph));
			}
			catch( GsException ge){
	    		// TODO : REFACTORING ACTION
	    		// TODO : Indicate the problem to the user?
	    		Debugger.error( "Unable to add action for this graph since its associated graph was not retrieved");
	    		Debugger.error( ge);
			}
		}
		return actions;
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
