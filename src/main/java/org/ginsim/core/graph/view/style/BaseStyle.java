package org.ginsim.core.graph.view.style;

abstract public class BaseStyle<S extends Style> implements Style {

	protected String name;
	protected final S parent;
	
	public BaseStyle(S parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object getParentProperty(StyleProperty property) {
		Object value = getProperty(property);
		if (value == null && parent != null) {
			return parent.getParentProperty(property);
		}
		return value;
	}
}
