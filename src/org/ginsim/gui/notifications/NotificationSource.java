package org.ginsim.gui.notifications;

import org.ginsim.core.notification.Notification;

/**
 * 
 * @author Aurelien Naldi
 *
 */
public interface NotificationSource {
	public Notification getTopNotification();
	public void closeNotification();

}
