package org.ginsim.servicegui.tool.regulatorygraphanimation;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphModel;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.GraphicalAttributesStore;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.css.Colorizer;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphGUIListener;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.shell.editpanel.SelectionType;


/**
 * Main class of the animator plugin.
 * use the <code>animate</code> static method to run the animation on a dynamic or regulatory graph:
 * It performs some checks and run the GUI once everything looks OK.
 */
public class RegulatoryAnimator extends AbstractListModel implements GraphGUIListener {

    private static final long serialVersionUID = 2490572906584434122L;
    
    private RegulatoryGraph regGraph;
    private DynamicGraph dynGraph;

    private Vector<DynamicNode> path = new Vector();
    
    private GraphicalAttributesStore dynGas;
    
    private List nodeOrder;
    private PathPlayer pathPlayer = null;
    
 
    private AnimatorUI ui;

    private JFrame frame;

	private Colorizer colorizer;
    
    /**
     * @param frame
     * @param graph
     */
    public static void animate(JFrame frame, DynamicGraph dynGraph) {
    	RegulatoryGraph regGraph = null;
    	if (dynGraph != null) {
    		try{
    			regGraph = dynGraph.getAssociatedGraph();
    		}
    		catch (GsException ge) {
				regGraph = null;
			}
    	}
        if (regGraph == null || dynGraph == null) {
        	NotificationManager.publishWarning( null, "Could not start the animator");
            return;
        }
        // let's start the animator
        new RegulatoryAnimator(frame, regGraph, dynGraph);
    }
    
    /**
     * @param frame
     * @param regGraph
     * @param dynHieGraph
     */
    private RegulatoryAnimator(JFrame frame, RegulatoryGraph regGraph, DynamicGraph dynGraph) {
        this.frame = frame;
        this.regGraph = regGraph;
        this.dynGraph = dynGraph;
        nodeOrder = regGraph.getNodeOrder();
        dynGas = new GraphicalAttributesStore(dynGraph);
        initAnim();
    }

    
    
    /**
     * @param frame
     * @param regGraph
     * @param dynHieGraph
     */
    private RegulatoryAnimator(RegulatoryGraph regGraph) {
        this.frame = null;
        this.regGraph = regGraph;
        this.dynGraph = null;
        nodeOrder = regGraph.getNodeOrder();
        colorizer = new Colorizer(new RegulatoryAnimatorSelector(regGraph));
    }

    /**
     * initialize stuff, save old colors...
     */
    private void initAnim() {
        // reset all node's color and save them in the hashmap
        GUIManager.getInstance().addBlockClose(regGraph, this);
        GUIManager.getInstance().addBlockEdit( regGraph, this);
        GUIManager.getInstance().addBlockClose( dynGraph, this);
        GUIManager.getInstance().addBlockEdit( dynGraph, this);
        colorizer = new Colorizer(new RegulatoryAnimatorSelector(regGraph));
        GUIManager.getInstance().getGraphGUI(dynGraph).addGraphGUIListener(this);
        ui = new AnimatorUI(frame, this);      
    }
    
	/**
     * stop the animator, restore colors...
     */
    protected void endAnim() {
        if (pathPlayer != null && pathPlayer.isAlive()) {
            pathPlayer.interrupt();
        }
        
        GUIManager.getInstance().getGraphGUI(dynGraph).removeGraphGUIListener(this);
        revertPath(0);
        colorizer.undoColorize(regGraph);
        
        GUIManager.getInstance().removeBlockClose( regGraph, this);
        GUIManager.getInstance().removeBlockEdit( regGraph, this);
        GUIManager.getInstance().removeBlockClose( dynGraph, this);
        GUIManager.getInstance().removeBlockEdit( dynGraph, this);
        if (pathPlayer != null && pathPlayer.isAlive()) {
            pathPlayer.notify();
        }
    }
        
    protected void add2path (DynamicNode vertex) {
        if (vertex == null) {
            return;
        }
        // if path wasn't empty, check that this node can be added
        if (path.size() > 0 && dynGraph.getEdge(path.get(path.size()-1), vertex) == null) {
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
        DynamicNode vertex = path.get(0);
        dynGas.ensureStoreNode(vertex);
        dynGas.vreader.setBackgroundColor(Color.BLUE);
        dynGas.vreader.refresh();
        for (int i=1 ; i<path.size() ; i++) {
            DynamicNode vertex2 = path.get(i);
            dynGas.ensureStoreNode(vertex2);
            dynGas.vreader.setBackgroundColor(Color.BLUE);
            dynGas.vreader.refresh();

            Edge edge = dynGraph.getEdge(vertex, vertex2);
            dynGas.ensureStoreEdge(edge);
            dynGas.ereader.setLineColor(Color.MAGENTA);
            dynGas.ereader.refresh();
            vertex = vertex2;
        }
        
        // highlight available edges and vertices
        Iterator it = dynGraph.getOutgoingEdges(path.get(path.size()-1)).iterator();
        Vector v_highlight = new Vector();
        while (it.hasNext()) {
            v_highlight.add(it.next());
        }
        for (int i=0 ; i<v_highlight.size() ; i++) {
            Edge edge = (Edge)v_highlight.get(i);
            Object target = edge.getTarget();
            dynGas.ensureStoreEdge(edge);
            dynGas.ensureStoreNode(target);
            dynGas.vreader.setBorder(NodeBorder.STRONG);
            dynGas.vreader.setForegroundColor(Color.RED);
            dynGas.vreader.refresh();
            dynGas.ereader.setLineColor(Color.GREEN);
            dynGas.ereader.refresh();
        }
    }
        
    /**
     * rewind the path.
     * basically revert all color changes made on the dyngraph and eventually apply back
     * the first ones!
     * 
     * @param index index of the last node to remove.
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
            pathPlayer = new PathPlayer(path, this, start == -1 ? 0 : start);
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
        private RegulatoryAnimator animator;
        private int start;
        
        /**
         * @param path
         * @param animator
         * @param start
         */
        public PathPlayer(Vector path, RegulatoryAnimator animator, int start) {
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
    	((RegulatoryAnimatorSelector) colorizer.getSelector()).setState(((DynamicNode)path.get(i)).state);
        colorizer.doColorize(regGraph);
        ui.setSelected(i);
    }
    
    /**
     * export the selected path to be drawn with gnuplot
     */
    public void saveGnuPlotPath() {
        new AReg2GPConfig(frame, path, nodeOrder);
    }
    
    /**
     * @return the selected path
     */
    public Vector getPath() {
        return path;
    }

	public void updateGraphNotificationMessage( Graph graph) {
	}

	@Override
	public void graphSelectionChanged(GraphGUI gui) {
		GraphSelection<DynamicNode, ?> selection = gui.getSelection();
        if (selection.getSelectionType() == SelectionType.SEL_NODE) {
        	DynamicNode sel = selection.getSelectedNodes().get(0);
        	((RegulatoryAnimatorSelector) colorizer.getSelector()).setState(sel.state);
            colorizer.doColorize(regGraph);
            add2path( sel);
        }
	}

	@Override
	public void graphGUIClosed(GraphGUI gui) {
        endAnim();
	}

	@Override
	public GraphEventCascade graphChanged(GraphModel g, GraphChangeType type, Object data) {
		return null;
	}
	
}

