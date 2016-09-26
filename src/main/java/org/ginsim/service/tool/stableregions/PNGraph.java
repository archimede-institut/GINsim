package org.ginsim.service.tool.stableregions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class PNGraph {
	private List<List<Integer>> adj = new LinkedList<List<Integer>>();
	private int size; //num of nodes
	private HashMap<String, Integer> nodesMap = new HashMap<String, Integer>(); //(name of the node, index of the node in the adj list)
	private HashMap<Integer, String> nodesNames = new HashMap<Integer, String>();
	private Set<String> transitions = new HashSet<String>();
	private Set<String> places = new HashSet<String>();
	private HashMap<String, Set<String>> inputNodesMap = new HashMap<String, Set<String>>();
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
	public void addNode(String node){ //transition (composite node) or place (+ or -)
		nodesMap.put(node, this.size); //the index of the new node in the adj list
		nodesNames.put(this.size, node);
		List<Integer> list = new LinkedList<Integer>();
		this.adj.add(list);
		size++;
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
	
	public void addPlace(String s){
		this.places.add(s);
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
	
	public void addInputToTransition(String s, String t){
		Set<String> inputNodesSet = inputNodesMap.get(t);
		if (inputNodesSet != null){
			inputNodesSet.add(s);
			inputNodesMap.put(t, inputNodesSet);
		}
		else{
			inputNodesSet = new HashSet<String>();
			inputNodesSet.add(s);
			inputNodesMap.put(t, inputNodesSet);
		}
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
				for(String inputNode : this.inputNodesMap.get(node))
					if ((! set.contains(inputNode)) && (!isSourceNode.get(inputNode)))
						return false;
			}
				
		}
		return true;
	}
	
	
	public List<Set<String>> getOscillations(){
		SccTarjan tarjan = new SccTarjan();
		return tarjan.getSCCs();
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
				strongConnect(w);;
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
			if(isOscillation(sc))
				sccs.add(sc);
		}
	}
}

}
