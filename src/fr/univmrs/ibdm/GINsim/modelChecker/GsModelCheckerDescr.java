package fr.univmrs.ibdm.GINsim.modelChecker;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;

/**
 * Describer Interface for model checkers. Model checker implementations should provide 
 * an implementation of this interface and of <code>GsModelChecker</code>.
 */
public interface GsModelCheckerDescr {

    /**
     * @return the name of this model checker
     */
    public String getName();
    /**
     * test if the necessary tools are available.
     * @return true if it is available
     */
    public boolean isAvailable();
    /**
     * get the message to display when this checker is not available
     * @return the message
     */
    public String getNonAvailableInfo();
    
    /**
     * 
     * @param name
     * @param graph
     * @return a new model checker
     */
    public GsModelChecker createNew(String name, GsRegulatoryGraph graph);    
}
