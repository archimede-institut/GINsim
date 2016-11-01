package org.ginsim.service.tool.stableregions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.ginsim.johnsonCycles.ElementaryCyclesSearch;

public class PNGraph {
	private List<List<Integer>> adj = new LinkedList<List<Integer>>();
	private int size; //num of nodes
	private HashMap<String, Integer> nodesMap = new HashMap<String, Integer>(); //(name of the node, index of the node in the adj list)
	private HashMap<Integer, String> nodesNames = new HashMap<Integer, String>();
	private Set<String> transitions = new HashSet<String>();
	private Set<String> places = new HashSet<String>();
	private HashMap<String, List<String>> transitionsInputs = new HashMap<String, List<String>>();
	private HashMap<String, String> transitionsOutputs = new HashMap<String, String>();
	private HashMap<String, String> complementaryNodes = new HashMap<String, String>();
	private HashMap<String, Boolean> isSourceNode = new HashMap<String, Boolean>();
	
	public PNGraph(){
		size = 0;
	}
	
//	public void addNode(){
//		List<String> list = new LinkedList<String>();
//		this.adj.add(list);
//		size++;
//	}
//	
	public void addNode(String node, String placeTransition){ //transition (composite node) or place (+ or -)
		nodesMap.put(node, this.size); //the index of the new node in the adj list
		nodesNames.put(this.size, node);
		List<Integer> list = new LinkedList<Integer>();
		this.adj.add(list);
		size++;
		
		if(placeTransition == "transition")
			this.transitions.add(node);
		else if(placeTransition == "place")
			this.places.add(node);
	}
	
	public void addSourceNode(String node){
		this.isSourceNode.put(node, true);
	}
	
	public void setNotSourceNode(String node){
		this.isSourceNode.put(node, false);
	}
	
	public void addComplementaryNode(String node, String complement){
		this.complementaryNodes.put(node, complement);
	}
	
	public String getomplementaryNode(String node){
		return this.complementaryNodes.get(node);
	}
	
	public void removeTransition(String transition){
		this.transitions.remove(transition);
		this.transitionsInputs.remove(transition);
		this.transitionsOutputs.remove(transition);
	}
	
	public void addTransition(String t){
		this.transitions.add(t);
	}
	
	public Set<String> getTransitions(){
		return this.transitions;
	}
	
	public boolean isTransition(String node){
		if (this.transitions.contains(node))
			return true;
		else
			return false;
	}
	
	public boolean isPlace(String node){
		if (this.places.contains(node))
			return true;
		else
			return false;
	}
	
	public String getTransitionOutput(String t){
		return this.transitionsOutputs.get(t);
	}
	
	public List<String> getTransitionInputs(String t){
		return this.transitionsInputs.get(t);
	}
	
	public void addInputToTransition(String s, String t){							
		List<String> inputNodesSet = transitionsInputs.get(t);
		if (inputNodesSet != null){
			inputNodesSet.add(s);
			transitionsInputs.put(t, inputNodesSet);
		}
		else{
			inputNodesSet = new LinkedList<String>();
			inputNodesSet.add(s);
			transitionsInputs.put(t, inputNodesSet);
		}
	}
	
	public void addOutputToTransition(String t, String s){
		this.transitionsOutputs.put(t, s);
	}
	
