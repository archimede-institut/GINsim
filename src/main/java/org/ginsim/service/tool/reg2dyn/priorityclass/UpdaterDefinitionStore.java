package org.ginsim.service.tool.reg2dyn.priorityclass;

import org.ginsim.service.tool.reg2dyn.updater.UpdaterDefinition;

/**
 * Simple interface to store and retrieve a priority class setup.
 *
 * @author Aurelien Naldi
 */
public interface UpdaterDefinitionStore {

    /**
     * Retrieve the stored definition
     *
     * @return the stored class
     */
  UpdaterDefinition getUpdatingMode();

    /**
     * Store a priority definition
     *
     * @param pcdef
     */
    void setUpdatingMode(UpdaterDefinition pcdef);
}
