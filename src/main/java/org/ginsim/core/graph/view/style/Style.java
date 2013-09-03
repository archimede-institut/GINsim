package org.ginsim.core.graph.view.style;


/**
 * Define common methods for styles.
 * 
 * @author Aurelien Naldi
 */
public interface Style {

	/**
	 * Retrieve all properties used by this style.
	 * The default set of properties matches visual properties,
	 * but each style can use a different set of properties.
	 * For example, the default style for edges in a regulatory graph
	 * has several properties for edge colors: positive, negative, dual.
	 * 
	 * @return the list of properties used by this style.
	 */
	StyleProperty[] getProperties();
	
	/**
	 * Retrieve the value of a property.
	 * This handles internal properties directly and should not be used to
	 * retrieve the resulting visual settings (use dedicated methods for that).
	 * This should be used to save properties, or for the style edition panel. 
	 *  
	 * @param prop the property to retrieve
	 * @return the value of the property, which can be null.
	 */
	Object getProperty(StyleProperty prop);
	
	/**
	 * Assign a value to a property.
	 * This handles internal properties directly. It should be used
	 * to restore them when loading a graph or for the style edition panel. 
	 * 
	 * @param prop the property to set
	 * @param value the assigned value (can be null)
	 */
	void setProperty(StyleProperty prop, Object value);

	String getName();

	void setName(String name);
	
	Object getParentProperty(StyleProperty property);

}
