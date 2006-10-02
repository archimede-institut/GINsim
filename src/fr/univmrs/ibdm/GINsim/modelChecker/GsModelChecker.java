package fr.univmrs.ibdm.GINsim.modelChecker;

import fr.univmrs.ibdm.GINsim.gui.GsValueList;
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
     * change the name of this test
     * @param name: the new name
     */
    public void setName(String name);
    
    /**
     * @param mutants list of mutants on which to run the test
     * @return the result of the run
     */
    public boolean[] run(GsRegulatoryMutants mutants);

    /**
     * edit the test
     */
    public void edit();
    /**
     * get an object saying if this test should work for a given mutant
     * @param mutant
     * @return
     */
	public GsValueList getInfo(Object mutant);
	/**
	 * a mutant has been suppressed, cleanup associated info.
	 * @param mutant
	 */
	public void cleanupInfo(Object mutant);
}
