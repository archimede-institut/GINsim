package fr.univmrs.tagc.GINsim.graph;

import java.util.List;

import javax.swing.JOptionPane;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * Actions associated with the eventCascade notification message
 */
public class GsGraphEventCascadeNotificationAction implements GsGraphNotificationAction {

    public String[] getActionName() {
        String[] t_action = { Translator.getString("STR_details") };
        return t_action;
    }

    public boolean perform( Graph graph, Object data, int index) {
        List v = (List)data;
        StringBuffer s = new StringBuffer();
        for (int i=0 ; i<v.size() ; i++) {
        	s.append(v.get(i).toString());
        }
        JOptionPane.showMessageDialog(graph.getGraphManager().getMainFrame(), s, Translator.getString("STR_details"), JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public boolean timeout( Graph graph, Object data) {
        return false;
    }

}
