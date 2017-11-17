package org.ginsim.common.application;

import java.util.ArrayList;
import java.util.List;

/**
 * When something bad happen, a GsException must be raised.
 * it will be used by the GUI to display an error message.
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
    private List<String> message;
    private Exception exception;
    
    /**
     * create a GsException.
     * @param gravity the gravity level of the exception
     * @param message the error message
     */
    public GsException (int gravity, String message) {
    	
        this.gravity = gravity;
        this.message = new ArrayList<String>();
        this.message.add( message);
        this.exception = null;
    }
    
    public GsException( String message, Exception excep){
    	
    	this.gravity = GRAVITY_ERROR;
    	this.message = new ArrayList<String>();
    	this.message.add( message);
    	this.exception = excep;
    }
    
    /**
     * create a GsException.
     * @param gravity the gravity level of the exception
     * @param e the original exception
     */
    public GsException (int gravity, Exception e) {
        this.gravity = gravity;
        this.message = new ArrayList<String>();
        this.exception = e;
    }
    
    public GsException( int gravity, String[] messages){
    	
        this.gravity = gravity;
        this.message = new ArrayList<String>();
        for( String text : messages){
        	this.message.add( text);
        }
        this.exception = null;
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
    	
    	String result = "";
    	
    	if( message != null){
    		for( String text : message){
    			result += Txt.t(text) + ":";
    		}
    		result += "\n";
    	}
        if (exception != null) {
        	String m = exception.getLocalizedMessage();
        	if (m == null) {
        		result += "Exception is : " + m;
        	} else {
        		result += "Exception is : " + Txt.t(m);
        	}
        }
        else{
        	// Remove the last ":" from the string
        	result = result.substring(0, result.length()-1);
        }
        
        return result;
    }
    /**
     * get a title for the error window.
     * @return a title (for the error message)
     */
    public String getTitle() {
    	
        return Txt.t("STR_error");
    }
    
	public void addMessage(String message) {
		
		this.message.add( message);
	}
}
