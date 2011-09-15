package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.util.ArrayList;
import java.util.List;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.global.GsMain;
import fr.univmrs.tagc.GINsim.global.GsWhatToDoFrame;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;

/**
 * Simple example showing how to create and fill a regulatory graph.
 * It is meant to be used as reminder for authors of import filters.
 * 
 * @author Aurelien Naldi
 */
public class LRGCreator {

	public static void main(String[] args) {
		// launch the true main class: not needed if launched from a plugin
		GsMain.main(args);
		
		// create a simple graph
		GsRegulatoryGraph lrg = new GsRegulatoryGraph();
		
		// add a few vertices
		GsRegulatoryVertex g0, g1, g2;
		g0 = (GsRegulatoryVertex)lrg.interactiveAddVertex(0, 0, 0);
		g1 = (GsRegulatoryVertex)lrg.interactiveAddVertex(0, 30, 0);
		g2 = (GsRegulatoryVertex)lrg.interactiveAddVertex(0, 0, 100);

		// increase max value for g0
		// we have to give the lrg as argument for consistency checks and cascading events
		g0.setMaxValue((byte)2, lrg);
		
		// add positive and negative interactions
		lrg.interactiveAddEdge(g0, g2, 0);
		lrg.interactiveAddEdge(g1, g2, 1);
		
		// get an edge (interactiveAddEdge does not return it)
		GsGraphManager manager = lrg.getGraphManager();
		GsDirectedEdge edge = (GsDirectedEdge)manager.getEdge(g0, g2);
		GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)edge.getUserObject();
		// change its threshold
		me.setMin(0, (byte)2);
		
		// create a logical parameter.
		// it takes a value and a list of interactions
		List edges = new ArrayList();
		edges.add(me.getEdge(0));  // interaction == one of the edges inside a MultiEdge
		GsLogicalParameter parameter = new GsLogicalParameter(edges, 1);
		
		// add the logical parameter on g2
		g2.addLogicalParameter(parameter, true);  // manual parameter, logical function will call this method with "false"
		
		// propose to display the new graph 
		new GsWhatToDoFrame(null, lrg, true);
	}
}
