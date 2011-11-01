package org.ginsim.graph;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsFallBackEdgeAttributeReader;
import fr.univmrs.tagc.GINsim.graph.GsFallbackVertexAttributeReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;

public class JgraphtViewBackendImpl implements GraphViewBackend {

	JgraphtBackendImpl<?, ?> graph;
	
    private Map evsmap = null;
    private Map vvsmap = null;
    private GsEdgeAttributesReader fbEReader = null;
    private GsVertexAttributesReader fbVReader = null;

	public JgraphtViewBackendImpl(JgraphtBackendImpl<?,?> jgraphtBackendImpl) {
		this.graph = jgraphtBackendImpl;
	}

	public GsEdgeAttributesReader getEdgeReader() {
        return getFallBackEReader();
	}
	public GsVertexAttributesReader getVertexReader() {
        return getFallBackVReader();
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
