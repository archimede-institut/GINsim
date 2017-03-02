package org.ginsim.service.tool.modelreduction;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.modifier.reduction.ModelReducer;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(Service.class)
@Alias("reduction")
@ServiceStatus(EStatus.RELEASED)
public class ModelReductionService implements Service {

    public ModelReducer getModelReducer( RegulatoryGraph graph) {
    	return getModelReducer(graph.getModel());
    }

    public ModelReducer getModelReducer( LogicalModel model) {
    	return new ModelReducer(model);
    }

    /**
     * Rebuild a regulatory graph from a reduced model.
     * The reconstructor will restore node position and metadata based on the content of the original graph.
     *
     * @param reducedModel
     * @param originalGraph
     * @return a new LRG based on the original one
     */
    public ReconstructionTask getReconstructionTask( LogicalModel reducedModel, RegulatoryGraph originalGraph) {
        return new ReconstructionTask( reducedModel, originalGraph);
    }
}
