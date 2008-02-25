package fr.univmrs.tagc.GINsim.circuit;

import javax.swing.JFrame;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * main method for the circuit plugin
 * 
 * @author aurelien
 */
public class GsCircuitPlugin implements GsPlugin, GsActionProvider {

    private GsPluggableActionDescriptor[] t_action = null;

    public void registerPlugin() {
        GsRegulatoryGraphDescriptor.registerActionProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType,
            GsGraph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
        if (t_action == null) {
            t_action = new GsPluggableActionDescriptor[1];
            t_action[0] = new GsPluggableActionDescriptor("STR_circuit",
                    "STR_circuit_descr", null, this, ACTION_ACTION, 0);
        }
        return t_action;
    }

    public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_ACTION) {
            return;
        }
        if (graph.getNodeOrder().size() < 1) {
            graph.addNotificationMessage(new GsGraphNotificationMessage(graph, Translator.getString("STR_emptyGraph"), GsGraphNotificationMessage.NOTIFICATION_WARNING));
            return;
        }
		if (ref == 0) {
            new GsCircuitFrame(frame, graph);
		}
	}
}
