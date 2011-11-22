package org.ginsim.service.layout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.NodeAttributesReader;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.graph.dynamicgraph.DynamicNode;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;


public class DynamicLayoutMultidimention {
    private static final int padx = 25;
    private static final int pady = 25;
   
    private int width;
	private int height;
    
    private int pivot;
   
	private EdgeAttributesReader ereader;
    private NodeAttributesReader vreader;
	
    private final Color[] colorPalette;
	private final DynamicGraph graph;
	private final byte[] newNodeOrder;
	private final boolean useStraightEdges;

	public DynamicLayoutMultidimention(DynamicGraph graph, byte[] nodeOrder, boolean straightEdges, Color[] colorPalette) throws GsException{
		this.graph = graph;
		this.newNodeOrder = nodeOrder;
		this.colorPalette = colorPalette;
		this.useStraightEdges = straightEdges;
		
		runLayout();
	}
    

	public void runLayout() throws GsException{
        //Check if it is a DynamicGraph
		Iterator it = graph.getVertices().iterator();
		Object v = it.next();
	    if (v == null || !(v instanceof DynamicNode)) {
			System.out.println("wrong type of graph for this layout");
	    	return;
	    }
		vreader = graph.getNodeAttributeReader();
		ereader = graph.getEdgeAttributeReader();
		
	    byte[] maxValues = getMaxValues( graph.getAssociatedGraph().getNodeOrder());
	    
	    //move the nodes
	    DynamicNode vertex = (DynamicNode)v;
	    vreader.setNode(vertex);
	    this.width = vreader.getWidth() + padx*maxValues.length/2;
	    this.height = vreader.getHeight() + pady*maxValues.length/2;	   
	    
	    do {
	    	moveNode(vertex, maxValues);
		    vertex = (DynamicNode)it.next();
		} while (it.hasNext());
    	moveNode(vertex, maxValues);
    	
    	//move the edges
    	it = graph.getEdges().iterator();
    	for (Edge edge: graph.getEdges()) {
    		moveEdge(edge, maxValues);
    	}
    }
	
	/**
	 * Move the vertex to its correct position.
	 * @param vertex
	 * @param maxValues
	 */
	private void moveNode(DynamicNode vertex, byte[] maxValues) {
	    vreader.setNode(vertex);
    	byte[] state = vertex.state;
       	int x = 0;
    	int dx = 1;
    	for (int i = 0; i < pivot; i++) {
			x += getState(state, i)*dx;
			dx *= maxValues[i];
		}
    	int y = 0;
    	int dy = 1;
    	for (int i = pivot; i < maxValues.length; i++) {
			y += getState(state, i)*dy;
			dy *= maxValues[i];
		}
	    vreader.setPos(5+x*width, 5+y*height);
        vreader.refresh();		
	}
	
	/**
	 * Move an edge and set the proper style.
	 * @param edge
	 * @param maxValues
	 */
	private void moveEdge(Edge edge, byte[] maxValues) {
		byte[] diffstate = getDiffStates((DynamicNode)edge.getSource(), (DynamicNode)edge.getTarget());
		int change = get_change(diffstate);
		
		ereader.setEdge(edge);
	   	List points = ereader.getPoints();
		Point2D first, p1, p2, last;
		first = (Point2D)points.get(0);
		last =  (Point2D)points.get(points.size()-1);
		p1 =(Point2D) first.clone();
		p2 = null;
		double dx, dy;
		double pad = 25;

		if (useStraightEdges) {
			dx = get_dx(diffstate, maxValues, 0);
			dy = get_dy(diffstate, maxValues, 0);
			if (dx > 0 && dy > 0 ) { //the edge is diagonal
				dx = get_dx(diffstate, maxValues, 1);
				dy = get_dy(diffstate, maxValues, 1);
				p1.setLocation(first.getX()+(last.getX()-first.getX())/2+dy*pad, first.getY()+(last.getY()-first.getY())/2+dx*pad);
			} else {
				p2 =(Point2D) last.clone();
				int w = vreader.getWidth();
				int h = vreader.getHeight();
				p1.setLocation(first.getX()+gap(dx, dy, w, h), first.getY()+gap(dy, dx, h, w));
				p2.setLocation( last.getX()+gap(dx, dy, w, h),  last.getY()+gap(dy, dx, h, w));
			}
		} else {
			dx = get_dx(diffstate, maxValues, 1);
			dy = get_dy(diffstate, maxValues, 1);
			p1.setLocation(p1.getX()+(last.getX()-p1.getX())/2+dy*pad, p1.getY()+(last.getY()-p1.getY())/2+dx*pad);
		}
		points = new LinkedList();
    	points.add(first);
    	points.add(p1);
		if (p2 != null) points.add(p2);
    	points.add(last);
		ereader.setPoints(points);
		
		ereader.setLineColor(colorPalette[change]);
		ereader.setLineWidth(reduceChange(change)*0.5f+1.5f);
		ereader.setRouting(EdgeAttributesReader.ROUTING_NONE);
		if (p2 != null) {
			ereader.setStyle(EdgeAttributesReader.STYLE_STRAIGHT);
		} else {
			ereader.setStyle(EdgeAttributesReader.STYLE_CURVE);
		}
		ereader.refresh();
	}

