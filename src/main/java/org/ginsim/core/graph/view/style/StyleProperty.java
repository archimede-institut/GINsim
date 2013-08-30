package org.ginsim.core.graph.view.style;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;

abstract public class StyleProperty {

	private static int NEXTKEY = 0;
	
	public static final StyleProperty BACKGROUND = new ColorProperty("background", true);
	public static final StyleProperty FOREGROUND = new ColorProperty("foreground", true);
	public static final StyleProperty TEXT       = new ColorProperty("text", true);
	public static final StyleProperty SHAPE      = new EnumProperty("shape", NodeShape.RECTANGLE, true);
	public static final StyleProperty BORDER     = new EnumProperty("border", NodeBorder.SIMPLE, true);


	public static final StyleProperty COLOR      = new ColorProperty("color", true);
	public static final StyleProperty ENDING     = new EnumProperty("ending", EdgeEnd.POSITIVE, true);
	public static final StyleProperty PATTERN    = new EnumProperty("pattern", EdgePattern.SIMPLE, true);
	
	
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

	abstract public Class getPropertyClass();
	
	public String getString(Object value) {
		return value.toString();
	}
}

class ColorProperty extends StyleProperty {

	protected ColorProperty(String name, boolean isCore) {
		super(name, isCore);
	}
	protected ColorProperty(String name) {
		super(name, false);
	}

	@Override
	public Object getValue(String s) {
		return ColorPalette.getColorFromCode(s);
	}

	@Override
	public String getString(Object value) {
		if (value instanceof Color) {
			Color color = (Color)value;
			return ColorPalette.getColorCode(color);
		}
		return null;
	}
	@Override
	public Class getPropertyClass() {
		return Color.class;
	}
}

class EnumProperty extends StyleProperty {
	private final Enum fallback;
	protected EnumProperty(String name, Enum fallback) {
		this(name, fallback, false);
	}
	protected EnumProperty(String name, Enum fallback, boolean isCore) {
		super(name, isCore);
		this.fallback = fallback;
	}
	@Override
	public Object getValue(String s) {
		try {
			return Enum.valueOf(fallback.getClass(), s);
		} catch (Exception e) {
			return fallback;
		}
	}
	@Override
	public Class getPropertyClass() {
		return Enum.class;
	}
}