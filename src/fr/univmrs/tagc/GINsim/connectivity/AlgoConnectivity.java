package fr.univmrs.tagc.GINsim.connectivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.common.ProgressListener;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * the class with the algorithms for strongest connected component
 */
public class AlgoConnectivity extends Thread {

	protected GsReducedGraph reducedGraph = null;
    private Graph g = null;

    private static final String S_SEARCH_CC = Translator.getString("STR_connectivity_searching");
    
    /** find SCC and create the reduced graph (also need to find paths between SCC) */
    public static final int MODE_FULL = 0;
    /** only find SCC */
    public static final int MODE_COMPO = 1;
    /** only find SCC and colorize */
    public static final int MODE_COLORIZE = 2;
    
    /** find SCC using an algorithm searching the edges between the components from the outgoing edges of each component*/
    public static final int SCC_GRAPH_BY_OUTGOING_EDGES = 0;
    /** find SCC using an algorithm searching paths between the components*/
    public static final int SCC_GRAPH_BY_PATH_SEARCHING = 1;
    
	private Object[] t_vertex;
	
	/** list of node explored for depth search */
	private boolean[] explored ;
	/** time of last exploration of each node */
	private int[] lastExploration ;
	/** time of first exploration of each node */
	private int[] firstExploration ;
	private int count;
	private int time ;
    private int mode;
	private ProgressListener frame;
    private boolean canceled = false;
    
	/**
	 * get ready to run.
	 * 
	 * @param graphm
	 * @param frame
     * @param searchMode MODE_COMPO=only find components; MODE_FULL=also search for path and create the reduced graph
	 */
	public void configure( Graph graphm, ProgressListener frame, int searchMode) {
		
		this.frame = frame;
        this.g = graphm;
        this.mode = searchMode;
	}

   public void run() {
	   if (frame != null) {
	       frame.setResult(compute());
	   } else {
		   compute();
	   }
   }

   public Object compute() {
        reducedGraph = null;
        int nbCompo = 0;
        try {
            t_vertex = g.getVertices().toArray();
            explored = new boolean[t_vertex.length];
            lastExploration = new int[t_vertex.length];
            firstExploration = new int[t_vertex.length];
            count = 0;
            time = 0;

            List component;
            // TODO : REFACTORING ACTION
            // TODO : change this test since graphModel is a Graph now
            if (g instanceof GsJgraphtGraphManager) {
                List jcp = ((GsJgraphtGraphManager)g).getStrongComponent();
                nbCompo = jcp.size();
                component = new ArrayList();
                String sid;
                int id = 0;
                if (mode == MODE_FULL) {
                    reducedGraph = new GsReducedGraph(g);
                    for (int i=0 ; i<nbCompo; i++) {
                        Set set = (Set)jcp.get(i);
                        if (set.size() == 1) {
                            sid = null;
                        } else {
                            sid = "cc-"+id++;
                        }
                        GsNodeReducedData node = new GsNodeReducedData(sid, set);
                        component.add(node);
                        reducedGraph.addVertex(node);
                    }
                } else {
                    for (int i=0 ; i<nbCompo; i++) {
                        Set set = (Set)jcp.get(i);
                        if (set.size() == 1) {
                            sid = null;
                        } else {
                            sid = "cc-"+id++;
                        }
                        GsNodeReducedData node = new GsNodeReducedData(sid, set);
                        component.add(node);
                    }
                }
           } else {
                component = findConnectedComponent();
                if (mode == MODE_FULL) {
                    reducedGraph = new GsReducedGraph(g);
                    nbCompo = component.size();
                    if (frame != null) {
                        frame.setProgressText("Number of component : "+nbCompo+", creating the new graph");
                    }
                    for (int i=0 ; i<nbCompo && canceled == false; i++) {
                        reducedGraph.addVertex((GsNodeReducedData)component.get(i));
                    }
                }
            }
           
            if (mode == MODE_COMPO) {
                return component;
            }
            if (frame != null) {
                frame.setProgressText( Translator.getString("STR_connectivity_nbcompo") + " : "+nbCompo+" ; "+Translator.getString("STR_connectivity_finalize"));
            }
            if (mode == MODE_FULL) {
                GsVertexAttributesReader vreader = reducedGraph.getVertexAttributeReader();
                if (nbCompo > 1) {
    	        	createSCCGraphByOutgoingEdges(nbCompo, component, reducedGraph, vreader);
                }           	
            } else if (mode == MODE_COLORIZE) {
            	((ConnectivityFrame)frame).setComponents(component);
            	((ConnectivityFrame)frame).colorize();
            	return component;
            }
            
        } catch (InterruptedException e) {
            if (reducedGraph != null && nbCompo != reducedGraph.getVertexCount()) {
                reducedGraph = null;
            }
        }
        return reducedGraph;
   }

