package org.ginsim.core.notification;

public class WarningNotification extends Notification {

	public WarningNotification( Object topic, String message) {
		
		super( topic, message, Notification.NOTIFICATION_WARNING);
	}

}
