package fr.univmrs.tagc.GINsim.circuit;

import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.common.managerresources.Translator;

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

    public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
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

    public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_ACTION) {
            return;
        }
        if (graph.getNodeOrderSize() < 1) {
            new NotificationMessage((NotificationMessageHolder)graph, Translator.getString("STR_emptyGraph"), NotificationMessage.NOTIFICATION_WARNING);
            return;
        }
		if (ref == 0) {
            new GsCircuitFrame( frame, graph);
		}
	}
}
