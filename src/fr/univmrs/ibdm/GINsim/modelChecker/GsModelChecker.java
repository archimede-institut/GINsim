package fr.univmrs.ibdm.GINsim.modelChecker;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;

/**
 * Model checker implementations should implement this interface.
 */
public interface GsModelChecker {

    /**
     * @return the name of this test
     */
    public String getName();
    
    /**
     * @param mutants list of mutants on which to run the test
     * @return the result of the run
     */
    public boolean[] run(GsRegulatoryMutants mutants);

    /**
     * edit the test
     */
    public void edit();
}
