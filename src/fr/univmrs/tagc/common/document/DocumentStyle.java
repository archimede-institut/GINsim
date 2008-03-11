package fr.univmrs.tagc.common.document;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class DocumentStyle {
	
	public static final String COLOR = "color";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String FONT_SIZE = "font-size";
	
	private Map styles;
	public String curStyle;
	
	public DocumentStyle() {
		styles = new Hashtable();
	}

	public void addStyle(String value) {
		String style = value;
		styles.put(style, new Hashtable());
		curStyle = style;
	}
	
	public void addProperty(String name, Object value) {
		((Map)styles.get(curStyle)).put(name, value);
	}
	public void addProperties(Object[] properties) throws ArrayIndexOutOfBoundsException {
		Map style = (Map)styles.get(curStyle);
		if (properties.length%2 == 1) {
			throw new ArrayIndexOutOfBoundsException();
		}
    	for (int i=0 ; i< properties.length ; i+=2) {
    		style.put(properties[i].toString(), properties[i+1]);
    	}
	}
	
	public String getCurrentStyle() {
		return curStyle;
	}
	
	public Iterator getStyleIterator() {
		return styles.keySet().iterator();
	}
	public Map getPropertiesForStyle(String style) {
		return (Map)styles.get(style);
	}
	public Iterator getPropertiesIteratorForStyle(String style) {
		return ((Map)styles.get(style)).keySet().iterator();
	}
}
