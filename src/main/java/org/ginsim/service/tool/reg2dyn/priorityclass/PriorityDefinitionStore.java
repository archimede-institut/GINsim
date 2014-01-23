package org.ginsim.service.tool.reg2dyn.priorityclass;

/**
 * Simple interface to store and retrieve a priority class setup.
 *
 * @author Aurelien Naldi
 */
public interface PriorityDefinitionStore {

    /**
     * Retrieve the stored definition
     *
     * @return the stored class
     */
  PriorityClassDefinition getPriorityDefinition();

    /**
     * Store a priority definition
     *
     * @param pcdef
     */
    void setPriorityDefinition(PriorityClassDefinition pcdef);
}
