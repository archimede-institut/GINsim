package org.ginsim.graph.backend;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.FallBackEdgeAttributeReader;
import org.ginsim.graph.common.FallbackVertexAttributeReader;
import org.ginsim.graph.common.VertexAttributesReader;


public class JgraphtViewBackendImpl implements GraphViewBackend {

	JgraphtBackendImpl<?, ?> graphBackend;
	
    private Map evsmap = null;
    private Map vvsmap = null;
    private EdgeAttributesReader fbEReader = null;
    private VertexAttributesReader fbVReader = null;

    GraphViewBackend viewBackend = null;
    
	public JgraphtViewBackendImpl(JgraphtBackendImpl<?,?> jgraphtBackendImpl) {
		this.graphBackend = jgraphtBackendImpl;
	}

	public EdgeAttributesReader getEdgeAttributeReader() {
		
		if (viewBackend != null) {
			return viewBackend.getEdgeAttributeReader();
		}
        return getFallBackEReader();
	}
	public VertexAttributesReader getVertexAttributeReader() {
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
		if (viewBackend != null && backend != null) {
			backend.getVertexAttributeReader().copyFrom(viewBackend.getVertexAttributeReader());
			backend.getEdgeAttributeReader().copyFrom(viewBackend.getEdgeAttributeReader());
		}
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
    protected EdgeAttributesReader getFallBackEReader() {
        if (fbEReader == null) {
            fbEReader = new FallBackEdgeAttributeReader(getEdgeVSMap());
        }
        return fbEReader;
    }
    /**
     * @return a generic vertexAttributeReader storing it's data to a local hashMap
     */
    protected VertexAttributesReader getFallBackVReader() {
        if (fbVReader == null) {
            fbVReader = new FallbackVertexAttributeReader(getVertexVSMap());
        }
        return fbVReader;
    }
}
