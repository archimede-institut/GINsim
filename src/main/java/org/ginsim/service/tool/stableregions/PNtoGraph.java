package org.ginsim.service.tool.stableregions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;

import org.ginsim.johnsonCycles.ElementaryCyclesSearch;


public class PNtoGraph{
	private PNGraph pnGraph;
	private final List<NodeInfo> nodeOrder;
	private final MDDManager ddmanager;
	private final int[] functions;
	private final int len;
	
	
	public PNtoGraph(LogicalModel model) {
		this.nodeOrder = model.getNodeOrder();
		this.ddmanager = model.getMDDManager();
		this.functions = model.getLogicalFunctions();
		
		this.len = nodeOrder.size();
		pnGraph = new PNGraph();
	}
	
	
	private void browse(List v_result, MDDManager ddmanager, int f, int nodeIndex, List<NodeInfo> v_node, int len) {
        if (ddmanager.isleaf(f)) {
            TransitionData td = new TransitionData();
            td.value = f;
            td.maxValue = v_node.get(nodeIndex).getMax();
            td.nodeIndex = nodeIndex;
            td.t_cst = null;
            
            v_result.add(td);
        } else {
            int[][] t_cst = new int[len][3];
            for (int i=0 ; i<t_cst.length ; i++) {
                t_cst[i][0] = -1;
            }
            browse(v_result, t_cst, 0, ddmanager, f, nodeIndex, v_node);
        }
    }

    private int t_num = 0;
    
	private void browse(List v_result, int[][] t_cst, int level, MDDManager ddmanager, int f, int nodeIndex, List<NodeInfo> v_node) {
        if (ddmanager.isleaf(f)) {
        	String s_node = v_node.get(nodeIndex).toString();
        	String s_transition = "t_" + s_node + "_" + t_num;
        	t_num++;
        	pnGraph.addTransition(s_transition);
        	if(f == 0)
        		pnGraph.addOutputToTransition(s_transition, s_node+ "-");
        	else if(f == 1)
        		pnGraph.addOutputToTransition(s_transition, s_node+ "+");
			
            for (int i=0 ; i<t_cst.length ; i++) {
                int index = t_cst[i][0];
                if (index == -1) {
                    break;
                }
                if(t_cst[i][1] == 0)
                	pnGraph.addInputToTransition(v_node.get(index).toString() + "-", s_transition);
                else if(t_cst[i][1] == 1)
                	pnGraph.addInputToTransition(v_node.get(index).toString() + "+", s_transition);
                
                }
            
            return;
        }

        // specify on which node constraints are added
        MDDVariable var = ddmanager.getNodeVariable(f);
        t_cst[level][0] = ddmanager.getVariableIndex( var);
        for (int i=0 ; i<var.nbval; i++) {
            int next = ddmanager.getChild(f, i);
            int j=i+1;
            while(j<var.nbval) {
                if (ddmanager.getChild(f, j) == next) {
                    j++;
                } else {
                    break;
                }
            }
            j--;
            t_cst[level][1] = i;
            t_cst[level][2] = j;
            browse(v_result, t_cst, level+1, ddmanager, next, nodeIndex, v_node);
            i = j;
        }
        // "forget" added constraints
        t_cst[level][0] = -1;
    }
	
	private void makeTransitions(List[] t_transition) {for (int i=0 ; i<len ; i++) {
            int f = functions[i];
            Vector v_transition = new Vector();
            t_transition[i] = v_transition;
            browse(v_transition, ddmanager, f, i, nodeOrder, len);
        }
	}
	
