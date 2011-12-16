package org.ginsim.core.graph.view.css;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.ginsim.core.graph.view.AttributesReader;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.utils.DataUtils;




/**
 * EdgeStyle store some graphical attributes of an edge
 * Attributes : 
 *     - lineColor : the line color = any Color
 *     - shape : the style of the edge = straight or curve
 *     - lineEnd : the line end = positive, negative, double or unknown
 */
public class EdgeStyle implements Style {
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
	public boolean curve;
	public EdgeEnd lineEnd;
	public float border;
	
	
	static Pattern parserPattern = null;

	/**
	 * A new style from the with all values to NULL
	 */
	public EdgeStyle() {
		this(null, null, false, NULL_BORDER);
	}

	public EdgeStyle(Color lineColor) {
		this(lineColor, null, false, NULL_BORDER);
	}

	
	/**
	 * A new style from the scratch
	 * @param lineColor the lineColor color for the node
	 * @param shape the shape for the line 
	 * @param lineEnd the style for the end of the line 
	 * 
	 * @see EdgeAttributesReader
	 */
	public EdgeStyle(Color lineColor, EdgeEnd lineEnd, boolean curve, float border) {
		this.lineColor	= lineColor;
		this.lineEnd 	= lineEnd;
		this.curve 		= curve;
		this.border 	= border;
	}

	/**
	 * A new style from a GsAttributesReader areader
 	 * @param areader
 	 */
	public EdgeStyle(AttributesReader areader) {
		lineColor 	= ((EdgeAttributesReader) areader).getLineColor();
		curve 		= ((EdgeAttributesReader) areader).isCurve();
		lineEnd 	= ((EdgeAttributesReader) areader).getLineEnd();
		border	 	= ((EdgeAttributesReader) areader).getLineWidth();
	}
	
	/**
	 * A new style copied from another
	 * @param s
	 */
	public EdgeStyle(Style s) {
		lineColor 	= ((EdgeStyle) s).lineColor;
		curve 		= ((EdgeStyle) s).curve;
		lineEnd 	= ((EdgeStyle) s).lineEnd;
		border	 	= ((EdgeStyle) s).border;
	}
	
	public void merge(Style s) {
		if (s == null) return;
		if (((EdgeStyle) s).lineColor != null)	lineColor 	= ((EdgeStyle) s).lineColor;
		if (((EdgeStyle) s).lineEnd != null)		lineEnd 	= ((EdgeStyle) s).lineEnd;		
		if (((EdgeStyle) s).border != NULL_BORDER)			border 		= ((EdgeStyle) s).border;
		curve = ((EdgeStyle) s).curve;
	}

	public void apply(AttributesReader areader) {
		if (lineColor != null)	((EdgeAttributesReader) areader).setLineColor(lineColor);
		((EdgeAttributesReader) areader).setCurve(curve);
		if (lineEnd != null) 		((EdgeAttributesReader) areader).setLineEnd(lineEnd);
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
		if (lineColor!= null) s += tabs+CSS_LINECOLOR+": "+DataUtils.getColorCode(lineColor)+"\n";
		if (curve) s += tabs+CSS_SHAPE+": "+(curve ? CSS_SHAPE_CURVE:CSS_SHAPE_STRAIGHT)+"\n";
		if (border != NULL_BORDER) s += tabs+CSS_BORDER+": "+border+"\n";
		if (lineEnd != null) {
			s += tabs+CSS_LINEEND+": ";
			switch (lineEnd) {
			case POSITIVE:
				s += CSS_LINEEND_POSITIVE;
				break;
			case NEGATIVE:
				s += CSS_LINEEND_NEGATIVE;
				break;
			case DOUBLE:
				s += CSS_LINEEND_DOUBLE;
				break;
			case UNKNOWN:
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
	 * @throws CSSSyntaxException if there is an error in the syntax
	 */
	public static Style fromString(String []lines) throws PatternSyntaxException, CSSSyntaxException {
		Color lineColor = null;
		boolean curve = false;
		EdgeEnd lineEnd = null;
		float border = NULL_BORDER;
		
		if (parserPattern == null) parserPattern = Pattern.compile("([a-zA-Z0-9\\-_]+):\\s*#?([a-zA-Z0-9\\-_]+);");
		
		for (int i = 0; i < lines.length; i++) {
			Matcher m = parserPattern.matcher(lines[i].trim());
			String key = m.group(1).trim(), value = m.group(2).trim();
			
			if (m.groupCount() < 2) throw new CSSSyntaxException("Malformed line "+i+" : "+lines[i]+". Must be 'key: value;'");
			if (key.equals(CSS_LINECOLOR)) {
				try {
					lineColor = DataUtils.getColorFromCode(value);
				} catch (NumberFormatException e) {
					throw new CSSSyntaxException("Malformed color code at line "+i+" : "+lines[i]+". Must be from 000000 to FFFFFF");
				}
			} else if (key.equals(CSS_SHAPE)) {
				if 		(value.equals(CSS_SHAPE_CURVE)) 	curve = true;
				else if (value.equals(CSS_SHAPE_STRAIGHT)) 	curve = false;
				else throw new CSSSyntaxException("Unknown edge shape at line "+i+" : "+lines[i]+". Must be "+CSS_SHAPE_CURVE+" or "+CSS_SHAPE_STRAIGHT);
			} else if (key.equals(CSS_LINEEND)) {
				if 		(value.equals(CSS_LINEEND_POSITIVE)) 	lineEnd = EdgeEnd.POSITIVE;
				else if (value.equals(CSS_LINEEND_NEGATIVE)) 	lineEnd = EdgeEnd.NEGATIVE;
				else if (value.equals(CSS_LINEEND_DOUBLE))	 	lineEnd = EdgeEnd.DOUBLE;
				else if (value.equals(CSS_LINEEND_UNKNOWN)) 	lineEnd = EdgeEnd.UNKNOWN;
				else throw new CSSSyntaxException("Unknown edge lineEnd at line "+i+" : "+lines[i]+". Must be "+CSS_LINEEND_POSITIVE+", "+CSS_LINEEND_NEGATIVE+", "+CSS_LINEEND_DOUBLE+" or "+CSS_LINEEND_UNKNOWN);
			} else {
				throw new CSSSyntaxException("Edge has no key "+key+" at line "+i+" : "+lines[i]+". Must be "+CSS_LINECOLOR+", "+CSS_SHAPE+" or "+CSS_LINEEND);
			}
		}
		return new EdgeStyle(lineColor, lineEnd, curve, border);
	}

	public Object clone() {
		return new EdgeStyle(this);
	}
}
