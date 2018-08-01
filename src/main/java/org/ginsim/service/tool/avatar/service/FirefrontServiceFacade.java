package org.ginsim.service.tool.avatar.service;

import java.io.File;

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
import org.ginsim.service.tool.avatar.simulation.FirefrontSimulation;
import org.ginsim.service.tool.avatar.simulation.MDDUtils;
import org.ginsim.service.tool.avatar.simulation.Simulation;
import org.ginsim.service.tool.avatar.utils.AvaOptions;
import org.kohsuke.MetaInfServices;

/**
 * Facade of a Firefront simulation.<br>
 * Instantiates a Firefront simulation from arguments within a command line,
 * provides soundness checks, and outputs statistics and help tips.
 * 
 * @author Pedro T. Monteiro
 * @author Rui Henriques
 */
@MetaInfServices(Service.class)
@Alias(value = "firefrontservice")
@ServiceStatus(EStatus.DEVELOPMENT)
public class FirefrontServiceFacade implements Service {

	/**
	 * Runs a firefront simulation
	 * 
	 * @param sim
	 *            the parameterized firefront simulation
	 * @return the discovered attractors, their reachability, and remaining
	 *         contextual information
	 * @throws Exception
	 *             thrown due to errors while reading and writing files, conflicting
	 *             parameters and unexpected behavior of the simulation
	 */
	public static Result run(FirefrontSimulation sim) throws Exception {
		Result result = sim.runSimulation();// other args
		// if(result.errors()) System.out.println("The magnitude of alpha/beta is too
		// small -- numerical errors might have occurred!");
		// System.out.println("Elapsed time: "+result.time+"ms\n\n");
		// System.out.println(">> Results <<\n"+result.toString());
		// for(State attractor : result.pointAttractors.values())
		// System.out.println("Stable states "+attractor.toString()+" with probability
		// bounds=["+result.attractorsLowerBound.get(attractor.key)+","+result.attractorsUpperBound.get(attractor.key)+"]");
		return result;
	}

	/**
	 * Runs a firefront simulation from a given set of arguments
	 * 
	 * @param args
	 *            textual arguments specifying the input model and the parameters of
	 *            the firefront simulation
	 * @return the discovered attractors, their reachability, and remaining
	 *         contextual information
	 * @throws Exception
	 *             thrown due to errors while reading and writing files, conflicting
	 *             parameters and unexpected behavior of the simulation
	 */
	public static Result run(String[] args) throws Exception {
		FirefrontSimulation sim = (FirefrontSimulation) getSimulation(args);
		return run(sim);
	}

	/**
	 * Creates a firefront simulation from a string command
	 * 
	 * @param args
	 *            command specifying the input model and the parameters of the
	 *            firefront simulation
	 * @return the firefront simulation to be executed
	 * @throws Exception
	 *             thrown due to errors while reading and writing files, conflicting
	 *             parameters and unexpected behavior of the simulation
	 */
	public static Simulation getSimulation(String args) throws Exception {
		return getSimulation(args.split("( --)|=|--"));
	}

	/**
	 * Creates a firefront simulation from a given set of arguments
	 * 
	 * @param args
	 *            textual arguments specifying the input model and the parameters of
	 *            the firefront simulation
	 * @return the firefront simulation to be executed
	 * @throws Exception
	 *             thrown due to errors while reading and writing files, conflicting
	 *             parameters and unexpected behavior of the simulation
	 */
	public static Simulation getSimulation(String[] args) throws Exception {

		FirefrontSimulation sim = new FirefrontSimulation();
		String filename = AvaOptions.getStringValue("input", args);
		// System.out.println("FILENAME:"+filename);
		if (filename != null)
			sim = (FirefrontSimulation) addModel(sim, filename);
		double alpha = AvaOptions.getDoubleValue("alpha", args);
		double beta = AvaOptions.getDoubleValue("beta", args);
		int depth = AvaOptions.getIntValue("maxDepth", args); // -1 if undefined
		sim.maxExpand = AvaOptions.getIntValue("maxExpand", args);
		if (alpha > 0)
			sim.alpha = alpha; // optional
		if (beta > 0)
			sim.beta = beta; // optional
		if (depth > 0)
			sim.maxDepth = depth; // optional
		// if(maxRuns>0) sim.maxRuns=maxRuns; //optional

		sim.quiet = AvaOptions.getBoolValue("quiet", args);
		sim.outputDir = AvaOptions.getStringValue("output-dir", args);
		if (sim.outputDir == null)
			sim.outputDir = new File("").getAbsolutePath();

		// System.out.println("FIREFRONT\n"+"Model: "+model.getName());
		// System.out.println("Initial states:");
		// for(byte[] state : model.getInitialStates()) System.out.println(" "+new
		// State(state));
		// System.out.println("Alpha threshold:"+ alpha + "\nBeta threshold:"+beta);

		return sim;
	}

	public static Simulation addModel(Simulation sim, String filename) throws Exception {
		StatefulLogicalModel model = null;
		if (filename.contains(".avatar")) {
			// AvatarImport avaImport = new AvatarImport(new File(filename));
			// model = avaImport.getModel(); //model.fromNuSMV(filename);
		} else {
			RegulatoryGraph graph = (RegulatoryGraph) GSGraphManager.getInstance().open(filename);
			NamedStatesHandler nstatesHandler = (NamedStatesHandler) ObjectAssociationManager.getInstance()
					.getObject(graph, NamedStatesManager.KEY, true);
			model = new StatefulLogicalModelImpl(graph.getModel(),
					MDDUtils.getStates(nstatesHandler, graph.getNodeInfos()), graph.getGraphName());
		}
		sim.addModel(model);
		return sim;
	}

	/**
	 * Provides help on the parameters of firefront simulations
	 * 
	 * @return description of the parameters of firefront simulations
	 */
	public String getHelp() {
		return "FireFront: Exploring the Dynamics of Asynchronous Logical Models\n\n"
				+ "firefront [options] model_file\n\nOptions:\n\n"
				+ "\t--alpha=NUMBER\tSpecifies the minimum probability required for a state to be explored (default: 10^-5)\n"
				+ "\t--beta=NUMBER\tSpecifies the minimum probability in the firefront to proceed with the global exploration (default: 10^-5)\n"
				+ "\t--max-steps=NUMBER\tSpecifies the maximum number of iterations performed by the program (default: square of the size of the state space)\n"
				+ "\t--output-dir=PATH\tSpecifies the output directory for output files (default: current directory)\n"
				+ "\t--plots\tGenerates an PNG file with a graph of the evolution of the set sizes/probabilities (Firefront, Neglected, Attractors)\n"
				+ "\t--quiet\tSupresses all output except for results and fatal errors.\n";
	}
}
