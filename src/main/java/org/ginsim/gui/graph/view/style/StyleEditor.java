package org.ginsim.gui.graph.view.style;

import java.util.List;

import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.Style;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.gui.utils.data.ObjectEditor;

public class StyleEditor extends ObjectEditor<Style> {

	public static final int PROP_NAME = 0;
	
	private final StyleManager manager;
	
	private boolean isEdge = false;
	private Style style = null;
	
	public StyleEditor(StyleManager manager) {
		this.manager = manager;
	}
	
	public void setStyle(Style style) {
		this.style = style;
		if (style instanceof EdgeStyle) {
			this.isEdge = true;
		} else {
			this.isEdge = false;
		}
	}
	
	@Override
	public String getStringValue(int prop) {
		if (style == null) {
			return null;
		}
		
		switch (prop) {
		case PROP_NAME:
			String name = style.getName();
			if (name == null) {
				return "Default Style";
			}
			return style.getName();
		}
		return null;
	}

	
	@Override
	public int getIntValue(int prop) {
		return 0;
	}

	@Override
	public boolean isValidValue(int prop, String value) {
		if (value == null) {
			return false;
		}
		
		List<Style> styles = isEdge ? manager.getEdgeStyles() : manager.getNodeStyles();
		
		for (Style s:styles) {
			if ( value.equals( s.getName()) ) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isValidValue(int prop, int value) {
		return false;
	}

	@Override
	public boolean setValue(int prop, String value) {

		if (style != null && isValidValue(prop, value)) {
			if (getRawValue(prop) == null) {
				return false;
			}
			style.setName(value);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean setValue(int prop, int value) {
		return false;
	}

	@Override
	public Object getRawValue(int prop) {
		if (style == null) {
			return null;
		}
		
		switch (prop) {
		case PROP_NAME:
			return style.getName();
		}

		return null;
	}
	
}