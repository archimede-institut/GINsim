package org.ginsim.gui.service.tool.reg2dyn;

public class SimulationQueuedState {
	public byte[] state;
	public Object previous = null;
	public boolean multiple = false;
	public int depth;
	
	SimulationQueuedState(byte[] state, int depth, Object previous, boolean multiple) {
		this.state = state;
		this.previous = previous;
		this.depth = depth;
		this.multiple = multiple;
	}
}

