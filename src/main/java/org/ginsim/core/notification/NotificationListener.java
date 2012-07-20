package org.ginsim.core.notification;



public interface NotificationListener {

	/**
	 * Receive a notification from the NotificationManager and add it from the notification list
	 * 
	 * @param message the received Notification
	 */
	public void receiveNotification( Notification message );
	
	
	/**
	 * Receive a notification from the NotificationManager and remove it from the notification list
	 * 
	 * @param message the received Notification
	 */
	public void deleteNotification( Notification message );
}
