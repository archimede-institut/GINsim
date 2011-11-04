package org.ginsim.gui.graph.backend;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.ginsim.gui.graph.EditMode;
import org.ginsim.gui.shell.FrameActions;
import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphConstants;

import fr.univmrs.tagc.GINsim.graph.GsGraphSelectionChangeEvent;

/**
 * jgraph marquee handler
 */
@SuppressWarnings("rawtypes")
public class MarqueeHandler extends BasicMarqueeHandler implements GraphSelectionListener {
	
	private JGraph jgraph;
	private JgraphGUIImpl graphUI;
	private FrameActions actions;
	// Holds the Start and the Current Point
	private Point2D start, current;
	private boolean leaveCell = false;
	private Object firstObject,currentObject ;
	/**
	 * 	@param g
	 */
	public MarqueeHandler(JgraphGUIImpl graphUI) {
		super();
		this.graphUI = graphUI;
		this.jgraph = graphUI.getJGraph();
		jgraph.setMarqueeHandler(this);
		jgraph.addGraphSelectionListener(this);
		
		// FIXME: we need access at least to the editing modes
		// gsactions = graphUI.getActions();
	}
	/**
	 * 
	 * @param point
	 * @return the object at the specified position
	 */
	private Object getObjectAtPoint(Point point) {
		// Scale from Screen to Model
		//Point tmp = graph.toScreen(new Point(point));
		Point tmp = new Point(point);
				
		//return the object
		return jgraph.getFirstCellForLocation(tmp.x, tmp.y);
	}

