package fr.univmrs.tagc.GINsim.modelChecker;

import java.awt.Component;
import java.io.File;
import java.util.Map;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.datastore.NamedObject;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * Model checker implementations should implement this interface.
 */
public interface GsModelChecker extends NamedObject {

    /**
     * @param mutants list of mutants on which to run the test
     * @return the result of the run
     * @throws InterruptedException 
     */
    public void run(GsRegulatoryMutants mutants, GsModelCheckerUI ui, File outputDir) throws InterruptedException;

    /**
     * get an object saying if this test should work for a given mutant
     * @param mutant
     * @return
     */
	public Object getInfo(Object mutant);
	/**
	 * @return the map of all (expected) results.
	 */
	public Map getInfoMap();
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
	public Component getEditPanel(GsGraph graph, StackDialog dialog);
	/**
	 * @return the type of model checker to use
	 */
	public String getType();
	
	/**
	 * @return the list of attributes for this test
	 */
	public Map getAttrList();
	/**
	 * restore the configuration of this test.
	 * @param m_attr a Map of all settings
	 */
	public void setCfg(Map m_attr);
}
