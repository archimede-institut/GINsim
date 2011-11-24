package org.ginsim.core.notification;

import org.ginsim.graph.common.Graph;

public class InformationNotification extends Notification {


	public InformationNotification( String message) {
		
		super( message, Notification.NOTIFICATION_INFO);
	}
}
