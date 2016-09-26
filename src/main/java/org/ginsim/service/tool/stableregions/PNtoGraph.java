package org.ginsim.service.tool.stableregions;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;

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
	
	
	private void browse(List v_result, MDDManager ddmanager, int f, int[][] t_priorities, int nodeIndex, List<NodeInfo> v_node, int len) {
        if (ddmanager.isleaf(f)) {
            TransitionData td = new TransitionData();
            td.value = f;
            td.maxValue = v_node.get(nodeIndex).getMax();
            td.nodeIndex = nodeIndex;
            td.t_cst = null;
            if (t_priorities != null) {
				td.increasePriority = t_priorities[nodeIndex][0];
				td.decreasePriority = t_priorities[nodeIndex][1];
			}
            v_result.add(td);
        } else {
            int[][] t_cst = new int[len][3];
            for (int i=0 ; i<t_cst.length ; i++) {
                t_cst[i][0] = -1;
            }
            browse(v_result, t_cst, 0, ddmanager, f, t_priorities, nodeIndex, v_node);
        }
    }

    private void browse(List v_result, int[][] t_cst, int level, MDDManager ddmanager, int f, int[][] t_priorities, int nodeIndex, List<NodeInfo> v_node) {
        if (ddmanager.isleaf(f)) {
            TransitionData td = new TransitionData();
            td.value = f;
            td.maxValue = v_node.get(nodeIndex).getMax();
            td.nodeIndex = nodeIndex;
            if (t_priorities != null) {
				td.increasePriority = t_priorities[nodeIndex][0];
				td.decreasePriority = t_priorities[nodeIndex][1];
			}
            td.t_cst = new int[t_cst.length][3];
            int ti = 0;
            for (int i=0 ; i<t_cst.length ; i++) {
                int index = t_cst[i][0];
                if (index == -1) {
                    break;
                }
                if (index == nodeIndex) {
                    td.minValue = t_cst[i][1];
                    td.maxValue = t_cst[i][2];
                } else {
                    td.t_cst[ti][0] = index;
                    td.t_cst[ti][1] = t_cst[i][1];
                    td.t_cst[ti][2] = v_node.get(index).getMax() - t_cst[i][2];
                    if (td.t_cst[ti][1] > 0 || td.t_cst[ti][2] > 0) {
                        ti++;
                    }
                }
            }
            if (ti == 0) {
                td.t_cst = null;
            } else {
                td.t_cst[ti][0] = -1;
            }
            v_result.add(td);
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
            browse(v_result, t_cst, level+1, ddmanager, next, t_priorities, nodeIndex, v_node);
            i = j;
        }
        // "forget" added constraints
        t_cst[level][0] = -1;
    }
	
	private void prepareExportGraph(List[] t_transition) {
    	int[][] t_priorities = null;
        for (int i=0 ; i<len ; i++) {
            int f = functions[i];
            Vector v_transition = new Vector();
            t_transition[i] = v_transition;
            browse(v_transition, ddmanager, f, t_priorities, i, nodeOrder, len);
        }
	}
	
	public void exportGraph() throws IOException{
    	List[] t_transition = new List[len];
        prepareExportGraph(t_transition);
    	doExportGraph(nodeOrder, t_transition);
    }
	
	public PNGraph getPnGraph() throws IOException{
		exportGraph();
		return this.pnGraph;
	}

	protected void doExportGraph(List<NodeInfo> v_no, List[] t_transition) throws IOException {

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
			if(this.functions[i] == 0 || this.functions[i] == 10){
				pnGraph.addSourceNode(v_no.get(i).toString()+"+");
				pnGraph.addSourceNode(v_no.get(i).toString()+"-");
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
                    	/*if there is only one test arc for this transition, ignore the transition and add an edge between the places*/
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

	
