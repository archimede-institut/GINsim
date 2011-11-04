package org.ginsim.layout;

import org.ginsim.graph.Graph;
import org.ginsim.graph.GraphView;

import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.common.GsException;

/**
 * This class contains different placement algorithms, 
 * two algorithms for the placement in level and two others for the placement on rings 
 * 
 * @author Cecile Menahem
 * @author Aurelien Naldi
 */
public class GsLayoutPlugin {

    private static final int LEVEL = 0;
    private static final int LEVEL_INV = 1;
    private static final int RING = 2;
    private static final int RING_INV = 3;
    
    public void runLayout(int ref, Graph graph, GraphView view) throws GsException {
		// first count nodes in each category
		GsVertexAttributesReader vreader = view.getVertexReader();
		int nbRoot = 0;
		int nbStables = 0;
		int nbClassic = 0;
		int maxHeight = 0;
		int maxWidth = 0;
		for (Object vertex: graph.getVertices()) {
		    vreader.setVertex(vertex);
		    int tmp = vreader.getHeight();
		    maxHeight = (tmp > maxHeight) ? tmp : maxHeight;
		    tmp = vreader.getWidth();
		    maxWidth = (tmp > maxWidth) ? tmp : maxWidth;
		    if (graph.getIncomingEdges(vertex).size() == 0) {
		        nbRoot++;
		    } else if (graph.getOutgoingEdges(vertex).size() == 0) {
		        nbStables++;
		    } else {
		        nbClassic++;
		    }
		}

		GsLayoutAlgo algo;
		boolean inversed = false;
		int ref2 = ref;
        if (ref % 2 == 1) {
            inversed = true;
            ref2--;
        }
		switch (ref2) {
			case RING:
			    algo = new GsRingLayout();
			    	break;
			default:
			    algo = new GsLevelLayout();
		}
		algo.configure(vreader, nbRoot, nbStables, nbClassic, maxHeight, maxWidth);
		
		// then rebrowse all nodes to do the actual placement
        if (inversed) {
            for (Object vertex: graph.getVertices()) {
                vreader.setVertex(vertex);
                if (graph.getIncomingEdges(vertex).size() == 0) {
                    algo.placeNextStable();
                } else if (graph.getOutgoingEdges(vertex).size() == 0) {
                    algo.placeNextRoot();
                } else {
                    algo.placeNextClassic();
                }
                vreader.refresh();
            }
        } else {
            for (Object vertex: graph.getVertices()) {
    		    vreader.setVertex(vertex);
    		    if (graph.getIncomingEdges(vertex).size() == 0) {
    		        algo.placeNextRoot();
    		    } else if (graph.getOutgoingEdges(vertex).size() == 0) {
    		        algo.placeNextStable();
    		    } else {
    		        algo.placeNextClassic();
    		    }
    		    vreader.refresh();
    		}
        }
    }
}
