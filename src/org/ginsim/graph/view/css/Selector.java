package org.ginsim.graph.view.css;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.dynamicgraph.DynamicNode;
import org.ginsim.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.view.AttributesReader;


/**
 * A Selector implementation for GINsim. It intend to be have the same usage than the HTML's CSS one.
 * 
 *
 */
public abstract class Selector {
	static protected Map selectors = new HashMap();
	protected String identifier;
	private Map m;
	
	protected Selector(String identifier) {
		this.identifier = identifier;
		m = new HashMap();
		resetDefaultStyle();
	}
	
	/**
	 * Register your selector to enable parsing it.
	 * @param identifier the identifier of the selector
	 * @param cla the selector's class
	 */
	public static void registerSelector(String identifier, Class cla) {
		selectors.put(identifier, cla);
	}

	/**
	 * return a new selector corresponding to an identifier
	 * @param id the identifier
	 * @return a new selector with identifier id or null if the class doesn't exists or can't be instantiated.
	 */
	public static Selector getNewSelector(String identifier) {
		try {
			return (Selector)((Class)selectors.get(identifier)).newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return the identifier for this selector
	 * @return identifier
	 */
	public String getIdentifer() {
		return identifier;
	}

	/**
	 * A collection of all the categories this selector respond to.
	 * @return the categories id
	 */
	public Collection categories() {
		return m.values();
	}

	/**
	 * Get the style for a category id or null if the category doesn't exist. 
	 * @param category the id of the category
	 * @return the default style
	 */
	public Style getStyle(String category) {
		if (!m.containsKey(category)) {
			return missingCategory(category);
		}
		return (Style) m.get(category);
	}
	
	/**
	 * Get the style for an object using getCategory to determine its category  or null if the category doesn't exist or has no category
	 * 
	 * If possible call getStyleForNode or getStyleForEdge, when you know the type of object
	 * 
	 * @param obj
	 * @return the default style for this object
	 */	
	public Style getStyle(Object obj) {
		return getStyle(getCategory(obj));
	}
	
	/**
	 * Get the style for a node using getCategory to determine its category  or null if the category doesn't exist or has no category
	 * @param obj a node
	 * @return the default style for this object
	 */	
	public Style getStyleForNode(Object obj) {
		return getStyle(getCategoryForNode(obj));
	}
	
	/**
	 * Get the style for an edge using getCategory to determine its category  or null if the category doesn't exist or has no category
	 * @param obj an edge
	 * @return the default style for this object
	 */	
	public Style getStyleForEdge(Object obj) {
		return getStyle(getCategoryForEdge(obj));
	}
	
	/**
	 * Set the style for a category id. 
	 * @param category the id of the category
	 * @return false if the category doesn't exists
	 */
	public boolean setStyle(String category, Style style) {
		if (!m.containsKey(category)) {
            return missingCategory(category, style);
        }
		m.put(category, style);
		return true;
	}

	/**
	 * Set the style for an object using getCategory to determine its category
	 * 
	 * If possible call setStyleForNode or setStyleForEdge, when you know the type of object
	 * 
	 * @param obj
	 * @return the default style for this object
	 */	
	public boolean setStyle(Object obj, Style style) {
		return setStyle(getCategory(obj), style);
	}
	
	/**
	 * Set the style for a node using getCategory to determine its category
	 * @param obj a node
	 * @return the default style for this object
	 */	
	public boolean setStyleForNode(Object obj, Style style) {
		return setStyle(getCategoryForNode(obj), style);
	}
	
	/**
	 * Set the style for an edge using getCategory to determine its category
	 * @param obj an edge
	 * @return the default style for this object
	 */	
	public boolean setStyleForEdge(Object obj, Style style) {
		return setStyle(getCategoryForEdge(obj), style);
	}
	
	/**
	 * Apply the style to an element using an attributesReder for a category id or null if the category doesn't exist.
	 * @param category the id of the category
	 * @return false if the category doesn't exist
	 */
	public boolean applyStyle(String category, AttributesReader areader) {
		Style s = (Style) m.get(category);
		if (s != null) {
            s.apply(areader);
        } else {
            return missingCategory(category, areader);
        }
		return true;
	}

	/**
	 * Apply the style to an element using an attributesReder for a category id or null if the category doesn't exist.
	 * 
	 * If possible call applyStyleForNode or applyStyleForEdge, when you know the type of object
	 * 
	 * @param obj
	 * @return false if the category doesn't exist
	 */	
	public boolean applyStyle(Object obj, AttributesReader areader) {
		return applyStyle(getCategory(obj), areader);
	}

	/**
	 * Apply the style to a node using an attributesReder for a category id or null if the category doesn't exist.
	 * @param obj a node
	 * @return false if the category doesn't exist
	 */	
	public boolean applyStyleForNode(Object obj, AttributesReader areader) {
		return applyStyle(getCategoryForNode(obj), areader);
	}
	
	/**
	 * Apply the style to an edge using an attributesReder for a category id or null if the category doesn't exist.
	 * @param obj an edge
	 * @return false if the category doesn't exist
	 */	
	public boolean applyStyleForEdge(Object obj, AttributesReader areader) {
		return applyStyle(getCategoryForEdge(obj), areader);
	}
	
	/**
	 * Declare a new category responding to this selector
	 * @param category the identifier for this category
	 * @param defaultStyle its default style
	 */
	protected void addCategory(String category, Style defaultStyle) {
		m.put(category, defaultStyle);
	}
	
	/**
	 * Reset the categories to their default values.
	 */
	public abstract void resetDefaultStyle();

	/**
	 * Called when the category doesn't exist in the map.
	 * @param category
	 * @return null
	 */
	protected Style missingCategory(String category) {
		return null;
	}

	/**
	 * Called when the category doesn't exist in the map.
	 * @param category
	 * @param areader the areader to apply the corresponding style on.
	 * @return false
	 */
	protected boolean missingCategory(String category, AttributesReader areader) {
		return false;
	}

	/**
	 * Called when the category doesn't exist in the map.
	 * @param category
	 * @param style the style to save for this category
	 * @return false
	 */
	protected boolean missingCategory(String category, Style style) {
		return false;
	}
	
	/**
	 * Indicates if the Selector contains nodes styles. True by default, you should override this function if the selector doesn't respond to Nodes.
	 * @return true
	 */
	public boolean respondToNodes() {
		return true;
	}
	
	/**
	 * Indicates if the Selector contains edges styles. True by default, you should override this function if the selector doesn't respond to Nodes.
	 * @return true
	 */
	public boolean respondToEdges() {
		return true;
	}
	
	/**
	 * Indicates if the selector require a category or can be called alone in a sheet document.
	 * @return false
	 */
	public boolean requireCategory() {
		return false;
	}

	/**
	 * Return the category corresponding to the object obj or null if it doesn't respond to any.
	 * 
	 * If possible call getCategoryForNode or getCategoryForEdge, when you know the type of object
	 * 
	 * @param obj
	 * @return
	 */
	public String getCategory(Object obj) {
		if (isNode(obj)) {
            return getCategoryForNode(obj);
        }
		if (isEdge(obj)) {
            return getCategoryForEdge(obj);
        }
		return null;
	}

	/**
	 * Return the category corresponding to the object obj or null if it doesn't respond to any.
	 * 
	 * When subclassing Selector you should use a cache system if you can't get the category for an object in a constant time. And therefore also extend the function flush() to empty this cache (set to to null).
	 * @param obj
	 * @return
	 */
	abstract public String getCategoryForNode(Object obj);
	/**
	 * Return the category corresponding to the object obj or null if it doesn't respond to any.
	 * 
	 * When subclassing Selector you should use a cache system if you can't get the category for an object in a constant time. And therefore also extend the function flush() to empty this cache (set to to null).
	 * @param obj
	 * @return
	 */
	abstract public String getCategoryForEdge(Object obj);
	
	/**
	 * @param obj
	 * @return true if obj is a node
	 */
	protected boolean isNode(Object obj) {
		if (obj instanceof RegulatoryNode) {
            return true;
        }
		if (obj instanceof DynamicNode) {
            return true;
        }
		return false;
	}

	/**
	 * @param obj
	 * @return true if obj is an edge
	 */
	protected boolean isEdge(Object obj) {
		if (obj instanceof Edge) {
            return true;
        }
		if (obj instanceof RegulatoryEdge) {
            return true;
        }
		if (obj instanceof RegulatoryMultiEdge) {
            return true;
        }
		return false;
	}

	/**
	 * By default this does nothing but subclasses of Selector could use this function to free the cache of category<=>object.
	 * Should only be called if you are sure you doesn't need to use getCategory on this selector anymore.
	 */
	public void flush() {};
	
	public String toString() {
		return identifier;
	}
	
	public String toString(String category) {
		return identifier+category;
	}
}
