package fr.univmrs.tagc.GINsim.dynamicGraph;

import javax.swing.JFrame;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.common.GsException;

/**
 * offer some facilities to analyse the state transition graph.
 */
public class GsDynamicAnalyserPlugin implements GsPlugin, GsActionProvider {

    private GsPluggableActionDescriptor[] t_actions ;

    public void registerPlugin() {
        GsDynamicGraphDescriptor.registerActionProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
		if (t_actions == null) {
			t_actions = new GsPluggableActionDescriptor[1];
			t_actions[0] = new GsPluggableActionDescriptor("STR_searchPath", "STR_searchPath_descr", null, this, ACTION_ACTION, 0);
		}
		return t_actions;
    }

    public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_ACTION) {
            return;
        }
        new GsDynamicSearchPathConfig(frame, (GsDynamicGraph)graph);
    }
}
