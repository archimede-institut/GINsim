package org.ginsim.gui.graph.backend;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.ginsim.gui.graph.AddEdgeAction;
import org.ginsim.gui.graph.AddNodeAction;
import org.ginsim.gui.graph.EditAction;
import org.ginsim.gui.graph.EditActionManager;
import org.ginsim.gui.graph.EditMode;
import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.Edge;

/**
 * jgraph marquee handler
 */
@SuppressWarnings("rawtypes")
public class MarqueeHandler extends BasicMarqueeHandler {
	
	private final JGraph jgraph;
	private final JgraphGUIImpl graphUI;
	private final EditActionManager editManager;

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
		this.editManager = graphUI.getEditActionManager();
		
		jgraph.setMarqueeHandler(this);
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
		Object selected = jgraph.getFirstCellForLocation(tmp.x, tmp.y);
		if (editManager.getSelectedAction().getMode() == EditMode.EDGE) {
			Object next = selected;
			Object previous = selected;
			while (selected instanceof Edge) {
				next = jgraph.getNextCellForLocation(next, tmp.x, tmp.y);
				if (next == selected || next == previous) {
					break;
				}
				previous = next;
				if (!(next instanceof Edge)) {
					selected = next;
					break;
				}
			}
		} 

		return selected;
	}

	@Override
	public boolean isForceMarqueeEvent(MouseEvent e) {
		currentObject = getObjectAtPoint(e.getPoint());
		
		if (editManager.getSelectedAction().getMode() != EditMode.EDIT) {
			return true;
		}
		return super.isForceMarqueeEvent(e);
	}
    
	
	@Override
	public void mouseDragged(MouseEvent event) {

		// If remembered Start Point is Valid
		if (start != null && !event.isConsumed() && !(editManager.getSelectedAction().getMode() == EditMode.EDGEPOINT)) {
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
		} else {
			super.mouseDragged(event);
		}
	}

	@Override
	public void mouseMoved(MouseEvent event) {
	    if (event.getPoint() == null) {
	        return;
	    }
		Object obj = getObjectAtPoint(event.getPoint());
		// Check Mode and Find Port
		if (obj != null && !event.isConsumed() 
			&& ( (obj instanceof Edge ) &&  editManager.getSelectedAction().getMode() == EditMode.EDGEPOINT) ) {
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
		EditAction currentAction = editManager.getSelectedAction();
		
		if ( currentObject != null 
			&& !(currentObject instanceof Edge) 
			&& currentAction.getMode() == EditMode.EDGE ) {
			// Remember Start Location
			int cx = (int)jgraph.getCellBounds(currentObject).getCenterX();
			int cy = (int)jgraph.getCellBounds(currentObject).getCenterY();
			start = jgraph.toScreen(new Point2D.Double(cx,cy));
			
			// Remember First Object
			firstObject = currentObject;

			// Consume Event
			event.consume();
		} else super.mousePressed(event);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		EditAction currentAction = editManager.getSelectedAction();
	    // If Valid Event, Current and First Port
		if (event != null && !event.isConsumed() && leaveCell 
			&& currentObject != null && firstObject != null 
			&& currentAction instanceof AddEdgeAction ) {
		    
			AddEdgeAction action = (AddEdgeAction)currentAction;
			// Then Establish Connection
			Object source = ((DefaultGraphCell)firstObject).getUserObject();
			Object target = ((DefaultGraphCell)currentObject).getUserObject();

			// actually call the action
			action.addEdge(editManager, source, target);
			
			// Consume Event
			event.consume();
			jgraph.clearSelection();
		} else if (event != null && currentAction instanceof AddNodeAction) { 

			AddNodeAction action = (AddNodeAction)currentAction;
			action.addNode(editManager, (int)event.getPoint().getX(), (int)event.getPoint().getY());
		} else if (event != null && currentAction.getMode() == EditMode.EDGEPOINT) {

            CellHandle hd=jgraph.getUI().getHandle();
            if (hd!=null) {
                MouseEvent ev=new MouseEvent((Component)event.getSource(),MouseEvent.MOUSE_PRESSED,0,InputEvent.BUTTON3_DOWN_MASK ,event.getX(),event.getY(),event.getClickCount(),event.isPopupTrigger(),  MouseEvent.BUTTON3 );
                hd.mousePressed(ev);
                ev=new MouseEvent((Component)event.getSource(),MouseEvent.MOUSE_RELEASED,0,InputEvent.BUTTON3_DOWN_MASK ,event.getX(),event.getY(),event.getClickCount(),event.isPopupTrigger(),    MouseEvent.BUTTON3 );
                hd.mouseReleased(ev);
                jgraph.getGraphLayoutCache().reload();
                currentAction.performed(editManager);
            }
		} else {
			jgraph.repaint();
		}
		
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
		// If Valid First Port, Start and Current Point
		if (firstObject != null && start != null && current != null) {
			// Then Draw A Line From Start to Current Point
			g.drawLine((int)start.getX(), (int)start.getY(), (int)current.getX(), (int)current.getY());
		}
	}

}
