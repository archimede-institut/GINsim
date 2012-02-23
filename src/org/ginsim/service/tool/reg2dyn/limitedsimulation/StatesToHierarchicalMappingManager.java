package org.ginsim.service.tool.reg2dyn.limitedsimulation;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.objectassociation.BasicGraphAssociatedManager;

/**
 * An GraphAssociatedObjectManager providing an HashMap<DynamicNode, HierarchicalNode> storing a mapping between states and their corresponding HierarchicalNode
 * 
 * Usage : 
 * 	(HashMap<DynamicNode, HierarchicalNode>) ObjectAssociationManager.getInstance().getObject(dynamicGraph, StatesToHierarchicalMappingManager.key, true);
 * @author Duncan Berenguier
 *
 */
public class StatesToHierarchicalMappingManager extends BasicGraphAssociatedManager {

	public static final String KEY = "statesToHierarchical";

	public StatesToHierarchicalMappingManager() {
		super(KEY, null);
	}
	
	@Override
	public boolean needSaving(Graph graph) {
		return false;
	}

	@Override
	public void doSave(OutputStreamWriter out, Graph graph) throws GsException {
	}

	@Override
	public Object doOpen(InputStream is, Graph graph) throws GsException {
		return null;
	}

	@Override
	public Object doCreate(Graph graph) {
		return new HashMap<byte[], HierarchicalNode>();
	}


}
