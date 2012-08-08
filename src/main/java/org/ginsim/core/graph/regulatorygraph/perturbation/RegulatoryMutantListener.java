package org.ginsim.core.graph.regulatorygraph.perturbation;

/**
 * be warned when a mutant is added or removed.
 */
public interface RegulatoryMutantListener {

    /**
     * a mutant has been added.
     * @param mutant
     */
    public void mutantAdded(Object mutant);
    /**
     * a mutant has been removed.
     * @param mutant
     */
    public void mutantRemoved(Object mutant);
}
