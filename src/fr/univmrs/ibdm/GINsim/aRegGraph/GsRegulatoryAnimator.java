package fr.univmrs.ibdm.GINsim.aRegGraph;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.ibdm.GINsim.graph.GraphChangeListener;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.graph.GsGraphSelectionChangeEvent;
import fr.univmrs.ibdm.GINsim.graph.GsGraphicalAttributesStore;
import fr.univmrs.ibdm.GINsim.graph.GsNewGraphEvent;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * Main class of the animator plugin.
 * use the <code>animate</code> static method to run the animation on a dynamic or regulatory graph:
 * It performs some checks and run the GUI once everything looks OK.
 */
public class GsRegulatoryAnimator extends AbstractListModel implements GraphChangeListener {

    private static final long serialVersionUID = 2490572906584434122L;
    
    private GsRegulatoryGraph regGraph;
    private GsDynamicGraph dynGraph;

    private Vector path = new Vector();
    
    private GsGraphicalAttributesStore gas;
    private GsGraphicalAttributesStore dynGas;
    
    private Vector nodeOrder;
    private PathPlayer pathPlayer = null;
    
    private static final Color[] BGCOLS = { 
            Color.WHITE,
            new Color(1, (float)0.8, (float)0.8),
            new Color(1, (float)0.6, (float)0.6),
            new Color(1, (float)0.4, (float)0.4),
            new Color(1, (float)0.2, (float)0.2)
    };
    private static final int NBCOL = BGCOLS.length-1;

    private GsAnimatorUI ui;

    private JFrame frame;
    
