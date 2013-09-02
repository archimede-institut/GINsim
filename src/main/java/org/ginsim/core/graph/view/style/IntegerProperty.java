package org.ginsim.core.graph.view.style;


public class IntegerProperty extends StyleProperty {
	
	public final int fallback;
	public final int min, max, step;
	
	protected IntegerProperty(String name, boolean isCore, int min, int max, int step, int fallback) {
		super(name, isCore);
		this.min = min;
		this.max = max;
		this.step = step;
		this.fallback = fallback;
	}
	protected IntegerProperty(String name, int min, int max, int step, int fallback) {
		this(name, false, min, max, step, fallback);
	}

	@Override
	public Object getValue(String s) {
		try {
			int v = Integer.parseInt(s);
			if (v < min) {
				return min;
			}
			if (v > max) {
				return max;
			}
			return v;
		} catch (Exception e) {
			return fallback;
		}
	}

	@Override
	public String getString(Object value) {
		return ""+value;
	}
	
}
