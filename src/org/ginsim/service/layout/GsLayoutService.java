package org.ginsim.service.layout;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.service.GsService;
import org.mangosdk.spi.ProviderFor;


/**
 * This class contains different placement algorithms, 
 * two algorithms for the placement in level and two others for the placement on rings 
 * 
 * @author Cecile Menahem
 * @author Aurelien Naldi
 */
@ProviderFor(GsService.class)
public class GsLayoutService implements GsService {

    public static final int LEVEL = 0;
    public static final int LEVEL_INV = 1;
    public static final int RING = 2;
    public static final int RING_INV = 3;
    
    public static void runLayout(int ref, Graph graph) throws GsException {
		// first count nodes in each category
		VertexAttributesReader vreader = graph.getVertexAttributeReader();
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
