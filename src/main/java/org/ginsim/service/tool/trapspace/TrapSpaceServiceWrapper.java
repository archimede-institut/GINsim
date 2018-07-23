package org.ginsim.service.tool.trapspace;

import org.colomoto.biolqm.LQMServiceManager;
import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceList;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceService;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceTask;
import org.colomoto.common.task.Task;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;


/**
 * Search for trap-spaces in Regulatory graphs.
 */
@ProviderFor( Service.class)
@Alias("trapspace")
@ServiceStatus(EStatus.RELEASED)
public class TrapSpaceServiceWrapper implements Service {

	private final static TrapSpaceService BACKEND = LQMServiceManager.get(TrapSpaceService.class);
	
	/**
	 * This constructor should be called by the service manager,
	 * other users will have to get the first instance
	 */
	public TrapSpaceServiceWrapper() {
	}

	public TrapSpaceTask getTask(LogicalModel model) {
		TrapSpaceTask task = BACKEND.getTask(model);
		task.bdd = true;
		return task;
	}
}
