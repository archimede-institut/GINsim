package org.ginsim.service.tool.avatar.simulation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.tool.simulation.updater.AsynchronousUpdater;
import org.colomoto.biolqm.tool.simulation.updater.SequentialUpdater;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.service.tool.avatar.domain.AbstractStateSet;
import org.ginsim.service.tool.avatar.domain.CompactStateSet;
import org.ginsim.service.tool.avatar.domain.MDDStateSet;
import org.ginsim.service.tool.avatar.domain.State;
import org.ginsim.service.tool.avatar.domain.StateSet;

/**
 * Facilities associated with simulation services. Including:<br>
 * 1) Random generation of an initial state from a given pattern or state-set.<br>
 * 2) Inference of state patterns from a given set of states.<br>
 * 3) Generating a drawable graph from a complex attractor.<br> 
 * 
 * @author Rui Henriques
 * @version 1.0
 */
public final class SimulationUtils {

	/**
	 * Selects an initial state from a given pattern or state-set
	 * @param model the logical model to possibly check whether the selected state has successors
	 * @param states the set of possible states
	 * @param hasSuccessors true to impose that the selected initial state has successors 
	 * @return
	 */
	public static State getRandomState(LogicalModel model, List<byte[]> states, boolean hasSuccessors) {
		SequentialUpdater updater = new SequentialUpdater(model);
		List<NodeInfo> nodes = model.getNodeOrder();
		Random r = new Random();

		/** A: select state conditions **/
		byte[] state = states.get(r.nextInt(states.size()));
		
		/** B: generate state satisfying conditions **/
		byte[] newstate = new byte[state.length];
		if(hasSuccessors){
			do {
				for(int i=0, l=nodes.size(); i<l; i++) 
					if(state[i]==-1) newstate[i]=(byte)r.nextInt(nodes.get(i).getMax()+1);
					else newstate[i]=state[i];
			} while(updater.getSuccessor(state)==null);
		} else {
			for(int i=0, l=nodes.size(); i<l; i++) 
				if(state[i]==-1) newstate[i]=(byte)r.nextInt(nodes.get(i).getMax()+1);
				else newstate[i]=state[i];
		}
		return new State(newstate);
	}
	
	/**
	 * Generates a drawable dynamic graph from a complex attractor
	 * @param graph the type of graph to be generated
	 * @param attractor the complex attractor
	 * @param model the logical model to provide contextual information
	 * @return the dynamic graph mapped from the given complex attractor
	 */
	public static DynamicGraph getGraphFromAttractor(DynamicGraph graph, AbstractStateSet attractor, LogicalModel model) {
        AsynchronousUpdater updater = new AsynchronousUpdater(model);
        Map<String,DynamicNode> nodes = new HashMap<String,DynamicNode>();
        
        if(attractor instanceof StateSet){
	        for(State s : ((StateSet)attractor).getStates()){
				DynamicNode node = new DynamicNode(s.state);
				graph.addNode(node);
				nodes.put(s.key, node);
	        }
	        for(State s1 : ((StateSet)attractor).getStates()){
	        	for(byte[] s2 : updater.getSuccessors(s1.state)){
        			graph.addEdge(nodes.get(s1.key),nodes.get(new State(s2).key),false); 
	        	}	        	
	        	/*StateSet successors = new StateSet(updater.getSuccessors(s1.state));
	        	for(State s2 : ((StateSet)attractor).getStates()){
	        		if(successors.contains(s2)) 
	        			graph.addEdge(nodes.get(s1.key),nodes.get(s2.key),false); 
	        	}*/
	        }
        } else if(attractor instanceof MDDStateSet){
		        for(State s : ((MDDStateSet)attractor).getStates()){
					DynamicNode node = new DynamicNode(s.state);
					graph.addNode(node);
					nodes.put(s.key, node);
		        }
		        for(State s1 : ((MDDStateSet)attractor).getStates()){
		        	for(byte[] s2 : updater.getSuccessors(s1.state))
	        			graph.addEdge(nodes.get(s1.key),nodes.get(new State(s2).key),false); 
		        }
        } else {
	        for(byte[] s : ((CompactStateSet)attractor).getStates()){
				DynamicNode node = new DynamicNode(s); 
				graph.addNode(node);
	        }
        }
		return graph;
	}

