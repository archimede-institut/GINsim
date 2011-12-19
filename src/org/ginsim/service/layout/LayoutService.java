package org.ginsim.service.layout;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;


/**
 * This class contains different placement algorithms, 
 * two algorithms for the placement in level and two others for the placement on rings 
 * 
 * @author Cecile Menahem
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
public class LayoutService implements Service {

    public static final int LEVEL = 0;
    public static final int LEVEL_INV = 1;
    public static final int RING = 2;
    public static final int RING_INV = 3;
    
    public static void runLayout(int ref, Graph graph) throws GsException {
		// first count nodes in each category
		NodeAttributesReader vreader = graph.getNodeAttributeReader();
		int nbRoot = 0;
		int nbStables = 0;
		int nbClassic = 0;
		int maxHeight = 0;
		int maxWidth = 0;
		for (Object vertex: graph.getNodes()) {
		    vreader.setNode(vertex);
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

		LayoutAlgo algo;
		boolean inversed = false;
		int ref2 = ref;
        if (ref % 2 == 1) {
            inversed = true;
            ref2--;
        }
		switch (ref2) {
			case RING:
			    algo = new RingLayout();
			    	break;
			default:
			    algo = new LevelLayout();
		}
		algo.configure(vreader, nbRoot, nbStables, nbClassic, maxHeight, maxWidth);
		
		// then rebrowse all nodes to do the actual placement
        if (inversed) {
            for (Object vertex: graph.getNodes()) {
                vreader.setNode(vertex);
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
            for (Object vertex: graph.getNodes()) {
    		    vreader.setNode(vertex);
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
