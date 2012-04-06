package org.ginsim.core.graph.view;


public enum MovingEdgeType {
	
	NONE(false, false, false),
	SOURCE(true, false, false),
	TARGET(false, true, false),
	BOTH(true, true, false),

	SEL(false, false, true),
	SEL_SOURCE(true, false, true),
	SEL_TARGET(false, true, true),
	SEL_BOTH(true, true, true),
	;
	
	public final boolean source, target, edge;
	
	public static MovingEdgeType getMovingType(boolean selected, boolean source, boolean target) {
		if (selected) {
			if (source) {
				if (target) {
					return SEL_BOTH;
				}
				return SEL_SOURCE;
			}
			if (target) {
				return SEL_TARGET;
			}
			return SEL;
		}

		if (source) {
			if (target) {
				return BOTH;
			}
			return SOURCE;
		}
		if (target) {
			return TARGET;
		}
		return NONE;
	}
	
	private MovingEdgeType(boolean source, boolean target, boolean edge) {
		this.source = source;
		this.target = target;
		this.edge = edge;
	}
}