	public void addEdge(String node1, String node2){
		int index1 = this.nodesMap.get(node1);
		int index2 = this.nodesMap.get(node2);
		this.adj.get(index1).add(index2);
	}
	
	
	public int get_size (){
		return this.size;
	}
	
	
	public void printPnGraph(){
		for(String node : nodesMap.keySet()){
			List<Integer> nbours = adj.get(nodesMap.get(node));
			System.out.print(node+ " : ");
			for(int nb : nbours)
				System.out.print(nodesNames.get(nb) + "   ");
			System.out.println();
		}
	}
	
	
	public boolean isOscillation(Set<String> set){
		for(String node: set){
			if(isPlace(node)){
				if (!set.contains(complementaryNodes.get(node)))
					return false;
			}
			else if(isTransition(node)){
				for(String inputNode : this.transitionsInputs.get(node))
					if (! set.contains(inputNode)) 
						return false;
			}
				
		}
		return true;
	}
	
	
	public boolean isStableMotif(Set<String> set){
		for(String node: set){
			if(isPlace(node)){
				if (set.contains(complementaryNodes.get(node)))
					return false;
			}
			else if(isTransition(node)){
				for(String inputNode : this.transitionsInputs.get(node))
					if (! set.contains(inputNode)) 
						return false;
			}
				
		}
		return true;
	}
	
	
	public void printAttractors(Set<Set<String>> sccs){
		for(Set<String> scc: sccs){
			Set<String> toPrintScc = new HashSet<String>();
			for(String node: scc)
				//if(isPlace(node)){
					toPrintScc.add(node/*.substring(0, node.length()-1)*/);
				//}
			System.out.println(toPrintScc);
		}
		
	}
	
	
	public List<Set<String>> getOscillations(){
		SccTarjan tarjan = new SccTarjan();
		List<Set<String>> sccs = tarjan.getSCCs();
		List<Set<String>> oscillations = new LinkedList<Set<String>>();
		for(Set<String> scc: sccs)
			if(isOscillation(scc))
				oscillations.add(scc);
		return oscillations;
	}
	
	private List<Integer> compositeCycles = new LinkedList<Integer>(); //cycles which contain at least one composite node
	private HashMap<String, List<Integer>> compositeCyclesMap = new HashMap<String, List<Integer>> ();
	//contains for each composite node all the cycles which contain this composite node
	private HashMap<Integer, List<Integer>> numberCompositeInCycles = new HashMap<Integer, List<Integer>>();
	//contains for each number the list of cycles which contain this number of composite nodes
	private HashMap<Integer, List<String>> cycleNumsMap = new HashMap<Integer, List<String>>();
	private HashMap<Integer, Set<String>> compNodesIncycles = new HashMap<Integer, Set<String>>();
	//contains for each cycleNumber the composite nodes in the respective cycle
	

	Set<Integer> markedCycles = new HashSet<Integer>(); //cycles which are already in a scc
	Set<Set<String>> stableMotifs = new HashSet<Set<String>>();
	
