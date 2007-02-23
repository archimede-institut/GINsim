package fr.univmrs.ibdm.GINsim.piccolo;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.umd.cs.piccolox.swing.PScrollPane;
import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.gui.GsMainFrame;

public class GsSimpleGraphManager extends GsGraphManager {

	HashMap m_nodes = new HashMap();
	GsGraph gsgraph;
	GsPCanvas canvas = null;
	PScrollPane psp = null;
	
	private GsSimpleVertexAttributeReader vreader;
	private GsSimpleEdgeAttributeReader ereader;
	
	public GsSimpleGraphManager (GsGraph gsGraph) {
		this.gsgraph = gsGraph;
	}
	
	public Object addEdge(Object source, Object target, Object data) {
		NodeInfo ni_s = (NodeInfo)m_nodes.get(source);
		if (ni_s == null) {
			return null;
		}
		NodeInfo ni_t;
		if (source == target) {
			ni_t = ni_s;
		} else {
			ni_t = (NodeInfo)m_nodes.get(target);
			if (ni_t == null) {
				return null;
			}
		}
		for (int i=ni_s.v_outgoing.size()-1 ; i>=0 ; i--) {
			GsDirectedEdge de = (GsDirectedEdge)ni_s.v_outgoing.get(i);
			if (de.getTargetVertex() == target) {
				return de;
			}
		}
		ArcInfo arc = new ArcInfo(source, target, data);
		ni_s.v_outgoing.add(arc);
		ni_t.v_incoming.add(arc);
		return arc;
	}

	public boolean addVertex(Object vertex) {
		Object r = m_nodes.put(vertex,	new NodeInfo(vertex));
		return true;
	}

	public boolean containsEdge(Object from, Object to) {
		NodeInfo ni = (NodeInfo)m_nodes.get(from);
		if (ni == null) {
			return false;
		}
		for (int i=ni.v_outgoing.size()-1 ; i>=0 ; i++) {
			GsDirectedEdge de = (GsDirectedEdge)ni.v_outgoing.get(i);
			if (to == de.getTargetVertex() ) {
				return true;
			}
		}
		return false;
	}

	public boolean containsVertex(Object vertex) {
		return m_nodes.containsKey(vertex);
	}

	public Object getEdge(Object source, Object target) {
		NodeInfo ni = (NodeInfo)m_nodes.get(source);
		if (ni == null) {
			return null;
		}
		for (int i=ni.v_outgoing.size()-1 ; i>=0 ; i--) {
			GsDirectedEdge de = (GsDirectedEdge)ni.v_outgoing.get(i);
			if (target == de.getTargetVertex() ) {
				return de;
			}
		}
		return null;
	}

	public GsEdgeAttributesReader getEdgeAttributesReader() {
		if (ereader == null) {
			ereader = new GsSimpleEdgeAttributeReader();
		}
		return ereader;
	}

	public Iterator getEdgeIterator() {
		return new GsEdgeIterator(m_nodes);
	}

	public JPanel getGraphMapPanel(JScrollPane sp) {
		// TODO Auto-generated method stub
		return new JPanel();
	}

	public JComponent getGraphPanel() {
		if (canvas == null) {
			canvas = new GsPCanvas(gsgraph);
			psp = new PScrollPane(canvas);
		}
		return psp;
	}

	public GsGraph getGsGraph() {
		return gsgraph;
	}

	public List getIncomingEdges(Object vertex) {
		NodeInfo ni = (NodeInfo)m_nodes.get(vertex);
		if (ni == null) {
			return null;
		}
		return ni.v_incoming;
	}

	public List getOutgoingEdges(Object vertex) {
		NodeInfo ni = (NodeInfo)m_nodes.get(vertex);
		if (ni == null) {
			return null;
		}
		return ni.v_outgoing;
	}

	public Iterator getSelectedEdgeIterator() {
		if (canvas == null) {
			return null;
		}
		return canvas.v_selectedArcs.iterator();
	}

	public Iterator getSelectedVertexIterator() {
		if (canvas == null) {
			return null;
		}
		return canvas.v_selectedNodes.iterator();
	}

	public List getShortestPath(Object source, Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[] getVertexArray() {
		return m_nodes.keySet().toArray();
	}

	public GsVertexAttributesReader getVertexAttributesReader() {
		if (vreader == null) {
			vreader = new GsSimpleVertexAttributeReader(m_nodes);
		}
		return vreader;
	}

	public int getVertexCount() {
		return m_nodes.size();
	}

	public Iterator getVertexIterator() {
		return m_nodes.keySet().iterator();
	}

	public Iterator getVertexInfoIterator() {
		return m_nodes.values().iterator();
	}

	public void placeVertex(Object vertex, int x, int y) {
		NodeInfo ni = (NodeInfo)m_nodes.get(vertex);
		if (ni == null) {
			return;
		}
		ni.x = x;
		ni.y = y;
	}

	public void ready() {
		// TODO Auto-generated method stub

	}


	public void removeEdge(Object source, Object target) {
		// TODO Auto-generated method stub

	}

	public void removeVertex(Object obj) {
		// TODO Auto-generated method stub

	}


	public void setMainFrame(GsMainFrame m) {
		mainFrame = m;
	}

	
	public void select(Vector v) {
		// TODO Auto-generated method stub
	}
	public void selectAll() {
		// TODO Auto-generated method stub
	}
	public void invertSelection() {
		// TODO Auto-generated method stub
	}
	public boolean isGridActive() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isGridDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	public void setGridActive(boolean b) {
		// TODO Auto-generated method stub
	}
	public void showGrid(boolean b) {
		// TODO Auto-generated method stub
	}
	public void undo() {
		// TODO Auto-generated method stub
	}
	public void redo() {
		// TODO Auto-generated method stub
	}
	public void vertexToFront(boolean b) {
		// TODO Auto-generated method stub
	}
	public void zoomIn() {
		// TODO Auto-generated method stub
	}
	public void zoomNormal() {
		// TODO Auto-generated method stub
	}
	public void zoomOut() {
		// TODO Auto-generated method stub

	}
	public void delete() {
		// TODO Auto-generated method stub

	}
	public void displayEdgeName(boolean b) {
		// TODO Auto-generated method stub

	}
	public void displayVertexName(boolean b) {
		// TODO Auto-generated method stub

	}

	public NodeInfo getvertexInfo(Object data) {
		return (NodeInfo)m_nodes.get(data);
	}

	public void select(Object obj) {
		// TODO Auto-generated method stub
		
	}
}

class NodeInfo {
	public static final int SHAPE_RECT = 0;
	public static final int SHAPE_ELLIPSE = 1;
	
