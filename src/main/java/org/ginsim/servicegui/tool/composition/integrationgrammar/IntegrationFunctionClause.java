package org.ginsim.servicegui.tool.composition.integrationgrammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.colomoto.logicalmodel.NodeInfo;

public class IntegrationFunctionClause {

	Map<NodeInfo, Byte> constraints = new HashMap<NodeInfo, Byte>();

	public IntegrationFunctionClause() {

	}

	public void addConstraint(NodeInfo node, byte value) {
		this.constraints.put(node, new Byte(value));
	}

	public boolean hasConstraint(NodeInfo node) {
		return this.constraints.containsKey(node);
	}

	public Byte getConstraintValue(NodeInfo node) {
		return this.constraints.get(node);
	}

	public IntegrationFunctionClause conjunctionWith(
			IntegrationFunctionClause clause) {
		if (clause.isImpossible())
			this.setImpossible();
		else if (!clause.isTautological()) {
			Set<NodeInfo> list = this.getKeySet();
			list.addAll(clause.getKeySet());

			Map<NodeInfo, Byte> novelConstraints = new HashMap<NodeInfo, Byte>();

			for (NodeInfo node : list) {
				if (this.hasConstraint(node) && !clause.hasConstraint(node)) {
					novelConstraints.put(node, this.getConstraintValue(node));
				} else if (!this.hasConstraint(node)
						&& clause.hasConstraint(node)) {
					novelConstraints.put(node, clause.getConstraintValue(node));
				} else if (this.hasConstraint(node)
						&& clause.hasConstraint(node)
						&& this.getConstraintValue(node) == clause
								.getConstraintValue(node)) {
					novelConstraints.put(node, this.getConstraintValue(node));
				} else {
					this.setImpossible();
				}
			}
		}

		return this;
	}

	protected Set<NodeInfo> getKeySet() {
		return this.constraints.keySet();
	}

	public boolean isImpossible() {
		return this.constraints == null;
	}

	public boolean isTautological() {
		return this.constraints != null && this.constraints.isEmpty();
	}

	public void setImpossible() {
		this.constraints = null;
	}

	public void setTautological() {
		this.constraints = new HashMap<NodeInfo, Byte>();

	}

	public byte[] asByteArray(CompositionContext context) {
		int size = context.getLowLevelComponents().size();
		byte[] clause = new byte[size];

		int i = 0;
		for (NodeInfo node : context.getLowLevelComponents()) {
			clause[i] = (this.constraints.containsKey(node) ? this
					.getConstraintValue(node).byteValue() : (byte) -1);
			i++;
		}

		return clause;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof IntegrationFunctionClause) || (object == null))
			return false;
		IntegrationFunctionClause clause = (IntegrationFunctionClause) object;
		return this.constraints.equals(clause.constraints);

	}

	@Override
	public int hashCode() {
		return this.constraints.hashCode();
	}

}
