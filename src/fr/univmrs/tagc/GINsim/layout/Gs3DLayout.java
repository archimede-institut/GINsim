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

public class Gs3DLayout implements GsPlugin, GsActionProvider {
    private static final int LAYOUT3D = 1;
	private GsEdgeAttributesReader ereader;
    private GsVertexAttributesReader vreader;
    private Color[] colorPalette;
    private int[] decalx, decaly;
    private int stateWidth = 0;

    private static int MARGIN = 10;
	
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
	    byte[] maxValues = getMaxValues(nodeOrder);
        initColorPalette(maxValues.length);
	    //move the nodes
	    GsDynamicNode vertex = (GsDynamicNode)v;
	    vreader.setVertex(vertex);
	    stateWidth = vreader.getWidth()+MARGIN;
        initDecal(maxValues);
	    
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
  	
    	double left = MARGIN;
    	double top = MARGIN;
    	
    	for (int i = 0; i < state.length; i++) {
    		left += state[i]*decalx[i];
    		top += state[i]*decaly[i];
		}
    	    	
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
		if (state.length > i) return state[i];
		else return 0;
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
    public void initDecal(byte[] maxValues) {
    	int l = (maxValues.length < 3?3:maxValues.length);
       	decalx = new int[l];
       	decaly = new int[l];
      	
       	decalx[0] = stateWidth*2; 
       	decaly[0] = 0;

       	decalx[1] = 0; 
       	decaly[1] = stateWidth*2;

       	decalx[2] = 50; 
       	decaly[2] = 50;

      	if (maxValues.length > 3) {
           	decalx[3] = stateWidth*(maxValues[0]+2)+stateWidth+MARGIN;
           	decaly[3] = 30;
          	if (maxValues.length > 4) {
               	decalx[4] = 30;
               	decaly[4] = stateWidth*(maxValues[1]+2)+stateWidth+MARGIN;
           	}
       	}
       	
    	for (int i = 5; i < maxValues.length; i++) {
			decalx[i] = 2*decalx[i-2]+(i%2==0?stateWidth*(maxValues[i-2]):0);
			decaly[i] = 2*decaly[i-2]+(i%2==1?stateWidth*(maxValues[i-2]):0);
		}
    	
	}
    
}
