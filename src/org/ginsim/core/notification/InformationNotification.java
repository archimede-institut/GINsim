package org.ginsim.core.notification;


public class InformationNotification extends Notification {


	public InformationNotification( Object topic, String message) {
		
		super( topic, message, Notification.NOTIFICATION_INFO);
	}
	

}
