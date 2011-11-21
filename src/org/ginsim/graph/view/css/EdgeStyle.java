package org.ginsim.graph.view.css;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.ginsim.graph.common.AttributesReader;
import org.ginsim.graph.common.EdgeAttributesReader;

import fr.univmrs.tagc.common.Tools;


/**
 * EdgeStyle store some graphical attributes of an edge
 * Attributes : 
 *     - lineColor : the line color = any Color
 *     - shape : the style of the edge = straight or curve
 *     - lineEnd : the line end = positive, negative, double or unknown
 */
public class EdgeStyle implements Style {
	public final static Color NULL_LINECOLOR = null;
	public final static int NULL_SHAPE = -99;
	public final static int NULL_LINEEND = -99;
	public final static float NULL_BORDER = -99;

	public final static String CSS_LINECOLOR		= "line-color";
	public final static String CSS_SHAPE			= "shape";
	public final static String CSS_SHAPE_STRAIGHT	= "straight";
	public final static String CSS_SHAPE_CURVE		= "curve";
	public final static String CSS_LINEEND			= "line-end";
	public final static String CSS_LINEEND_POSITIVE	= "positive";
	public final static String CSS_LINEEND_NEGATIVE	= "negative";
	public final static String CSS_LINEEND_DOUBLE	= "double";
	public final static String CSS_LINEEND_UNKNOWN	= "unknown";
	public final static String CSS_BORDER			= "border";
	
	public Color lineColor;
	public int shape; //to prevent the use of the word style
	public int lineEnd;
	public float border;
	
	
	static Pattern parserPattern = null;

	/**
	 * A new style from the with all values to NULL
	 */
	public EdgeStyle() {
		this.lineColor 	= NULL_LINECOLOR;
		this.lineEnd 	= NULL_LINEEND;
		this.shape 		= NULL_SHAPE;
		this.border		= NULL_BORDER;
	}

	
	/**
	 * A new style from the scratch
	 * @param lineColor the lineColor color for the vertex
	 * @param shape the shape for the line 
	 * @param lineEnd the style for the end of the line 
	 * 
	 * @see EdgeAttributesReader
	 */
	public EdgeStyle(Color lineColor, int lineEnd, int shape, float border) {
		this.lineColor	= lineColor;
		this.lineEnd 	= lineEnd;
		this.shape 		= shape;
		this.border 	= border;
	}

	/**
	 * A new style from a GsAttributesReader areader
 	 * @param areader
 	 */
	public EdgeStyle(AttributesReader areader) {
		lineColor 	= ((EdgeAttributesReader) areader).getLineColor();
		shape 		= ((EdgeAttributesReader) areader).getStyle();
		lineEnd 	= ((EdgeAttributesReader) areader).getLineEnd();
		border	 	= ((EdgeAttributesReader) areader).getLineWidth();
	}
	
	/**
	 * A new style copied from another
	 * @param s
	 */
	public EdgeStyle(Style s) {
		lineColor 	= ((EdgeStyle) s).lineColor;
		shape 		= ((EdgeStyle) s).shape;
		lineEnd 	= ((EdgeStyle) s).lineEnd;
		border	 	= ((EdgeStyle) s).border;
	}
	
	public void merge(Style s) {
		if (s == null) return;
		if (((EdgeStyle) s).lineColor != NULL_LINECOLOR)	lineColor 	= ((EdgeStyle) s).lineColor;
		if (((EdgeStyle) s).shape != NULL_SHAPE)			shape 		= ((EdgeStyle) s).shape;
		if (((EdgeStyle) s).lineEnd != NULL_LINEEND)		lineEnd 	= ((EdgeStyle) s).lineEnd;		
		if (((EdgeStyle) s).border != NULL_BORDER)			border 		= ((EdgeStyle) s).border;		
	}

