package org.ginsim.service.tool.modelreversion;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.LogicalModelModifier;
import org.colomoto.logicalmodel.modifier.reverse.ModelReverser;
import org.colomoto.logicalmodel.modifier.reverse.ModelReverserImpl;
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

	public LogicalModelModifier getModelReverser() {
		return new LocalModelReverser();
	}

}

class LocalModelReverser implements LogicalModelModifier {
	public LocalModelReverser() {
	}

	public LogicalModel apply(LogicalModel model) {
		ModelReverserImpl worker = new ModelReverserImpl(model);
		worker.reverse();
		return worker.getModel();
	}
}
