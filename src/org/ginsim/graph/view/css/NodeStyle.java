package org.ginsim.graph.view.css;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.ginsim.graph.common.AttributesReader;
import org.ginsim.graph.common.NodeAttributesReader;

import fr.univmrs.tagc.common.Tools;

/**
 * NodeStyle store some graphical attributes of a node
 * Attributes : 
 *     - background : the color for the background = any Color
 *     - foreground : the color for the text and the border = any Color
 *     - border : the style of theborder = simple, raised or strong
 *     - shape : the shape = ellipse or rectangle
 */
public class NodeStyle implements Style {
	public static final Color NULL_BACKGROUND = null;
	public static final Color NULL_FOREGROUND = null;
	public static final int NULL_BORDER = -99;
	public static final int NULL_SHAPE = -99;

	public final static String CSS_BACKGROUND		= "background";
	public final static String CSS_FOREGROUND		= "foreground";
	public final static String CSS_SHAPE			= "shape";
	public final static String CSS_SHAPE_ELLIPSE	= "ellipse";
	public final static String CSS_SHAPE_RECTANGLE	= "rectangle";
	public final static String CSS_BORDER			= "border";
	public final static String CSS_BORDER_SIMPLE	= "simple";
	public final static String CSS_BORDER_RAISED	= "raised";
	public final static String CSS_BORDER_STRONG	= "strong";

	public Color background;
	public Color foreground;
	public int border;
	public int shape;
	
	static Pattern parserPattern = null;

	/**
	 * A new style from the with all values to NULL
	 */
	public NodeStyle() {
		this.background = NULL_BACKGROUND;
		this.foreground = NULL_FOREGROUND;
		this.border 	= NULL_BORDER;
		this.shape 		= NULL_SHAPE;
	}

	/**
	 * A new style from the scratch
	 * @param background the background color for the node
	 * @param foreground the foreground color for the node
	 * @param border the style for the line 
	 * @param shape the shape for the line 
	 * 
	 * @see NodeAttributesReader
	 */
	public NodeStyle(Color background, Color foreGround, int border, int shape) {
		this.background = background;
		this.foreground = foreGround;
		this.border 	= border;
		this.shape 		= shape;
	}

	/**
	 * A new style from a GsAttributesReader areader
 	 * @param areader
 	 */
	public NodeStyle(AttributesReader areader) {
		background 	= ((NodeAttributesReader) areader).getBackgroundColor();
		foreground 	= ((NodeAttributesReader) areader).getForegroundColor();
		border 		= ((NodeAttributesReader) areader).getBorder();
		shape 		= ((NodeAttributesReader) areader).getShape();
	}
	
	/**
	 * A new style copied from another
	 * @param s
	 */
	public NodeStyle(Style s) {
		background 	= ((NodeStyle) s).background;
		foreground 	= ((NodeStyle) s).foreground;
		border 		= ((NodeStyle) s).border;
		shape 		= ((NodeStyle) s).shape;
	}
	
	public void merge(Style s) {
		if (s == null) return;
		if (((NodeStyle) s).background != NULL_BACKGROUND)	background 	= ((NodeStyle) s).background;
		if (((NodeStyle) s).foreground != NULL_FOREGROUND)	foreground 	= ((NodeStyle) s).foreground;
		if (((NodeStyle) s).border != NULL_BORDER)			border 		= ((NodeStyle) s).border;		
		if (((NodeStyle) s).shape != NULL_SHAPE)				shape 		= ((NodeStyle) s).shape;
	}

	public void apply(AttributesReader areader) {
		if (background != NULL_BACKGROUND) 	((NodeAttributesReader) areader).setBackgroundColor(background);
		if (foreground != NULL_FOREGROUND) 	((NodeAttributesReader) areader).setForegroundColor(foreground);
		if (border != NULL_BORDER) 			((NodeAttributesReader) areader).setBorder(border);
		if (shape != NULL_SHAPE)			((NodeAttributesReader) areader).setShape(shape);
		areader.refresh();
	}
	
	/**
	 * a css string representation of this style.
	 */

	public String toString() {
		return toString(0);
	}

