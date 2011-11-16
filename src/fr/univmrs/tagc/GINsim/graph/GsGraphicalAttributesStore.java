package fr.univmrs.tagc.GINsim.graph;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;


public class GsGraphicalAttributesStore {

	public GsEdgeAttributesReader ereader;
	public GsVertexAttributesReader vreader;

	Map oldColors = new HashMap();
	Graph graph;
	
	public GsGraphicalAttributesStore( Graph graph) {
		
		this.graph = graph;
		this.ereader = graph.getEdgeAttributeReader();
		this.vreader = graph.getVertexAttributeReader();
	}
	
	public void storeAll() {
        Iterator it = graph.getVertices().iterator();
        while (it.hasNext()) {
            Object vertex = it.next();
            vreader.setVertex(vertex);
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
                vreader.setVertex(obj);
                ((StoreColor)oldColors.get(obj)).restore(vreader);
            }
        }
        oldColors.clear();
	}
	
	public void ensureStoreVertex(Object o) {
		vreader.setVertex(o);
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
		vreader.setVertex(o);
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
    protected StoreColor (GsVertexAttributesReader vreader) {
        background = vreader.getBackgroundColor();
        foreGround = vreader.getForegroundColor();
        border = vreader.getBorder();
        
        vreader.setBackgroundColor(Color.WHITE);
        vreader.setForegroundColor(Color.BLACK);
        vreader.setBorder(0);
        vreader.refresh();
    }
    
    protected StoreColor (GsEdgeAttributesReader ereader) {

        foreGround = ereader.getLineColor();
        background = null;
        border = 0;
    }
    
    protected void restore (GsVertexAttributesReader vreader) {
        vreader.setBackgroundColor(background);
        vreader.setForegroundColor(foreGround);
        vreader.setBorder(border);
        vreader.refresh();
    }
    
    protected void restore (GsEdgeAttributesReader ereader) {
        ereader.setLineColor(foreGround);
        ereader.refresh();
    }
}
