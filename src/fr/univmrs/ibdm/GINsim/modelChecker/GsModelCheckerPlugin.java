package fr.univmrs.ibdm.GINsim.modelChecker;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationAction;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.plugin.GsPlugin;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;

/**
 * main method for the reg2dyn plugin
 * 
 * @author aurelien
 */
public class GsModelCheckerPlugin implements GsPlugin, GsActionProvider {

    private GsPluggableActionDescriptor[] t_action = null;
    protected static Vector v_checker = new Vector();
    protected static Vector v_unavailable_checker = new Vector();

    static {
        // define supported model checkers here
        GsModelCheckerDescr checker = new GsNuSMVCheckerDescr();
        if (checker.isAvailable()) {
            v_checker.add(checker);
        } else {
            v_unavailable_checker.add(checker);
        }
    }
    
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
            t_action[0] = new GsPluggableActionDescriptor("STR_modelChecker",
                    "STR_modelChecker_descr", null, this, ACTION_ACTION, 0);
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
            if (v_checker.size() < 1) {
                GsGraphNotificationAction gaction = new GsGraphNotificationAction() {
                
                    public boolean timeout(GsGraph graph, Object data) {
                        return true;
                    }
                
                    public boolean perform(GsGraph graph, Object data, int index) {
                        GsModelCheckerPlugin.showUnavailableChecker((JFrame)data);
                        return true;
                    }
                
                    public String[] getActionName() {
                        String[] t = new String[1];
                        t[0] = Translator.getString("STR_details");
                        return t;
                    }
                
                };
                graph.addNotificationMessage(new GsGraphNotificationMessage(graph, Translator.getString("STR_no_model_checker"), gaction, frame, GsGraphNotificationMessage.NOTIFICATION_WARNING));
                return;
            }
            GsModelCheckerUI ui = new GsModelCheckerUI((GsRegulatoryGraph)graph);
            JOptionPane.showMessageDialog(null, ui);
		}
	}

    protected static void showUnavailableChecker(JFrame frame) {
        StringBuffer s = new StringBuffer();
        for (int i=0 ; i<v_unavailable_checker.size() ; i++) {
            GsModelCheckerDescr checker = (GsModelCheckerDescr)v_unavailable_checker.get(i);
            s.append(checker.getName());
            s.append(": ");
            s.append(checker.getNonAvailableInfo());
            s.append("\n");
        }
        JOptionPane.showMessageDialog(frame, s, Translator.getString("STR_unavailable_checkers"), JOptionPane.INFORMATION_MESSAGE);
    }
}
