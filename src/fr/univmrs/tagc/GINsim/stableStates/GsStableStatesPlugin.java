package fr.univmrs.tagc.GINsim.stableStates;

import javax.swing.JFrame;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * register the stableStates plugin: look for stable states without running a simulation
 */
public class GsStableStatesPlugin implements GsPlugin, GsActionProvider {

	private GsPluggableActionDescriptor[] t_action = null;
    
    public void registerPlugin() {
        GsRegulatoryGraphDescriptor.registerActionProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_stableStates", "STR_stableStates_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
    }

    public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
    	
    	if (!(graph instanceof GsRegulatoryGraph) || graph.getNodeOrder().size() < 1) {
            graph.addNotificationMessage(new GsGraphNotificationMessage(graph, 
            		Translator.getString(graph instanceof GsRegulatoryGraph ? "STR_emptyGraph" : "STR_notRegGraph"), 
            		GsGraphNotificationMessage.NOTIFICATION_WARNING));
    		return;
    	}
    	GsStableStateUI ui = new GsStableStateUI((GsRegulatoryGraph)graph);
    	ui.setVisible(true);
    }
}
