package org.ginsim.service.tool.avatar.service;

import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.service.tool.avatar.domain.Result;
import org.mangosdk.spi.ProviderFor;

/**
 * Provider of Avatar services in GINsim
 * @author Pedro Monteiro
 * @author Rui Henriques
 */
@ProviderFor(Service.class)
@ServiceStatus(EStatus.DEVELOPMENT)
public class AvatarService implements Service {

	/** Allowed types of simulations */
	public enum AttractorFinderAlgo { Avatar, Firefront, MonteCarlo };
	
	/**
	 * Runs a simulation from an inputted set of arguments
	 * @param args the arguments for the simulation
	 * @param algorithm the selected type of simulation
	 * @return the discovered attractors, their reachability, and remaining contextual information from the simulation
	 * @throws Exception
	 */
	public static Result run(String[] args, AttractorFinderAlgo algorithm) throws Exception {
		switch(algorithm){
			case Avatar : return AvatarServiceFacade.run(args);
			case Firefront : return FirefrontServiceFacade.run(args);
			default : return MonteCarloServiceFacade.run(args);
		}
	}
}
