package fr.univmrs.tagc.GINsim.graph;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.NodeAttributesReader;


public class GraphicalAttributesStore {

	public EdgeAttributesReader ereader;
	public NodeAttributesReader vreader;

	Map oldColors = new HashMap();
	Graph graph;
	
	public GraphicalAttributesStore( Graph graph) {
		
		this.graph = graph;
		this.ereader = graph.getEdgeAttributeReader();
		this.vreader = graph.getNodeAttributeReader();
	}
	
	public void storeAll() {
        Iterator it = graph.getVertices().iterator();
        while (it.hasNext()) {
            Object vertex = it.next();
            vreader.setNode(vertex);
            oldColors.put(vertex, new StoreColor(vreader));

            Collection<Edge> edges = graph.getOutgoingEdges(vertex);
            for (Edge edge: edges) {
                ereader.setEdge(edge);
                oldColors.put(edge, new StoreColor(ereader));
            }
        }
	}
	
	public void restoreAll() {
        Iterator it = oldColors.keySet().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if ( obj instanceof Edge) {
                ereader.setEdge(obj);
                ((StoreColor)oldColors.get(obj)).restore(ereader);
            } else {
                vreader.setNode(obj);
                ((StoreColor)oldColors.get(obj)).restore(vreader);
            }
        }
        oldColors.clear();
	}
	
	public void ensureStoreNode(Object o) {
		vreader.setNode(o);
        if (!oldColors.containsKey(o)) {
        	oldColors.put(o, new StoreColor(vreader));
        }
	}
	
	public void ensureStoreEdge(Edge edge) {
        ereader.setEdge(edge);
        if (!oldColors.containsKey(edge)) {
            oldColors.put(edge, new StoreColor(ereader));
        }
	}
	
	public void restore(Object o) {
		vreader.setNode(o);
        StoreColor oc = (StoreColor)oldColors.get(o);
        if (oc != null ) {
            oc.restore(ereader);
        }
	}	
}

class StoreColor {
    private final Color background;
    private final Color foreGround;
    private final int border;
       
    /**
     * @param vreader
     */
    protected StoreColor (NodeAttributesReader vreader) {
        background = vreader.getBackgroundColor();
        foreGround = vreader.getForegroundColor();
        border = vreader.getBorder();
        
        vreader.setBackgroundColor(Color.WHITE);
        vreader.setForegroundColor(Color.BLACK);
        vreader.setBorder(0);
        vreader.refresh();
    }
    
    protected StoreColor (EdgeAttributesReader ereader) {

        foreGround = ereader.getLineColor();
        background = null;
        border = 0;
    }
    
    protected void restore (NodeAttributesReader vreader) {
        vreader.setBackgroundColor(background);
        vreader.setForegroundColor(foreGround);
        vreader.setBorder(border);
        vreader.refresh();
    }
    
    protected void restore (EdgeAttributesReader ereader) {
        ereader.setLineColor(foreGround);
        ereader.refresh();
    }
}
