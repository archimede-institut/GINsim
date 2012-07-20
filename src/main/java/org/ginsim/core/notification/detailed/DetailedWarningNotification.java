package org.ginsim.core.notification.detailed;

import org.ginsim.core.notification.WarningNotification;

public class DetailedWarningNotification extends WarningNotification implements DetailedNotification{

	private String details;
	
	public DetailedWarningNotification( Object topic, String message, String details) {
	
		super( topic, message);
		this.details = details;
	}
	
	/**
	 * Returns the notification details
	 * 
	 * @return the notification details
	 */
	public String getDetails() {
		
		return details;
	}
}
