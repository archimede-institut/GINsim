package org.ginsim.servicegui.tool.composition.integrationgrammar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.servicegui.tool.composition.CompositionSpecificationDialog;

public class CompositionContextImpl implements CompositionContext {
	private CompositionSpecificationDialog dialog = null;
	private List<NodeInfo> nodeOrder = null;

	public CompositionContextImpl(CompositionSpecificationDialog dialog,
			List<NodeInfo> nodeOrder) {
		this.dialog = dialog;
		this.nodeOrder = nodeOrder;
	}

	@Override
	public List<NodeInfo> getLowLevelComponents() {
		return nodeOrder;
	}

	@Override
	public Set<Integer> getNeighbourIndices(int instance, int distance) {
		Set<Integer> results = this.getAllNeighbourIndicesCloserThan(instance,
				distance);
		Set<Integer> frontier = this.getAllNeighbourIndicesCloserThan(instance,
				distance - 1);

		results.removeAll(frontier);
		return results;
	}

	private Set<Integer> getAllNeighbourIndicesCloserThan(int instance,
			int distance) {
		Set<Integer> results = new HashSet<Integer>();

		if (distance <= 0) {
			results.add(new Integer(instance));
			return results;
		} else if (distance == 1) {
			for (int n = 1; n <= dialog.getNumberInstances(); n++) {
				if (dialog.areNeighbours(instance, n)) {
					results.add(new Integer(n));
				}
			}
			return results;
		} else {
			Set<Integer> frontier = this.getAllNeighbourIndicesCloserThan(
					instance, distance - 1);
			for (Integer v : frontier) {
				for (int n = 1; n <= dialog.getNumberInstances(); n++)
					if (dialog.areNeighbours(v.intValue(), n))
						results.add(n);
			}
			return results;
		}
	}

	@Override
	public NodeInfo getLowLevelComponentFromName(String componentName,
			int instance) {
		// TODO Not implemented; requires composed Model
		return null;
	}

}
