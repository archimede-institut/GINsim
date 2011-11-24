package fr.univmrs.tagc.GINsim.graph;

import java.util.List;

import javax.swing.JOptionPane;




import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * Actions associated with the eventCascade notification message
 */
public class GraphEventCascadeNotificationAction implements NotificationMessageAction {

    public String[] getActionName() {
        String[] t_action = { Translator.getString("STR_details") };
        return t_action;
    }

    public boolean perform( NotificationMessageHolder graph, Object data, int index) {
        List v = (List)data;
        StringBuffer s = new StringBuffer();
        for (int i=0 ; i<v.size() ; i++) {
        	s.append(v.get(i).toString());
        }
        // TODO: move to the new service API and set the right parent frame
        JOptionPane.showMessageDialog(null, s, Translator.getString("STR_details"), JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public boolean timeout( NotificationMessageHolder graph, Object data) {
        return false;
    }

}
