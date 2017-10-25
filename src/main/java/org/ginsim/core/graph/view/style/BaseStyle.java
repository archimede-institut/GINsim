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

    protected String getCSSNameSuffix() {
        if (name == null) {
            return "";
        }
        return "_"+name.trim().toLowerCase().replaceAll(" ", "_");
    }
    
    @Override
    public Style getParent() {
    	return parent;
    }
    
    @Override
    public void copy(Style source) {
    	setName(source.getName());
    	for (StyleProperty prop: getProperties()) {
    		setProperty(prop, source.getProperty(prop));
    	}
    }
    
    public boolean equals(Object other) {
    	if (other == null || other.getClass() != getClass()) {
    		return false;
    	}
    	
		Style style = (Style)other;
		if (style.getProperties() != getProperties()) {
			return false;
		}
		if (style.getName() != getName()) {
			return false;
		}
		
		return true;
    }
}
