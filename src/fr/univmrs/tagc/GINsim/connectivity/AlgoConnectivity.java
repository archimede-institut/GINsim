package fr.univmrs.tagc.GINsim.connectivity;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphtGraphManager;
import fr.univmrs.tagc.common.ProgressListener;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * the class with the algorithms for strongest connected component
 */
public final class AlgoConnectivity extends Thread {

	private GsReducedGraph reducedGraph = null;
	private GsGraphManager graphModel;
    private GsGraph g = null;

    private static final String S_SEARCH_CC = Translator.getString("STR_connectivity_searching");
    
    /** find SCC and create the reduced graph (also need to find paths between SCC) */
    public static final int MODE_FULL = 0;
    /** only find SCC */
    public static final int MODE_COMPO = 1;
    
	private Object[] t_vertex;
	
	/** list of node explored for depht search */
	private int[] explored ;
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
	public void configure(GsGraph graphm, ProgressListener frame, int searchMode) {
		this.graphModel = graphm.getGraphManager();
		this.frame = frame;
        this.g = graphm;
        this.mode = searchMode;
	}
	
    public void run() {
        reducedGraph = null;
        int nbCompo = 0;
        try {
            t_vertex = graphModel.getVertexArray();
            explored =new int[t_vertex.length];
            lastExploration = new int[t_vertex.length];
            firstExploration = new int[t_vertex.length];
            count = 0;
            time = 0;
    
            List component;
            if (false && graphModel instanceof GsJgraphtGraphManager) {
                 List jcp = ((GsJgraphtGraphManager)graphModel).getStrongComponent();
                 if (mode == MODE_FULL) {
                     component = new Vector();
                     String sid;
                     int id = 0;
                     reducedGraph = new GsReducedGraph(g);
                     for (int i=0 ; i<jcp.size(); i++) {
                         Set set = (Set)jcp.get(i);
                         if (set.size() == 1) {
                             sid = null;
                         } else {
                             sid = "cc-"+(id++);
                         }
                         GsNodeReducedData node = new GsNodeReducedData(sid, (Set)jcp.get(i));
                         component.add(node);
                         reducedGraph.addVertex(node);
                     }
                 }
            } else {
                component = findConnectedComponent();
                if (mode == MODE_FULL) {
                    reducedGraph = new GsReducedGraph(g);
                    nbCompo = component.size();
                    frame.setProgressText("Number of component : "+nbCompo+", creating the new graph");
                    for (int i=0 ; i<nbCompo && canceled == false; i++) {
                        reducedGraph.addVertex((GsNodeReducedData)component.get(i));
                    }
                }
            }
            
            if (mode == MODE_COMPO) {
                frame.setResult(component);
                return;
            }
            
            frame.setProgressText( Translator.getString("STR_connectivity_nbcompo") + " : "+nbCompo+" ; "+Translator.getString("STR_connectivity_finalize"));
            GsGraphManager gmanager = reducedGraph.getGraphManager();
            GsVertexAttributesReader vreader = gmanager.getVertexAttributesReader();
    
            // search a path between CC, and try to not spend too much time at it.
            // it's quite stupid but aims at being not as slow as it could...
            for (int i=0 ; i<nbCompo; i++) {
                if (canceled) {
                    break;
                }
                GsNodeReducedData currentNode = (GsNodeReducedData)component.get(i);
                for (int j=i+1 ; j<nbCompo; j++) {
                    GsNodeReducedData otherNode = (GsNodeReducedData)component.get(j);
                    
                    Vector v_source = currentNode.getContent();
                    Vector v_target = otherNode.getContent();
                    boolean b1 = true;
                    boolean b2 = true;
                    for (int k=0 ; k< v_source.size() ; k++) {
                        if (canceled) {
                            throw new InterruptedException();
                        }
                        Object o_source = v_source.get(k);
                        for (int l=0 ; l<v_target.size() ; l++) {
                            if (b1 && graphModel.getEdge(o_source, v_target.get(l)) != null) {
                                reducedGraph.addEdge(currentNode, otherNode);
                                b1 = false;
                                if (!b2) {
                                    break;
                                }
                            }
                            if (b2 && graphModel.getEdge(v_target.get(l), o_source) != null) {
                                reducedGraph.addEdge(otherNode, currentNode);
                                b2 = false;
                                if (!b1) {
                                    break;
                                }
                            }
                        }
                        if (!b1 && !b2) {
                            break;
                        }
                    }
                }
                if (gmanager.getOutgoingEdges(currentNode).size() == 0) {
                    vreader.setVertex(currentNode);
                    vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
                }
            }
        } catch (InterruptedException e) {
            if (reducedGraph != null && nbCompo != reducedGraph.getGraphManager().getVertexCount()) {
                reducedGraph = null;
            }
        }
        frame.setResult(reducedGraph);
    }

