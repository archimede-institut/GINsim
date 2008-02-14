package fr.univmrs.ibdm.GINsim.graph;

import java.util.Vector;

import javax.swing.JOptionPane;

import fr.univmrs.tagc.manageressources.Translator;

/**
 * Actions associated with the eventCascade notification message
 */
public class GsGraphEventCascadeNotificationAction implements GsGraphNotificationAction {

    public String[] getActionName() {
        String[] t_action = { Translator.getString("STR_details") };
        return t_action;
    }

    public boolean perform(GsGraph graph, Object data, int index) {
        Vector v = (Vector)data;
        StringBuffer s = new StringBuffer();
        for (int i=0 ; i<v.size() ; i++) {
        	s.append(v.get(i).toString());
        }
        JOptionPane.showMessageDialog(graph.getGraphManager().getMainFrame(), s, Translator.getString("STR_details"), JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public boolean timeout(GsGraph graph, Object data) {
        return false;
    }

}
