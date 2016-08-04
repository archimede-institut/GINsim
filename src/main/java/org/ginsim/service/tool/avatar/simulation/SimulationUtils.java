package org.ginsim.service.tool.avatar.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.io.avatar.AvatarUtils;
import org.colomoto.logicalmodel.tool.simulation.avatar.AvatarUpdater;
import org.colomoto.logicalmodel.tool.simulation.avatar.FirefrontUpdater;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDManagerFactory;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.MDDVariableFactory;
import org.colomoto.mddlib.PathSearcher;
import org.colomoto.mddlib.internal.MDDStore;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.service.tool.avatar.domain.AbstractStateSet;
import org.ginsim.service.tool.avatar.domain.CompactStateSet;
import org.ginsim.service.tool.avatar.domain.Dictionary;
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
		AvatarUpdater updater = new AvatarUpdater(model);
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
        FirefrontUpdater updater = new FirefrontUpdater(model);
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
        FirefrontUpdater updater = new FirefrontUpdater(model);
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

	/**
	 * Maps an oracle (set of state patterns) into a set of specific states
	 * @param oracle the oracle (set of state patterns) to be unfolded
	 * @param nodes knowledge of the states of the components within a state pattern
	 * @return the set of specific states associated with the given state patterns
	 */
	public static StateSet toStateSet(List<byte[]> oracle, List<NodeInfo> nodes) {
		List<byte[]> all = new ArrayList<byte[]>();
		while(!oracle.isEmpty()){
			byte[] state = oracle.remove(0);
			int index = firstMultiple(state);
			if(index==-1) all.add(state);
			else {
				for(int i=0, l=nodes.get(index).getMax(); i<=l; i++){ 
					byte[] newstate = Arrays.copyOf(state,state.length);
					newstate[index] = (byte)i;
					oracle.add(newstate);
				}
			}
		}
		return new StateSet(all);
	}
	
	private static int firstMultiple(byte[] state) {
		for(int i=0, l=state.length; i<l; i++) if(state[i]==-1) return i;
		return -1;
	}

	/**
	 * Infers a set of state patterns from a given set of states
	 * @param model the logical model to facilitate the generation of the MDD for the extraction of pattern
	 * @param stateset the set of states to be compacted
	 * @return the set of state patterns associated with the given states 
	 */
	public static List<byte[]> getStatePatterns(List<NodeInfo> vars, AbstractStateSet stateset) {
		MDDVariableFactory mvf = new MDDVariableFactory();
		for(NodeInfo ni : vars) mvf.add(ni,(byte)(ni.getMax()+1));
		List<Set<Integer>> statesPerComponent = new ArrayList<Set<Integer>>();
		for(int i=0, l=vars.size(); i<l; i++) statesPerComponent.add(new HashSet<Integer>());
		MDDManager ddmanager = MDDManagerFactory.getManager(mvf,10);

		List<byte[]> states = new ArrayList<byte[]>();
		if(stateset instanceof StateSet)
			for(State s : ((StateSet)stateset).getStates()) states.add(s.state);
		else if(stateset instanceof MDDStateSet)
			for(State s : ((MDDStateSet)stateset).getStates()) states.add(s.state);
		int mdd = ddmanager.nodeFromStates(states,1);
		
		PathSearcher searcher = new PathSearcher(ddmanager,1);
		int[] path = searcher.setNode(mdd);
		List<byte[]> result = new ArrayList<byte[]>();
		for (int p : searcher) result.add(AvatarUtils.toByteArray(path));
		for(byte[] s : result) System.out.println(">>"+AvatarUtils.toString(s));
		return result;
	}

	//mdd = getMDD(states.get(1),ddmanager);
	/*for(State s : stateset.getStates()){
		if(mdd==0) mdd=getMDD(s.state,ddmanager);
		else mdd = MDDBaseOperators.OR.combine(
				ddmanager, mdd, getMDD(s.state,ddmanager));
		for(int i=0, l=vars.size(); i<l; i++) statesPerComponent.get(i).add((int)s.state[i]);
	}*/

	private static int getMDD(byte[] state, MDDManager ddmanager) {
		return ddmanager.nodeFromState(state,1);
		/*int mdd=1, i=0;
		for(MDDVariable var : ddmanager.getAllVariables()){
			byte s=state[i++]; 
			if(s==-1) continue;
			if(var.nbval>2){
				int[] children = AvatarUtils.getChildrenWithSingleNode(var.nbval, s);
				mdd = MDDBaseOperators.AND.combine(ddmanager,mdd,var.getNode(children));
			} else { 
				mdd = MDDBaseOperators.AND.combine(ddmanager,mdd,s==0 ? var.getNode(1,0) : var.getNode(0,1));
			}
		}
		return mdd;*/
	}
	
	/**
	 * Illustrative class to test the simulation utils
	 * @param args to be ignored
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		//reduceStates();
		removeMDDPaths();
	}

	private static void removeMDDPaths() {
		List<byte[]> additions = Arrays.asList(
				new byte[]{1,0,1,0},
				new byte[]{1,0,0,1},
				new byte[]{1,1,1,1});
		byte[] removal = new byte[]{1,0,0,1};
		
		MDDVariableFactory mvf = new MDDVariableFactory();
		List<NodeInfo> vars = Arrays.asList(
				new NodeInfo("A",(byte)1),
				new NodeInfo("B",(byte)1),
				new NodeInfo("C",(byte)1),
				new NodeInfo("D",(byte)1));
		for(NodeInfo ni : vars) mvf.add(ni,(byte)(ni.getMax()+1));
		MDDManager mdd = MDDManagerFactory.getManager(mvf,500);
		
		int mddNode1 = mdd.nodeFromStates(additions,499);
		System.out.println(mdd.dumpMDD(mddNode1));
		int mddNode2 = mdd.not(mdd.nodeFromState(removal,1));
		System.out.println(mdd.dumpMDD(mddNode2));
		int mddNode = MDDBaseOperators.AND.combine(mdd,mddNode1,mddNode2);
		System.out.println(mdd.dumpMDD(mddNode));
		
		/*List<Integer> nodes = new ArrayList<Integer>();
		nodes.add(mddNode);
		int node = mddNode;
		while(!mdd.isleaf(node)){
			MDDVariable curVar = mdd.getNodeVariable(node);
			int index =	mdd.getVariableIndex(curVar);
			node = mdd.getChild(node,removal[index]);
			nodes.add(node);
		}*/
		
		/*System.out.println("nodes:"+nodes);
		for(int i=nodes.size()-2; i>=0; i--){
			if(mdd.isleaf(nodes.get(i+1))){
				System.out.println("HERE!");
				int removenode = mdd.use(nodes.get(i-1));
				System.out.println(mdd.dumpMDD(removenode));
			} else break;
		}*/
	}

	private static void reduceStates() {
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
		}*/
	}

}
