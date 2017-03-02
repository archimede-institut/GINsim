package org.ginsim.service.tool.avatar.service;

import org.colomoto.biolqm.StatefulLogicalModel;
import org.colomoto.biolqm.StatefulLogicalModelImpl;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesHandler;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesManager;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.service.tool.avatar.domain.Result;
import org.ginsim.service.tool.avatar.simulation.MDDUtils;
import org.ginsim.service.tool.avatar.simulation.MonteCarloSimulation;
import org.ginsim.service.tool.avatar.simulation.Simulation;
import org.ginsim.service.tool.avatar.utils.AvaException;
import org.ginsim.service.tool.avatar.utils.AvaOptions;
import org.mangosdk.spi.ProviderFor;


/**
 * Facade of a Monte Carlo simulation.<br>
 * Instantiates a Monte Carlo simulation from arguments within a command line, provides soundness checks, and outputs statistics and help tips.
 * @author Pedro Monteiro
 * @author Rui Henriques
 */
@ProviderFor(Service.class)
@Alias(value="montecarloservice")
@ServiceStatus(EStatus.DEVELOPMENT)
public class MonteCarloServiceFacade implements Service {

	
	/**
	 * Runs a Monte Carlo simulation from a given set of arguments
	 * @param args textual arguments specifying the input model and the parameters of the Monte Carlo simulation
	 * @return the discovered attractors, their reachability, and remaining contextual information
	 * @throws Exception thrown due to errors while reading and writing files, conflicting parameters and unexpected behavior of the simulation
	 */
	public static Result run(String[] args) throws Exception{
		return run((MonteCarloSimulation)getSimulation(args));
	}
	public static Result run(MonteCarloSimulation sim) throws Exception{
		Result results = sim.runSimulation();
		return results;
	}

	/**
	 * Creates a montecarlo simulation from a string command 
	 * @param args command specifying the input model and the parameters of the montecarlo simulation	 
	 * @return the montecarlo simulation to be executed
	 * @throws Exception thrown due to errors while reading and writing files, conflicting parameters and unexpected behavior of the simulation
	 */
	public static Simulation getSimulation(String args) throws Exception {
		return getSimulation(args.split("( --)|=|--"));
	}
	
	/**
	 * Creates a Monte Carlo simulation from a given set of arguments
	 * @param args textual arguments specifying the input model and the parameters of the Monte Carlo simulation
	 * @return the Monte Carlo simulation to be executed
	 * @throws Exception thrown due to errors while reading and writing files, conflicting parameters and unexpected behavior of the simulation
	 */
	public static Simulation getSimulation(String[] args) throws Exception {

		/** A: resolve inputted parameters **/

		MonteCarloSimulation sim = new MonteCarloSimulation();
		//if(args.length<2) throw new AvaException("Usage: model runs [random_init [max_steps]]");
		String filename = AvaOptions.getStringValue("input",args);
		if(filename!=null) sim = (MonteCarloSimulation) addModel(sim,filename);
		
		sim.maxSteps = AvaOptions.getIntValue("maxDepth",args); 
		sim.runs = AvaOptions.getIntValue("runs",args);
		if(sim.runs<0) throw new AvaException("The number of runs is required");
		
		/** B: run montecarlo simulation **/

		//System.out.println("MonteCarlo\n"+"Model: "+model.getName());
		//System.out.println("Initial state(s):"+sim.istates+"\n");
		//System.out.println("Number of simulations = "+sim.runs);
		return sim;
	}

	public static Simulation addModel(Simulation sim, String filename) throws Exception{
		StatefulLogicalModel model = null;
		if(filename.contains(".avatar")){
			//AvatarImport avaImport = new AvatarImport(new File(filename));
			//model = avaImport.getModel(); //model.fromNuSMV(filename);
		} else {
			RegulatoryGraph graph = (RegulatoryGraph)GSGraphManager.getInstance().open(filename);
	        NamedStatesHandler nstatesHandler = (NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(graph, NamedStatesManager.KEY, true);
			model = new StatefulLogicalModelImpl(graph.getModel(),MDDUtils.getStates(nstatesHandler,graph.getNodeInfos()));
		}
		sim.addModel(model);
		return sim;
	}

	/**
	 * Provides help on the parameters of Monte Carlo simulations
	 * @return description of the parameters of Monte Carlo simulations
	 */
	public String getHelp() {
		  return "avatar - Stochastic Exploration of the Dynamics of Asynchronous Logical Models\n\n"
			+ "avatar [options] model_file\n\n" + "Options:\n\n"
			+ "\t--runs=NUMBER\t\tSpecifies the number of simulations to perform (default: 100)\n"
		    + "\t--init\t\tIndicates whether random initial states should be selected in each run. Possible values: fixed, random, sampling.\n"
		    + "\t--sampling\t\tIndicates the number of seed in case --init=sampling.\n"
		    + "\t--max-steps=NUMBER\t\tSpecifies the maximum number of exploration steps in each run\n"
		    + "\t--plots\t\tEnables the generation of probability estimation plots and trajectory length distribution\n"
		    + "\t--output-dir=PATH\t\tIndicates the output directory for plot generation\n"
			+ "\t--quiet\t\tSuppresses all output except for results and fatal errors\n\n\n";
	}

	/**
	 * Testing class for running a Monte Carlo simulation
	 * @param args textual arguments specifying the input model and the parameters of the avatar simulation
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		run(args);
	}
}
