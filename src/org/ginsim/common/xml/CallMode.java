package org.ginsim.common.xml;

public enum CallMode {
	/**
	 * No call will be made, probably useless: why would we create a callDescription for it?
	 */
	NOCALL(false, false, false),
	/**
	 * Only call at the start of the element.
	 */
	STARTONLY(true, false, false),
	/**
	 * Only call at the end of the element.
	 */
	ENDONLY(false, true, false),
	/**
	 * Only call at the end of the element and read the content.
	 */
	ENDONLYREAD(false, true, true),
	/**
	 * Call both at the start and end of the element.
	 */
	BOTH(true, true, false),
	/**
	 * Call both at the start and end of the element and read the content.
	 */
	BOTHREAD(true, true, true);
	
	public final boolean atStart, atEnd, readContent;
	
	private CallMode(boolean atStart, boolean atEnd, boolean readContent) {
		this.atStart = atStart;
		this.atEnd = atEnd;
		this.readContent = readContent;
	}
}
