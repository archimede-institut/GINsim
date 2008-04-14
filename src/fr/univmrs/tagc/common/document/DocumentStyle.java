package fr.univmrs.tagc.common.document;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>DocumentStyle contain a generic representation of the style for a document<br>
 * A DocumentStyle is a map of styles identified by their name (like the class in HTML), followed by a map of couples (property, value) and the function to manipulate them.</p>
 * 
 * <p>DocumentStyle contains a set of constants representing the minimal set of styles a DocumentWriter should support.</p>
 * 
 * @see DocumentWriter
 */
public class DocumentStyle {
	
	public static final String LIST_TYPE = "list-type";
	public static final String COLOR = "color";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String FONT_SIZE = "font-size";
	public static final String TABLE_BORDER = "table-border";
	
	private Map styles;
	private String curStyle;
	
	public DocumentStyle() {
		styles = new Hashtable();
	}

	/**
	 * Create a new style identified by value.<br>
	 * The current style is set to it.
	 * @param value the style's class (HTML sense)
	 */
	public void addStyle(String value) {
		String style = value;
		styles.put(style, new Hashtable());
		curStyle = style;
	}
	
	/**
	 * Add a property with a value to the current style
	 * @param property
	 * @param value
	 */
	public void addProperty(String property, Object value) {
		((Map)styles.get(curStyle)).put(property, value);
	}
	
	/**
	 * Add a property with a value to the style
	 * @param style
	 * @param property
	 * @param value
	 */
	public void addProperty(String style, String property, Object value) {
		((Map)styles.get(style)).put(property, value);
	}
	
	/**
	 * Add all the properties to the style
	 * @param properties the array, must looks like [property, value, property, value, ...]
	 */
	public void addProperties(Object[] properties) throws ArrayIndexOutOfBoundsException {
		Map style = (Map)styles.get(curStyle);
		if (properties.length%2 == 1) {
			throw new ArrayIndexOutOfBoundsException();
		}
    	for (int i=0 ; i< properties.length ; i+=2) {
    		style.put(properties[i].toString(), properties[i+1]);
    	}
	}
	
	/**
	 * Return the current style
	 * @return the current style
	 */
	public String getCurrentStyle() {
		return curStyle;
	}
	
	/**
	 * Return an iterator over the styles identifiers.
	 * @return an iterator over the styles identifiers.
	 */
	public Iterator getStyleIterator() {
		return styles.keySet().iterator();
	}
	
	/**
	 * Return all the properties for a style
	 * @param style the style's identifier
	 * @return the properties
	 */
	public Map getPropertiesForStyle(String style) {
		return (Map)styles.get(style);
	}
	
	/**
	 * Return an iterator over the properties for a style
	 * @param style the style's identifier
	 * @return an iterator over the properties
	 */
	public Iterator getPropertiesIteratorForStyle(String style) {
		return ((Map)styles.get(style)).keySet().iterator();
	}
}
