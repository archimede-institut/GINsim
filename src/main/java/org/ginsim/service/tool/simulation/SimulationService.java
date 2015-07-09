package org.ginsim.service.tool.simulation;

import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

/**
 * Simple simulation service
 *
 * @author Aurelien Naldi
 */
@ProviderFor( Service.class)
@ServiceStatus(EStatus.DEVELOPMENT)
public class SimulationService implements Service {

    // TODO: setup a nice simulation API

}
