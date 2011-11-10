package org.ginsim.graph.backend;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractGraph;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;

/**
 * a "simple" jgrapht implementation using hashmap.
 * it aims at "low" memory consumption without getting too slow.
 */
public class GsJGraphtBaseGraph<V,E extends GsDirectedEdge<V>> extends AbstractGraph<V, E> implements DirectedGraph<V,E> {
    
    protected Map<V, VInfo<V,E>> m_vertices = new HashMap<V, VInfo<V,E>>(10);
    private EdgeFactory<V,E> ef;
    private Set<E> edgeSet = null;
    private Set<V> vertexSet = null;
    private int edgeCount = 0;
    
    private static final Set emptySet = new HashSet();
    
    /**
     * 
     * @param ef the edge factory
     */
    public GsJGraphtBaseGraph( EdgeFactory<V,E> ef ) {
        this.ef = ef;
    }

    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        // no multiple edges here
        return null;
    }

    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        VInfo<V,E> vinfo = m_vertices.get(sourceVertex);
        if (vinfo == null) {
            return null;
        }
        return vinfo.getOutgoing(targetVertex);
    }

    @Override
    public EdgeFactory<V,E> getEdgeFactory() {
        return ef;
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        // at the same time, check if vertices exists in graph AND replace them if necessary
        VInfo<V,E> vinfo = m_vertices.get(sourceVertex);
        if (vinfo == null) {
            return null;
        }
        
        V src = vinfo.self;
        vinfo = m_vertices.get(targetVertex);
        if (vinfo == null) {
            return null;
        }
        // really create/add the edge
        E e = ef.createEdge( src, vinfo.self );
        if (addEdge(sourceVertex, targetVertex, e)) {
            return e;
        }
        return null;
    }

	@Override
	public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        VInfo<V,E> vinfo = m_vertices.get(sourceVertex);
        if (vinfo.addOutgoing(e)) {
            vinfo = m_vertices.get(targetVertex);
            vinfo.addIncoming(e);
            edgeCount++;
            return true;
        }
        return false;
    }

    @Override
    public boolean addVertex(V v) {
        if (m_vertices.containsKey(v)) {
            return false;
        }
        m_vertices.put(v, new VInfo<V,E>( v ));
        return true;
    }

    @Override
    public boolean containsEdge(E e) {
        VInfo<V,E> vinfo = m_vertices.get(e.getSource());
        if (vinfo == null) {
            return false;
        }
        return vinfo.containsOutgoing(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return m_vertices.containsKey(v);
    }

    @Override
    public Set<E> edgeSet() {
        if (edgeSet == null) {
            edgeSet = new EdgeSet(this);
        }
        return edgeSet;
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        Set<E> l1 = incomingEdgesOf(vertex);
        Set<E> l2 = outgoingEdgesOf(vertex);
        if (l1 == null) {
            return l2;
        } else if (l2 == null) {
            return l1;
        } else {
            Set<E> l = new HashSet<E>(l1);
            l.addAll(l2);
            return l;
        }
    }

    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        VInfo<V,E> vinfo = m_vertices.get(sourceVertex);
        if (vinfo == null) {
            return null;
        }
        E e = vinfo.getOutgoing(targetVertex);
        if (e != null) {
            vinfo.removeOutgoing(e);
            m_vertices.get(targetVertex).removeIncoming(e);
            edgeCount--;
        }
        return e;
    }

    @Override
    public boolean removeEdge(E e) {
        VInfo<V,E> vinfo = m_vertices.get(e.getSource());
        if (vinfo == null) {
            return false;
        }
        if (!vinfo.containsOutgoing(e)) {
            return false;
        }
        vinfo.removeOutgoing(e);
        m_vertices.get(e.getTarget()).removeIncoming(e);
        edgeCount--;
        return true;
    }

    @Override
    public boolean removeVertex(V v) {
        VInfo<V,E> vinfo = m_vertices.get(v);
        if (vinfo == null) {
            return false;
        }
        vinfo.cleanup(m_vertices);
        m_vertices.remove(v);
        return true;
    }

    @Override
    public Set<V> vertexSet() {
        if (vertexSet == null) {
            vertexSet = Collections.unmodifiableSet(m_vertices.keySet());
        }
        return vertexSet;
    }

    @Override
    public int inDegreeOf(V vertex) {
        return incomingEdgesOf(vertex).size();
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        Set<E> l = m_vertices.get(vertex).l_incoming;
        if (l == null) {
            return emptySet;
        }
        return l;
    }

    @Override
    public int outDegreeOf(V vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        Set<E> l = m_vertices.get(vertex).l_outgoing;
        if (l == null) {
            return emptySet;
        }
        return l;
    }
    
	@Override
	public V getEdgeSource(E e) {
		return e.getSource();
	}

	@Override
	public V getEdgeTarget(E e) {
		return e.getTarget();
	}

	@Override
	public double getEdgeWeight(E e) {
		return 0;
	}

	protected int getEdgesCount() {
		return edgeCount;
	}
	protected Collection<VInfo<V, E>> getVInfoSet() {
		return m_vertices.values();
	}
}

