package fr.univmrs.ibdm.GINsim.jgraph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org._3pq.jgrapht.DirectedGraph;
import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.EdgeFactory;
import org._3pq.jgrapht.graph.AbstractGraph;

/**
 * a "simple" jgrapht implementation using hashmap.
 * it aims at "low" memory consumption without getting too slow.
 * 
 */
public class GsJGraphtBaseGraph extends AbstractGraph implements DirectedGraph {
    
    protected Map m_vertices = new HashMap(256);
    private EdgeFactory ef;
    private Set edgeSet = null;
    private Set vertexSet = null;
    private int edgeCount = 0;
    
    private static final List emptyList = new Vector();
    
    /**
     * 
     * @param ef the edge factory
     */
    public GsJGraphtBaseGraph( EdgeFactory ef ) {
        this.ef = ef;
    }

    public List getAllEdges(Object sourceVertex, Object targetVertex) {
        // no multiple edges here
        return null;
    }

    public Edge getEdge(Object sourceVertex, Object targetVertex) {
        VInfo vinfo = (VInfo)m_vertices.get(sourceVertex);
        if (vinfo == null) {
            return null;
        }
        return vinfo.getOutgoing(targetVertex);
    }

    public EdgeFactory getEdgeFactory() {
        return ef;
    }

    public Edge addEdge(Object sourceVertex, Object targetVertex) {
        
        // at the same time, check if vertices exists in graph AND replace them if necessary
        VInfo vinfo = (VInfo)m_vertices.get(sourceVertex);
        if (vinfo == null) {
            return null;
        }
        
        Object src = vinfo.self;
        vinfo = (VInfo)m_vertices.get(targetVertex);
        if (vinfo == null) {
            return null;
        }
        // really create/add the edge
        Edge e = ef.createEdge( src, vinfo.self );
        if (addEdge(e)) {
            return e;
        }
        return null;
    }

    public boolean addEdge(Edge e) {
        VInfo vinfo = (VInfo)m_vertices.get(e.getSource());
        if (vinfo.addOutgoing(e)) {
            vinfo = (VInfo)m_vertices.get(e.getTarget());
            vinfo.addIncoming(e);
            edgeCount++;
            return true;
        }
        return false;
    }

    public boolean addVertex(Object v) {
        if (m_vertices.containsKey(v)) {
            return false;
        }
        m_vertices.put(v, new VInfo( v ));
        return true;
    }

    public boolean containsEdge(Edge e) {
        VInfo vinfo = (VInfo)m_vertices.get(e.getSource());
        if (vinfo == null) {
            return false;
        }
        return vinfo.containsOutgoing(e);
    }

    public boolean containsVertex(Object v) {
        return m_vertices.containsKey(v);
    }

    public Set edgeSet() {
        if (edgeSet == null) {
            edgeSet = new EdgeSet();
        }
        return edgeSet;
    }

    public List edgesOf(Object vertex) {
        List l1 = incomingEdgesOf(vertex);
        List l2 = outgoingEdgesOf(vertex);
        if (l1 == null) {
            return l2;
        } else if (l2 == null) {
            return l1;
        } else {
            Vector l = new Vector(l1);
            l.addAll(l2);
            return l;
        }
    }

    public Edge removeEdge(Object sourceVertex, Object targetVertex) {
        VInfo vinfo = (VInfo)m_vertices.get(sourceVertex);
        if (vinfo == null) {
            return null;
        }
        Edge e = vinfo.getOutgoing(targetVertex);
        if (e != null) {
            vinfo.removeOutgoing(e);
            ((VInfo)m_vertices.get(targetVertex)).removeIncoming(e);
            edgeCount--;
        }
        return e;
    }

    public boolean removeEdge(Edge e) {
        VInfo vinfo = (VInfo)m_vertices.get(e.getSource());
        if (vinfo == null) {
            return false;
        }
        if (!vinfo.containsOutgoing(e)) {
            return false;
        }
        vinfo.removeOutgoing(e);
        ((VInfo)m_vertices.get(e.getTarget())).removeIncoming(e);
        edgeCount--;
        return true;
    }

    public boolean removeVertex(Object v) {
        VInfo vinfo = (VInfo)m_vertices.get(v);
        if (vinfo == null) {
            return false;
        }
        vinfo.cleanup(m_vertices);
        m_vertices.remove(v);
        return true;
    }