	public void createSCCGraphByOutgoingEdges(int nbCompo, List component, Graph graph, GsVertexAttributesReader vreader) throws InterruptedException {
		//Complexity = #nodes + #edges + #component => O(3n+1)
		if (nbCompo == 1) {
            return;																				//The graph is already created, no edges to add.
        }
		HashMap<Object, GsNodeReducedData> nodeParentSCC = new HashMap(); //Map the a node to its parent SCC
		
		for (int scc_i=0 ; scc_i<nbCompo; scc_i++) {															//for each SCC
            if (canceled) {
                break;
            }
            GsNodeReducedData currentSCCNode = (GsNodeReducedData)component.get(scc_i);
            Vector nodesInSCC = currentSCCNode.getContent();
            for (Iterator it = nodesInSCC.iterator(); it.hasNext();) {											//  for each nodes in the SCC
            	if (canceled) {
                    throw new InterruptedException();
                }
				nodeParentSCC.put(it.next(), currentSCCNode);													//     add the node in the map nodeParentSCC as a key, with the current SCC node as value
            }
		}
		
		
		for (int scc_i=0 ; scc_i<nbCompo; scc_i++) {															//for each SCC
            if (canceled) {
                break;
            }
            GsNodeReducedData currentSCCNode = (GsNodeReducedData)component.get(scc_i);
            Vector nodesInSCC = currentSCCNode.getContent();
            for (Iterator it = nodesInSCC.iterator(); it.hasNext();) {											//  for each nodes in the SCC
            	if (canceled) {
                    throw new InterruptedException();
                }
				Object currentNode = it.next();
				Collection<GsDirectedEdge> outgoingEdges = g.getOutgoingEdges(currentNode);
				for (GsDirectedEdge edge: outgoingEdges) {							//    for each edge outgoing from this node
                	if (canceled) {
                        throw new InterruptedException();
                    }						
					Object targetNode = edge.getTarget();
					GsNodeReducedData targetParent = nodeParentSCC.get(targetNode);
					if (nodeParentSCC.get(targetNode) != currentSCCNode) {			//      if the target of the edge is not in the SCC
						reducedGraph.addEdge(currentSCCNode, targetParent);
					//	targets.put(targetNode.toString(), currentSCCNode);			//      add it to the targets map <=> say the current SCC is targeting the SCC containing targetNode
					}
				}
				
			}
        }
            
    	for (int scc_i=0 ; scc_i<nbCompo; scc_i++) {															//for each SCC
            if (canceled) {
                break;
            }
            GsNodeReducedData currentSCCNode = (GsNodeReducedData)component.get(scc_i);
            if (graph.getOutgoingEdges(currentSCCNode).size() == 0) {												//  set the node's shape to ellipse if the node has no outgoing edges (is terminal).
            	vreader.setVertex(currentSCCNode);
                vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
            }
        }
	}

	/**
	 * Search of all strongly connected components
	 * NOTE: this is mainly useless: we try to use jgrapht's internal search instead.
     * NOTE2: in fact it seems to be still useful: jgrapht's search doesn't work...
	 * @return all strongly connected component
	 * @throws InterruptedException 
	 */
	private List findConnectedComponent() throws InterruptedException {
		int id = 0;
		// depth first search on the Graph graphModel
        frame.setProgressText(S_SEARCH_CC+Translator.getString("STR_connectivity_DFS"));
		depthSearch(t_vertex,g);
		
		// compute the reverse graph of graphModel
        frame.setProgressText(S_SEARCH_CC+Translator.getString("STR_connectivity_reverse"));
		//change the order of nodes in function of the higher postorder 
		Object[] tAllNode = Tools.decrease(lastExploration,t_vertex);
	
		// depth first search on the reverse graph
        frame.setProgressText(S_SEARCH_CC+Translator.getString("STR_connectivity_reverseDFS"));
		depthSearch(tAllNode,g, true);
		
		// sort the arrays	
		Tools.increase(firstExploration, tAllNode);		
		int end = 0;
		//the list of all connected components
		Vector components = new Vector(0);
		int index = firstExploration.length;
		
		// sort nodes in function of their components
		while (end<tAllNode.length){
			Vector temp = new Vector(0);
			index = firstExploration[end];
            if (canceled) {
                throw new InterruptedException();
            }
			for (int i=end ; i<firstExploration.length ; i++) {
				if (firstExploration[i] == index){
					end++;
					temp.add(tAllNode[i]);
				}
			}
			if (!temp.isEmpty()){
			    if (temp.size() == 1) {
                    // add a prefix even if alone in it's CC: it must be a valid id
			        components.add(new GsNodeReducedData("u-"+temp.get(0), temp));
			    } else {
			        components.add(new GsNodeReducedData("cc-"+id++, temp));
			    }
			}
		}
		return components;
	}
	/**
	 * Recursive function called by the depth search algorithms
	 * @param i - the index of the node which will be explored
	 * @param allNode
	 * @param graph
	 * @throws InterruptedException 
	 */
	
	private void explore(int i, Object[] allNode, Graph graph, boolean reverse) throws InterruptedException  {
		int allSize = allNode.length; 
		explored[i] = true;
		firstExploration[i] = count;
		for(int j=0;j<allSize;j++){
			Object edge = reverse ? graph.getEdge(allNode[j],allNode[i]) : graph.getEdge(allNode[i],allNode[j]);
			if (edge != null){
				if (!explored[j]){
					explore(j,allNode,graph, reverse);
				}
			}
		}
		time++;
		lastExploration[i] = time;		
	}

	/**
	 * The depth first search algorithms
	 * @param allNode
	 * @param graph
	 * @return the list of the last and the first treatment of each node
	 * @throws InterruptedException 
	 */
	private void depthSearch(Object[] allNode, Graph graph) throws InterruptedException  {
		
		depthSearch(allNode, graph, false);
	}
	private void depthSearch(Object[] allNode, Graph graph, boolean reverse) throws InterruptedException  {
		
		int allSize = allNode.length;  
		for(int i=0;i<allSize;i++){
			explored[i] = false;
		}
		time = 0;
		for(int i=0;i<allSize;i++) {
            if (canceled) {
                throw new InterruptedException();
            }
			if (!explored[i]) {
				explore(i, allNode, graph, reverse);
			}
			count++;
		}
	}
	
    /**
     * stop the computation
     */
    public void cancel() {
        canceled = true;
    }
}
