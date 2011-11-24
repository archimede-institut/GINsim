package org.ginsim.core.notification;

import fr.univmrs.tagc.common.Timeout;
import fr.univmrs.tagc.common.TimeoutObject;

/**
 * A notification message
 *
 */
public class Notification implements TimeoutObject {

	private String message;
    private byte type;
    
    /** info, disappear quickly (7s) */
    public static final byte NOTIFICATION_INFO = 0;
    /** info, stay slowly (12s) */
    public static final byte NOTIFICATION_INFO_LONG = 1;
    /** warning, disappear quickly (10s) */
    public static final byte NOTIFICATION_WARNING = 2;
    /** warning, disappear slowly (15s) */
    public static final byte NOTIFICATION_WARNING_LONG = 3;
    /** error, disappear quickly (20s) */
    public static final byte NOTIFICATION_ERROR = 4;
    /** error, disappear slowly (30s) */
    public static final byte NOTIFICATION_ERROR_LONG = 5;
    
    
    /**
     * 
     * @param graph
     * @param message
     * @param type
     */
    public Notification ( String message, byte type) {
    	
		this.message = message;
        this.type = type;
        
        int timeout = 0;
        switch (type) {
            case NOTIFICATION_INFO:
                timeout = 7;
                break;
            case NOTIFICATION_INFO_LONG:
                timeout = 12;
                break;
            case NOTIFICATION_WARNING:
                timeout = 10;
                break;
            case NOTIFICATION_WARNING_LONG:
                timeout = 15;
                break;
            case NOTIFICATION_ERROR:
                timeout = 20;
                break;
            case NOTIFICATION_ERROR_LONG:
                timeout = 30;
                break;
            default:
                this.type = NOTIFICATION_ERROR;
        }
        
        if (timeout > 0) {
            Timeout.addTimeout( this, timeout*1000);
        }
        
        NotificationManager.getInstance().publish( this);
    }

    /**
     * 
     * 
     */
    public void timeout() {

    	NotificationManager.getInstance().publishDeletion( this);
    }

	public String toString() {
		return message;
	}
    
    /**
     * @return the type of this notification.
     */
    public byte getType() {
        return type;
    }
   
}
