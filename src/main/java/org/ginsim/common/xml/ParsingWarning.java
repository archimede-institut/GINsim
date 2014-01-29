package org.ginsim.common.xml;

import org.xml.sax.SAXParseException;

/**
 * Describe a warning found while parsing an XML file.
 *
 * @author Aurelien Naldi
 */
public class ParsingWarning {

	private final String message;
	private final int firstLine;
	private int extraCount = 0;
	
	public ParsingWarning(SAXParseException e) {
		this.message = e.getMessage();
		this.firstLine = e.getLineNumber();
	}

	public boolean merge(SAXParseException e) {
		if (message.equals(e.getMessage())) {
			extraCount++;
			return true;
		}
		return false;
	}

	public String getMessage() {
		return message;
	}
	
	public int getFirstLine() {
		return firstLine;
	}
	
	public int getExtraCount() {
		return extraCount;
	}
}
