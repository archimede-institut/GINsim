package fr.univmrs.ibdm.GINsim.dynamicGraph;

import javax.swing.JFrame;

import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.common.GsException;

/**
 * offer some facilities to analyse the state transition graph.
 */
public class GsDynamicAnalyserPlugin implements GsPlugin, GsActionProvider {

    private GsPluggableActionDescriptor[] t_actions ;

    public void registerPlugin() {
        GsDynamicGraphDescriptor.registerActionProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
		if (t_actions == null) {
			t_actions = new GsPluggableActionDescriptor[1];
			t_actions[0] = new GsPluggableActionDescriptor("STR_searchPath", "STR_searchPath_descr", null, this, ACTION_ACTION, 0);
		}
		return t_actions;
    }

    public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_ACTION) {
            return;
        }
        new GsDynamicSearchPathConfig(frame, (GsDynamicGraph)graph);
    }
}
