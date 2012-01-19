package org.ginsim.common.xml;

import org.xml.sax.SAXParseException;

public class ParsingWarning {

	private final String message;
	private final int firstLine;
	private int extraCount = 0;
	
	public ParsingWarning(SAXParseException e) {
		this.message = e.getMessage();
		this.firstLine = e.getLineNumber();
	}

	public boolean merge(SAXParseException e) {
		String m = e.getMessage();
		if (message.equals(e.getMessage())) {
			extraCount++;
			return true;
		}
		return false;
	}

}