	/**
	 * a css string representation of this style.
	 * @param tabs_count the number of tabulations to append at the begining of each line
	 */
	public String toString(int tabs_count) {
		String s = "", tabs = "\t";
		for (int i = 1; i < tabs_count; i++) {
			tabs += "\t";
		}
		if (background != NULL_BACKGROUND) s += tabs+CSS_BACKGROUND+": "+Tools.getColorCode(background)+"\n"; 
		if (foreground != NULL_FOREGROUND) s += tabs+CSS_FOREGROUND+": "+Tools.getColorCode(foreground)+"\n";
		if (border != NULL_BORDER) {
			s += tabs+CSS_BORDER+": ";
			switch (border) {
			case NodeAttributesReader.BORDER_SIMPLE:
				s += CSS_BORDER_SIMPLE;
				break;
			case NodeAttributesReader.BORDER_RAISED:
				s += CSS_BORDER_RAISED;
				break;
			case NodeAttributesReader.BORDER_STRONG:
				s += CSS_BORDER_STRONG;
				break;
			}
			s += "\n";
		}
		if (shape != NULL_SHAPE) s += tabs+CSS_SHAPE+": "+(shape == NodeAttributesReader.SHAPE_ELLIPSE?CSS_SHAPE_ELLIPSE:CSS_SHAPE_RECTANGLE)+"\n";
		return s;
	}
	
	
	/**
	 * Create a new style from an array of strings
	 * 
	 * @param lines
	 * @return the new style
	 * @throws PatternSyntaxException
	 * @throws GsCSSSyntaxException if there is an error in the syntax
	 */
	public static Style fromString(String []lines) throws PatternSyntaxException, GsCSSSyntaxException {
		Color background = NULL_BACKGROUND;
		Color foreground = NULL_FOREGROUND;
		int shape = NULL_SHAPE;
		int border = NULL_BORDER;
		
		if (parserPattern == null) parserPattern = Pattern.compile("([a-zA-Z0-9\\-_]+):\\s*#?([a-zA-Z0-9\\-_]+);");
		
		for (int i = 0; i < lines.length; i++) {
			Matcher m = parserPattern.matcher(lines[i].trim());
			String key = m.group(1).trim(), value = m.group(2).trim();
			
			if (m.groupCount() < 2) throw new GsCSSSyntaxException("Malformed line "+i+" : "+lines[i]+". Must be 'key: value;'");
			if (key.equals(CSS_BACKGROUND)) {
				try {
					background = Tools.getColorFromCode(value);
				} catch (NumberFormatException e) {
					throw new GsCSSSyntaxException("Malformed color code at line "+i+" : "+lines[i]+". Must be from 000000 to FFFFFF");
				}
			} else if (key.equals(CSS_FOREGROUND)) {
				try {
					foreground = Tools.getColorFromCode(value);
				} catch (NumberFormatException e) {
					throw new GsCSSSyntaxException("Malformed color code at line "+i+" : "+lines[i]+". Must be from 000000 to FFFFFF");
				}
			} else if (key.equals(CSS_SHAPE)) {
				if 		(value.equals(CSS_SHAPE_ELLIPSE)) 	shape = NodeAttributesReader.SHAPE_ELLIPSE;
				else if (value.equals(CSS_SHAPE_RECTANGLE)) shape = NodeAttributesReader.SHAPE_RECTANGLE;
				else throw new GsCSSSyntaxException("Unknown vertex shape at line "+i+" : "+lines[i]+". Must be "+CSS_SHAPE_ELLIPSE+" or "+CSS_SHAPE_RECTANGLE);
			} else if (key.equals(CSS_BORDER)) {
				if 		(value.equals(CSS_BORDER_SIMPLE)) 	border = NodeAttributesReader.BORDER_SIMPLE;
				else if (value.equals(CSS_BORDER_RAISED)) 	border = NodeAttributesReader.BORDER_RAISED;
				else if (value.equals(CSS_BORDER_STRONG)) 	border = NodeAttributesReader.BORDER_STRONG;
				else throw new GsCSSSyntaxException("Unknown vertex border at line "+i+" : "+lines[i]+". Must be "+CSS_BORDER_SIMPLE+", "+CSS_BORDER_RAISED+" or "+CSS_BORDER_STRONG);
			} else {
				throw new GsCSSSyntaxException("Node has no key "+key+" at line "+i+" : "+lines[i]+". Must be "+CSS_BACKGROUND+", "+CSS_FOREGROUND+", "+CSS_SHAPE+" or "+CSS_BORDER);
			}
		}
		return new NodeStyle(background, foreground, border, shape);
	}
	public Object clone() {
		return new NodeStyle(this);
	}
}
