package org.ginsim.core.graph.view.css;

public class CSSSyntaxException extends Exception {
	private static final long serialVersionUID = -178881160709719504L;
	private static String defaultMessage = "Error in the css syntax"; 

	public CSSSyntaxException() {
		super(defaultMessage);
	}

	public CSSSyntaxException(String message) {
		super(defaultMessage+" : "+message);
	}

	public CSSSyntaxException(String message, String text, int i) {
		super(defaultMessage+" : "+message+" "+i+" ["+text.substring(Math.max(0, i-2), Math.min(i+2, text.length()))+"]");
	}

	public CSSSyntaxException(Throwable cause) {
		super(defaultMessage, cause);
	}

	public CSSSyntaxException(String message, Throwable cause) {
		super(defaultMessage+" : "+message, cause);
	}

}