    /**
     * @param frame
     * @param graph
     */
    public static void animate(JFrame frame, GsGraph graph) {
        GsRegulatoryGraph regGraph = null;
        GsDynamicGraph dynGraph = null;

        if (graph instanceof GsDynamicGraph) {
            // we need an associated dynamic graph
            regGraph = (GsRegulatoryGraph) graph.getAssociatedGraph();
            dynGraph = (GsDynamicGraph)graph;
        }
        
        if (regGraph == null || dynGraph == null) {
        	graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "Could not start the animator", GsGraphNotificationMessage.NOTIFICATION_WARNING));
            return;
        }
        // let's start the animator
        new GsRegulatoryAnimator(frame, regGraph, dynGraph);
    }
    
    /**
     * @param frame
     * @param regGraph
     * @param dynGraph
     */
    private GsRegulatoryAnimator(JFrame frame, GsRegulatoryGraph regGraph, GsDynamicGraph dynGraph) {
        this.frame = frame;
        this.regGraph = regGraph;
        this.dynGraph = dynGraph;
        nodeOrder = regGraph.getNodeOrder();
        gas = new GsGraphicalAttributesStore(regGraph);
        dynGas = new GsGraphicalAttributesStore(dynGraph);
        initAnim();
    }
    
    /**
     * initialize stuff, save old colors...
     */
    private void initAnim() {
        // reset all node's color and save them in the hashmap
        regGraph.addBlockClose(this);
        regGraph.addBlockEdit(this);
        dynGraph.addBlockClose(this);
        dynGraph.addBlockEdit(this);
        gas.storeAll();
        dynGraph.getGraphManager().getEventDispatcher().addGraphChangedListener(this);
        ui = new GsAnimatorUI(frame, this);
    }
    
    /**
     * stop the animator, restore colors...
     */
    protected void endAnim() {
        if (pathPlayer != null && pathPlayer.isAlive()) {
            pathPlayer.interrupt();
        }
        
        dynGraph.getGraphManager().getEventDispatcher().removeGraphChangeListener(this);
        revertPath(0);
        gas.restoreAll();
        
        regGraph.removeBlockClose(this);
        regGraph.removeBlockEdit(this);
        dynGraph.removeBlockClose(this);
        dynGraph.removeBlockEdit(this);
        if (pathPlayer != null && pathPlayer.isAlive()) {
            pathPlayer.notify();
        }
    }
    
    protected void add2path (Object vertex) {
        if (vertex == null || !(vertex instanceof GsDynamicNode)) {
            return;
        }
        // if path wasn't empty, check that this vertex can be added
        if (path.size() > 0 && dynGraph.getGraphManager().getEdge(path.get(path.size()-1), vertex) == null) {
               return;
        }
        path.add(vertex);
        unMarkAllPath();
        markAllPath();
        fireContentsChanged(this, path.size()-2, path.size()-1);
    }
    
    private void unMarkAllPath() {
    	dynGas.restoreAll();
    }
    
    private void markAllPath() {
        if (path.size() == 0) {
            return;
        }
        // mark all vertices and followed edges
        Object vertex = path.get(0);
        dynGas.ensureStoreVertex(vertex);
        dynGas.vreader.setBackgroundColor(Color.BLUE);
        dynGas.vreader.refresh();
        for (int i=1 ; i<path.size() ; i++) {
            Object vertex2 = path.get(i);
            dynGas.ensureStoreVertex(vertex2);
            dynGas.vreader.setBackgroundColor(Color.BLUE);
            dynGas.vreader.refresh();

            GsDirectedEdge edge = (GsDirectedEdge)dynGraph.getGraphManager().getEdge(vertex, vertex2);
            dynGas.ensureStoreEdge(edge);
            dynGas.ereader.setLineColor(Color.MAGENTA);
            dynGas.ereader.refresh();
            vertex = vertex2;
        }
        
        // highlight available edges and vertices
        Iterator it = dynGraph.getGraphManager().getOutgoingEdges(path.get(path.size()-1)).iterator();
        Vector v_highlight = new Vector();
        while (it.hasNext()) {
            v_highlight.add(it.next());
        }
        for (int i=0 ; i<v_highlight.size() ; i++) {
            GsDirectedEdge edge = (GsDirectedEdge)v_highlight.get(i);
            Object target = edge.getTargetVertex();
            dynGas.ensureStoreEdge(edge);
            dynGas.ensureStoreVertex(target);
            dynGas.vreader.setBorder(GsVertexAttributesReader.BORDER_STRONG);
            dynGas.vreader.setForegroundColor(Color.RED);
            dynGas.vreader.refresh();
            dynGas.ereader.setLineColor(Color.GREEN);
            dynGas.ereader.refresh();
        }
    }
    
    /**
     * colorize the regulatory graph according to a given state (node of the dynamic graph).
     * 
     * @param state
     */
    protected void colorizeGraph(int[] state) {
        if (state == null || state.length != nodeOrder.size()) {
            return;
        }
        
        for (int i=0 ; i<state.length ; i++) {
            GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);
            
            // choose the vertex's color
            int curVal = state[i];
            int col;
            if (curVal == 0) {
                col = 0;
            } else {
                col = (NBCOL*curVal/vertex.getMaxValue());
                if (col == 0) {
                    col = 1;
                } else if (col >= NBCOL) { // SHOULDN'T happen but....
                    col = NBCOL - 1;
                }
            }
            // apply the vertex's color
            gas.vreader.setVertex(vertex);
            gas.vreader.setBackgroundColor(BGCOLS[col]);
            gas.vreader.refresh();
            
            // colorize edges
            List l_edge = regGraph.getGraphManager().getOutgoingEdges(vertex);
            for (int j=0 ; j<l_edge.size() ; j++) {
                GsRegulatoryMultiEdge edge = (GsRegulatoryMultiEdge)((GsDirectedEdge)l_edge.get(j)).getUserObject();
                gas.ereader.setEdge(l_edge.get(j));
                gas.ereader.setLineColor(Color.GRAY);
                for (int k=0 ; k<edge.getEdgeCount() ; k++) {
                    if (edge.isActive(k, curVal)) {
                        gas.ereader.setLineColor(Color.RED);
                        break;
                    }
                }
                gas.ereader.refresh();
            }
        }
    }
    
    /**
     * rewind the path.
     * basically revert all color changes made on the dyngraph and eventually apply back
     * the first ones!
     * 
     * @param index index of the last vertex to remove.
     */
    protected void revertPath(int index) {
        if (path.size() == 0 || index < 0 || index >= path.size()) {
            return;
        }
        
        unMarkAllPath();
        for (int i=path.size()-1 ; i>=index ; i--) {
            path.remove(i);
        }
        
        // if the new path is empty: stop here
        fireContentsChanged(this, 0, path.size());
        if (index == 0) {
            return;
        }
        
        markAllPath();
    }
    
    public void graphChanged(GsNewGraphEvent event) {
        endAnim();
    }
    public void graphSelectionChanged(GsGraphSelectionChangeEvent event) {
        if (event.getNbEdge() == 0 && event.getNbVertex() == 1) {
            colorizeGraph( ((GsDynamicNode)event.getV_vertex().get(0)).state );
            add2path(event.getV_vertex().get(0));
        }
    }
    
    public void graphClosed(GsGraph graph) {
        endAnim();
    }

    public int getSize() {
        return path.size();
    }

    public Object getElementAt(int index) {
        return path.get(index);
    }

    /**
     * @param start
     */
    public void playPath(int start) {
        if (pathPlayer != null) {
            if (pathPlayer.isAlive()) {
                pathPlayer.interrupt();
            }
            
            return;
        }
        if (pathPlayer == null) {
            pathPlayer = new PathPlayer(path, this, (start == -1) ? 0 : start);
        }
        
        ui.busyPlaying();
        pathPlayer.start();
    }
    
    protected void playerEnded() {
        pathPlayer = null;
        ui.ready2play();
    }

    private class PathPlayer extends Thread {
        
        private Vector path;
        private GsRegulatoryAnimator animator;
        private int start;
        
        /**
         * @param path
         * @param animator
         * @param start
         */
        public PathPlayer(Vector path, GsRegulatoryAnimator animator, int start) {
            this.path = path;
            this.animator = animator;
            this.start = start;
        }

        public void run() {
            for (int i=start ; i<path.size() ; i++) {
                animator.colorizeGraph(i);
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
            animator.playerEnded();
        }
}

    /**
     * colorize the regulatory graph according to the selected element.
     * 
     * @param i index of the selected element in the path list
     */
    public void colorizeGraph(int i) {
        colorizeGraph(((GsDynamicNode)path.get(i)).state);
        ui.setSelected(i);
    }
    
    /**
     * export the selected path to be drawn with gnuplot
     */
    public void saveGnuPlotPath() {
        new GsAReg2GPConfig(frame, path, nodeOrder);
    }
    
    /**
     * @return the selected path
     */
    public Vector getPath() {
        return path;
    }

	public void updateGraphNotificationMessage(GsGraph graph) {
	}
}

