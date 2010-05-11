package fr.univmrs.tagc.GINsim.layout;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.ColorPalette;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;

public class Gs3DLayout implements GsPlugin, GsActionProvider {
    private static final int LAYOUT3D = 1;
	private GsEdgeAttributesReader ereader;
    private GsVertexAttributesReader vreader;
    private Color[] colorPalette;
	  
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;

	
    private GsPluggableActionDescriptor[] t_layout = {
		new GsPluggableActionDescriptor("STR_3D_placement", "STR_3D_placement_descr", null, this, ACTION_LAYOUT, LAYOUT3D),
    };

   public void registerPlugin() {
        GsGraph.registerLayoutProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (actionType != ACTION_LAYOUT || !(graph instanceof GsDynamicGraph)) {
            return null;
        }
        return t_layout;
    }

    public void runAction(int actionType, int ref, GsGraph graph, JFrame parent) throws GsException {
        if (actionType != ACTION_LAYOUT) {
            return;
        }
        initColorPalette(3);
 
        GsGraphManager gmanager = graph.getGraphManager();
		Iterator it = gmanager.getVertexIterator();
		Object v = it.next();
	    if (v == null || !(v instanceof GsDynamicNode)) {
			System.out.println("wrong type of graph for this layout");
	    	return;
	    }
		vreader = gmanager.getVertexAttributesReader();
		ereader = gmanager.getEdgeAttributesReader();
		List nodeOrder = ((GsDynamicGraph)graph).getAssociatedGraph().getNodeOrder();
		if (nodeOrder.size() != 3) {
			Tools.error("The model must contain only three nodes for this layout", parent);
	    	return;
	    }
	    byte[] maxValues = getMaxValues(nodeOrder);
	    
	    //move the nodes
	    GsDynamicNode vertex = (GsDynamicNode)v;
	    vreader.setVertex(vertex);

	    do {
	    	moveVertex(vertex, maxValues);
		    vertex = (GsDynamicNode)it.next();
		} while (it.hasNext());
    	moveVertex(vertex, maxValues);
    	
    	//move the edges
    	it = gmanager.getEdgeIterator();
    	while (it.hasNext()) {
    		GsDirectedEdge edge = (GsDirectedEdge) it.next();
    		moveEdge(edge, maxValues);
    	}
    }
	

	/**
	 * Move the vertex to its correct position.
	 * @param vertex
	 * @param maxValues
	 */
	private void moveVertex(GsDynamicNode vertex, byte[] maxValues) {
	    vreader.setVertex(vertex);
    	byte[] state = vertex.state;
 
    	int x = getState(state, X);
    	int y = getState(state, Y);
    	int z = getState(state, Z);
    	
    	double left = 10+x*120+(getState(maxValues, Z)-z)*50;
    	double top = 10+(getState(maxValues, Y)-y)*120+(getState(maxValues, Z)-z)*60;
    	vreader.setPos((int)left, (int)top);
        vreader.refresh();		
	}

	private void moveEdge(GsDirectedEdge edge, byte[] maxValues) {
		ereader.setEdge(edge);		
		ereader.setRouting(GsEdgeAttributesReader.ROUTING_AUTO);
		ereader.setPoints(null);
		ereader.setStyle(GsEdgeAttributesReader.STYLE_STRAIGHT);
		
		byte[] diffstate = getDiffStates((GsDynamicNode)edge.getSourceVertex(), (GsDynamicNode)edge.getTargetVertex());
		int change = get_change(diffstate);
	
		ereader.setLineColor(colorPalette[change]);
		ereader.setLineWidth(1.5f);

		ereader.refresh();
	}

   /**
    * return the value of the state i according to the newNodeOrder
    * @param state
    * @param i
    * @return
    */
   private int getState(byte[] state, int i) {
		return state[i];
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
     * Construct the | bit operator for a table of byte.
     * A value in the table is 0, 
     *   if the corresponding gene (according to the newNodeOrder) did not change between the vertices.
     *   otherwise its the absolute difference (1 normally)
     * 
     * @param sourceVertex
     * @param targetVertex
     * @return
     */
	private byte[] getDiffStates(GsDynamicNode sourceVertex, GsDynamicNode targetVertex) {
		byte[] delta = new byte[sourceVertex.state.length];
		for (int i = 0; i < delta.length; i++) {
			delta[i] = (byte) Math.abs(getState(sourceVertex.state,i) - getState(targetVertex.state,i));
		}
		return delta;
	}

    /**
     * Get the maxvalues (the level max - 1 of each node) and return it. 
     */
	public byte[] getMaxValues(List nodeOrder) {
    	byte[] maxValues = new byte[nodeOrder.size()];
    	int i = 0;
    	for (Iterator it = nodeOrder.iterator(); it.hasNext();) {
    		GsRegulatoryVertex v = (GsRegulatoryVertex) it.next();
    		maxValues[i++] = (byte) (v.getMaxValue());
    	}			
    	return maxValues;
    }

	/**
	 * Create a color palette by variing the hue.
	 * @param n the count of color in the palette
	 */
    public void initColorPalette(int n) {
    	if (n <= ColorPalette.defaultPalette.length) {
    		colorPalette = ColorPalette.defaultPalette;
    		return;
    	}
    	colorPalette = new Color[n];
    	for (int i = 0; i < n ; i++) {
			colorPalette[i] = Color.getHSBColor((float)i/(float)n , 0.85f, 1.0f);
		}
    }
}
