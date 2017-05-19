package org.ginsim.service.tool.trapspace;

import org.colomoto.biolqm.LQMServiceManager;
import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceIdentifier;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceList;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceSettings;
import org.colomoto.biolqm.tool.trapspaces.TrapSpaceTool;
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

	public TrapSpaceIdentifier getIdentifier(LogicalModel model, TrapSpaceSettings settings) {
		return TOOL.getIdentifier(model, settings);
	}
	
	public TrapSpaceSettings getSettings() {
		return TOOL.getSettings(null);
	}
	
	public TrapSpaceList launch(LogicalModel model, TrapSpaceSettings settings) throws Exception {
		return TOOL.getSolutions(model, settings);
	}
}
