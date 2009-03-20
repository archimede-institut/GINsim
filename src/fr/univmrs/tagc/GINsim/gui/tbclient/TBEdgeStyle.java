package fr.univmrs.tagc.GINsim.gui.tbclient;

import java.awt.*;
import java.util.regex.*;

import fr.univmrs.tagc.GINsim.css.*;
import fr.univmrs.tagc.GINsim.graph.*;
import java.util.Vector;

public class TBEdgeStyle extends EdgeStyle {
	public final static String CSS_LINEWIDTH	= "line-width";
	public final static float DEFAULT_WIDTH = 1;

	protected float width;

	public TBEdgeStyle() {
		super();
		width = DEFAULT_WIDTH;
	}
	public void setWidth(float f) {
		width = f;
	}
	public TBEdgeStyle(Color lineColor, int shape, int lineEnd, float width) {
		super(lineColor, shape, lineEnd);
		this.width = width;
	}
	public TBEdgeStyle(GsAttributesReader areader) {
		super(areader);
		width = ((GsEdgeAttributesReader)areader).getLineWidth();
	}
	public TBEdgeStyle(Style s) {
		super(s);
		width = ((TBEdgeStyle)s).width;
	}
	public void merge(Style s) {
		super.merge(s);
		width = ((TBEdgeStyle)s).width;
	}
	public void apply(GsAttributesReader areader) {
		((GsEdgeAttributesReader)areader).setLineWidth(width);
		super.apply(areader);
	}
	public String toString(int tabs_count) {
		String tabs = "";
		for (int i = 0; i < tabs_count; i++) tabs += "\t";
		return super.toString(tabs_count) + tabs + CSS_LINEWIDTH + ": " + width + "\n";
	}
	public static Style fromString(String[] lines) throws PatternSyntaxException, GsCSSSyntaxException {
		float f = DEFAULT_WIDTH;
		String[] tok;
		Vector v = new Vector();
		for (int i = 0; i < lines.length; i++)
			if (lines[i].startsWith(CSS_LINEWIDTH)) {
				tok = lines[i].split(": *", 2);
				f = Float.parseFloat(tok[1].trim());
			}
			else
				v.addElement(lines[i]);
		EdgeStyle s = (EdgeStyle)EdgeStyle.fromString((String[])v.toArray());
		TBEdgeStyle ts = new TBEdgeStyle(s);
		ts.setWidth(f);
		return ts;
	}
	public Object clone() {
		return new TBEdgeStyle(this);
	}
}
