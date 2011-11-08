package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import javax.swing.JFrame;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.common.GsException;

public class GsDynamicalHierarchicalSimplifierPlugin implements GsPlugin, GsActionProvider {

    private GsPluggableActionDescriptor[] t_actions ;
    
	public void registerPlugin() {
        GsDynamicalHierarchicalGraphDescriptor.registerActionProvider(this);
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
		if (t_actions == null) {
			t_actions = new GsPluggableActionDescriptor[1];
			t_actions[0] = new GsPluggableActionDescriptor("STR_dynHier_simplify", "STR_dynHier_simplify_descr", null, this, ACTION_ACTION, 0);
		}
		return t_actions;
	}

	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
	       if (actionType != ACTION_ACTION) {
	            return;
	        }
	        new GsDynamicalHierarchicalSimplifierFrame(frame, (GsDynamicalHierarchicalGraph)graph);
	}

}
