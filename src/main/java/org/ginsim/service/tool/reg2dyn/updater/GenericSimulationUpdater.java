package org.ginsim.service.tool.reg2dyn.updater;

import org.colomoto.biolqm.tool.simulation.multiplesuccessor.MultipleSuccessorsUpdater;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Wrap LogicalModel's updater API into GINsim.
 * This updater lets a helper provide the list of successors and only deals with
 * the iteration part.
 *
 * @author Aurelien Naldi
 */
public class GenericSimulationUpdater implements SimulationUpdater {

	private final MultipleSuccessorsUpdater updater;
	private List<byte[]> successors = null;
	private int k;
	private int n;
	private boolean multiple;
	
	int depth;
	Object node;

	public GenericSimulationUpdater(MultipleSuccessorsUpdater updater) {
		this.updater = updater;
	}

	@Override
	public boolean hasNext() {
		return k < n;
	}

	@Override
	public Object next() {
		byte[] next = nextState();
		multiple = false;
		SimulationQueuedState ret = new SimulationQueuedState(next, depth+1, node, multiple);
		return ret;
	}

	@Override
	public byte[] nextState() {
		if (hasNext()) {
			byte[] next = successors.get(k);
			k++;
			return next;
		}
		throw new NoSuchElementException("No more successor states");
	}

	@Override
	public void setState(byte[] state, int depth, Object node) {
		successors = updater.getSuccessors(state);
		this.depth = depth;
		this.node = node;
		k = 0;
		if (successors == null) {
			n = 0;
		} else {
			n = successors.size();
		}
	}

	@Override
	public SimulationUpdater cloneForState(byte[] state) {
		SimulationUpdater clone = this.clone();
		clone.setState(state, 0, null);
		return clone;
	}

	@Override
	public GenericSimulationUpdater clone() {
		return new GenericSimulationUpdater(updater);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove");
	}
}

