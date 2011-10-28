package org.ginsim.gui.notifications;

import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;

/**
 * 
 * @author Aurelien Naldi
 *
 */
public interface NotificationSource {
	public GsGraphNotificationMessage getTopNotification();
	public void closeNotification();

}
