package org.ginsim.service.tool.avatar.service;

import java.io.File;
import org.colomoto.logicalmodel.StatefulLogicalModel;
import org.colomoto.logicalmodel.io.avatar.AvatarImport;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.service.tool.avatar.domain.Result;
import org.ginsim.service.tool.avatar.simulation.AvatarSimulation;
import org.ginsim.service.tool.avatar.simulation.AvatarSimulation.AvatarStrategy;
import org.ginsim.service.tool.avatar.simulation.Simulation;
import org.ginsim.service.tool.avatar.utils.AvaException;
import org.ginsim.service.tool.avatar.utils.AvaOptions;
import org.mangosdk.spi.ProviderFor;

/**
 * Facade of an Avatar simulation.<br>
 * Instantiates an Avatar simulation from arguments within a command line, provides soundness checks, and outputs statistics and help tips.
 * @author Pedro Monteiro
 * @author Rui Henriques
 */
@ProviderFor(Service.class)
@Alias(value="avatarservice")
@ServiceStatus(EStatus.DEVELOPMENT)
public class AvatarServiceFacade implements Service {

	static int DEFAULT_TAU        = 2;
	static int DEFAULT_RUNS       = 100;
	static int DEFAULT_MAX_STEPS  = 1000;
	static int DEFAULT_MIN_CSIZE  = 4;
	static int SMALL_STATE_SPACE  = (int) Math.pow(2,10);
	static int DEFAULT_MAX_PSIZE  = (int) Math.pow(2,15);
	static int MIN_TRANSIENT_SIZE = 32;

	/**
	 * Runs an avatar simulation
	 * @param sim the parameterized avatar simulation
	 * @return the discovered attractors, their reachability, and remaining contextual information
	 * @throws Exception thrown due to errors while reading and writing files, conflicting parameters and unexpected behavior of the simulation
	 */
	public static Result run(AvatarSimulation sim) throws Exception {
		long time = System.currentTimeMillis();
		Result result = sim.runSimulation();
		result.time = (System.currentTimeMillis()-time);
		System.out.println("Elapsed time: "+result.time+"ms\n");
	    System.out.println(">> Results <<\n"+result.toString());
		return result;
    }
	
	/**
	 * Runs an avatar simulation from a given set of arguments
	 * @param args textual arguments specifying the input model and the parameters of the avatar simulation
	 * @return the discovered attractors, their reachability, and remaining contextual information
	 * @throws Exception thrown due to errors while reading and writing files, conflicting parameters and unexpected behavior of the simulation
	 */
	public static Result run(String[] args) throws Exception {
		Simulation sim = getSimulation(args);
		return run((AvatarSimulation)sim);
	}
	
	/**
	 * Creates an avatar simulation from a given set of arguments
	 * @param args textual arguments specifying the input model and the parameters of the avatar simulation
	 * @return the avatar simulation to be executed
	 * @throws Exception thrown due to errors while reading and writing files, conflicting parameters and unexpected behavior of the simulation
	 */
	public static Simulation getSimulation(String[] args) throws Exception {
		String filename = AvaOptions.getStringValue("input",args);
		if(filename==null) throw new AvaException("A model file is required");
		
		AvatarImport avaImport = new AvatarImport(new File(filename));
		StatefulLogicalModel model = avaImport.getModel(); //model.fromNuSMV(filename);
		AvatarSimulation sim = new AvatarSimulation(model); 
		
		sim.runs = AvaOptions.getIntValue("runs",args);
		sim.tauInit = AvaOptions.getIntValue("tau",args);
		sim.maxSteps = AvaOptions.getIntValue("max-steps",args);		
		sim.minCSize = AvaOptions.getIntValue("min-cycle-size",args);
		sim.maxPSize = (int)AvaOptions.getDoubleValue("max-psize",args);
		sim.minTransientSize = AvaOptions.getIntValue("min-transient-size",args);
		if(AvaOptions.getBoolValue("no-extension",args)){
			if(sim.minCSize>2){
		    	System.out.println("Minimum cycle size for rewrite was reset to 2");
		    	sim.minCSize = 2;
		    }
		}
		sim.keepTransients = AvaOptions.getBoolValue("keep-transients",args);
		sim.keepOracle = AvaOptions.getBoolValue("keep-oracle",args);
		sim.plots = AvaOptions.getBoolValue("plots",args);
		sim.quiet = AvaOptions.getBoolValue("quiet",args);
		if(AvaOptions.getBoolValue("matrix",args))
			sim.strategy = AvatarStrategy.MatrixInversion;
		else if(AvaOptions.getBoolValue("approx",args))
			sim.strategy = AvatarStrategy.Approximate;
		else sim.strategy = AvatarStrategy.RandomExit;
		sim.outputDir = AvaOptions.getStringValue("output-dir",args);
		//sim.init = AvaOptions.getStringValue("state",args);
		sim.smallStateSpace = SMALL_STATE_SPACE;
		System.out.println("AVATAR\nModel: "+model.getName()+"\n"+parametersToString(sim));
		return sim;
	}

