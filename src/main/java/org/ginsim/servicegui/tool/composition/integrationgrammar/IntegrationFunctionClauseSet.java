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
		if (clause != null && this.clauseSet != null)
			this.clauseSet.add(clause);
	}

	public Set<IntegrationFunctionClause> getClauses() {
		return this.clauseSet;
	}

	public void conjunctionWith(IntegrationFunctionClauseSet set) {
		if (set.isImpossible())
			this.setImpossible();
		else
			for (IntegrationFunctionClause clause : set.getClauses())
				this.conjunctionWith(clause);
	}

	public void disjunctionWith(IntegrationFunctionClauseSet set) {
		if (set.isImpossible())
			return;
		else if (set.isTautological())
			this.setTautological();
		else
			for (IntegrationFunctionClause clause : set.getClauses())
				this.disjunctionWith(clause);

	}

	public IntegrationFunctionClauseSet conjunctionWith(IntegrationFunctionClause clause) {
		if (clause.isImpossible())
			this.setImpossible();
		else if (!this.isImpossible()) {
			Set<IntegrationFunctionClause> novelSet = new HashSet<IntegrationFunctionClause>();
			for (IntegrationFunctionClause oldClause : this.clauseSet) {
				novelSet.add(oldClause.conjunctionWith(clause));
			}
			this.clauseSet = novelSet;
		}
		
		return this;
	}

	public IntegrationFunctionClauseSet disjunctionWith(IntegrationFunctionClause clause) {
		if (clause != null) {
			if (this.isImpossible())
				this.clauseSet = new HashSet<IntegrationFunctionClause>();

			this.clauseSet.add(clause);
		}
		return this;
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
	
	
	public IntegrationFunctionClauseSet negate(){
		List<IntegrationFunctionClauseSet> toConjugate = new ArrayList<IntegrationFunctionClauseSet>();
		
		for (IntegrationFunctionClause clause : this.clauseSet){
			IntegrationFunctionClauseSet negation = new IntegrationFunctionClauseSet();
			
			for (NodeInfo node : clause.getKeySet()){
				for (byte value = 0; value <= node.getMax(); value++)
					if (value != clause.getConstraintValue(node)){
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
	

	
	
}
