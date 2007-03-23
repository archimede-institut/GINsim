package fr.univmrs.ibdm.GINsim.stableStates;

import javax.swing.JFrame;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.plugin.GsPlugin;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;

/**
 * register the stableStates plugin: look for stable states without running a simulation
 */
public class GsStableStatesPlugin implements GsPlugin, GsActionProvider {

	private GsPluggableActionDescriptor[] t_action = null;
    
    public void registerPlugin() {
        GsRegulatoryGraphDescriptor.registerActionProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_stableStates", "STR_stableStates_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
    }

    public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
    	System.out.println("look for stable states here!");
    	GsSearchStableStates search = new GsSearchStableStates(graph);
    	search.run();
    }
}
