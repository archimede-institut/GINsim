package org.ginsim.common.xml;

/**
 * Define how to handle a given tag while parsing an XML document with the XMLHelper.
 *
 * @author Aurelien Naldi
 */
public class CallDescription {
	final int id;
	final int constraint;
	final CallMode mode;
	CallDescription other = null;
	
	/**
	 * Describes how to deal with a tag when parsing a XML file
	 * 
	 * @param id will be used when forwarding the call to startElement and endElement
	 * @param constraint when to match this call. Not yet implemented
	 * @param mode determines whether forwarding is done at the start or end of the element and the content should be read.
	 */
	public CallDescription(int id, int constraint, CallMode mode) {
		this.id = id;
		this.constraint = constraint;
		this.mode = mode;
	}

}
