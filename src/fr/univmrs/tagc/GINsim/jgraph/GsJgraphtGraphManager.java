package fr.univmrs.tagc.GINsim.jgraph;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org._3pq.jgrapht.DirectedGraph;
import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.ListenableGraph;
import org._3pq.jgrapht.alg.DijkstraShortestPath;
import org._3pq.jgrapht.alg.StrongConnectivityInspector;
import org._3pq.jgrapht.ext.JGraphModelAdapter;
import org._3pq.jgrapht.graph.ListenableDirectedGraph;
import org.jgraph.graph.*;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.*;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;

/**
 * Implementation of a graphManager using jgraph/jgrapht.
 * 
 */
public class GsJgraphtGraphManager extends GsGraphManager {

    private ListenableGraph     	g 				= null;
    private JGraphModelAdapter 		m_jgAdapter    	= null;
    private GsJgraph 				jgraph 			= null;
    private GsGraph 				gsGraph		    = null;
    private GsParallelEdgeRouting 	pedgerouting	= null;
    private GraphUndoManager	    undoManager		= null;
    
    private boolean visible = false;
    
    private int vertexCount = 0;
    
    private int curX = 10;
    private int curY = 10;
    
    private static final int minX = 10;
    private static final int maxX = 700;
    private static final int incX = 120;
    private static final int incY = 40;
    
    private AttributeMap defaultVertexAttr;
    private AttributeMap defaultEdgeAttr;

    /**
     * 
     * @param gsGraph
     * @param mainFrame
     */
    public GsJgraphtGraphManager(GsGraph gsGraph, GsMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.gsGraph = gsGraph;
        
        // use a lighter implementation of a directedGraph!
        g = new ListenableDirectedGraph(new GsJGraphtBaseGraph(new GsJgraphtEdgeFactory()) );
        
        // and keep the standard one not too far away...
        // g = new ListenableDirectedGraph(new DefaultDirectedGraph(new GsJgraphtEdgeFactory()) );

        setMainFrame(mainFrame);
    }
    
    public JComponent getGraphPanel() {
        return jgraph;
    }

    public JPanel getGraphMapPanel(JScrollPane sp) {
        return GPOverviewPanel.createOverviewPanel(this, sp);
    }

    public boolean addVertex(Object vertex) {
        if (g.addVertex(vertex)) {
            if (visible) {
                positionVertexAuto(vertex);
            }
            vertexCount++;
            return true;
        }
        return false;
    }

    /**
     * @param vertex
     */
    private void positionVertexAuto(Object vertex) {
        placeVertex(vertex, curX, curY);
        curX += incX;
        if (curX > maxX) {
            curX = minX;
            curY += incY;
        }
    }

    public Object addEdge(Object source, Object target, Object data) {
    		Edge newedge = g.addEdge(source, target);
    		if (newedge == null) {
    		    return null;
    		}
    		((GsDirectedEdge)newedge).setUserObject(data);
    		
    		if (visible) {
    			if (source == target) {
    				DefaultEdge[] t_cell = {m_jgAdapter.getEdgeCell(newedge)};
					GraphConstants.setRouting( t_cell[0].getAttributes(), pedgerouting);
        			GraphConstants.setLineStyle(t_cell[0].getAttributes(), GraphConstants.STYLE_BEZIER);
        			GraphConstants.setRemoveAttributes(
        					t_cell[0].getAttributes(),
        					new Object[] { GraphConstants.POINTS });
        			
            		m_jgAdapter.cellsChanged(t_cell);
    			} else {
	    			Edge edge = g.getEdge(target, source);
	        		if ( edge != null) {
	    				DefaultEdge[] t_cell = {m_jgAdapter.getEdgeCell(edge), m_jgAdapter.getEdgeCell(newedge)};
	    				if (t_cell[0] != null) {
	    				    GraphConstants.setRouting( t_cell[0].getAttributes(), pedgerouting);
	            			GraphConstants.setLineStyle(t_cell[0].getAttributes(), GraphConstants.STYLE_BEZIER);
	    				}
	    				if (t_cell[1] != null) {
	    				    GraphConstants.setRouting( t_cell[1].getAttributes(), pedgerouting);
	            			GraphConstants.setLineStyle(t_cell[1].getAttributes(), GraphConstants.STYLE_BEZIER);
	    				}
	    				m_jgAdapter.cellsChanged(t_cell);
	        		}					
        		}
    		}
        return newedge;
    }

