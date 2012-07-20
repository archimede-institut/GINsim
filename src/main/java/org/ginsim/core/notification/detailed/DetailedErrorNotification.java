package org.ginsim.core.notification.detailed;

import org.ginsim.core.notification.ErrorNotification;

public class DetailedErrorNotification extends ErrorNotification implements DetailedNotification{

	private String details;
	
	public DetailedErrorNotification( Object topic, String message, String details) {
	
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