    /**
     * Compute the gap in a complex way for straight edges
     * @param d_main
     * @param d_orth
     * @param size_main
     * @param size_orth
     * @return
     */
    private  double gap(double d_main, double d_orth, int size_main, int size_orth) {
		return size_main/1.75*(d_orth>0?1:0)+d_orth*12+d_main*3-size_orth/4;
	}

	/**
     * return the coordinate of the first change between the two states.
     * @param diffstate
     * @return
     */
	private int get_change(byte[] diffstate) {
    	for (int i = 0; i < diffstate.length; i++) {
    		if (diffstate[i] != 0) {
    			return i;
    		}
    	}
		return 0;
	}
    
	/**
	 * Transform the change accordingly to the number of row.
	 * @param change
	 * @return
	 */
    private int reduceChange(int change) {
    	if (change >= pivot) {
			return change - pivot;
		} else {
			return change;
		}
    }

    /**
     * Compute a distance from "no change between two states". The change have different weights depending on their index in the newNodeOrder.
     * @param diffstate
     * @param maxValues
     * @param start 
     * @return
     */
	private double get_dx(byte[] diffstate, byte[] maxValues, int start) {
    	int dx = 0;
    	int ddx = 1;
    	for (int i = start; i < diffstate.length/2; i++) {
			dx +=  diffstate[i]*ddx;
			ddx *= maxValues[i];
		}
		return dx;
	}
    /**
     * Compute a distance from "no change between two states". The change have different weights depending on their index in the newNodeOrder.
     * @param diffstate
     * @param maxValues
     * @param start 
     * @return
     */
	private double get_dy(byte[] diffstate, byte[] maxValues, int start) {
      	int dx = 0;
    	int ddx = 1;
    	for (int i = pivot+start; i < diffstate.length; i++) {
			dx +=  diffstate[i]*ddx;
			ddx *= maxValues[i];
		}
		return dx;
	}

    /**
     * Construct the | bit operator for a table of byte.
     * A value in the table is 0, 
     *   if the corresponding gene (according to the newNodeOrder) did not change between the vertices.
     *   otherwise its the absolute difference (1 normally)
     * 
     * @param sourceNode
     * @param targetNode
     * @return
     */
	private byte[] getDiffStates(DynamicNode sourceNode, DynamicNode targetNode) {
		byte[] delta = new byte[sourceNode.state.length];
		for (int i = 0; i < delta.length; i++) {
			delta[i] = (byte) Math.abs(getState(sourceNode.state,i) - getState(targetNode.state,i));
		}
		return delta;
	}

    /**
     * return the value of the state i according to the newNodeOrder
     * @param state
     * @param i
     * @return
     */
    private int getState(byte[] state, int i) {
		return state[newNodeOrder[i]];
	}

    /**
     * Get the maxvalues (the level max of each node) and return it. 
     * The nodes are correctly indexed with newNodeOrder
     */
	public byte[] getMaxValues(List nodeOrder) {
    	byte[] maxValues = new byte[nodeOrder.size()];
    	int i = 0;
    	for (Iterator it = nodeOrder.iterator(); it.hasNext();) {
    		RegulatoryNode v = (RegulatoryNode) it.next();
    		maxValues[newNodeOrder[i++]] = (byte) (v.getMaxValue()+1);
    	}			
    	return maxValues;
    }
    
}
