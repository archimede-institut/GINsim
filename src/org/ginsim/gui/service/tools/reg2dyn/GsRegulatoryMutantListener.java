package org.ginsim.gui.service.tools.reg2dyn;

/**
 * be warned when a mutant is added or removed.
 */
public interface GsRegulatoryMutantListener {

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
