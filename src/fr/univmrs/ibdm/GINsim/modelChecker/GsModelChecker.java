package fr.univmrs.ibdm.GINsim.modelChecker;

import java.awt.Component;
import java.io.File;
import java.util.Map;

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
    public boolean[] run(GsRegulatoryMutants mutants, File outputDir);

    /**
     * get an object saying if this test should work for a given mutant
     * @param mutant
     * @return
     */
	public Object getInfo(Object mutant);
	/**
	 * a mutant has been suppressed, cleanup associated info.
	 * @param mutant
	 */
	public void delMutant(Object mutant);
	
	/**
	 * forget results and restore user-entered expected results
	 */
	public void cleanup();
	
	/**
	 * @return a panel to edit the properties of this thing
	 */
	public Component getEditPanel();
	/**
	 * @return the type of model checker to use
	 */
	public String getType();
	
	/**
	 * @return the list of attributes for this test
	 */
	public Map getAttrList();
}
