package org.ginsim.service.tool.reg2dyn.updater;

import java.util.NoSuchElementException;

import org.colomoto.biolqm.tool.simulation.deterministic.DeterministicUpdater;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;

/**
 * Wrap LogicalModel's updater API into GINsim.
 * This updater lets a helper provide the list of successors and only deals with
 * the iteration part.
 *
 * @author Aurelien Naldi
 */
public class DeterministicSimulationUpdater implements SimulationUpdater {

	private final DeterministicUpdater updater;
	byte[] next = null;
	private boolean multiple;
	
	int depth;
	Object node;

	public DeterministicSimulationUpdater(DeterministicUpdater updater) {
		this.updater = updater;
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public Object next() {
		byte[] next = nextState();
		multiple = false;
		return new SimulationQueuedState(next, depth+1, node, multiple);
	}

	@Override
	public byte[] nextState() {
		if (next == null) {
			throw new NoSuchElementException("No more successor states");
		}
		byte[] ret = next;
		next = null;
		return ret;
	}

	@Override
	public void setState(byte[] state, int depth, Object node) {
		next = updater.getSuccessor(state);
		this.depth = depth;
		this.node = node;
	}

	@Override
	public SimulationUpdater cloneForState(byte[] state) {
		SimulationUpdater clone = this.clone();
		clone.setState(state, 0, null);
		return clone;
	}

	@Override
	public DeterministicSimulationUpdater clone() {
		return new DeterministicSimulationUpdater(updater);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove");
	}

}

