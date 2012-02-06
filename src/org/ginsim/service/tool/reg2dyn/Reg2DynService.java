package org.ginsim.service.tool.reg2dyn;

import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateManager;
import org.ginsim.core.graph.regulatorygraph.mutant.MutantListManager;
import org.ginsim.core.service.Service;
import org.ginsim.service.tool.reg2dyn.htg.HTGSimulation;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
public class Reg2DynService implements Service {

    static {
    	if( !ObjectAssociationManager.getInstance().isObjectManagerRegistred( RegulatoryGraph.class, MutantListManager.key)){
    		ObjectAssociationManager.getInstance().registerObjectManager(RegulatoryGraph.class, new MutantListManager());
    	}
        ObjectAssociationManager.getInstance().registerObjectManager( RegulatoryGraph.class, new InitialStateManager());
        ObjectAssociationManager.getInstance().registerObjectManager( RegulatoryGraph.class, new SimulationParametersManager());
    }
    
	public Simulation get( RegulatoryGraph graph, SimulationManager singleSimulationFrame, SimulationParameters currentParameter){
		
		Simulation sim;
		
		if (currentParameter.simulationStrategy == SimulationParameters.STRATEGY_STG) {
			sim = new Simulation( graph, singleSimulationFrame, currentParameter);
		} else {
			sim = new HTGSimulation( graph, singleSimulationFrame, currentParameter);
		}
		
		return sim;
	}

}
