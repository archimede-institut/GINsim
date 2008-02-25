package fr.univmrs.tagc.GINsim.graph;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;


public class GsGraphicalAttributesStore {

	public GsEdgeAttributesReader ereader;
	public GsVertexAttributesReader vreader;

	Map oldColors = new HashMap();
	
	GsGraph graph;
	
	public GsGraphicalAttributesStore(GsGraph graph) {
		this.graph = graph;
		this.ereader = graph.getGraphManager().getEdgeAttributesReader();
		this.vreader = graph.getGraphManager().getVertexAttributesReader();
	}

	
	public void storeAll() {
        Iterator it = graph.getGraphManager().getVertexIterator();
        while (it.hasNext()) {
            Object vertex = it.next();
            vreader.setVertex(vertex);
            oldColors.put(vertex, new StoreColor(vreader));

            List l_edge = graph.getGraphManager().getOutgoingEdges(vertex);
            for (int j=0 ; j<l_edge.size() ; j++) {
                Object edge = ((GsDirectedEdge)l_edge.get(j)).getUserObject();
                ereader.setEdge(l_edge.get(j));
                oldColors.put(edge, new StoreColor(ereader));
            }
        }
	}
	
	public void restoreAll() {
        Iterator it = oldColors.keySet().iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if ( obj instanceof GsDirectedEdge) {
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
	
	public void ensureStoreEdge(GsDirectedEdge edge) {
        ereader.setEdge(edge);
        if (!oldColors.containsKey(edge)) {
            oldColors.put(edge, new StoreColor(ereader));
        }
	}
}

class StoreColor {
    private final Color background;
    private final Color foreGround;
    private final int border;
    
    protected static final int VERTEX = 0;
    protected static final int EDGE = 1;
    
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
