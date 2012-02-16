package org.ginsim.service.tool.stablestates;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.javaMDD.MDDFactory;
import fr.univmrs.tagc.javaMDD.PathSearcher;


/**
 * This implements an analytic search of stable states. A state "x" is stable if, for every gene "i",
 * K(x) = x(i).
 * 
 * To find a stable state, one can build a MDD for each gene, giving the context under which 
 * THIS gene is stable.Then the stable states can be found by combining these diagrams.
 * 
 * To improve performances, the individuals "stability" MDD are not built independently 
 * but immediately assembled.
 * The order in which they are considerd is also chosen to keep them small as long as possible.
 */
@ProviderFor( Service.class)
public class StableStatesService implements Service {

	/**
	 * This constructor should be called by the service manager,
	 * other users will have to get the first instance
	 */
	public StableStatesService() {
	}

	public StableStateSearcher getSearcher(RegulatoryGraph graph) {
		return new StableStatesAlgoImpl( graph);
	}
	
	public StableStateSearcher getStableStateSearcher( RegulatoryGraph regGraph, List nodeOrder, Perturbation mutant) {
		StableStateSearcher searcher = getSearcher(regGraph);
		searcher.setPerturbation(mutant);
		return searcher;
	}

//	public OMDDNode run( RegulatoryGraph regGraph, List nodeOrder, Perturbation mutant, OMDDNode[] trees) {
//		StableStateSearcher algo = getSearcher( regGraph);
//		algo.setPerturbation(mutant);
//		return algo.getStables();
//	}
	
	public void testNewStableSearch( RegulatoryGraph regGraph) {
		System.out.println("-========= Stables =========-");
		StableStateFinder finder = new StableStateFinder(regGraph);
		MDDFactory factory = finder.getFactory();
		
		int stables = finder.find();
		PathSearcher searcher = new PathSearcher(1);
		int[] path = searcher.setNode(factory, stables);
		for (int v: searcher) {
			for (int k: path) {
				if (k==-1) {
					System.out.print("* ");
				} else {
					System.out.print(k+" ");
				}
			}
			System.out.println();
		}
		System.out.println("-============================-");
	}
}
