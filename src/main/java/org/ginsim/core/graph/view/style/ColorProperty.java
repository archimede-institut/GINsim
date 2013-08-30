package org.ginsim.core.graph.view.style;

import java.awt.Color;

import org.ginsim.common.utils.ColorPalette;

public class ColorProperty extends StyleProperty {
	
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
}