	public void makePNGraph(){
		for (int i=0 ; i<len ; i++) 
        {	
			pnGraph.addNode(nodeOrder.get(i).toString()+"+", "place"); //node+
			pnGraph.addNode(nodeOrder.get(i).toString()+"-", "place"); //node-
			pnGraph.addComplementaryNode(nodeOrder.get(i).toString()+"+", nodeOrder.get(i).toString()+"-");
			pnGraph.addComplementaryNode(nodeOrder.get(i).toString()+"-", nodeOrder.get(i).toString()+"+");
        }
			/*if(this.functions[i] == 0 ){ //TODO
				pnGraph.addSourceNode(nodeOrder.get(i).toString()+"-");
				pnGraph.setNotSourceNode(nodeOrder.get(i).toString()+"+");
			}
			else if (this.functions[i] == 1){
				pnGraph.addSourceNode(nodeOrder.get(i).toString()+"+");
				pnGraph.setNotSourceNode(nodeOrder.get(i).toString()+"-");
			}
			else if (nodeOrder.get(i).isInput()){
				pnGraph.addSourceNode(nodeOrder.get(i).toString()+"-");
				pnGraph.addSourceNode(nodeOrder.get(i).toString()+"+");
			}
			else{
				pnGraph.setNotSourceNode(nodeOrder.get(i).toString()+"+");
				pnGraph.setNotSourceNode(nodeOrder.get(i).toString()+"-");
				
			}*/
		List<String> transitionsToRemove = new LinkedList<String>();
		
		for(String transition: pnGraph.getTransitions()){
			List<String> inputs = pnGraph.getTransitionInputs(transition);
			if(inputs.size() == 1){
				pnGraph.addEdge(inputs.get(0), pnGraph.getTransitionOutput(transition));
				transitionsToRemove.add(transition);
			}
			else{
				pnGraph.addNode(transition, "transition");
				for(String input: pnGraph.getTransitionInputs(transition))
					pnGraph.addEdge(input, transition);
				pnGraph.addEdge(transition, pnGraph.getTransitionOutput(transition));
			}

		}
		for(String t: transitionsToRemove)
			pnGraph.removeTransition(t);

	}
	
	public PNGraph getPnGraph() throws IOException{
		List[] t_transition = new List[len];
        makeTransitions(t_transition);
        makePNGraph();
		return this.pnGraph;
	}

