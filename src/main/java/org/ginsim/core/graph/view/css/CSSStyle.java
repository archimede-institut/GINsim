package org.ginsim.core.graph.view.css;

import org.ginsim.core.graph.view.AttributesReader;

/**
 * Style are dedicated to store some graphical attributes of graphical elements like node and edges
 */
public interface CSSStyle {
	/**
	 * Merge the defined attributes from the style s
	 * @param s
	 */
	public abstract void merge(CSSStyle s);
	
	/**
	 * Apply the style to an element using its GsAttributesReader
	 * @param areader
	 */
	public abstract void apply(AttributesReader areader);
	
	public abstract Object clone();

	/**
	 * Define the value of a property of a style
	 * @param property
	 * @param value
	 * @param i the index in the parsed text
	 */
	public abstract void setProperty(String property, String value, int i) throws CSSSyntaxException;
}
