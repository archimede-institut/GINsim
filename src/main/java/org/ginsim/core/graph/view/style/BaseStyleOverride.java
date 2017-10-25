package org.ginsim.core.graph.view.style;

abstract public class BaseStyleOverride<S extends Style> implements Style {

	protected final S defaultStyle;
	protected S baseStyle;
	


	public BaseStyleOverride(S defaultStyle) {
		this.defaultStyle = defaultStyle;
		this.baseStyle = defaultStyle;
	}
	
	public void setBaseStyle(S style) {
		if (style == null) {
			this.baseStyle = defaultStyle;
		} else {
			this.baseStyle = style;
		}
	}
	
	@Override
	public StyleProperty[] getProperties() {
		return null;
	}

	@Override
	public Object getProperty(StyleProperty prop) {
		return null;
	}

	@Override
	public void setProperty(StyleProperty prop, Object value) {
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public Object getParentProperty(StyleProperty property) {
		return null;
	}
	
	@Override
	public Style getParent() {
		return defaultStyle;
	}
}