	@Override
	public boolean isForceMarqueeEvent(MouseEvent e) {
		currentObject = getObjectAtPoint(e.getPoint());
		
		if (actions.getCurrentGroup() != EditMode.EDIT) {
			return true;
		}
		return super.isForceMarqueeEvent(e);
	}
    
	
	@Override
	public void mouseDragged(MouseEvent event) {
		// If remembered Start Point is Valid
		if (start != null && !event.isConsumed() && !(actions.getCurrentGroup() == EditMode.EDGEPOINT)) {
			// Fetch Graphics from Graph
			Graphics g = jgraph.getGraphics();
			// Xor-Paint the old Connector (Hide old Connector)
			drawLine(Color.black, jgraph.getBackground(), g);
			// Reset Remembered Port
			currentObject = getObjectAtPoint(event.getPoint());
			// If Port was found then Point to Port Location
			if (currentObject != null &&  !(currentObject instanceof Edge )) {
				int cx=(int)jgraph.getCellBounds(currentObject).getCenterX();
				int cy=(int)jgraph.getCellBounds(currentObject).getCenterY();
				current = jgraph.toScreen(new Point(cx,cy));
				if (firstObject!=currentObject) leaveCell=true;
			}
			// Else If no Port was found then Point to Mouse Location
			else {
				current = jgraph.snap(event.getPoint());
				currentObject = null;
				leaveCell=true;
			} 
			// Xor-Paint the new Connector
			drawLine(jgraph.getBackground(), Color.black, g);
			// Consume Event
			event.consume();
		}
		// Call Superclass
		super.mouseDragged(event);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
	    if (event.getPoint() == null) {
	        return;
	    }
		Object obj = getObjectAtPoint(event.getPoint());
		// Check Mode and Find Port
		if (obj != null && !event.isConsumed() 
			&& ( (obj instanceof Edge ) &&  actions.getCurrentGroup() == EditMode.EDGEPOINT) ) {
			// Set Cusor on Graph (Automatically Reset)
			jgraph.setCursor(new Cursor(Cursor.HAND_CURSOR));
			// Consume Event
			event.consume();
		}
		// Call Superclass only if "necessary"
		if (obj != null) {
		    super.mouseMoved(event);
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if ( currentObject != null 
			&& !(currentObject instanceof Edge) 
			&& actions.getCurrentGroup() == EditMode.EDGE ) {
			// Remember Start Location
			int cx = (int)jgraph.getCellBounds(currentObject).getCenterX();
			int cy = (int)jgraph.getCellBounds(currentObject).getCenterY();
			start = jgraph.toScreen(new Point2D.Double(cx,cy));
			
			// Remember First Object
			firstObject = currentObject;

//			CellHandle hd = jgraph.getUI().getHandle();
//			if (hd != null) {
//				if (GsGraphConstants.getRouting(((DefaultGraphCell)currentObject).getAttributes())!=null) { 
//					jgraph.getGraphEditor().setLineStyle(currentObject,GsGraphConstants.STYLE_ORTHOGONAL);
//					jgraph.getGraphEditor().setEdgeRouting(currentObject,null);
//					jgraph.getGraphLayoutCache().reload();
//				} else {
//					MouseEvent ev = new MouseEvent((Component)event.getSource(),MouseEvent.MOUSE_PRESSED,0,MouseEvent.BUTTON3_DOWN_MASK ,event.getX(),event.getY(),event.getClickCount(),event.isPopupTrigger(),	MouseEvent.BUTTON3 );
//					hd.mousePressed(ev);
//					ev = new MouseEvent((Component)event.getSource(),MouseEvent.MOUSE_RELEASED,0,MouseEvent.BUTTON3_DOWN_MASK ,event.getX(),event.getY(),event.getClickCount(),event.isPopupTrigger(),	MouseEvent.BUTTON3 );
//					hd.mouseReleased(ev);
//					jgraph.getGraphLayoutCache().reload();
//				}
//			}
				
			// Consume Event
			event.consume();
		} else super.mousePressed(event);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
	    // If Valid Event, Current and First Port
		if (event != null && !event.isConsumed() && leaveCell 
			&& currentObject != null && firstObject != null 
			&& actions.getCurrentGroup() == EditMode.EDGE) {
		    
			// Then Establish Connection
			Object source = ((DefaultGraphCell)firstObject).getUserObject();
			Object target = ((DefaultGraphCell)currentObject).getUserObject();
			// FIXME: use an EditAction
//		    graphUI.getGraph().addEdge(source, target, actions.getCurrentEditMode().getValue());
			// Consume Event
			event.consume();
			jgraph.clearSelection();
			actions.changeModeIfUnlocked();
		} else if (event != null && actions.getCurrentGroup() == EditMode.NODE) { 

		    // add the new vertex!
			// FIXME: use an EditAction
//			Object vertex = graphUI.getGraph().addVertex(actions.getCurrentEditMode().getValue());
			// FIXME: set position
			// setPosition(vertex, (int)event.getPoint().getX(), (int)event.getPoint().getY(); 
		    actions.changeModeIfUnlocked();
		} else if (event != null && actions.getCurrentGroup() == EditMode.EDGEPOINT) {

            CellHandle hd=jgraph.getUI().getHandle();
            if (hd!=null) {
                MouseEvent ev=new MouseEvent((Component)event.getSource(),MouseEvent.MOUSE_PRESSED,0,InputEvent.BUTTON3_DOWN_MASK ,event.getX(),event.getY(),event.getClickCount(),event.isPopupTrigger(),  MouseEvent.BUTTON3 );
                hd.mousePressed(ev);
                ev=new MouseEvent((Component)event.getSource(),MouseEvent.MOUSE_RELEASED,0,InputEvent.BUTTON3_DOWN_MASK ,event.getX(),event.getY(),event.getClickCount(),event.isPopupTrigger(),    MouseEvent.BUTTON3 );
                hd.mouseReleased(ev);
                jgraph.getGraphLayoutCache().reload();
                actions.changeModeIfUnlocked();
            }
		} else jgraph.repaint();
		// Reset Global Vars
		firstObject = currentObject = null;
		start = current = null;
		leaveCell=false;
		// Call Superclass
		super.mouseReleased(event);
	}

	/**
	 * 
	 * @param fg
	 * @param bg
	 * @param g
	 */
	private void drawLine(Color fg, Color bg, Graphics g) {
		// Set Foreground
		g.setColor(fg);
		// Set Xor-Mode Color
		g.setXORMode(bg);
		// Highlight the Current Port
		hightLightObj(jgraph.getGraphics());
		// If Valid First Port, Start and Current Point
		if (firstObject != null && start != null && current != null) {
			// Then Draw A Line From Start to Current Point
			g.drawLine((int)start.getX(), (int)start.getY(), (int)current.getX(), (int)current.getY());
		}
	}

	/**
	 * Use the Preview Flag to Draw a Highlighted Port
	 * @param g
	 */
	protected void hightLightObj(Graphics g) {
		CellView cell=null;
		// If Current Port is Valid
		if (currentObject instanceof CellView) cell =(CellView) currentObject;
		if (cell != null) {
			// If Not Floating Port...
			boolean o = (GraphConstants.getOffset(cell.getAttributes()) != null);
			// ...Then use Parent's Bounds
			Rectangle2D r = (o) ? cell.getBounds() : cell.getParentView().getBounds();
			// Scale from Model to Screen
			r = jgraph.toScreen(r);
			// Add Space For the Highlight Border
			r.setFrame(r.getX()-3, r.getY()-3, r.getWidth()+6, r.getHeight()+6);
			// Paint Port in Preview (=Highlight) Mode
			jgraph.getUI().paintCell(g, cell, r, true);
		}
	}
	
	@Override
    public void valueChanged(GraphSelectionEvent e) {
        Vector v_vertex = new Vector(1);
        Vector v_edge = new Vector(1);
        int nbVertex = 0;
        int nbEdge = 0;
        Object[] t_select = jgraph.getSelectionCells();
        for (int i=0 ; i<t_select.length ; i++) {
            if (t_select[i] instanceof DefaultEdge) {
                v_edge.add(((DefaultEdge)t_select[i]).getUserObject());
                nbEdge++;
            } else if (t_select[i] instanceof DefaultGraphCell) {
                v_vertex.add(((DefaultGraphCell)t_select[i]).getUserObject());
                nbVertex++;
            }
        }
        graphUI.SelectionChanged(new GsGraphSelectionChangeEvent(v_edge, v_vertex));
    }
}
