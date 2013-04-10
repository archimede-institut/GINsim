package org.ginsim.servicegui.tool.composition.integrationgrammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.colomoto.logicalmodel.NodeInfo;

public class IntegrationFunctionClauseSet {

	private Set<IntegrationFunctionClause> clauseSet = null;

	public IntegrationFunctionClauseSet() {

	}

	public void addClause(IntegrationFunctionClause clause) {
		if (this.clauseSet == null)
			this.clauseSet = new HashSet<IntegrationFunctionClause>();

		this.clauseSet.add(clause);
	}

	public Set<IntegrationFunctionClause> getClauses() {
		return this.clauseSet;
	}

	public IntegrationFunctionClauseSet conjunctionWith(
			IntegrationFunctionClauseSet set) {

		if (set == null)
			return this;

		IntegrationFunctionClauseSet result = new IntegrationFunctionClauseSet();

		if (set.isImpossible() || this.isImpossible())
			result.setImpossible();
		else if (this.isTautological() && set.isTautological())
			result.setTautological();
		else if (this.isTautological())
			for (IntegrationFunctionClause clause : set.getClauses())
				result.addClause(clause);
		else if (set.isTautological())
			for (IntegrationFunctionClause clause : this.getClauses())
				result.addClause(clause);
		else {
			result.setImpossible();
			for (IntegrationFunctionClause clause : set.getClauses()) {
				IntegrationFunctionClauseSet intermediate = this
						.conjunctionWith(clause);
				if (!intermediate.isImpossible())
					for (IntegrationFunctionClause toAdd : intermediate
							.getClauses())
						result = result.disjunctionWith(toAdd);
			}
		}

		return result;

	}

	public IntegrationFunctionClauseSet disjunctionWith(
			IntegrationFunctionClauseSet set) {

		if (set == null)
			return this;

		IntegrationFunctionClauseSet result = new IntegrationFunctionClauseSet();

		if (this.isImpossible() && set.isImpossible())
			result.setImpossible();
		else if (this.isTautological() || set.isTautological())
			result.setTautological();
		else if (this.isImpossible())
			for (IntegrationFunctionClause clause : set.getClauses())
				result.addClause(clause);
		else if (set.isImpossible())
			for (IntegrationFunctionClause clause : this.getClauses())
				result.addClause(clause);
		else {
			result.setImpossible();

			for (IntegrationFunctionClause clause : set.getClauses()) {
				IntegrationFunctionClauseSet intermediate = this
						.disjunctionWith(clause);
				if (!intermediate.isImpossible())
					for (IntegrationFunctionClause toAdd : intermediate
							.getClauses())
						result.disjunctionWith(toAdd);
			}
		}

		return result;

	}

	public IntegrationFunctionClauseSet conjunctionWith(
			IntegrationFunctionClause clause) {

		IntegrationFunctionClauseSet result = new IntegrationFunctionClauseSet();

		if (clause.isImpossible() || this.isImpossible())
			result.setImpossible();
		else if (this.isTautological() && clause.isTautological())
			result.setTautological();
		else if (this.isTautological())
			result.addClause(clause);
		else if (clause.isTautological())
			for (IntegrationFunctionClause c : this.getClauses())
				result.addClause(c);
		else {
			result.setImpossible();
			for (IntegrationFunctionClause c : this.getClauses())
				result = result.disjunctionWith(c.conjunctionWith(clause));
		}

		return result;
	}

	public IntegrationFunctionClauseSet disjunctionWith(
			IntegrationFunctionClause clause) {

		IntegrationFunctionClauseSet result = new IntegrationFunctionClauseSet();

		if (clause.isImpossible() && this.isImpossible())
			result.setImpossible();
		else if (clause.isTautological() || this.isTautological())
			result.setTautological();
		else if (clause.isImpossible())
			for (IntegrationFunctionClause c : this.getClauses())
				result.addClause(c);
		else if (this.isImpossible())
			result.addClause(clause);
		else {
			for (IntegrationFunctionClause c : this.getClauses())
				result.addClause(c);
			result.addClause(clause);
		}

		return result;

	}

	public boolean isImpossible() {
		return this.clauseSet == null;
	}

	public boolean isTautological() {
		return this.clauseSet != null && this.clauseSet.isEmpty();
	}

	public void setImpossible() {
		this.clauseSet = null;
	}

	public void setTautological() {
		this.clauseSet = new HashSet<IntegrationFunctionClause>();
	}

	public IntegrationFunctionClauseSet negate() {
		List<IntegrationFunctionClauseSet> toConjugate = new ArrayList<IntegrationFunctionClauseSet>();

		for (IntegrationFunctionClause clause : this.clauseSet) {
			IntegrationFunctionClauseSet negation = new IntegrationFunctionClauseSet();

			for (NodeInfo node : clause.getKeySet()) {
				for (byte value = 0; value <= node.getMax(); value++)
					if (value != clause.getConstraintValue(node).byteValue()) {
						IntegrationFunctionClause novelClause = new IntegrationFunctionClause();
						novelClause.addConstraint(node, value);
						negation.addClause(novelClause);
					}
			}

			toConjugate.add(negation);
		}

		IntegrationFunctionClauseSet result = new IntegrationFunctionClauseSet();
		result.setTautological();

		for (IntegrationFunctionClauseSet set : toConjugate)
			result.conjunctionWith(set);

		this.clauseSet = result.clauseSet;
		return this;
	}

	public String asString() {
		String out = "\n([[DISJUCTION]]\n";

		if (this.isImpossible())
			out += "IMPOSSIBLE";
		else if (this.isTautological())
			out += "UNIVERSAL";
		else
			for (IntegrationFunctionClause clause : this.getClauses())
				out += clause.asString();

		return out += "\n)\n";
	}

}