	/**
	 * Generates a drawable reduced graph from a complex attractor
	 * @param graph the type of graph to be generated
	 * @param attractor the complex attractor
	 * @param model the logical model to provide contextual information
	 * @return the reduced graph mapped from the given complex attractor
	 */
	public static ReducedGraph getGraphFromAttractor(ReducedGraph graph, StateSet attractor, LogicalModel model) {
		AsynchronousUpdater updater = new AsynchronousUpdater(model);
        Map<String,NodeReducedData> nodes = new HashMap<String,NodeReducedData>();
        for(State s : attractor.getStates()){
        	NodeReducedData node = new NodeReducedData(s.key,Arrays.asList(s.state)); 
			graph.addNode(node);
			nodes.put(s.key, node);
        }
        for(State s1 : attractor.getStates()){
        	StateSet successors = new StateSet(updater.getSuccessors(s1.state));
        	for(State s2 : attractor.getStates()){
        		if(successors.contains(s2)) 
        			graph.addEdge(nodes.get(s1.key),nodes.get(s2.key)); 
        	}
        }
		return graph;
	}

	public static boolean isSingleState(byte[] vec) {
		for(byte v : vec) if(v<0) return false;
		return true;
	}

	/*private static void reduceStates() {
		List<NodeInfo> order = new ArrayList<NodeInfo>();
		for(int i=0;i<30;i++) order.add(new NodeInfo("N"+i,(byte)1));
		long[] factors = new long[order.size()];
		for(int i=0, l=order.size(); i<l; i++) factors[i]=(long)Math.pow(2,i);
		Dictionary.codingShortStates(factors);
		
		List<byte[]> states = Arrays.asList(
						new byte[]{1,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,1,1},
						new byte[]{1,0,1,1,0,1,1,1,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,0,1},
						new byte[]{1,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,1,0},
						new byte[]{1,0,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,1,1},
						new byte[]{1,0,1,1,0,1,1,1,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,1,1},
						new byte[]{1,0,1,1,0,1,1,0,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,0,0},
						new byte[]{1,0,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,0,1},
						new byte[]{1,0,1,1,0,1,1,1,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,1,0},
						new byte[]{1,0,1,1,0,1,1,0,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,1,1},
						new byte[]{1,0,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,0,0},
						new byte[]{1,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,0,0},
						new byte[]{1,0,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,1,0},
						new byte[]{1,0,1,1,0,1,1,0,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,0,1},
						new byte[]{1,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,0,1},
						new byte[]{1,0,1,1,0,1,1,0,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,1,0},
						new byte[]{1,0,1,1,0,1,1,1,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,1,0,0,0,0});
		StateSet attractor = new StateSet(states);
		List<byte[]> reduced = getStatePatterns(order,attractor);		
		
		/*String dir = "C:\\Users\\Rui\\Documents\\00 Avatar\\Avatar Material\\table-models\\";
		String model = "random_001_v010_k2.avatar";
		List<String> options = Arrays.asList(
				"--runs=1000 --state=init --max-steps=10000 --tau=4 --min-cycle-size=3 "
				+ "--keep-transients --keep-oracle --approx "
				+ "--max-psize="+Math.pow(2,14)+" --min-transient-size=100 "
				+ "--plots --quiet --output-dir=/output --input="+dir);

		for(String option : options){
			Simulation sim = AvatarServiceFacade.getSimulation((option+model).split("( --)|=|--")); 
			Result result = AvatarServiceFacade.run((AvatarSimulation)sim);
			for(AbstractStateSet complex : result.complexAttractors.values()){
				System.out.println("Complex attractor: "+complex.toString());
				if(complex instanceof StateSet){
					List<byte[]> reduced = getStatePatterns(sim.model,(StateSet)complex);
				}
			}
		}
	}*/

}
