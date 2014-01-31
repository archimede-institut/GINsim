package org.ginsim.service.tool.modelsimplifier;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.reduction.ModelReducer;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(Service.class)
@Alias("reduction")
@ServiceStatus(EStatus.RELEASED)
public class ModelSimplifierService implements Service {

    public ModelReducer getModelReducer( RegulatoryGraph graph) {
    	return getModelReducer(graph.getModel());
    }

    public ModelReducer getModelReducer( LogicalModel model) {
    	return new ModelReducer(model);
    }
}