	public Set<Set<String>> getStableMotifs(){
		List<List<String>> cycles = getCycles();
		List<List<String>> cyclesToRemove = new LinkedList<List<String>>();
		
		int numOfCompInCycle = 0; //The number of composite nodes in the cycle 
		int cycleNum = 0;
		
		for(List<String> cycle: cycles){
			for(String node: cycle){
				if(isPlace(node)){
					if(cycle.contains(getomplementaryNode(node))){ //remove cycles which contain both a node and its complementary
						cyclesToRemove.add(cycle);
						break;
					}
				}
				else if(isTransition(node))
					for(String inputNode: getTransitionInputs(node))
						if(cycle.contains(getomplementaryNode(inputNode))){
							cyclesToRemove.add(cycle); //remove cycles which contains a composite node and the complementary of one of its inputs
							break;
						}
			}
		}
		
		cycles.removeAll(cyclesToRemove);
		
		for(List<String> cycle: cycles){
			numOfCompInCycle = 0;
			cycleNum++;
			cycleNumsMap.put(cycleNum, cycle);
			for(String node: cycle){
				if(isTransition(node)){
					numOfCompInCycle++;
					
					//initialize
					List<Integer> compcycl = new LinkedList<Integer>();
					compositeCyclesMap.put(node, compcycl);
					
					Set<String> compnodes = compNodesIncycles.get(cycleNum);
					if(compnodes == null)
						compnodes = new HashSet<String>();
					compnodes.add(node);
					compNodesIncycles.put(cycleNum, compnodes);
				}
			}
			
			List<Integer> list = numberCompositeInCycles.get(numOfCompInCycle);
			if(list == null)
				list = new LinkedList<Integer>();
			list.add(cycleNum);
			numberCompositeInCycles.put(numOfCompInCycle, list);
			}
		
		
		//sort the cycles regarding the number of composite nodes
		List<Integer> keys = new LinkedList<Integer>(numberCompositeInCycles.keySet());
		Collections.sort(keys);
		for(int numOfCompItr: keys) if(numOfCompItr > 0){
			List<Integer> cyclesItr = numberCompositeInCycles.get(numOfCompItr);
			for(int cycleItr: cyclesItr){
				if(!compositeCycles.contains(cycleItr))
					compositeCycles.add(cycleItr);
				if(compNodesIncycles.get(cycleItr) != null)
				for(String node: compNodesIncycles.get(cycleItr)){
					List<Integer> compcycl= compositeCyclesMap.get(node);
					compcycl.add(cycleItr);
					compositeCyclesMap.put(node, compcycl);
				}	
			}
		}
		
		for(int cycleNumItr: compositeCycles)
			/*if(!markedCycles.contains(cycleNumItr))*/{
				boolean b = true;
				Set<String> scc = new HashSet<String>();
				scc.addAll(cycleNumsMap.get(cycleNumItr));
				//markedCycles.add(cycleNumItr);
				Set<String> toCheckComp = new HashSet<String>();
				for (String compnode: compNodesIncycles.get(cycleNumItr)){
					toCheckComp.addAll(compNodesIncycles.get(cycleNumItr));
					b = recComposeCycles(compnode, scc, toCheckComp);
					if(b == false)
						break;
				}
				if(b)
					stableMotifs.add(scc);
			}
		
		if(numberCompositeInCycles.get(0) != null)
		for (int cycNum: numberCompositeInCycles.get(0)) {   //add all the cycles without composite nodes
			Set<String> set = new HashSet<String>();
			for(String node: cycleNumsMap.get(cycNum))
				set.add(node);
			stableMotifs.add(set);
		}
		return stableMotifs;
	}
	
	/*private boolean recComposeCycles(String compNode, Set<String> scc){
		for(String input: transitionsInputs.get(compNode))
			if(! scc.contains(input)){
				for(int compcycleNum: compositeCyclesMap.get(compNode)){
					if(cycleNumsMap.get(compcycleNum).contains(input)){
						scc.addAll(cycleNumsMap.get(compcycleNum));
						if(numberCompositeInCycles.get(1).contains(compcycleNum)){
							markedCycles.add(compcycleNum);
							break;
						}
						else{
							for(String newCompNode: compNodesIncycles.get(compcycleNum)) 
								if (! newCompNode.equals(compNode)){
									markedCycles.add(compcycleNum);
									return recComposeCycles(newCompNode, scc);
							}
						}
						break;
					}
				}
				return false;
			}
		return true;
	}*/

	private boolean recComposeCycles(String compNode, Set<String> scc, Set<String> toCheckComp){
		for(String input: transitionsInputs.get(compNode))
			if(! scc.contains(input)){
				for(int compcycleNum: compositeCyclesMap.get(compNode)){
					if(cycleNumsMap.get(compcycleNum).contains(input)){
						if(compatibleCycle(compcycleNum, scc)){
							scc.addAll(cycleNumsMap.get(compcycleNum));
							toCheckComp.addAll(compNodesIncycles.get(compcycleNum));
							for(String newCompNode: compNodesIncycles.get(compcycleNum)) 
								if (! newCompNode.equals(compNode)){
								//markedCycles.add(compcycleNum);
								return recComposeCycles(newCompNode, scc, toCheckComp);

							}
							break;
						}
					}
				}
				return false;
			}
		toCheckComp.remove(compNode);
		if(toCheckComp.isEmpty())
			return true;
		else
			return recComposeCycles(toCheckComp.iterator().next(), scc, toCheckComp);
	}
	
