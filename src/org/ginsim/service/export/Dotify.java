package org.ginsim.service.export;

/**
 * Indicates a class can be dumped into dot (graphviz language)
 * 
 * Thanks to Aurelien for his brilliant idea for the name of this interface
 *
 */
public interface Dotify {
	
	/**
	 * Return a dot representation of the object as a String
	 * @return a String
	 */
	public String toDot();
	
	/**
	 * Return a dot representation of the edge as a String
	 * The object is the source of the edge, the first parameter is the target.
	 * @return a String
	 */
	public String toDot(Object to);
}
