package org.ginsim.core.notification;

import java.awt.Color;

public enum NotificationType {

	INFO(3, Color.CYAN),
	LONGINFO(5, Color.CYAN),

	WARNING(10, Color.ORANGE),
	LONGWARNING(15, Color.ORANGE),

	ERROR(20, Color.RED),
	LONGERROR(-1, Color.RED);

	public final int timeout;
	public final Color color;
	
	private NotificationType(int timeout, Color color) {
		this.timeout = timeout;
		this.color = color;
	};
}
