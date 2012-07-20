package org.ginsim.core.notification;

public class ErrorNotification extends Notification {

	public ErrorNotification( Object topic, String message) {
		
		super( topic, message, Notification.NOTIFICATION_ERROR);
	}

}