    public void placeVertex( Object vertex, int x, int y ) {
        if (!visible) {
			return;
		}
        DefaultGraphCell cell   = m_jgAdapter.getVertexCell( vertex );
        AttributeMap     attr   = cell.getAttributes();
        Rectangle2D      bounds = GraphConstants.getBounds( attr );

        Rectangle2D newBounds =
            new Rectangle2D.Double( x, y, bounds.getWidth(),
                bounds.getHeight() );

        GraphConstants.setBounds( attr, newBounds );

        AttributeMap cellAttr = new AttributeMap();
        cellAttr.put( cell, attr );
        m_jgAdapter.edit( cellAttr, null, null, null );
    }

    /**
     * @return the jgraph graph behind this graphManager.
     */
    public GsJgraph getJgraph() {
        return jgraph;
    }

    public void selectAll() {
        jgraph.setSelectionCells(jgraph.getRoots());
    }

    public void select(List l) {
        jgraph.setSelectionCells( new Object[0]);
        if (l == null) {
            return;
        }
        for (int i=0 ; i<l.size() ; i++) {
            Object o = l.get(i);
            if (o instanceof GsDirectedEdge) {
                jgraph.addSelectionCell(m_jgAdapter.getEdgeCell((Edge)o));
            } else {
                jgraph.addSelectionCell(m_jgAdapter.getVertexCell(o));
            }
        }
    }
    
    public void vertexToFront(boolean b) {
        if (!visible) {
            return;
        }
        // move all vertex to front;
        Object[] t = getVertexArray();
        for (int i=0 ; i<t.length ; i++) {
            t[i] = m_jgAdapter.getVertexCell(t[i]);
        }
        if (b) {
            m_jgAdapter.toFront(t);
        } else {
            m_jgAdapter.toBack(t);
        }
    }
    
    public void invertSelection() {
		Object[] selects = jgraph.getSelectionCells();
		Object roots[] = jgraph.getRoots();
		int len = roots.length;
		int nbsel = selects.length;
		Vector toselect = new Vector(len - nbsel);
		for (int i=0 ; i<len ; i++) {
			toselect.add(roots[i]);
		}
		
		for (int i=len-1 ; i>=0 ; i--) {
			Object cur = roots[i];
			for (int j=0 ; j<nbsel ; j++) {
				if (selects[j] == cur) {
					toselect.remove(i);
					break;
				}
			}
		}
		jgraph.setSelectionCells(toselect.toArray());

    }

    public void showGrid(boolean b) {
        jgraph.setGridVisible(b);
    }
    
    public void setGridActive(boolean b) {
        jgraph.setGridEnabled(b);
    }

    public void zoomOut() {
        jgraph.setScale(jgraph.getScale()-0.1);
    }

    public void zoomIn() {
        jgraph.setScale(jgraph.getScale()+0.1);
    }

    public void zoomNormal() {
        jgraph.setScale(1);
    }

    public void displayEdgeName(boolean b) {
       jgraph.setEdgeLabelDisplayed(b);
    }

    /**
     * show/hide vertex name: not applicable as they are always visible.
     * @see fr.univmrs.tagc.GINsim.graph.GsGraphManager#displayVertexName(boolean)
     */
    public void displayVertexName(boolean b) {
    		// NOTHING
    }
    
    public void undo() {
		if (undoManager.canUndo()) {
			undoManager.undo();
		}
    }

    public void redo() {
    		if (undoManager.canRedo()) {
    			undoManager.redo();
    		}
    }

