package org.ginsim.core.graph.view.css;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.ginsim.common.utils.DataUtils;
import org.ginsim.core.graph.view.AttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;



/**
 * NodeStyle store some graphical attributes of a node
 * Attributes : 
 *     - background : the color for the background = any Color
 *     - foreground : the color for the text and the border = any Color
 *     - border : the style of theborder = simple, raised or strong
 *     - shape : the shape = ellipse or rectangle
 */
public class NodeStyle implements Style {
	@Deprecated
	public static final Color NULL_FOREGROUND  = null;
	@Deprecated
	public static final NodeBorder NULL_BORDER = null;
	@Deprecated
	public static final NodeShape  NULL_SHAPE  = null;

	public final static String CSS_BACKGROUND		= "background";
	public final static String CSS_FOREGROUND		= "foreground";
	public final static String CSS_SHAPE			= "shape";
	public final static String CSS_BORDER			= "border";
	
	public final static String CSS_SHAPE_ELLIPSE	= "ellipse";
	public final static String CSS_SHAPE_RECTANGLE	= "rectangle";
	public final static String CSS_BORDER_SIMPLE	= "simple";
	public final static String CSS_BORDER_RAISED	= "raised";
	public final static String CSS_BORDER_STRONG	= "strong";

	public Color background;
	public Color foreground;
	public NodeBorder border;
	public NodeShape shape;
	
	static Pattern parserPattern = null;

	/**
	 * A new style from the with all values to NULL
	 */
	public NodeStyle() {
		this(null, null, null, null);
	}

	/**
	 * A new Style defining only colors.
	 * 
	 * @param background
	 * @param foreground
	 */
	public NodeStyle(Color background, Color foreground) {
		this(background, foreground, null, null);
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
	public NodeStyle(Color background, Color foreGround, NodeBorder border, NodeShape shape) {
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
	
	public void merge(Style sa) {
		if (!(sa instanceof NodeStyle)) {
			return;
		}
		
		NodeStyle s = (NodeStyle)sa; 
		if (s.background != null)   background = s.background;
		if (s.foreground != null)   foreground = s.foreground;
		if (s.border != null)       border     = s.border;		
		if (s.shape != null)        shape      = s.shape;
	}

	public void apply(AttributesReader areader) {
		if (areader instanceof NodeAttributesReader) {
			return;
		}
		
		NodeAttributesReader nreader = (NodeAttributesReader)areader;
		if (background != null) nreader.setBackgroundColor(background);
		if (foreground != null) nreader.setForegroundColor(foreground);
		if (border != null)     nreader.setBorder(border);
		if (shape != null)      nreader.setShape(shape);
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
		if (background != null) s += tabs+CSS_BACKGROUND+": "+DataUtils.getColorCode(background)+"\n"; 
		if (foreground != null) s += tabs+CSS_FOREGROUND+": "+DataUtils.getColorCode(foreground)+"\n";
		if (border != null) {
			s += tabs+CSS_BORDER+": ";
			switch (border) {
			case SIMPLE:
				s += CSS_BORDER_SIMPLE;
				break;
			case RAISED:
				s += CSS_BORDER_RAISED;
				break;
			case STRONG:
				s += CSS_BORDER_STRONG;
				break;
			}
			s += "\n";
		}
		if (shape != null) s += tabs+CSS_SHAPE+": "+(shape == NodeShape.ELLIPSE?CSS_SHAPE_ELLIPSE:CSS_SHAPE_RECTANGLE)+"\n";
		return s;
	}
	
	
	/**
	 * Create a new style from an array of strings
	 * 
	 * @param lines
	 * @return the new style
	 * @throws PatternSyntaxException
	 * @throws CSSSyntaxException if there is an error in the syntax
	 */
	public static Style fromString(String []lines) throws PatternSyntaxException, CSSSyntaxException {
		Color background  = null;
		Color foreground  = null;
		NodeShape shape   = null;
		NodeBorder border = null;
		
		if (parserPattern == null) parserPattern = Pattern.compile("([a-zA-Z0-9\\-_]+):\\s*#?([a-zA-Z0-9\\-_]+);");
		
		for (int i = 0; i < lines.length; i++) {
			Matcher m = parserPattern.matcher(lines[i].trim());
			String key = m.group(1).trim(), value = m.group(2).trim();
			
			if (m.groupCount() < 2) throw new CSSSyntaxException("Malformed line "+i+" : "+lines[i]+". Must be 'key: value;'");
			if (key.equals(CSS_BACKGROUND)) {
				try {
					background = DataUtils.getColorFromCode(value);
				} catch (NumberFormatException e) {
					throw new CSSSyntaxException("Malformed color code at line "+i+" : "+lines[i]+". Must be from 000000 to FFFFFF");
				}
			} else if (key.equals(CSS_FOREGROUND)) {
				try {
					foreground = DataUtils.getColorFromCode(value);
				} catch (NumberFormatException e) {
					throw new CSSSyntaxException("Malformed color code at line "+i+" : "+lines[i]+". Must be from 000000 to FFFFFF");
				}
			} else if (key.equals(CSS_SHAPE)) {
				if 		(value.equals(CSS_SHAPE_ELLIPSE)) 	shape = NodeShape.ELLIPSE;
				else if (value.equals(CSS_SHAPE_RECTANGLE)) shape = NodeShape.RECTANGLE;
				else throw new CSSSyntaxException("Unknown vertex shape at line "+i+" : "+lines[i]+". Must be "+CSS_SHAPE_ELLIPSE+" or "+CSS_SHAPE_RECTANGLE);
			} else if (key.equals(CSS_BORDER)) {
				if 		(value.equals(CSS_BORDER_SIMPLE)) 	border = NodeBorder.SIMPLE;
				else if (value.equals(CSS_BORDER_RAISED)) 	border = NodeBorder.RAISED;
				else if (value.equals(CSS_BORDER_STRONG)) 	border = NodeBorder.STRONG;
				else throw new CSSSyntaxException("Unknown vertex border at line "+i+" : "+lines[i]+". Must be "+CSS_BORDER_SIMPLE+", "+CSS_BORDER_RAISED+" or "+CSS_BORDER_STRONG);
			} else {
				throw new CSSSyntaxException("Node has no key "+key+" at line "+i+" : "+lines[i]+". Must be "+CSS_BACKGROUND+", "+CSS_FOREGROUND+", "+CSS_SHAPE+" or "+CSS_BORDER);
			}
		}
		return new NodeStyle(background, foreground, border, shape);
	}
	public Object clone() {
		return new NodeStyle(this);
	}
}
