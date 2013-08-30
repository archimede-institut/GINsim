package org.ginsim.core.graph.view.style;

import org.ginsim.core.graph.view.EdgePattern;

public class EnumProperty extends StyleProperty {
	private final Object[] values;
	private final Class cl;
	
	protected EnumProperty(String name, Enum[] values) {
		this(name, values, false);
	}
	protected EnumProperty(String name, Enum[] values, boolean isCore) {
		super(name, isCore);
		this.values = values;
		this.cl = values[0].getClass();
	}
	@Override
	public Object getValue(String s) {
		try {
			return Enum.valueOf(cl, s);
		} catch (Exception e) {
			return values[0];
		}
	}
	
	public Object[] getValues() {
		return values;
	}
}