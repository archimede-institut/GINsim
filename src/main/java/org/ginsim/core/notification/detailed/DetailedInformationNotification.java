package org.ginsim.core.notification.detailed;

import org.ginsim.core.notification.InformationNotification;

public class DetailedInformationNotification extends InformationNotification implements DetailedNotification{

	private String details;
	
	public DetailedInformationNotification( Object topic, String message, String details) {
	
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
