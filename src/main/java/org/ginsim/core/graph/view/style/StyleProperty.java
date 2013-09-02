package org.ginsim.core.graph.view.style;

import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;

abstract public class StyleProperty {

	private static int NEXTKEY = 0;
	
	public static final ColorProperty BACKGROUND = new ColorProperty("background", true);
	public static final ColorProperty FOREGROUND = new ColorProperty("foreground", true);
	public static final ColorProperty TEXT       = new ColorProperty("text", true);
	public static final EnumProperty SHAPE      = new EnumProperty("shape", NodeShape.values(), true);
	public static final EnumProperty BORDER     = new EnumProperty("border", NodeBorder.values(), true);


	public static final ColorProperty COLOR      = new ColorProperty("color", true);
	public static final EnumProperty ENDING     = new EnumProperty("ending", EdgeEnd.values(), true);
	public static final EnumProperty PATTERN    = new EnumProperty("pattern", EdgePattern.values(), true);
	

	public static final IntegerProperty WIDTH     = new IntegerProperty("width", true, 15, 400, 5, 40);
	public static final IntegerProperty HEIGHT    = new IntegerProperty("height", true, 15, 400, 5, 25);
	public static final IntegerProperty LINEWIDTH = new IntegerProperty("line_width", true, 1, 7, 1, 1);


	public static StyleProperty[] merge(StyleProperty[] base, StyleProperty[] extra) {
		StyleProperty[] result = new StyleProperty[base.length + extra.length];
		int pos = 0;
		for (StyleProperty prop: base) {
			result[pos++] = prop;
		}
		for (StyleProperty prop: extra) {
			result[pos++] = prop;
		}
		return result;
	}
	
	public static StyleProperty createColorProperty(String name) {
		return new ColorProperty(name);
	}
	
	public final boolean isCore;
	public final int key;
	public final String name;
	
	protected StyleProperty(String name, boolean isCore) {
		this.name = name;
		this.isCore = isCore;
		this.key = NEXTKEY++;
	}
	
	abstract public Object getValue(String s);

	public String getString(Object value) {
		return value.toString();
	}
}