    public void delete() {
		//if selection
		if (jgraph.getSelectionCount()>0) {
			//get selected cells
			Object[] cells=jgraph.getSelectionCells();
            // just in case: empty the selection before
            jgraph.setSelectionCells(new Object[0]);
			for (int i=0;i<cells.length;i++) {
				//get jgrapht object
				Object obj=((DefaultGraphCell)cells[i]).getUserObject();
				//if it's an edge
				if (obj instanceof Edge) {
				    gsGraph.removeEdge(obj);
				//else it's a node
				} else {
				    gsGraph.removeVertex(obj);
				}
			}
		}
    }

    public void removeVertex(Object obj) {
        g.removeVertex(obj);
        vertexCount--;
    }

    public Object getEdge(Object source, Object target) {
        return g.getEdge(source, target);
    }

    public GsGraph getGsGraph() {
        return gsGraph;
    }

    public Iterator getVertexIterator() {
        return g.vertexSet().iterator();
    }

    public Iterator getEdgeIterator() {
        return g.edgeSet().iterator();
    }

	public Iterator getSelectedEdgeIterator() {
		if (visible) {
			return new GsSelectedEdgeIterator(mainFrame.getSelectedVertices(), mainFrame.getSelectedEdges());
		}
        return g.edgeSet().iterator();
	}

	public Iterator getSelectedVertexIterator() {
		if (visible) {
			return new GsSelectedVertexIterator(mainFrame.getSelectedVertices());
		}
        return g.vertexSet().iterator();
	}

    public List getIncomingEdges(Object vertex) {
    	if (g instanceof ListenableDirectedGraph) {
    		return ((ListenableDirectedGraph)g).incomingEdgesOf(vertex);
    	}
        return g.edgesOf(vertex);
    }

    public List getOutgoingEdges(Object vertex) {
        if (g instanceof ListenableDirectedGraph) {
            return ((ListenableDirectedGraph)g).outgoingEdgesOf(vertex);
        }
        return g.edgesOf(vertex);
    }

    /**
     * @return the mainFrame in which this graph is opened (may be null)
     */
    public GsMainFrame getMainFrame() {
        return mainFrame;
    }

    public void removeEdge(Object source, Object target) {
		if (visible) {
			Edge edge = g.getEdge(target, source);
			DefaultEdge de = m_jgAdapter.getEdgeCell(edge);
	    		if ( edge != null && GraphConstants.getRouting(de.getAttributes()) == pedgerouting) {
	    			AttributeMap attr = de.getAttributes();
	    		    de.getAttributes().remove(GraphConstants.ROUTING);
			        List l = GraphConstants.getPoints(attr);
                    if (l != null) {
                        while ( l.size() > 2) {
                            l.remove(1);
                        }
                        GraphConstants.setPoints(attr, l);
                    }

	    			m_jgAdapter.cellsChanged(new Object[] {de});
	    		}
		}
        g.removeEdge(source, target);
    }

    public void ready() {
        if (mainFrame == null) {
            visible = false;
        } else {
	        new GsMarqueeHandler(this);
	        visible = true;
        }        
    }

    public GsVertexAttributesReader getVertexAttributesReader() {
        if (visible) {
            return new GsJgraphVertexAttribute(this);
        }
        return getFallBackVReader();
    }

    public GsEdgeAttributesReader getEdgeAttributesReader() {
        if (visible) {
            return new GsJgraphEdgeAttribute(this);
        }
        return getFallBackEReader();
    }

    /**
     * @param vertex
     * @return the vertex's attributeMap
     */
    public AttributeMap getVertexAttributesMap(Object vertex) {
        return m_jgAdapter.getVertexCell( vertex ).getAttributes();
    }

    public int getVertexCount() {
    		return vertexCount;
    }

