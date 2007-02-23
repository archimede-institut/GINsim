package fr.univmrs.ibdm.GINsim.piccolo;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;

public class GsPCanvas extends PCanvas implements GsGraphListener {
	private static final long serialVersionUID = -4925567253703356494L;

	Vector v_selectedNodes = new Vector();
	Vector v_selectedArcs = new Vector();
	
	private static final String DATA_KEY = "data";
	
	GsSimpleGraphManager manager;
	HashMap m_glink = new HashMap();

    final PCamera camera;
    
    GsPLayer layer;
	
	public GsPCanvas(GsGraph graph) {
		camera = getCamera();
		layer = new GsPLayer(camera);
		camera.removeLayer(0);
		camera.addLayer(layer);

        // show all nodes and edges!
        manager = (GsSimpleGraphManager)graph.getGraphManager();
        graph.addGraphListener(this);
        Iterator it = manager.getVertexInfoIterator();
        while (it.hasNext()) {
        	NodeInfo ni = (NodeInfo)it.next();
        	GsPNode node = new GsPNode(ni);
        	node.addAttribute(DATA_KEY, ni);
        	layer.addChild(node);
        	m_glink.put(ni.data, node);
        }
        it = manager.getEdgeIterator();
        while (it.hasNext()) {
        	GsDirectedEdge de = (GsDirectedEdge)it.next();
        	PPath edge = new PPath();
        	m_glink.put(de, edge);
        	layer.addChild(edge);
        	updateEdge(de);
        }
        addInputEventListener(new MouseHandler(graph, this));
    }

	protected void setGrid(boolean visible) {
		layer.setGrid(visible);
	}
    public void updateEdge(GsPNode node) {
        NodeInfo ni = (NodeInfo) node.getAttribute(DATA_KEY);
        for (int i=ni.v_incoming.size()-1 ; i>=0 ; i--) {
        	updateEdge((GsDirectedEdge)ni.v_incoming.get(i));
        }
        for (int i=ni.v_outgoing.size()-1 ; i>=0 ; i--) {
        	updateEdge((GsDirectedEdge)ni.v_outgoing.get(i));
        }
    }
    public void updateEdge(GsDirectedEdge de) {
        // Note that the node's "FullBounds" must be used
        // (instead of just the "Bounds") because the nodes
        // have non-identity transforms which must be included
        // when determining their position.

    	PPath edge = (PPath)m_glink.get(de);
        PNode node1 = (PNode) m_glink.get(de.getSourceVertex());
        PNode node2 = (PNode) m_glink.get(de.getTargetVertex());
        Point2D start = node1.getFullBoundsReference().getCenter2D();
        Point2D end = node2.getFullBoundsReference().getCenter2D();
        edge.reset();
        edge.moveTo((float)start.getX(), (float)start.getY());
        edge.lineTo((float)end.getX(), (float)end.getY());
    }
    
	public GsGraphEventCascade edgeAdded(Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public GsGraphEventCascade edgeRemoved(Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public GsGraphEventCascade edgeUpdated(Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public GsGraphEventCascade graphMerged(Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public GsGraphEventCascade vertexAdded(Object data) {
		NodeInfo ni = manager.getvertexInfo(data);
    	GsPNode node = new GsPNode(ni);
    	node.addAttribute(DATA_KEY, ni);
    	layer.addChild(node);
    	m_glink.put(ni.data, node);
    	node.repaint();
    	return null;
	}

	public GsGraphEventCascade vertexRemoved(Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public GsGraphEventCascade vertexUpdated(Object data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void select(Object obj) {
		clearSelection(false);
		if (obj instanceof NodeInfo) {
			v_selectedNodes.add(((NodeInfo)obj).data);
		} else {
			
		}
		// TODO: fire event ?
	}
	private void clearSelection(boolean fire) {
		// TODO: mark as unselected
		for (int i=0 ; i<v_selectedArcs.size() ; i++) {
			
		}
		for (int i=0 ; i<v_selectedNodes.size() ; i++) {
			
		}
		v_selectedArcs.clear();
		v_selectedNodes.clear();
	}

}