    public Set vertexSet() {
        if (vertexSet == null) {
            vertexSet = Collections.unmodifiableSet(m_vertices.keySet());
        }
        return vertexSet;
    }

    private class VInfo {
        Object self;
        List l_incoming;
        List l_outgoing;
        
        protected VInfo( Object o ) {
            self = o;
        }
        
        protected boolean addIncoming( Edge e ) {
            if (l_incoming == null) {
                l_incoming = new Vector();
                l_incoming.add(e);
                return true;
            }
            if (l_incoming.contains(e)) {
                return false;
            }
            l_incoming.add(e);
            return true;
        }
        protected boolean addOutgoing( Edge e ) {
            if (l_outgoing == null) {
                l_outgoing = new Vector();
                l_outgoing.add(e);
                return true;
            }
            if (l_outgoing.contains(e)) {
                return false;
            }
            l_outgoing.add(e);
            return true;
        }
        
        protected boolean containsOutgoing(Edge e) {
            if (l_outgoing == null) {
                return false;
            }
            return l_outgoing.contains(e);
        }
        
        protected void removeIncoming(Edge e) {
            if (l_incoming != null) {
                l_incoming.remove(e);
            }
        }
        protected void removeOutgoing(Edge e) {
            if (l_outgoing != null) {
                l_outgoing.remove(e);
            }
        }
        protected Edge getOutgoing(Object target) {
            if (l_outgoing == null) {
                return null;
            }
            for (int i=0 ; i<l_outgoing.size() ; i++) {
                Edge e = (Edge)l_outgoing.get(i);
                if (e.getTarget().equals(target)) {
                    return e;
                }
            }
            return null;
        }
        
        protected void cleanup( Map m_vertices ) {
            Iterator it;
            VInfo vinfo;
            if (l_incoming != null) {
                it = l_incoming.iterator();
                while (it.hasNext()) {
                    Edge e = (Edge)it.next();
                    vinfo = (VInfo)m_vertices.get(e.getSource());
                    vinfo.removeOutgoing(e);
                }
            }
            if (l_outgoing != null) {
                it = l_outgoing.iterator();
                while (it.hasNext()) {
                    Edge e = (Edge)it.next();
                    vinfo = (VInfo)m_vertices.get(e.getTarget());
                    vinfo.removeIncoming(e);
                }
            }
        }
    }
    
    private class EdgeSet implements Set {
        private GsJGraphtBaseGraph g;
        

        public int size() {
            return g.getEdgesCount();
        }

        public boolean isEmpty() {
            return (g.getEdgesCount() == 0);
        }

        public boolean contains(Object o) {
            return g.containsEdge((Edge)o);
        }

        public Iterator iterator() {
            return new EdgeIterator();
        }

        public Object[] toArray() {
            // shouldn't be needed
            return null;
        }

        public Object[] toArray(Object[] a) {
            // shouldn't be needed
            return a;
        }

        public boolean add(Object o) {
            return false;
        }

        public boolean remove(Object o) {
            return false;
        }

        public boolean containsAll(Collection c) {
            Iterator it = c.iterator();
            while (it.hasNext()) {
                if (!contains(it.next())) {
                    return false;
                }
            }
            return true;
        }

        public boolean addAll(Collection c) {
            return false;
        }

        public boolean retainAll(Collection c) {
            return false;
        }

        public boolean removeAll(Collection c) {
            return false;
        }

        public void clear() {
        }
    }
    
    private class EdgeIterator implements Iterator {

        Object next;
        Iterator i_vertices;
        Iterator i_edges;
        
        public boolean hasNext() {
            return (next != null);
        }

        public Object next() {
            Object ret = next;
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

        protected EdgeIterator() {
            this.i_vertices = m_vertices.values().iterator();
            selectNext();
        }
    }

    public int inDegreeOf(Object vertex) {
        return incomingEdgesOf(vertex).size();
    }

    public List incomingEdgesOf(Object vertex) {
        List l = ((VInfo)m_vertices.get(vertex)).l_incoming;
        if (l == null) {
            return emptyList;
        }
        return l;
    }

    public int outDegreeOf(Object vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    public List outgoingEdgesOf(Object vertex) {
        List l = ((VInfo)m_vertices.get(vertex)).l_outgoing;
        if (l == null) {
            return emptyList;
        }
        return l;
    }
    
    protected int getEdgesCount() {
        return edgeCount;
    }
}