	public void setMainFrame(GsMainFrame m) {
		mainFrame = m;
        if (mainFrame != null) {
            defaultVertexAttr = JGraphModelAdapter.createDefaultVertexAttributes();
            defaultEdgeAttr = JGraphModelAdapter.createDefaultEdgeAttributes(g);
            
	        GsJgraphEdgeAttribute.applyDefault(defaultEdgeAttr);
	        GsJgraphVertexAttribute.applyDefault(defaultVertexAttr);
	        pedgerouting = new GsParallelEdgeRouting();
            
            m_jgAdapter = new JGraphModelAdapter(g, defaultVertexAttr, defaultEdgeAttr);
	        jgraph = new GsJgraph( this );
	        visible = true;
            rereadVS();
        }
	}

	public Object getVertexByName(String id) {
		return super.getVertexByName(id);
	}

	/**
	 * @return Returns the defaultEdgeAttr.
	 */
	public AttributeMap getDefaultEdgeAttr() {
		return defaultEdgeAttr;
	}
	/**
	 * @return Returns the defaultVertexAttr.
	 */
	public AttributeMap getDefaultVertexAttr() {
		return defaultVertexAttr;
	}
	/**
	 * @return the jgrapht to jgraph model adapter
	 */
	public JGraphModelAdapter getM_jgAdapter() {
		return m_jgAdapter;
	}
	/**
	 * @return the parallel edge routing
	 */
	public GsParallelEdgeRouting getPedgerouting() {
		return pedgerouting;
	}

	public Object[] getVertexArray() {
		return g.vertexSet().toArray();
	}
	/**
	 * @return the jgrapht graph
	 */
    public ListenableGraph getG() {
        return g;
    }

    /**
     * @return the list of strong connected components
     * 
     */
    public List getStrongComponent() {
        return new StrongConnectivityInspector((DirectedGraph)g).stronglyConnectedSets();
    }

    /**
     * read existing graphic attributes and put them into the new jgraph.
     */
    private void rereadVS() {
        if (hasFallBackVSData()) {
            Map vsdata = getEdgeVSMap();
            Iterator it = vsdata.keySet().iterator();
            GsEdgeAttributesReader fereader = getFallBackEReader();
            GsEdgeAttributesReader ereader = getEdgeAttributesReader();
            ereader.copyDefaultFrom(fereader);
            while (it.hasNext()) {
                Object o = it.next();
                fereader.setEdge(o);
                ereader.setEdge(o);
                ereader.copyFrom(fereader);
            }
            vsdata = getVertexVSMap();
            it = vsdata.keySet().iterator();
            GsVertexAttributesReader fvreader = getFallBackVReader();
            GsVertexAttributesReader vreader = getVertexAttributesReader();
            
            while (it.hasNext()) {
                Object o = it.next();
                fvreader.setVertex(o);
                vreader.setVertex(o);
                vreader.copyFrom(fvreader);
            }
        }
    }
    
    public List getShortestPath(Object source, Object target) {
        return DijkstraShortestPath.findPathBetween(g, source, target);
    }

    public boolean containsVertex(Object vertex) {
        return g.containsVertex(vertex);
    }
    
    public boolean containsEdge(Object from, Object to) {
        return g.containsEdge(from, to);
    }

    public boolean isGridDisplayed() {
        if (!visible) {
            return false;
        }
        return jgraph.isGridVisible();
    }

    public boolean isGridActive() {
        if (!visible) {
            return false;
        }
        return jgraph.isGridEnabled();
    }

    public void select(Object obj) {
        jgraph.setSelectionCells( new Object[0]);
        if (obj == null) {
            return;
        }
        if (obj instanceof GsDirectedEdge) {
        	if (obj instanceof Edge) {
                jgraph.addSelectionCell(m_jgAdapter.getEdgeCell((Edge)obj));
			} else {
				GsDirectedEdge de = (GsDirectedEdge)obj;
				jgraph.addSelectionCell(m_jgAdapter.getEdgeCell((Edge)getEdge(de.getSourceVertex(), de.getTargetVertex())));
			}
        } else {
            jgraph.addSelectionCell(m_jgAdapter.getVertexCell(obj));
        }
    }
    
    public BufferedImage getImage() {
    	if (jgraph != null) {
    		return jgraph.getImage(Color.WHITE, 0);
    	}
    	return null;
    }
}
