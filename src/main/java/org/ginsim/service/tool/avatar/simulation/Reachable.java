package org.ginsim.service.tool.avatar.simulation;

import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.StatefulLogicalModel;
import org.colomoto.logicalmodel.tool.simulation.avatar.FirefrontUpdater;
import org.ginsim.service.tool.avatar.domain.State;
import org.ginsim.service.tool.avatar.domain.StateSet;

/**
 * Facilities to compute the set of reachable states from a portion of the state space of a given discrete system
 * @author Rui Henriques
 * @version 1.0
 */
public final class Reachable {

	/**
	 * Method to find all the reachable states from a given portion of the state space
	 * @param model a stateful logical model possibly defining a set of initial states
	 * @return the set of reachable states
	 */
	public static StateSet computeReachableStates(StatefulLogicalModel model) {
		FirefrontUpdater updater = new FirefrontUpdater(model); 
		int totalstates=1;
		for(NodeInfo comp : model.getNodeOrder()) totalstates *= comp.getMax()+1;
		
		StateSet attractors = new StateSet();
		StateSet states = new StateSet(updater.getSuccessors(model.getInitialStates()));
		System.out.println("States: "+states);

		//int k=0;
		for(State next : states.getStates()) {
			//if(k++==1000) break;
			System.out.println("Next: "+next);
			if(attractors.contains(next)) continue;
			StateSet successors = new StateSet(updater.getSuccessors(next.state));
			System.out.println("Successors: "+successors);
			if(successors.contains(next)) attractors.add(next);
			int originalSize = states.size();
			states.addAll(successors);
			System.out.println("Retrieved "+(states.size()-originalSize)+" successors to add to a queue of "+states.size()+" states!");
		}
		System.out.println("Reached "+states.size()+" states from a total of "+totalstates+" states!\nPoint attractors = "+attractors);
		return states;
	}
}
