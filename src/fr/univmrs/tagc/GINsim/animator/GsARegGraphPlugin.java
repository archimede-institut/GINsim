package fr.univmrs.tagc.GINsim.animator;

import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;

/**
 * register the aRegGraph plugin: animate the regulatory graph 
 * according to a path in the associated state transition graph.
 */
public class GsARegGraphPlugin implements GsPlugin, GsActionProvider {

	private GsPluggableActionDescriptor[] t_action = null;
    
    public void registerPlugin() {
        GsDynamicGraphDescriptor.registerActionProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
        if (!graph.isVisible() || actionType != ACTION_ACTION) {
            return null;
        }
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_aRegGraph", "STR_aRegGraph_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
    }

    public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
        GsRegulatoryAnimator.animate(frame, graph);
    }
}