	/**
	 * Search of all strong connected components
	 * NOTE: this is mainly unusefull: we try to use jgrapht's internal search instead.
     * NOTE2: in fact it seems to be still usefull: jgrapht's search doesn't work...
	 * @return all strong connected component
	 * @throws InterruptedException 
	 */
	private List findConnectedComponent() throws InterruptedException {
		int id = 0;
		// depht first search on the Graph graphModel
        frame.setProgressText(S_SEARCH_CC+Translator.getString("STR_connectivity_DFS"));
		int[] last = (int[])dephtSearch(t_vertex,graphModel).elementAt(0);
		
		// compute the reverse graph of graphModel
        frame.setProgressText(S_SEARCH_CC+Translator.getString("STR_connectivity_reverse"));
		GsGraph tG = tGraph();
		//change the order of nodes in fonction of the higher postorder 
		Object[] tAllNode = Tools.decrease(last,t_vertex);
	
		// depht first search on the reverse graph
        frame.setProgressText(S_SEARCH_CC+Translator.getString("STR_connectivity_reverseDFS"));
		Vector prepost = dephtSearch(tAllNode,tG.getGraphManager());
		int[] pre = (int[])prepost.elementAt(1);

		// sort the arrays	
		Tools.increase(pre,tAllNode);		
		int end = 0;
		//the list of all connected components
		Vector components = new Vector(0);
		int indice= pre.length;
		
		// sort nodes in fonction of their components
		while (end<tAllNode.length){
			Vector temp = new Vector(0);
			indice=pre[end];
            if (canceled) {
                throw new InterruptedException();
            }
			for (int i=end;i<pre.length;i++) {
				if (pre[i] == indice){
					end++;
					temp.add(tAllNode[i]);
				}
			}
			if (!temp.isEmpty()){
			    if (temp.size() == 1) {
                    // add a prefix even if alone in it's CC: it must be a valid id
			        components.add(new GsNodeReducedData("u-"+temp.get(0), temp));
			    } else {
			        components.add(new GsNodeReducedData("cc-"+(id++), temp));
			    }
			}
		}
		return components;
	}
	/**
	 * Recursive function called by the depht search algorithms
	 * @param i - the indice of the node which will be explore
	 * @param allNode
	 * @param graph
	 * @throws InterruptedException 
	 */
	
	private void explore(int i,Object[] allNode,GsGraphManager graph) throws InterruptedException  {
		int allSize = allNode.length; 
		explored[i]= 1;// begin of the exploration of i
		firstExploration[i]=count;
		for(int j=0;j<allSize;j++){
			if (graph.getEdge(allNode[i],allNode[j]) != null){
				if (explored[j]==0){
					explore(j,allNode,graph);
				}
			}
		}
		explored[i]= 2; // the exploration of i is finished
		time = time+1;
		lastExploration[i]=time;		
	}
	/**
	 * The depht first search algorithms
	 * @param allNode
	 * @param graph
	 * @return the list of the last and the first treatment of each node
	 * @throws InterruptedException 
	 */
	private Vector dephtSearch(Object[] allNode,GsGraphManager graph) throws InterruptedException  {
		Vector exploration = new Vector(0);
		int allSize = allNode.length;  
		for(int i=0;i<allSize;i++){
			explored[i] = 0;
		}
		time = 0;
		for(int i=0;i<allSize;i++) {
            if (canceled) {
                throw new InterruptedException();
            }
			if (explored[i] == 0) {
				explore(i,allNode,graph);
			}
			count++;
		}
		exploration.add(lastExploration);
		exploration.add(firstExploration);	
		return exploration;
	}
	
	/**
	 * Calculates the reverse graph 
	 * @return the reverse graph 
	 */
	private GsGraph tGraph()  {
		GsTmpGraph tGraph = new GsTmpGraph();

		int allSize = t_vertex.length;
		// tGraph is like graphModel but without edges
		for(int i=0;i<allSize;i++){
			tGraph.addVertex(t_vertex[i]);
		}
		// all orientation of edge are changed (inversed)
		for(int i=0;i<allSize;i++){
			Object ellati = t_vertex[i];
			for (int j=0;j<allSize;j++){
				if (graphModel.getEdge(ellati,t_vertex[j]) != null){
					tGraph.addEdge(t_vertex[j],ellati);
				}
			}
		}
		return tGraph;	
	}

    /**
     * stop the computation
     */
    public void cancel() {
        canceled = true;
    }
}
