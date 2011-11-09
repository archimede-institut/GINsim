package org.ginsim.exception;

import fr.univmrs.tagc.common.Timeout;
import fr.univmrs.tagc.common.TimeoutObject;

/**
 * A notification message
 *
 */
public class NotificationMessage extends GsException implements TimeoutObject {

	private String message;
	private NotificationMessageHolder holder;
    protected NotificationMessageAction action;
    private Object data;
    private byte type;

    // TODO: replace them with GsException gravities
    
    /** info, disappear quickly (7s) */
    public static final byte NOTIFICATION_INFO = 0;
    /** info, stay longer (12s) */
    public static final byte NOTIFICATION_INFO_LONG = 1;
    /** warning, disappear quickly (10s) */
    public static final byte NOTIFICATION_WARNING = 2;
    /** warning, disappear quickly (15s) */
    public static final byte NOTIFICATION_WARNING_LONG = 3;
    /** error, disappear quickly (20s) */
    public static final byte NOTIFICATION_ERROR = 4;
    /** error, disappear quickly (30s) */
    public static final byte NOTIFICATION_ERROR_LONG = 5;
    
    
    /**
     * 
     * @param graph
     * @param message
     * @param type
     */
    public NotificationMessage ( NotificationMessageHolder holder, String message, byte type) {
    	
        this( holder, message, null, null, type);
    }

    /**
     * 
     * @param graph
     * @param e
     */
    public NotificationMessage( NotificationMessageHolder holder, GsException e) {
        // TODO: show detail for exception
        this(holder, e.getMessage(), (e.getGravity() == GsException.GRAVITY_ERROR) ? NOTIFICATION_ERROR_LONG : NOTIFICATION_WARNING_LONG);
    }

	/**
	 * @param graph
	 * @param message
	 * @param action 
     * @param data
	 * @param type seconds after which the message will vanish (never = 0)
	 */
	public NotificationMessage ( NotificationMessageHolder holder, String message, NotificationMessageAction action, Object data, byte type) {
		super(type/2, message);
		this.holder = holder;
		this.message = message;
        this.action = action;
        this.data = data;
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
            Timeout.addTimeout(this, timeout*1000);
        }
        holder.addNotificationMessage(this);
	}

    public void timeout() {
        if (action != null) {
            if (!action.timeout(holder, data)) {
                return;
            }
        }
        holder.deleteNotificationMessage(this);
    }
	
	public String toString() {
		return message;
	}
	
	/**
	 * @return true if this notification has an attached action
	 */
	public boolean hasAction() {
		return action != null;
	}
	
	/**
	 * @param index 
	 */
	public void performAction(int index) {
		if (action == null) {
            holder.deleteNotificationMessage(this);
			return;
		}
        if (action.perform(holder, data, index)) {
            holder.deleteNotificationMessage(this);
        }
	}
    
    /**
     * @return the type of this notification.
     */
    public byte getType() {
        return type;
    }
    
	/**
	 * @return the text to show on the action button.
	 */
	public String[] getActionText() {
        if (action == null) {
            return null;
        }
        return action.getActionName();
	}
}
