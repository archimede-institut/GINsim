package fr.univmrs.ibdm.GINsim.piccolo;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Vector;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsActions;

public class MouseHandler extends PDragEventHandler {

	GsGraph graph;
	GsSimpleGraphManager manager;
	GsActions gsa;
	GsPCanvas canvas;

	GsPNode tmpNode;
	
	Vector v_selection = new Vector();
	
    public MouseHandler(GsGraph graph, GsPCanvas canvas) {
    	this.graph = graph;
    	this.manager = (GsSimpleGraphManager)graph.getGraphManager();
    	this.canvas = canvas;
    	gsa = graph.getGraphManager().getMainFrame().getGsAction();
        PInputEventFilter filter = new PInputEventFilter();
        filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
        setEventFilter(filter);
    }
    
    GsPNode getPickedNode(PInputEvent e) {
    	PNode picked = e.getPickedNode();
    	while (!(picked instanceof GsPNode)) {
    		picked = picked.getParent();
    		if (picked == null) {
    			return null;
    		}
    	}
    	return (GsPNode)picked;
    }

    public void mouseEntered(PInputEvent e) {
        super.mouseEntered(e);
        if (e.getButton() == MouseEvent.NOBUTTON) {
            GsPNode node = getPickedNode(e);
            if (node == null) {
            	return;
            }
            node.highlight(true);
        }
    }
    
    public void mouseExited(PInputEvent e) {
        super.mouseExited(e);
        if (e.getButton() == MouseEvent.NOBUTTON) {
            GsPNode node = getPickedNode(e);
            if (node == null) {
            	return;
            }
            node.highlight(false);
        }
    }
    
    
    public void keyTyped(PInputEvent event) {
    	System.out.println("key pressed/released");
		super.keyTyped(event);
	}

	public void mouseClicked(PInputEvent event) {
		super.mouseClicked(event);
		if (event.getButton() != 1) {
			return;
		}
		event.setHandled(true);
        v_selection.clear();
		Point2D pos = event.getPosition();
		switch(gsa.getCurrentMode()) {
			case GsActions.MODE_ADD_VERTEX:
				graph.interactiveAddVertex(gsa.getCurrentSubmode(), (int)pos.getX(), (int)pos.getY());
				gsa.changeModeIfUnlocked();
				break;
			case GsActions.MODE_DEFAULT:
				// update selection
				GsPNode node = getPickedNode(event);
	            if (node == null) {
	            	return;
	            }
	            v_selection.add(node);
		}
	}

	protected void startDrag(PInputEvent e) {
        e.setHandled(true);
        GsPNode node = getPickedNode(e);
        if (node == null) {
        	return;
        }
		switch(gsa.getCurrentMode()) {
			case GsActions.MODE_DEFAULT:
		        getPickedNode(e).startDrag(e);
		        break;
			case GsActions.MODE_ADD_EDGE:
				System.out.println("start add edge");
				tmpNode = node;
				break;
		}
    }
    
    protected void endDrag(PInputEvent e) {
		e.setHandled(true);
		GsPNode node = getPickedNode(e);
		if (node == null) {
			return;
		}
		switch(gsa.getCurrentMode()) {
			case GsActions.MODE_DEFAULT:
				node.endDrag(e);
				canvas.updateEdge(node);
				break;
			case GsActions.MODE_ADD_EDGE:
				System.out.println("end add edge");
				graph.interactiveAddEdge(tmpNode.ni.data, node.ni.data, gsa.getCurrentSubmode());
				break;
		}
	}

	protected void drag(PInputEvent e) {
        e.setHandled(true);
		switch(gsa.getCurrentMode()) {
		case GsActions.MODE_DEFAULT:
	        getPickedNode(e).startDrag(e);
	        break;
		case GsActions.MODE_ADD_EDGE:
			break;
		}
    }
}
