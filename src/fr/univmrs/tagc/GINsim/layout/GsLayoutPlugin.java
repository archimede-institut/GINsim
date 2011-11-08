package fr.univmrs.tagc.GINsim.layout;

import java.util.Iterator;

import javax.swing.JFrame;

import org.ginsim.graph.Graph;
import org.ginsim.service.layout.GsLayoutAlgo;
import org.ginsim.service.layout.GsLevelLayout;
import org.ginsim.service.layout.GsRingLayout;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.common.GsException;

/**
 * This class contains different placement algorithms, 
 * two algorithms for the placement in level and two others for the placement on rings 
 * 
 * @author cecile
 */
public class GsLayoutPlugin implements GsPlugin, GsActionProvider {

    private static final int LEVEL = 0;
    private static final int LEVEL_INV = 1;
    private static final int RING = 2;
    private static final int RING_INV = 3;
    
    private GsPluggableActionDescriptor[] t_layout = {
            new GsPluggableActionDescriptor("STR_level_placement", "STR_level_placement_descr", null, this, ACTION_LAYOUT, LEVEL),
            new GsPluggableActionDescriptor("STR_level_placement_inv", "STR_level_placement_inv_descr", null, this, ACTION_LAYOUT, LEVEL_INV),
            new GsPluggableActionDescriptor("STR_ring_placement", "STR_ring_placement_descr", null, this, ACTION_LAYOUT, RING),
            new GsPluggableActionDescriptor("STR_ring_placement_inv", "STR_ring_placement_inv_descr", null, this, ACTION_LAYOUT, RING_INV),
    };
	
    public void registerPlugin() {
        GsGraph.registerLayoutProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
        if (actionType != ACTION_LAYOUT) {
            return null;
        }
        return t_layout;
    }

    public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_LAYOUT) {
            return;
        }
		// first count nodes in each category
		GsGraphManager gmanager = graph.getGraphManager();
		Iterator it = gmanager.getVertexIterator();
		GsVertexAttributesReader vreader = gmanager.getVertexAttributesReader();
		int nbRoot = 0;
		int nbStables = 0;
		int nbClassic = 0;
		int maxHeight = 0;
		int maxWidth = 0;
		while (it.hasNext()) {
		    Object vertex = it.next();
		    vreader.setVertex(vertex);
		    int tmp = vreader.getHeight();
		    maxHeight = (tmp > maxHeight) ? tmp : maxHeight;
		    tmp = vreader.getWidth();
		    maxWidth = (tmp > maxWidth) ? tmp : maxWidth;
		    if (gmanager.getIncomingEdges(vertex).size() == 0) {
		        nbRoot++;
		    } else if (gmanager.getOutgoingEdges(vertex).size() == 0) {
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
		it = graph.getVertices().iterator();
        if (inversed) {
            while (it.hasNext()) {
                Object vertex = it.next();
                vreader.setVertex(vertex);
                if (gmanager.getIncomingEdges(vertex).size() == 0) {
                    algo.placeNextStable();
                } else if (gmanager.getOutgoingEdges(vertex).size() == 0) {
                    algo.placeNextRoot();
                } else {
                    algo.placeNextClassic();
                }
                vreader.refresh();
            }
        } else {
    		while (it.hasNext()) {
    		    Object vertex = it.next();
    		    vreader.setVertex(vertex);
    		    if (gmanager.getIncomingEdges(vertex).size() == 0) {
    		        algo.placeNextRoot();
    		    } else if (gmanager.getOutgoingEdges(vertex).size() == 0) {
    		        algo.placeNextStable();
    		    } else {
    		        algo.placeNextClassic();
    		    }
    		    vreader.refresh();
    		}
        }
    }
}