/**
 * Store information about a node and its incoming and outgoing edges.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
class VInfo<V,E extends GsDirectedEdge<V>> {
    V self;
    Set<E> l_incoming;
    Set<E> l_outgoing;
    
    protected VInfo( V o ) {
        self = o;
    }
    
    protected boolean addIncoming(E e ) {
        if (l_incoming == null) {
            l_incoming = new HashSet<E>();
            l_incoming.add(e);
            return true;
        }
        if (l_incoming.contains(e)) {
            return false;
        }
        l_incoming.add(e);
        return true;
    }
    protected boolean addOutgoing(E e ) {
        if (l_outgoing == null) {
            l_outgoing = new HashSet<E>();
            l_outgoing.add(e);
            return true;
        }
        if (l_outgoing.contains(e)) {
            return false;
        }
        l_outgoing.add(e);
        return true;
    }
    
    protected boolean containsOutgoing(E e) {
        if (l_outgoing == null) {
            return false;
        }
        return l_outgoing.contains(e);
    }
    
    protected void removeIncoming(E e) {
        if (l_incoming != null) {
            l_incoming.remove(e);
        }
    }
    protected void removeOutgoing(E e) {
        if (l_outgoing != null) {
            l_outgoing.remove(e);
        }
    }
    protected E getOutgoing(V target) {
        if (l_outgoing == null) {
            return null;
        }
        for (E e: l_outgoing) {
            if (e.getTarget().equals(target)) {
                return e;
            }
        }
        return null;
    }
    
    protected void cleanup( Map<V,VInfo<V, E>> m_vertices ) {
        if (l_incoming != null) {
            for (E e: l_incoming) {
                m_vertices.get(e.getSource()).removeOutgoing(e);
            }
        }
        if (l_outgoing != null) {
            for (E e: l_outgoing) {
                m_vertices.get(e.getTarget()).removeIncoming(e);
            }
        }
    }
}

class EdgeSet<V,E extends GsDirectedEdge<V>> implements Set<E> {
	
    private final GsJGraphtBaseGraph<V,E> g;
    
    public EdgeSet(GsJGraphtBaseGraph<V, E> graphManager) {
		this.g = graphManager;
	}

	public int size() {
        return g.getEdgesCount();
    }

    public boolean isEmpty() {
        return (size() == 0);
    }

    public boolean contains(E edge) {
        return g.containsEdge(edge);
    }

    public Iterator<E> iterator() {
        return new EdgeIterator<V,E>(g.getVInfoSet());
    }

    public Object[] toArray() {
        return null;
    }

    public boolean add(E edge) {
        return false;
    }
    public void clear() {
    }

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
        for (Object o: c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}
}

class EdgeIterator<V,E extends GsDirectedEdge<V>> implements Iterator<E> {

    E next;
    Iterator<VInfo<V, E>> i_vertices;
    Iterator<E> i_edges;
    
    public boolean hasNext() {
        return (next != null);
    }

    public E next() {
        E ret = next;
        selectNext();
        return ret;
    }

    public void remove() {
        // unsupported
    }
    
    private void selectNext() {
        next = null;
        if (i_edges != null && i_edges.hasNext()) {
            next = i_edges.next();
        } else while (i_vertices.hasNext()) {
            VInfo vinfo = (VInfo)i_vertices.next();
            if (vinfo.l_outgoing != null) {
                i_edges = vinfo.l_outgoing.iterator();
                if (i_edges.hasNext()) {
                    next = i_edges.next();
                    break;
                }
            }
        }
    }

    protected EdgeIterator(Collection<VInfo<V, E>> infoSet) {
        this.i_vertices = infoSet.iterator();
        selectNext();
    }
}