package org.ginsim.core.notification;

import org.ginsim.graph.common.Graph;

public class ErrorNotification extends Notification {

	public ErrorNotification( String message) {
		
		super( message, Notification.NOTIFICATION_ERROR);
	}
}
