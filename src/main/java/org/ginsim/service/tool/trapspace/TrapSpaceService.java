package org.ginsim.service.tool.trapspace;

import org.colomoto.biolqm.LQMServiceManager;
import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceList;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceSettings;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceTool;
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
public class TrapSpaceService implements Service {

	private static TrapSpaceTool TOOL = LQMServiceManager.getTool(TrapSpaceTool.class);
	
	/**
	 * This constructor should be called by the service manager,
	 * other users will have to get the first instance
	 */
	public TrapSpaceService() {
	}

	public Task<TrapSpaceList> getTask(TrapSpaceSettings settings) {
		return TOOL.getTask(settings);
	}
	
	public TrapSpaceSettings getSettings(LogicalModel model) {
		return TOOL.getSettings(model);
	}
	
	public TrapSpaceList launch(TrapSpaceSettings settings) throws Exception {
		return TOOL.getResult(settings);
	}
}
