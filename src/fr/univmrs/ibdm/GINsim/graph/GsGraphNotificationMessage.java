package fr.univmrs.ibdm.GINsim.graph;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.global.GsTimeout;
import fr.univmrs.ibdm.GINsim.global.GsTimeoutObject;

/**
 * A notification message
 *
 */
public class GsGraphNotificationMessage implements GsTimeoutObject {

	private String message;
	private GsGraph graph;
    protected GsGraphNotificationAction action;
    private Object data;
    private short type;

    /** info, disappear quickly (7s) */
    public static final short NOTIFICATION_INFO = 0;
    /** info, stay longer (12s) */
    public static final short NOTIFICATION_INFO_LONG = 1;
    /** warning, disappear quickly (10s) */
    public static final short NOTIFICATION_WARNING = 2;
    /** warning, disappear quickly (15s) */
    public static final short NOTIFICATION_WARNING_LONG = 3;
    /** error, disappear quickly (20s) */
    public static final short NOTIFICATION_ERROR = 4;
    /** error, disappear quickly (30s) */
    public static final short NOTIFICATION_ERROR_LONG = 5;
    
    
    /**
     * 
     * @param graph
     * @param message
     * @param type
     */
    public GsGraphNotificationMessage (GsGraph graph, String message, short type) {
        this(graph, message, null, null, type);
    }

        
	/**
	 * @param graph
	 * @param message
	 * @param action 
     * @param data
	 * @param type seconds after which the message will vanish (never = 0)
	 */
	public GsGraphNotificationMessage (GsGraph graph, String message, GsGraphNotificationAction action, Object data, short type) {
		this.graph = graph;
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
            GsTimeout.addTimeout(this, timeout*1000);
        }
	}

    /**
     * 
     * @param graph
     * @param e
     */
    public GsGraphNotificationMessage(GsGraph graph, GsException e) {
        // TODO: show detail for exception
        this(graph, e.getMessage(), (e.getGravity() == GsException.GRAVITY_ERROR) ? NOTIFICATION_ERROR_LONG : NOTIFICATION_WARNING_LONG);
    }


    public void timeout() {
        if (action != null) {
            if (!action.timeout(graph, data)) {
                return;
            }
        }
        graph.deleteNotificationMessage(this);
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
            graph.deleteNotificationMessage(this);
			return;
		}
        if (action.perform(graph, data, index)) {
            graph.deleteNotificationMessage(this);
        }
	}
    
    /**
     * @return the type of this notification.
     */
    public short getType() {
        return type;
    }
    
//	/**
//	 * @param message
//	 * @return true if <code>message</code> is of the same class of notification message
//	 */
//	public boolean sameClass(GsGraphNotificationMessage message) {
//		return message != null && message.familly == familly;
//	}


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
