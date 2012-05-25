package org.ginsim.core.logicalmodel;

import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;

/**
 * 
 * @author Aurelien Naldi
 */
public class SimpleModelModifier implements LogicalModelModifier {

	private Perturbation perturbation = null;

	
	
	public SimpleModelModifier(Perturbation perturbation) {
		this.perturbation = perturbation;
	}



	@Override
	public LogicalModel apply(LogicalModel model) {
		
		if (perturbation != null) {
			// clone (?) and update the model
		}
		return model;
	}

}
