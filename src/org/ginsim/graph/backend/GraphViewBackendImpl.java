package org.ginsim.graph.backend;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.FallBackEdgeAttributeReader;
import org.ginsim.graph.common.FallbackNodeAttributeReader;
import org.ginsim.graph.common.NodeAttributesReader;


public class GraphViewBackendImpl implements GraphViewBackend {

    private Map evsmap = null;
    private Map vvsmap = null;

	private GraphViewListener listener;
    
	public EdgeAttributesReader getEdgeAttributeReader() {
		return  new FallBackEdgeAttributeReader(this, getEdgeVSMap());
	}
	public NodeAttributesReader getNodeAttributeReader() {
		return new FallbackNodeAttributeReader(this, getNodeVSMap());
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
    
    protected Map getNodeVSMap() {
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
