package fr.univmrs.tagc.GINsim.graph;
/**
 * attach an action to a notification
 */
public interface GsGraphNotificationAction {

    /**
     * run the action
     * @param graph 
     * @param data
     * @param index 
     * @return true if the notification should be removed
     */
    public boolean perform(GsGraph graph, Object data, int index);

    /**
     * get the name of the action, to display on the action button
     * @return the name of the action
     */
    public String[] getActionName();

    /**
     * for timeoutable notification, what should be done when the time is elapsed.
     * @param graph 
     * @param data 
     * 
     * @return true if the notification should be removed
     */
    public boolean timeout(GsGraph graph, Object data);
}
