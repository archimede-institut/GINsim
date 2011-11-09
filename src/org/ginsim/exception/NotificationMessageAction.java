package org.ginsim.exception;

/**
 * attach an action to a notification
 */
public interface NotificationMessageAction {

    /**
     * run the action
     * @param holder 
     * @param data
     * @param index 
     * @return true if the notification should be removed
     */
    public boolean perform( NotificationMessageHolder holder, Object data, int index);

    /**
     * get the name of the action, to display on the action button
     * @return the name of the action
     */
    public String[] getActionName();

    /**
     * for timeoutable notification, what should be done when the time is elapsed.
     * @param holder 
     * @param data 
     * 
     * @return true if the notification should be removed
     */
    public boolean timeout( NotificationMessageHolder holder, Object data);
}
