package org.ginsim.service.layout;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.GraphChangeType;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;

/**
 * Layout State Transition Graphs: place nodes on a cube according to activity levels
 *
 * @author Duncan Berenguier
 */
public class DynamicLayout3D extends BaseSTGLayout {

	private EdgeAttributesReader ereader;
    private NodeAttributesReader vreader;
    private Color[] colorPalette;
    private int[] decalx, decaly;
    private int stateWidth = 0;

    private final int MARGIN = 10;
    private int maxPossibleY = MARGIN;
    private final int DIMENSIONSTEP = 30;
	
    public static void runLayout(DynamicGraph graph) throws GsException {
    	DynamicLayout3D algo = new DynamicLayout3D();
    	algo.layout(graph);
    }
    
    public void layout(DynamicGraph graph) throws GsException {
		Iterator it = graph.getNodes().iterator();
		Object v = it.next();
	    if (v == null || !(v instanceof DynamicNode)) {
	    	LogManager.error( "Wrong type of graph for this layout");
	    	return;
	    }
	    
		vreader = graph.getNodeAttributeReader();
		ereader = graph.getEdgeAttributeReader();
		List<RegulatoryNode> nodeOrder = graph.getAssociatedGraph().getNodeOrder();
	    byte[] maxValues = getMaxValues(nodeOrder);
        initColorPalette(maxValues.length);
        
	    // move the nodes
	    DynamicNode vertex = (DynamicNode)v;
	    vreader.setNode(vertex);
	    stateWidth = vreader.getWidth()+MARGIN;
        initDecal(maxValues);
	    
	    do {
	    	moveNode(vertex, maxValues);
		    vertex = (DynamicNode)it.next();
		} while (it.hasNext());
    	moveNode(vertex, maxValues);
    	
    	// move the edges
    	for (Edge<DynamicNode> edge: graph.getEdges()) {
    		moveEdge(edge, maxValues);
    	}
        graph.fireGraphChange(GraphChangeType.GRAPHVIEWCHANGED, null);
    }
	

	/**
	 * Move the node to its correct position.
	 * @param node
	 * @param maxValues
	 */
	private void moveNode(DynamicNode node, byte[] maxValues) {
		vreader.setNode(node);
		byte[] state = node.state;

		double left = MARGIN;
		double bottom = maxPossibleY + DIMENSIONSTEP;

		for (int i = 0; i < state.length; i++) {
			left += state[i]*decalx[i];
			bottom -= state[i]*decaly[i];
		}

		vreader.setPos((int)left, (int)bottom);
	}

	private void moveEdge(Edge<DynamicNode> edge, byte[] maxValues) {
		ereader.setEdge(edge);		
		ereader.setPoints(null);
		
		byte[] diffstate = getDiffStates(edge.getSource(), edge.getTarget());
		int change = getChange(diffstate);
	}
	
	/**
	 * Create a color palette by varying the hue.
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
           	decaly[3] = - DIMENSIONSTEP;
          	if (maxValues.length > 4) {
               	decalx[4] = DIMENSIONSTEP;
               	decaly[4] = stateWidth*(maxValues[1]+2)+stateWidth+MARGIN;
           	}
       	}
       	
    	for (int i = 5; i < maxValues.length; i++) {
			decalx[i] = 2*decalx[i-2]+(i%2==0?stateWidth*(maxValues[i-2]):0);
			decaly[i] = 2*decaly[i-2]+(i%2==1?stateWidth*(maxValues[i-2]):0);
		}
    	
    	for (int i=0; i < maxValues.length; i++) {
    		maxPossibleY += decaly[i] * maxValues[i];
    	}
	}
    
}
