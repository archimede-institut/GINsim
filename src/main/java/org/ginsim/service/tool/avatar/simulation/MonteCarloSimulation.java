package org.ginsim.service.tool.avatar.simulation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.StatefulLogicalModel;
import org.colomoto.biolqm.io.avatar.AvatarUtils;
import org.colomoto.biolqm.tool.simulation.updater.RandomUpdaterWithRates;
import org.ginsim.service.tool.avatar.domain.AbstractStateSet;
import org.ginsim.service.tool.avatar.domain.CompactStateSet;
import org.ginsim.service.tool.avatar.domain.Result;
import org.ginsim.service.tool.avatar.domain.State;
import org.ginsim.service.tool.avatar.service.EnumAlgorithm;
import org.ginsim.service.tool.avatar.utils.AvaMath;
import org.ginsim.service.tool.avatar.utils.ChartGNUPlot;
import org.ginsim.service.tool.avatar.utils.ConfidenceInterval;

import com.panayotis.gnuplot.JavaPlot;

/**
 * Monte Carlo simulation for the analysis of point attractors
 * 
 * @author Rui Henriques
 * @author Pedro T. Monteiro
 * @version 1.0
 */
public class MonteCarloSimulation extends Simulation {

	/** number of iterations */
	public int runs;
	/** maximum depth */
	public int maxSteps;

	private RandomUpdaterWithRates updater;

	/**
	 * Instantiates a Monte Carlo simulation
	 */
	public MonteCarloSimulation() {
	}

	public void addModel(StatefulLogicalModel _model) {
		super.addModel(_model);
		updater = new RandomUpdaterWithRates(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ginsim.service.tool.avatar.simulation.Simulation#runSimulation()
	 */
	public Result runSim() throws IOException {
		Result result = new Result();
		List<Integer> depth = new ArrayList<Integer>();
		int truncated = 0;
		for (int sn = 1, i = 0; sn <= runs; sn++, i = 0) {
			State currentState = SimulationUtils.getRandomState(model, model.getInitialStates(), false);
			if (isGUI)
				publish("Iteration: " + sn + "\n  state=" + currentState);
			// if(model.mustStop()) sn=100000; //last iteration
			// StateSet Q = new StateSet(start);
//			if (!quiet)
//				output("Run:" + sn + "/" + runs);

			while (true) {

				boolean complex = false;
				for (AbstractStateSet trans : result.complexAttractors.values()) {
					if (trans.contains(currentState)) {
						result.incrementComplexAttractor(trans.getKey(), i);
						complex = true;
						if (!quiet)
							output("\tIncrementing attractor!");
						break;
					}
				}
				if (!complex) {
					for (CompactStateSet trans : oracle) {
						if (trans.contains(currentState)) {
							result.add(trans, i);
							complex = true;
							if (!quiet)
								output("\tIncrementing attractor!");
							break;
						}
					}
				}
				if (complex)
					break;

				// State s = Q.getUniformRandomState();
				byte[] s = updater.pickSuccessor(currentState.state);
				result.memory = (int) Math.max(result.memory, Runtime.getRuntime().totalMemory() / 1024);
				if (s == null) {
					if (result.contains(currentState)) {
						result.increment(currentState);
					} else {
						result.add(currentState);
					}
					result.attractorsDepths.get(currentState.key).add(i);
					break;
				}
				currentState = new State(s);
				i++;
				if (maxSteps > 0 && i >= maxSteps) {
					if (!quiet)
						output("\tReached maximum depth: quitting current simulation");
					truncated++;
					break;
				}
			}
			depth.add(i);
		}
		for (State a : result.pointAttractors.values()) {
			double prob = result.attractorsCount.get(a.key) / (double) (runs - truncated);
			String[] bounds = ConfidenceInterval.getConfidenceInterval(runs - truncated, prob).split(",");
			double freeProb = ((double) truncated) / (double) runs;
			try {
				result.setBounds(a.key, (1 - freeProb) * Double.valueOf(bounds[0]), Double.valueOf(bounds[1]));
			} catch (Exception e) {
				result.setBounds(a.key, Double.NaN, Double.NaN);
			}
		}

		// Plots
		if (result.attractorsDepths.size() > 0) {
			// System.out.println("::"+result.attractorsDepths);
			String title = "Depth of attractors";
			// System.out.println(result.attractorsDepths);
			Map<String, String> names = new HashMap<String, String>();
			for (String att : result.attractorsDepths.keySet()) {
				if (result.pointAttractors.containsKey(att))
					names.put(att, AvatarUtils.toString(result.pointAttractors.get(att).state));
				else
					names.put(att, att);
			}
			JavaPlot chart = ChartGNUPlot.getErrorBars(result.attractorsDepths, names, title,
					"Attractors", "Trajectory length");
			BufferedImage img2 = ChartGNUPlot.getImage(chart);
			result.addPlot(title, img2);
			if (!isGUI) {
				String filename = outputDir + "mc_" + model.getName() + "_depths.png";
				ChartGNUPlot.writePNGFile(img2, new File(filename));
			}
		}

		result.strategy = EnumAlgorithm.MONTE_CARLO;
		result.runs = runs;
		result.truncated = truncated;
		result.performed = runs - truncated;
		if (!quiet)
			output("Discovery depth: minimum: " + AvaMath.min(depth) + ", maximum: " + AvaMath.max(depth)
					+ ", average: " + AvaMath.mean(depth));
		if (!quiet)
			output("Simulations asked: " + runs + ", successful: " + truncated);
		result.log = saveOutput();
		return result;
	}

	@Override
	public void dynamicUpdateValues() {
		List<NodeInfo> components = model.getComponents();
		int allStates = 1;
		for (NodeInfo comp : components)
			allStates *= comp.getMax() + 1;
		runs = Math.max(10000, allStates);
		maxSteps = allStates;
		quiet = true;
	}

	@Override
	public String parametersToString() {
		return "\t#Runs=" + runs + "\n\tMax.Depth=" + maxSteps;
	}

	@Override
	public String getName() {
		return "MonteCarlo";
	}
}
