package org.ginsim.core.notification;

public class ExceptionNotification extends ErrorNotification{

	private Exception exception;
	
	public ExceptionNotification( Object topic, String message, Exception exception) {
		
		super( topic, message);
		this.exception = exception;
	}
	
	public Exception getException(){
		
		return exception;
	}
	
	
}