	/*public static Simulation clone(AvatarSimulation sim) {
		AvatarSimulation clone = new AvatarSimulation(sim.model);
		sim.model = null;
		clone.runs = sim.runs;
		clone.tauInit = sim.tauInit;
		clone.maxSteps = sim.maxSteps;		
		clone.minCSize = sim.minCSize;
		clone.maxPSize = sim.maxPSize;
		clone.minTransientSize = sim.minTransientSize;
		clone.keepTransients = sim.keepTransients;
		clone.keepOracle = sim.keepOracle;
		clone.plots = sim.plots;
		clone.quiet = sim.quiet;
		clone.strategy = sim.strategy;
		clone.outputDir = sim.outputDir;
		clone.smallStateSpace = sim.smallStateSpace;
		return clone;
	}*/

	private static String parametersToString(AvatarSimulation sim) {
		return "Parameters:\n\tRuns="+sim.runs+"\n\tMaximum depth="+ (sim.maxSteps>0 ? sim.maxSteps : "unbounded")
			+ "\n\tTau="+sim.tauInit+"\n\tMinimum size for re-write: "+sim.minCSize+" states\n\t"
			+ "Inflationary mode threshold="+sim.maxPSize+" explicit transitions"
			+ "\n\tKeep transients="+sim.keepTransients+" and oracles="+sim.keepOracle
			+ "\n\tLow exit ratio transient threshold="+sim.minTransientSize+" states\n";
	}
	
	/**
	 * Provides help on the parameters of avatar simulations
	 * @return description of the parameters of avatar simulations
	 */
	public String getHelp(){
	  return "avatar - Stochastic Exploration of the Dynamics of Asynchronous Logical Models\n\n"
		+ "avatar [options] model_file\n\n" + "Options:\n\n"
		+ "\t--runs=NUMBER\t\tSpecifies the number of simulations to perform (default: 100)\n"
	    + "\t--sampling\t\tIndicates whether random initial states should be selected in each run.\n"
		+ "\t\tRandom states are select honoring initial values specified for components, as well as any specified initial oracles\n"
	    + "\t--max-steps=NUMBER\t\tSpecifies the maximum number of exploration steps in each run\n"
	    + "\t--tau=NUMBER\t\tIndicates the initial value of the cycle extension phase parameter (default: 2)\n"
	    + "\t--no-extension\t\tSuppresses the cycle extension phase\n"
		+ "\t--min-cycle-size=NUMBER\t\tSpecifies the minimum number of elements in a cycle required to trigger a graph re-write operation (default: 4)\n"
		+ "\t--max-psize=NUMBER\t\tSpecifies the maximum number of explicit state transition representations spawned\n"
		+ "\t\tby re-write operations before inflationary mode is activated (unbounded expansion of all transient cycles) (default: 2^15)\n"
		+ "\t--expand-all-transients\t\tSpecifies unbounded expansion of all transient cycles (enabled by default for state spaces with 1024 states or less)\n"
		+ "\t--min-transient-size=NUMBER\t\tSpecifies the mininum size of a transient cycle for it to be kept for subsequent simulation runs (default: 32)\n"
	    + "\t--plots\t\tEnables the generation of probability estimation plots and trajectory length distribution\n"
	    + "\t--output-dir=PATH\t\tIndicates the output directory for plot generation\n"
		+ "\t--quiet\t\tSuppresses all output except for results and fatal errors\n\n\n";
	}

	/**
	 * Testing class for running an avatar simulation
	 * @param args textual arguments specifying the input model and the parameters of the avatar simulation
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		run(args);
	}

}