	private boolean compatibleCycle (int compcycleNum, Set<String> scc) {
		for(String node: cycleNumsMap.get(compcycleNum))
			if(isPlace(node)){
				if(scc.contains(complementaryNodes.get(node))){
					return false;   //if the new cycle contains a complementary node of a node in the scc
				}
			}
		return true;
	}

	/*private void recComposeCycles(String compNode, Set<String> scc, Set<String> compNodesRec){
		if(compNodesRec.isEmpty())
			return;
		for(String input: transitionsInputs.get(compNode)){
			if(scc.contains(complementaryNodes.get(input)))
				return;
			else if(! scc.contains(input)){
				for(int compcycleNum: compositeCyclesMap.get(compNode)){
					if(cycleNumsMap.get(compcycleNum).contains(input)){
						boolean cycleBool = true;
						for(String node: cycleNumsMap.get(compcycleNum))
							if(isPlace(node)){
								String compNodeProhib = complementaryNodes.get(node);
								if(scc.contains(compNodeProhib)){
									cycleBool = false;
									break;    //if the new cycle contains a complementary node of a node in the scc
								}
							}

						if (cycleBool){
							compNodesRec.addAll(compNodesIncycles.get(compcycleNum));
							for(String newCompNode: compNodesIncycles.get(compcycleNum)){ 
								if (! newCompNode.equals(compNode)){
									recComposeCycles(newCompNode, scc, compNodesRec);
									if(compNodesRec.contains(newCompNode)){
										cycleBool = false;
										break;
									}
								}
							}
							if(cycleBool){
								scc.addAll(cycleNumsMap.get(compcycleNum));
								return;
							}
						}
					}
				}
				return;
			}
	}

		compNodesRec.remove(compNode);
		
		return;
		
	}*/
	
	
	public List<List<String>> getCycles(){
		String[] nodes = new String[this.size];
		boolean[][] adjMatrix = new boolean[this.size][this.size];
		for(String node: nodesMap.keySet())
			nodes[nodesMap.get(node)] = node;
		for(int i=0; i<adj.size(); i++){
			List<Integer> list = adj.get(i);
			for (int j: list)
				adjMatrix[i][j] = true;
		}
		ElementaryCyclesSearch ecs = new ElementaryCyclesSearch(adjMatrix, nodes);
		//System.out.println(ecs.getElementaryCycles());
		return ecs.getElementaryCycles();
	}
	

class SccTarjan{
	private int id;
	int[] index = new int[size];
	int[] lowlink = new int[size];
    Stack<Integer> stack = new Stack<Integer>();
	List<Set<String>> sccs = new LinkedList<Set<String>>();

    protected SccTarjan(){
    	
    }
    
	public List<Set<String>> getSCCs(){
		for (int v = 0; v < size; v++){
	    	index[v] = -1;
	    }
		id = 0;
	    for (int v = 0; v < size; v++){
	    	if(index[v] == -1)
	    		strongConnect(v);
	    }
		return sccs;
	}
	
	private void strongConnect(int v){
		index[v] = id;
		lowlink[v] = id;
		id++;
		stack.push(v);
		for(int w: adj.get(v)){
			if(index[w] == -1){
				strongConnect(w);
				lowlink[v] = Math.min(lowlink[v], lowlink[w]);
			}
			else if(stack.contains(w))
				lowlink[v] = Math.min(lowlink[v], lowlink[w]);
		}
		
		if(lowlink[v] == index[v]){
			Set<String> sc = new HashSet<String>();
			int w;
			do{
				w = (Integer) stack.pop();
				sc.add(nodesNames.get(w));
			}while (w !=v);
			System.out.println(sc);
			sccs.add(sc);
		}
	}
}

}
