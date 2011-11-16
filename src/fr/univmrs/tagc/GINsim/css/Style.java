package fr.univmrs.tagc.GINsim.css;

import org.ginsim.graph.common.AttributesReader;

/**
 * Style are dedicated to store some graphical attributes of graphical elements like node and edges
 */
public interface Style {
	/**
	 * Merge the defined attributes from the style s
	 * @param s
	 */
	public abstract void merge(Style s);
	
	/**
	 * Apply the style to an element using its GsAttributesReader
	 * @param areader
	 */
	public abstract void apply(AttributesReader areader);
	
	public abstract Object clone();
}
