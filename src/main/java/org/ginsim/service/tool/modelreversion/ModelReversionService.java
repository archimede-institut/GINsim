package org.ginsim.service.tool.modelreversion;

import org.colomoto.biolqm.LQMServiceManager;
import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.modifier.ModelModifier;
import org.colomoto.biolqm.modifier.reverse.ReverseService;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

/**
 * Model reversion service.
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("reversion")
@ServiceStatus(EStatus.DEVELOPMENT)
public class ModelReversionService implements Service {

	private final ReverseService reverserService = LQMServiceManager.get(ReverseService.class);
	
	public ModelModifier getModelReverser(LogicalModel model) {
		return reverserService.getModifier(model);
	}

}
