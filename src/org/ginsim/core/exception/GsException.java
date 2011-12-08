package org.ginsim.core.exception;

import java.util.Vector;

import org.ginsim.gui.resource.Translator;

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
    private Vector<String> message;
    private Exception exception;
    
    /**
     * create a GsException.
     * @param gravity the gravity level of the exception
     * @param message the error message
     */
    public GsException (int gravity, String message) {
    	
        this.gravity = gravity;
        this.message = new Vector<String>();
        this.message.add( message);
        this.exception = null;
    }
    
    public GsException( String message, Exception excep){
    	
    	this.gravity = GRAVITY_ERROR;
    	this.message = new Vector<String>();
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
        this.message = new Vector<String>();
        this.exception = e;
    }
    
    public GsException( int gravity, String[] messages){
    	
        this.gravity = gravity;
        this.message = new Vector<String>();
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
    			result += Translator.getString( text) + ":";
    		}
    		result += "\n";
    	}
        if (exception != null) {
        	result += "Exception is : " + Translator.getString(exception.getLocalizedMessage());
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
    	
        return Translator.getString("STR_error_occured");
    }
    
	public void addMessage(String message) {
		
		this.message.add( message);
	}
}
