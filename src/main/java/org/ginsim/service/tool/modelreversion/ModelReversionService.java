package org.ginsim.service.tool.modelreversion;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.modifier.reverse.ModelReverser;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
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

	public ModelReverser getModelReverser() {
		return new ModelReverser();
	}

}
