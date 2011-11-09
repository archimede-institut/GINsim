package fr.univmrs.tagc.GINsim.connectivity;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsActionAction;
import org.ginsim.gui.service.GsExportAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.service.connectivity.ConnectivityService;
import org.ginsim.service.testservice.TestService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsOpenAction;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;

/**
 * register the connectivity service
 */
@ProviderFor(GsServiceGUI.class)
@GUIFor(ConnectivityService.class)
public class ConnectivityServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ConnectivityAction( graph));
		
		return actions;
	}
	
//	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
//        if (actionType != ACTION_ACTION) {
//            return null;
//        }
//	    if (graph instanceof GsReducedGraph) {
//	        if (!graph.isVisible()) {
//	            return null;
//	        }
//			if (t_reducedAction == null) {
//			    t_reducedAction = new GsPluggableActionDescriptor[1];
//			    t_reducedAction[0] = new GsPluggableActionDescriptor("STR_connectivityExtract", "STR_connectivityExtract_descr", null, this, ACTION_ACTION, 0);
//			}
//	        return t_reducedAction;
//	    }
//		if (t_action == null) {
//			t_action = new GsPluggableActionDescriptor[1];
//			t_action[0] = new GsPluggableActionDescriptor("STR_connectivity", "STR_connectivity_descr", null, this, ACTION_ACTION, 0);
//		}
//		return t_action;
//	}
	
//	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
//        if (actionType != ACTION_ACTION) {
//            return;
//        }
//	    if (graph instanceof GsReducedGraph) {
//	        GsGraphDescriptor gd = GsGinsimGraphDescriptor.getInstance();
//            String s_ag = graph.getAssociatedGraphID();
//            if (s_ag != null) {
//    	        Graph subgraph = GsOpenAction.open(gd, null, ((GsReducedGraph)graph).getSelectedMap(), s_ag);
//    	        if (subgraph != null) {
//    	            GsEnv.whatToDoWithGraph(null, subgraph, true);
//    	        }
//            }
//	    } else {
//            new ConnectivityFrame(frame, graph);
//	    }
//	}

	class ConnectivityAction extends GsActionAction{
		
		private final Graph graph;
		
		protected ConnectivityAction( Graph graph) {
	        super( "STR_connectivityExtract", null, "STR_connectivityExtract_descr", null);
			this.graph = graph;
		}
		
		@Override
		public void actionPerformed( ActionEvent arg0) {
			
		    if (graph instanceof GsReducedGraph) {
		        GsGraphDescriptor gd = GsGinsimGraphDescriptor.getInstance();
	            String s_ag = ((GsReducedGraph) graph).getAssociatedGraphID();
	            if (s_ag != null) {
	            	// TODO : REFACTORING ACTION
	            	// Change the GsOpenAction
	    	        Graph subgraph = GsOpenAction.open(gd, null, ((GsReducedGraph)graph).getSelectedMap(), s_ag);
	    	        if (subgraph != null) {
	    	            GsEnv.whatToDoWithGraph(null, subgraph, true);
	    	        }
	            }
		    } 
		    else {
	            new ConnectivityFrame( frame, graph);
		    }
		}
	}
	
}
