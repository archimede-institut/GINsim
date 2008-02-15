package fr.univmrs.tagc.common;

import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * When something bad happen, a GsException must be raised.
 * it will be used by the app to display an error message.
 */
public class GsException extends Exception {

    private static final long serialVersionUID = 7755661204275237367L;
    /** low gravity */
	public static final int GRAVITY_INFO = 0;
	/** normal error */
    public static final int GRAVITY_NORMAL = 1;
    /** really bad */
    public static final int GRAVITY_ERROR = 2;
    
    private int gravity;
    private String message;
    private Exception e = null;
    
    /**
     * create a GsException.
     * @param gravity the gravity level of the exception
     * @param message the error message
     */
    public GsException (int gravity, String message) {
        this.gravity = gravity;
        this.message = message;
    }
    /**
     * create a GsException.
     * @param gravity the gravity level of the exception
     * @param e the original exception
     */
    public GsException (int gravity, Exception e) {
        this.gravity = gravity;
        this.e = e;
    }
    /**
     * get the gravity of the error, this should be one of
     * GRAVITY_INFO, GRAVITY_NORMAL or GRAVITY_ERROR.
     * 
     * @return the gravity
     */
    public int getGravity() {
        return gravity;
    }
    /**
     * get the message associated with the exception.
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        if (e != null) {
            return e.getLocalizedMessage();
        }
        return message;
    }
    /**
     * get a title for the error window.
     * @return a title (for the error message)
     */
    public String getTitle() {
        return Translator.getString("STR_error_occured");
    }
}
