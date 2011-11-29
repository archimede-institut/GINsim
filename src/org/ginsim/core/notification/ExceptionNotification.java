package org.ginsim.core.notification;

import org.ginsim.graph.common.Graph;

public class ExceptionNotification extends ErrorNotification{

	private Exception exception;
	
	public ExceptionNotification( Graph graph, String message, Exception exception) {
		
		super( graph, message);
		this.exception = exception;
	}
	
	public Exception getException(){
		
		return exception;
	}
	
	
}