	Object data;
	Vector v_incoming = new Vector();
	Vector v_outgoing = new Vector();

	protected NodeInfo(Object data) {
		this.data = data;
	}

	int x;
	int y;
	
	int shape = SHAPE_RECT;
	
	int height = 20;
	int width = 30;
	
	Color fg = Color.BLACK;
	Color bg = Color.WHITE;
}

class ArcInfo implements GsDirectedEdge {
	Object source;
	Object target;
	Object data;
	
	Color col = Color.BLACK;

	public ArcInfo(Object source, Object target, Object data) {
		this.source = source;
		this.target = target;
		this.data = data;
	}

	public Object getSourceVertex() {
		return source;
	}

	public Object getTargetVertex() {
		return target;
	}

	public Object getUserObject() {
		return data;
	}

	public void setUserObject(Object obj) {
		this.data = obj;
	}
}

class GsEdgeIterator implements Iterator {

	Iterator it;
	Vector v_cur = null;
	int pos;
	Object next = null;
	
	GsEdgeIterator(Map m) {
		it = m.entrySet().iterator();
		go2next();
	}
	
	public boolean hasNext() {
		return next != null;
	}

	public Object next() {
		if (next == null) {
			return null;
		}
		Object r = next;
		go2next();
		return r;
	}

	public void remove() {
		go2next();
	}
	
	private void go2next() {
		if (v_cur != null && ++pos < v_cur.size()) {
			next = v_cur.get(pos);
			return;
		}
		while (it.hasNext()) {
			v_cur = ((NodeInfo)((Map.Entry)it.next()).getValue()).v_outgoing;
			if (v_cur.size() > 0) {
				pos = 0;
				next = v_cur.get(pos);
				return;
			}
		}
		v_cur = null;
		next = null;
	}
}

class GsSimpleVertexAttributeReader extends GsVertexAttributesReader {

	Map m;
	NodeInfo ni;
	
	public GsSimpleVertexAttributeReader(HashMap m_nodes) {
		this.m = m_nodes;
	}

	public Color getBackgroundColor() {
		return ni.bg;
	}

	public int getBorder() {
		return 0;
	}

	public Color getForegroundColor() {
		return ni.fg;
	}

	public int getHeight() {
		return ni.height;
	}

	public int getShape() {
		return 0;
	}

	public int getWidth() {
		return ni.width;
	}

	public int getX() {
		return ni.x;
	}

	public int getY() {
		return ni.y;
	}

	public void refresh() {
	}

	public void setBackgroundColor(Color color) {
		ni.bg = color;
	}

	public void setBorder(int index) {
		// TODO Auto-generated method stub
	}

	public void setForegroundColor(Color color) {
		ni.fg = color;
	}

	public void setPos(int x, int y) {
		ni.x = x;
		ni.y = y;
	}

	public void setShape(int shapeIndex) {
		// TODO Auto-generated method stub
	}

	public void setSize(int w, int h) {
		ni.width = w;
		ni.height = h;
	}

	public void setVertex(Object vertex) {
		ni = (NodeInfo)m.get(vertex);
	}
	
}
class GsSimpleEdgeAttributeReader extends GsEdgeAttributesReader {

	public float[] getDash() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getDefaultEdgeEndFill() {
		// TODO Auto-generated method stub
		return false;
	}

	public float getDefaultEdgeSize() {
		// TODO Auto-generated method stub
		return 1;
	}

	public int getDefaultStyle() {
		// TODO Auto-generated method stub
		return 1;
	}

	public Color getLineColor() {
		// TODO Auto-generated method stub
		return Color.BLACK;
	}

	public int getLineEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getLineWidth() {
		// TODO Auto-generated method stub
		return 1;
	}

	public List getPoints(boolean border) {
		return null;
	}

	public int getRouting() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	public void setDash(float[] dashArray) {
		// TODO Auto-generated method stub
		
	}

	public void setDefaultEdgeColor(Color color) {
		// TODO Auto-generated method stub
		
	}

	public void setDefaultEdgeEndFill(boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setDefaultEdgeSize(float s) {
		// TODO Auto-generated method stub
		
	}

	public void setDefaultStyle(int selectedIndex) {
		// TODO Auto-generated method stub
		
	}

	public void setEdge(Object obj) {
		// TODO Auto-generated method stub
		
	}

	public void setLineColor(Color color) {
		// TODO Auto-generated method stub
		
	}

	public void setLineEnd(int index) {
		// TODO Auto-generated method stub
		
	}

	public void setLineWidth(float w) {
		// TODO Auto-generated method stub
		
	}

	public void setPoints(List l) {
		// TODO Auto-generated method stub
		
	}

	public void setRouting(int index) {
		// TODO Auto-generated method stub
		
	}

	public void setStyle(int index) {
		// TODO Auto-generated method stub
		
	}
	
}
