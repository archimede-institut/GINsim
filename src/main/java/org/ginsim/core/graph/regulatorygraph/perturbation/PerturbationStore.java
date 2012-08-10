package org.ginsim.core.graph.regulatorygraph.perturbation;

/**
 * Simple store for perturbation.
 * 
 * @author Aurelien Naldi
 */
public class PerturbationStore implements PerturbationHolder {

	Perturbation perturbation;
	
	@Override
	public Perturbation getPerturbation() {
		return perturbation;
	}

	@Override
	public void setPerturbation(Perturbation perturbation) {
		this.perturbation = perturbation;
	}

}
