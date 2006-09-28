package fr.univmrs.ibdm.GINsim.aRegGraph;

import javax.swing.JFrame;

import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicGraphDescriptor;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.plugin.GsPlugin;

/**
 * register the aRegGraph plugin: animate the regulatory graph 
 * according to a path in the associated state transition graph.
 */
public class GsARegGraphPlugin implements GsPlugin, GsActionProvider {

	private GsPluggableActionDescriptor[] t_action = null;
    
    public void registerPlugin() {
        GsDynamicGraphDescriptor.registerActionProvider(this);
		Translator.pushBundle("fr.univmrs.ibdm.GINsim.ressources.messagesARegGraph");
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (!graph.isVisible() || actionType != ACTION_ACTION) {
            return null;
        }
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_aRegGraph", "STR_aRegGraph_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
    }

    public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
        GsRegulatoryAnimator.animate(frame, graph);
    }
}
