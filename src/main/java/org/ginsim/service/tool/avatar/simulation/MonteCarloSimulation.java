package org.ginsim.service.tool.avatar.simulation;

import java.util.ArrayList;
import java.util.List;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.StatefulLogicalModel;
import org.colomoto.logicalmodel.tool.simulation.avatar.MonteCarloUpdater;
import org.ginsim.service.tool.avatar.domain.Result;
import org.ginsim.service.tool.avatar.domain.State;
import org.ginsim.service.tool.avatar.utils.AvaMath;
import org.ginsim.service.tool.avatar.utils.ConfidenceInterval;

/**
 * Monte Carlo simulation for the analysis of point attractors
 * 
 * @author Rui Henriques, Pedro Monteiro
 * @version 1.0
 */
public class MonteCarloSimulation extends Simulation {

	/** number of iterations */
	public int runs;
	/** maximum depth */
	public int maxSteps;
	
	private MonteCarloUpdater updater;
	
	/**
	 * Instantiates a Monte Carlo simulation based on a logical model
	 * @param _model a stateful logical model possibly defining a set of initial states and oracles
	 */
	public MonteCarloSimulation(StatefulLogicalModel _model){
		super(_model);
		updater = new MonteCarloUpdater(model);
	}

	/* (non-Javadoc)
	 * @see org.ginsim.service.tool.avatar.simulation.Simulation#runSimulation()
	 */
	public Result runSimulation() {
		Result result = new Result();
		List<Integer> depth = new ArrayList<Integer>();
		int truncated = 0, performed = 0;
		for(int sn=1, i=0; sn<=runs; sn++, performed++, i=0) {
            State currentState = SimulationUtils.getRandomState(model, model.getInitialStates(), false);
			if(!quiet) output("Start:"+currentState);
			if(isGUI) publish(" Iteration:"+sn+"<br>state=["+currentState+"]");
	        //if(model.mustStop()) sn=100000; //last iteration
			//StateSet Q = new StateSet(start);
			if(!quiet) output("Run:"+sn+"/"+runs);

			while(true) {
				//State s = Q.getUniformRandomState();
				byte[] s = updater.pickSuccessor(currentState.state);
				if(s==null){
					if(result.contains(currentState)) result.increment(currentState);
					else result.add(currentState);
    	            result.attractorsDepths.get(currentState.key).add(i);
					break;
				}
				currentState = new State(s);
				i++;
				if(maxSteps>0 && i>=maxSteps){
	                output("Reached maximum depth: quitting current simulation");
					truncated++;
					break;
                }
			}
			depth.add(i);
		}
	    for(State a : result.pointAttractors.values()){
	    	double prob = result.attractorsCount.get(a.key)/(double)(runs-truncated);
	    	String[] bounds = ConfidenceInterval.getConfidenceInterval(runs-truncated,prob).split(",");
	    	try{
	    		result.setBounds(a.key, Double.valueOf(bounds[0]), Double.valueOf(bounds[1]));
	    	} catch(Exception e){
	    		result.setBounds(a.key, Double.NaN, Double.NaN);
	    	}
	    }
	    
	    result.strategy = "MonteCarlo";
	    result.runs = runs;
	    result.truncated = truncated;
	    result.performed = performed;
		output("Discovery depth: minimum: "+AvaMath.min(depth)+", maximum: "+AvaMath.max(depth)+", average: "+AvaMath.mean(depth));
	    output("Simulations asked: "+runs+", successful: "+truncated);
	    result.log = saveOutput();	    
		return result;
	}

	@Override
	public void dynamicUpdateValues() {
		List<NodeInfo> components =model.getNodeOrder();
		int allStates = 1;
		for(NodeInfo comp : components) allStates *= comp.getMax()+1; 
		runs=Math.max(10000, allStates);
		maxSteps=allStates;
		quiet=true;
	}
}
