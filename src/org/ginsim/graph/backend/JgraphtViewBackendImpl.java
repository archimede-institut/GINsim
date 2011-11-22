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

	private GraphViewListener listener;
    
	public JgraphtViewBackendImpl(JgraphtBackendImpl<?,?> jgraphtBackendImpl) {
		this.graphBackend = jgraphtBackendImpl;
	}

	public EdgeAttributesReader getEdgeAttributeReader() {
        if (fbEReader == null) {
            fbEReader = new FallBackEdgeAttributeReader(getEdgeVSMap());
        }
        return fbEReader;
	}
	public VertexAttributesReader getVertexAttributeReader() {
        if (fbVReader == null) {
        	System.out.println("create fallback");
            fbVReader = new FallbackVertexAttributeReader(this, getVertexVSMap());
        }
        return fbVReader;
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
	
	@Override
	public void addViewListener(GraphViewListener listener) {
		this.listener = listener;
	}
	
	public void refresh(Object o) {
		if (listener != null) {
			listener.refresh(o);
		}
	}
}
