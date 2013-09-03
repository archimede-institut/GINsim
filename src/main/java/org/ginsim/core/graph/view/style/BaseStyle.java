package org.ginsim.core.graph.view.style;

abstract public class BaseStyle<S extends Style> implements Style {

	protected final int key;
	protected final S parent;
	
	public BaseStyle(S parent, int key) {
		this.parent = parent;
		this.key = key;
	}
	
	@Override
	public int getKey() {
		return key;
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
