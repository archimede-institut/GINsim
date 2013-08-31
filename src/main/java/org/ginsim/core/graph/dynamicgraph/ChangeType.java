package org.ginsim.core.graph.dynamicgraph;

public enum ChangeType {

	NOCHANGE, INCREASE, DECREASE, MULTIPLE_INCREASE, MULTIPLE_DECREASE, MULTIPLE_BOTH;
	
	public ChangeType update(int a, int b) {
		if (a == b) {
			return this;
		}

		if (a > b) {
			// add a decrease
			switch (this) {
			case NOCHANGE:
				return DECREASE;
			case DECREASE:
			case MULTIPLE_DECREASE:
				return MULTIPLE_DECREASE;
			default:
				return MULTIPLE_BOTH;
			}
		}

		// add an increase
		switch (this) {
		case NOCHANGE:
			return INCREASE;
		case INCREASE:
		case MULTIPLE_INCREASE:
			return MULTIPLE_INCREASE;
		default:
			return MULTIPLE_BOTH;
		}
	}
}
