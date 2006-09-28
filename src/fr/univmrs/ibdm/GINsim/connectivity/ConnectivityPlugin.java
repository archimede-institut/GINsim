package fr.univmrs.ibdm.GINsim.connectivity;

import javax.swing.JFrame;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphDescriptor;
import fr.univmrs.ibdm.GINsim.gui.GsOpenAction;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.plugin.GsPlugin;

/**
 * register the connectivity plugin
 */
public class ConnectivityPlugin implements GsPlugin, GsActionProvider {

	private GsPluggableActionDescriptor[] t_action;
	private GsPluggableActionDescriptor[] t_reducedAction;

	public void registerPlugin() {
		GsGraph.registerActionProvider(this);
	}
	
	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
	    if (graph instanceof GsReducedGraph) {
	        if (!graph.isVisible()) {
	            return null;
	        }
			if (t_reducedAction == null) {
			    t_reducedAction = new GsPluggableActionDescriptor[1];
			    t_reducedAction[0] = new GsPluggableActionDescriptor("STR_connectivityExtract", "STR_connectivityExtract_descr", null, this, ACTION_ACTION, 0);
			}
	        return t_reducedAction;
	    }
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_connectivity", "STR_connectivity_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
	}
	
	public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_ACTION) {
            return;
        }
	    if (graph instanceof GsReducedGraph) {
	        GsGraphDescriptor gd = GsGinsimGraphDescriptor.getInstance();
            String s_ag = graph.getAssociatedGraphID();
            if (s_ag != null) {
    	        GsGraph subgraph = GsOpenAction.open(gd, null, ((GsReducedGraph)graph).getSelectedMap(), s_ag);
    	        if (subgraph != null) {
    	            GsEnv.whatToDoWithGraph(null, subgraph);
    	        }
            }
	    } else {
            new ConnectivityFrame(frame, graph);
	    }
	}

}
