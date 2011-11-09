package org.ginsim.graph.backend;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsFallBackEdgeAttributeReader;
import fr.univmrs.tagc.GINsim.graph.GsFallbackVertexAttributeReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;

public class JgraphtViewBackendImpl implements GraphViewBackend {

	JgraphtBackendImpl<?, ?> graphBackend;
	
    private Map evsmap = null;
    private Map vvsmap = null;
    private GsEdgeAttributesReader fbEReader = null;
    private GsVertexAttributesReader fbVReader = null;

    GraphViewBackend viewBackend = null;
    
	public JgraphtViewBackendImpl(JgraphtBackendImpl<?,?> jgraphtBackendImpl) {
		this.graphBackend = jgraphtBackendImpl;
	}

	public GsEdgeAttributesReader getEdgeAttributeReader() {
		
		if (viewBackend != null) {
			return viewBackend.getEdgeAttributeReader();
		}
        return getFallBackEReader();
	}
	public GsVertexAttributesReader getVertexAttributeReader() {
		if (viewBackend != null) {
			return viewBackend.getVertexAttributeReader();
		}
        return getFallBackVReader();
	}

	/**
	 * Change the back-end used for graph view.
	 * Used to switch from the generic back-end to a jgraph one.
	 * @param backend
	 */
	public void setGraphViewBackend(GraphViewBackend backend) {
		// FIXME: transfer view info from one to the other
		this.viewBackend = backend;
	}
	
	/**
	 * @return the place where local VS data is stored (create it if needed)
	 * @see #hasFallBackVSData()
	 */
    protected Map getEdgeVSMap() {
        if (evsmap == null) {
            evsmap = new HashMap();
        }
        return evsmap;
    }
    
    protected Map getVertexVSMap() {
        if (vvsmap == null) {
            vvsmap = new HashMap();
        }
        return vvsmap;
    }
	
    /**
     * @return a generic edgeAttribute storing it's data to a local hashMap
     */
    protected GsEdgeAttributesReader getFallBackEReader() {
        if (fbEReader == null) {
            fbEReader = new GsFallBackEdgeAttributeReader(getEdgeVSMap());
        }
        return fbEReader;
    }
    /**
     * @return a generic vertexAttributeReader storing it's data to a local hashMap
     */
    protected GsVertexAttributesReader getFallBackVReader() {
        if (fbVReader == null) {
            fbVReader = new GsFallbackVertexAttributeReader(getVertexVSMap());
        }
        return fbVReader;
    }
}