	/*protected void doExportGraph(List<NodeInfo> v_no, List[] t_transition) throws IOException {

		// TODO Auto-generated method stub
		int len = t_transition.length;
		
		//Places
		for (int i=0 ; i<len ; i++) 
        {	
			pnGraph.addNode(v_no.get(i).toString()+"+"); //node+
			pnGraph.addPlace(v_no.get(i).toString()+"+");
			pnGraph.addNode(v_no.get(i).toString()+"-"); //node-
			pnGraph.addPlace(v_no.get(i).toString()+"-");
			pnGraph.addComplementaryNode(v_no.get(i).toString()+"+", v_no.get(i).toString()+"-");
			pnGraph.addComplementaryNode(v_no.get(i).toString()+"-", v_no.get(i).toString()+"+");
			if(this.functions[i] == 0 ){ //TODO
				pnGraph.addSourceNode(v_no.get(i).toString()+"-");
				pnGraph.setNotSourceNode(v_no.get(i).toString()+"+");
			}
			else if (this.functions[i] == 1){
				pnGraph.addSourceNode(v_no.get(i).toString()+"+");
				pnGraph.setNotSourceNode(v_no.get(i).toString()+"-");
			}
			else if (v_no.get(i).isInput()){
				pnGraph.addSourceNode(v_no.get(i).toString()+"-");
				pnGraph.addSourceNode(v_no.get(i).toString()+"+");
			}
			else{
				pnGraph.setNotSourceNode(v_no.get(i).toString()+"+");
				pnGraph.setNotSourceNode(v_no.get(i).toString()+"-");
				
			}
				
        }
		
		
		
		//Transitions and edges
		for (int i=0 ; i<t_transition.length ; i++) 
        {
        	List v_transition = t_transition[i];
            String s_node = v_no.get(i).toString();
            int max = v_no.get(i).getMax();
            
            if (v_transition != null) 
            {
               for (int j=0 ; j<v_transition.size() ; j++) 
                {           	
                	TransitionData td = (TransitionData)v_transition.get(j);
                    if (td.value > 0 && td.minValue < td.value) 
                    {   
                    	int[] p = new int[3];
                    	int numOfTestArcs = 0;
                    	if (td.t_cst != null) {
                            for (int tstNodes=0 ; tstNodes< td.t_cst.length ; tstNodes++) {
                                int id = td.t_cst[tstNodes][0];
                                if(id == -1)
                                	break;
                                if (id != -1) {
                                	p = td.t_cst[tstNodes];
                                    numOfTestArcs++; 
                                }
                            }
                    	}
                    	
                    	if (numOfTestArcs == 1){
                    		int index = p[0];
							int lmin = p[1];
							int lmax = p[2];
							String tstNode = v_no.get(index).toString();
							if (lmin != 0) {
								pnGraph.addEdge(tstNode + "+", s_node + "+");
							}
							if (lmax != 0) {
								pnGraph.addEdge(tstNode + "-", s_node + "+"); 
							}
                    	}
                    	
                        if (numOfTestArcs > 1) {
							String s_transition = "t_" + s_node + "_" + j + "+";
							pnGraph.addTransition(s_transition);
							pnGraph.addNode(s_transition); 
							String s_src = v_no.get(td.nodeIndex).toString();
							if (td.minValue == 0) {
								pnGraph.addEdge(s_transition, s_src + "+");
							} 
							if (td.t_cst != null) {
								for (int ti = 0; ti < td.t_cst.length; ti++) {
									int index = td.t_cst[ti][0];
									if (index == -1) {
										break;
									}
									int lmin = td.t_cst[ti][1];
									int lmax = td.t_cst[ti][2];
									s_src = v_no.get(index).toString();
									if (lmin != 0) {
										pnGraph.addInputToTransition(s_src + "+", s_transition);
										pnGraph.addEdge(s_src + "+", s_transition);
									}
									if (lmax != 0) {
										pnGraph.addInputToTransition(s_src + "-", s_transition);
										pnGraph.addEdge(s_src + "-", s_transition); 
									}
								}
							} 
						}
                    }
                    
                    if (td.value < max && td.maxValue > td.value) {
                    	int[] p = new int[3];
                    	int numOfTestArcs = 0;
                    	if (td.t_cst != null) {
                            for (int tstNodes=0 ; tstNodes< td.t_cst.length ; tstNodes++) {
                                int id = td.t_cst[tstNodes][0];
                                if(id == -1)
                                	break;
                                if (id != -1) {
                                	p = td.t_cst[tstNodes];
                                    numOfTestArcs++; 
                                }
                            }
                    	}
                    	//if there is only one test arc for this transition, ignore the transition and add an edge between the places
                    	if (numOfTestArcs == 1){
                    		int index = p[0];
							int lmin = p[1];
							int lmax = p[2];
							String tstNode = v_no.get(index).toString();
							if (lmin != 0) {
								pnGraph.addEdge(tstNode + "+", s_node + "-");
							}
							if (lmax != 0) {
								pnGraph.addEdge(tstNode + "-", s_node + "-"); 
							}
                    	}
                    	
                        if (numOfTestArcs > 1) {
							String s_transition = "t_" + s_node + "_" + j + "-";
							pnGraph.addTransition(s_transition);
							pnGraph.addNode(s_transition); 
							String s_src = v_no.get(td.nodeIndex).toString();
							if (td.maxValue == max) {
								pnGraph.addEdge(s_transition, s_src+"-");
							} 
							if (td.t_cst != null) {
								for (int ti = 0; ti < td.t_cst.length; ti++) {
									int index = td.t_cst[ti][0];
									if (index == -1) {
										break;
									}
									int lmin = td.t_cst[ti][1];
									int lmax = td.t_cst[ti][2];
									s_src = v_no.get(index).toString();
									if (lmin != 0) {
										pnGraph.addInputToTransition(s_src + "+", s_transition);
										pnGraph.addEdge(s_src + "+", s_transition);
									}
									if (lmax != 0) {
										pnGraph.addInputToTransition(s_src + "-", s_transition);
										pnGraph.addEdge(s_src + "-", s_transition);
									}
								}
							} 
						}
                    }
                }
            }
        }
	}
	*/	
	

}



class TransitionData {
    /** target value of this transition */
    public int value;
    
    /** index of the concerned node */
    public int nodeIndex;

    /** minvalue for the concerned node (0 unless an autoregulation is present) */
    public int minValue;
    /** maxvalue for the concerned node (same as node's maxvalue unless an autoregulation is present) */
    public int maxValue;
    
    /** priority when decreasing */
    public int decreasePriority = 0;
    /** priority when increasing */
    public int increasePriority = 0;
    
    /** constraints of this transition: each row express range constraint for one of the nodes
     * and contains 3 values:
     *  <ul>
     *      <li>index of the node (or -1 after the last constraint)</li>
     *      <li>bottom and top limit of the range (top limit is pre-processed: maxvalue - realLimit)</li>
     *  </ul>
     */
    public int[][] t_cst;
    
}

	
