package org.ginsim.service.tool.reg2dyn.limitedsimulation;

import java.util.HashMap;

import org.ginsim.common.application.GsException;
import org.ginsim.common.callable.ProgressListener;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.service.Service;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
public class LimitedSimulationService implements Service {
    static {
    	if( !ObjectAssociationManager.getInstance().isObjectManagerRegistred( DynamicGraph.class, StatesToHierarchicalMappingManager.KEY)){
    		ObjectAssociationManager.getInstance().registerObjectManager(DynamicGraph.class, new StatesToHierarchicalMappingManager());
    	}
    }

	private LimitedSimulation simu;

	/**
	 * Construct the dynamic graph correponding the contraint on the HTG.
	 * @param htg
	 * @param constraint
	 * @return the dynamic graph
	 * @throws GsException if getAssociatedGraph goes wrong
	 */
	public DynamicGraph run(HierarchicalTransitionGraph htg, SimulationConstraint constraint, Perturbation mutant) throws GsException {
		if (!constraint.isValid()) {
			throw new GsException(GsException.GRAVITY_ERROR, "no_hierarchicalNode_selected");
		}
		
		SimulationParameters params = new SimulationParameters(htg.getAssociatedGraph());
		params.store.setObject(SimulationParameters.MUTANT, mutant);
		
		SimpleSimulationManager simulationManager = new SimpleSimulationManager();
		this.simu = new LimitedSimulation(htg, constraint, params, simulationManager);
		simu.run();
		return (DynamicGraph) simulationManager.graph;
		
	}

	public static HashMap<DynamicNode, HierarchicalNode> getStatesToHierarchicalNodes(DynamicGraph dynamicGraph) {
		return 	(HashMap<DynamicNode, HierarchicalNode>) ObjectAssociationManager.getInstance().getObject(dynamicGraph, StatesToHierarchicalMappingManager.KEY, true);
	}
}

class SimpleSimulationManager implements ProgressListener<Graph> {

	protected Graph graph;

	@Override
	public void setResult(Graph graph) {
		this.graph = graph;		
	}

	@Override
	public void setProgress(int n) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setProgress(String s) {
		// TODO Auto-generated method stub
	}

	@Override
	public void milestone(Object item) {
		// TODO Auto-generated method stub
	}
	
}