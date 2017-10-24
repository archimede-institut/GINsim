package org.ginsim.service.tool.avatar.params;

import java.util.Collection;

import org.ginsim.core.graph.*;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesHandler;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesManager;
import org.ginsim.core.utils.data.GenericListListener;
import org.ginsim.core.utils.data.NamedList;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetList;
import org.ginsim.service.tool.reg2dyn.updater.UpdaterDefinition;

/**
 * Stores the context of simulations (parameters and changes to the input model)
 * @author Rui Henriques
 * @version 1.0
 */
public class AvatarParameterList extends NamedList<AvatarParameters> implements GraphListener<RegulatoryGraph>, GenericListListener {
	
	private static final long serialVersionUID = 1L;
	private final RegulatoryGraph graph;
    private final PrioritySetList pcmanager;
    private final NamedStatesHandler imanager;

    /**
     * @param graph
     */
    public AvatarParameterList(Graph<RegulatoryNode,RegulatoryMultiEdge> graph, AvatarParameters param) {
        this.graph = (RegulatoryGraph) graph;
        imanager = (NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(graph, NamedStatesManager.KEY, true);
        imanager.getInitialStates().addListListener(this);
        imanager.getInputConfigs().addListListener(this);
        pcmanager = new PrioritySetList(this.graph);
        GSGraphManager.getInstance().addGraphListener(this.graph, this);
        if(param!=null) add(param);
    }
	
	@Override
	public String toString(){
		String result = "";
		for(AvatarParameters param : this)
			result += param.toFullString()+"\n";
		return result;
	}

    /***********************
     **** GRAPH CHANGES ****
     ***********************/
    
    /* (non-Javadoc)
     * @see org.ginsim.core.graph.GraphListener#graphChanged(org.ginsim.core.graph.GraphModel, org.ginsim.core.graph.GraphChangeType, java.lang.Object)
     */
    @Override
	public GraphEventCascade graphChanged(RegulatoryGraph g, GraphChangeType type, Object data) {
		switch (type) {
		case NODEADDED: nodeAdded(data); break;
		case NODEREMOVED: nodeRemoved(data); break;
		case GRAPHMERGED:
			Collection<?> items = (Collection<?>)data;
			for (Object item: items) if(!(item instanceof Edge)) nodeAdded(item);
			break;
		case NODEUPDATED:
			RegulatoryNode node = (RegulatoryNode)data;
			if (node.isInput()) nodeRemoved(node);
			else nodeAdded(node);
			break;
		}
		return null;
	}
	
    private void nodeRemoved(Object data) {
    	pcmanager.nodeOrder.remove(data); // remove it from priority classes
        for (int i=0 ; i<pcmanager.size() ; i++) {
        	UpdaterDefinition updater = pcmanager.get(i);
        	// TODO: future types of updaters may need some treatment as well
        	if (updater instanceof PrioritySetDefinition) {
        		PrioritySetDefinition pcdef = (PrioritySetDefinition)updater;
        		if (pcdef.m_elt != null) pcdef.m_elt.remove(data);
        	}
        }
    }
    
	private void nodeAdded(Object data) {
		RegulatoryNode node = (RegulatoryNode)data; //add it to the default priority class
		if (pcmanager.nodeOrder.contains(node)) return;
		pcmanager.nodeOrder.add(node);
        for (int i=0 ; i<pcmanager.size() ; i++) {
        	UpdaterDefinition updater = pcmanager.get(i);
        	// TODO: future types of updaters may need some treatment as well
        	if (updater instanceof PrioritySetDefinition) {
        		PrioritySetDefinition pcdef = (PrioritySetDefinition)updater;
        		if (pcdef.m_elt != null) pcdef.m_elt.put(node, pcdef.get(0));
        	}
        }
	}

	@Override
	public void itemAdded(Object item, int pos) {}
	@Override
	public void itemRemoved(Object item, int pos) {}
	@Override
	public void contentChanged() {}
	@Override
	public void structureChanged() {}
}
