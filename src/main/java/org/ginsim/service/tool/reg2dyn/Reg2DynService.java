package org.ginsim.service.tool.reg2dyn;

import org.colomoto.biolqm.LogicalModel;
import org.ginsim.common.callable.ProgressListener;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.service.tool.reg2dyn.htg.HTGSimulation;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
@Alias("simulation")
@ServiceStatus(EStatus.RELEASED)
public class Reg2DynService implements Service {

	public static final String KEY = "simulation";
	
	public Simulation get( LogicalModel model, ProgressListener<Graph> plist, SimulationParameters currentParameter){
		
		Simulation sim;
		
		if (currentParameter.simulationStrategy == SimulationParameters.STRATEGY_STG) {
			sim = new Simulation( model, plist, currentParameter);
		} else {
			sim = new HTGSimulation( model, plist, currentParameter);
		}
		
		return sim;
	}

}
