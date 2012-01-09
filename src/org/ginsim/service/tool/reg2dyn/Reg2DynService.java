package org.ginsim.service.tool.reg2dyn;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Service;
import org.ginsim.service.tool.reg2dyn.htg.HTGSimulation;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
public class Reg2DynService implements Service {

	public Simulation run( RegulatoryGraph graph, SimulationManager singleSimulationFrame, SimulationParameters currentParameter){
		
		Simulation sim;
		
		if (currentParameter.simulationStrategy == SimulationParameters.STRATEGY_STG) {
			sim = new Simulation( graph, singleSimulationFrame, currentParameter);
		} else {
			sim = new HTGSimulation( graph, singleSimulationFrame, currentParameter);
		}
		
		return sim;
	}

}