	public void apply(AttributesReader areader) {
		if (lineColor != NULL_LINECOLOR)	((EdgeAttributesReader) areader).setLineColor(lineColor);
		if (shape != NULL_SHAPE) 			((EdgeAttributesReader) areader).setStyle(shape);
		if (lineEnd != NULL_LINEEND) 		((EdgeAttributesReader) areader).setLineEnd(lineEnd);
		if (border != NULL_BORDER) 			((EdgeAttributesReader) areader).setLineWidth(border);
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
		if (lineColor!= NULL_LINECOLOR) s += tabs+CSS_LINECOLOR+": "+Tools.getColorCode(lineColor)+"\n";
		if (shape != NULL_SHAPE) s += tabs+CSS_SHAPE+": "+(shape == EdgeAttributesReader.STYLE_CURVE?CSS_SHAPE_CURVE:CSS_SHAPE_STRAIGHT)+"\n";
		if (border != NULL_BORDER) s += tabs+CSS_BORDER+": "+border+"\n";
		if (lineEnd != NULL_LINEEND) {
			s += tabs+CSS_LINEEND+": ";
			switch (lineEnd) {
			case EdgeAttributesReader.ARROW_POSITIVE:
				s += CSS_LINEEND_POSITIVE;
				break;
			case EdgeAttributesReader.ARROW_NEGATIVE:
				s += CSS_LINEEND_NEGATIVE;
				break;
			case EdgeAttributesReader.ARROW_DOUBLE:
				s += CSS_LINEEND_DOUBLE;
				break;
			case EdgeAttributesReader.ARROW_UNKNOWN:
				s += CSS_LINEEND_UNKNOWN;
				break;
			}
			s += "\n";
		}
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
		Color lineColor = NULL_LINECOLOR;
		int shape = NULL_SHAPE;
		int lineEnd = NULL_LINEEND;
		float border = NULL_BORDER;
		
		if (parserPattern == null) parserPattern = Pattern.compile("([a-zA-Z0-9\\-_]+):\\s*#?([a-zA-Z0-9\\-_]+);");
		
		for (int i = 0; i < lines.length; i++) {
			Matcher m = parserPattern.matcher(lines[i].trim());
			String key = m.group(1).trim(), value = m.group(2).trim();
			
			if (m.groupCount() < 2) throw new GsCSSSyntaxException("Malformed line "+i+" : "+lines[i]+". Must be 'key: value;'");
			if (key.equals(CSS_LINECOLOR)) {
				try {
					lineColor = Tools.getColorFromCode(value);
				} catch (NumberFormatException e) {
					throw new GsCSSSyntaxException("Malformed color code at line "+i+" : "+lines[i]+". Must be from 000000 to FFFFFF");
				}
			} else if (key.equals(CSS_SHAPE)) {
				if 		(value.equals(CSS_SHAPE_CURVE)) 	shape = EdgeAttributesReader.STYLE_CURVE;
				else if (value.equals(CSS_SHAPE_STRAIGHT)) 	shape = EdgeAttributesReader.STYLE_STRAIGHT;
				else throw new GsCSSSyntaxException("Unknown edge shape at line "+i+" : "+lines[i]+". Must be "+CSS_SHAPE_CURVE+" or "+CSS_SHAPE_STRAIGHT);
			} else if (key.equals(CSS_LINEEND)) {
				if 		(value.equals(CSS_LINEEND_POSITIVE)) 	lineEnd = EdgeAttributesReader.ARROW_POSITIVE;
				else if (value.equals(CSS_LINEEND_NEGATIVE)) 	lineEnd = EdgeAttributesReader.ARROW_NEGATIVE;
				else if (value.equals(CSS_LINEEND_DOUBLE))	 	lineEnd = EdgeAttributesReader.ARROW_DOUBLE;
				else if (value.equals(CSS_LINEEND_UNKNOWN)) 	lineEnd = EdgeAttributesReader.ARROW_UNKNOWN;
				else throw new GsCSSSyntaxException("Unknown edge lineEnd at line "+i+" : "+lines[i]+". Must be "+CSS_LINEEND_POSITIVE+", "+CSS_LINEEND_NEGATIVE+", "+CSS_LINEEND_DOUBLE+" or "+CSS_LINEEND_UNKNOWN);
			} else {
				throw new GsCSSSyntaxException("Edge has no key "+key+" at line "+i+" : "+lines[i]+". Must be "+CSS_LINECOLOR+", "+CSS_SHAPE+" or "+CSS_LINEEND);
			}
		}
		return new EdgeStyle(lineColor, lineEnd, shape, border);
	}

	public Object clone() {
		return new EdgeStyle(this);
	}
}
