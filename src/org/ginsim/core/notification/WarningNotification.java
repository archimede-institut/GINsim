package org.ginsim.core.notification;

import org.ginsim.graph.common.Graph;

public class WarningNotification extends Notification {

	public WarningNotification( String message) {
		
		super( message, Notification.NOTIFICATION_WARNING);
	}
}
