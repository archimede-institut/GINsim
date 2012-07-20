package org.ginsim.core.graph.view;

public enum EdgePattern {
	
	SIMPLE(null),
	DASH( new float[] {10, 4, 3, 5});

	private final float[] pattern;
	
	EdgePattern(float[] pattern) {
		this.pattern = pattern;
	}
	
	public float[] getPattern() {
		return pattern;
	}
	
}
