package org.ginsim.gui.notifications;

import org.ginsim.exception.NotificationMessage;

/**
 * 
 * @author Aurelien Naldi
 *
 */
public interface NotificationSource {
	public NotificationMessage getTopNotification();
	public void closeNotification();

}
