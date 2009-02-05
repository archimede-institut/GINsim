package fr.univmrs.tagc.GINsim.css;

public class GsCSSSyntaxException extends Exception {
	private static final long serialVersionUID = -178881160709719504L;
	private static String defaultMessage = "Error in the css syntax"; 

	public GsCSSSyntaxException() {
		super(defaultMessage);
	}

	public GsCSSSyntaxException(String message) {
		super(defaultMessage+" : "+message);
	}

	public GsCSSSyntaxException(Throwable cause) {
		super(defaultMessage, cause);
	}

	public GsCSSSyntaxException(String message, Throwable cause) {
		super(defaultMessage+" : "+message, cause);
	}

}
