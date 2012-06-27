package org.colomoto.logicalmodel.perturbation;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.LogicalModelModifier;
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
		
		if (perturbation == null) {
			return model;
		}
		
		// clone (?) and update the model
		LogicalModel m = model.clone();
		perturbation.apply(m);
		
		return model;
	}

}
