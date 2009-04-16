package fr.univmrs.tagc.GINsim.reg2dyn;


public class SimulationQueuedState {
	public short[] state;
	public Object previous = null;
	public boolean multiple = false;
	public int depth;
	
	SimulationQueuedState(short[] state, int depth, Object previous, boolean multiple) {
		this.state = state;
		this.previous = previous;
		this.depth = depth;
		this.multiple = multiple;
	}
}

