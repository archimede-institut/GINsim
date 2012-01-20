package org.ginsim.core.graph.regulatorygraph.initialstate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;


public class GsInitialStateList implements GraphListener<RegulatoryGraph> {
	RegulatoryGraph graph;

	List nodeOrder;
    List inputNodes = new ArrayList();
    List normalNodes = new ArrayList();

    final InitialStateList initialStates;
    final InitialStateList inputConfigs;
    
    public GsInitialStateList( Graph<?,?> graph) {
    	
        this.graph = (RegulatoryGraph) graph;
    	nodeOrder = this.graph.getNodeOrder();
        GraphManager.getInstance().addGraphListener( (RegulatoryGraph)graph, this);
        updateLists();
        
        initialStates = new InitialStateList(normalNodes, false);
        inputConfigs = new InitialStateList(inputNodes, true);
    }

	private void updateLists() {
	    inputNodes.clear();
	    normalNodes.clear();
	    Iterator it = nodeOrder.iterator();
	    while (it.hasNext()) {
	        RegulatoryNode vertex = (RegulatoryNode)it.next();
	        if (vertex.isInput()) {
	            inputNodes.add(vertex);
	        } else {
	            normalNodes.add(vertex);
	        }
	    }
	}
	
	public List getNormalNodes() {
		
		return normalNodes;
	}
	
	public List getInputNodes() {
		
		return inputNodes;
	}

	@Override
	public GraphEventCascade graphChanged(RegulatoryGraph g, GraphChangeType type, Object data) {
		List l_changes;
		switch (type) {
		case NODEADDED:
		    if (((RegulatoryNode)data).isInput()) {
	            inputNodes.add(data);
	        } else {
	            normalNodes.add(data);
	        }
			break;
		case NODEREMOVED:
		    // update lists
	        inputNodes.remove(data);
	        normalNodes.remove(data);
	        
	        l_changes = new ArrayList();
	        initialStates.vertexRemoved(data, l_changes);
	        inputConfigs.vertexRemoved(data, l_changes);
	        if (l_changes.size() > 0) {
	            return new InitialStateCascadeUpdate(l_changes);
	        }
	        break;
		case NODEUPDATED:
			RegulatoryNode node = (RegulatoryNode)data;
	        l_changes = new ArrayList();
		    // update lists
		    if (node.isInput() ? normalNodes.contains(data) : inputNodes.contains(data)) {
		        updateLists();
		        if (node.isInput()) {
		            initialStates.vertexRemoved(data, l_changes);
		        } else {
	                inputConfigs.vertexRemoved(data, l_changes);
		        }
		    }
	        initialStates.vertexUpdated(data, l_changes);
	        inputConfigs.vertexUpdated(data, l_changes);
	        if (l_changes.size() > 0) {
	            return new InitialStateCascadeUpdate(l_changes);
	        }
	        break;
		}
		return null;
	}

    public boolean isEmpty() {
        return inputConfigs.getNbElements() == 0 && initialStates.getNbElements() == 0;
    }

    public InitialStateList getInitialStates() {
        return initialStates;
    }

    public InitialStateList getInputConfigs() {
        return inputConfigs;
    }
}

class InitialStateCascadeUpdate implements GraphEventCascade {
    protected InitialStateCascadeUpdate(List v) {
        this.v = v;
    }
    List v;

    public String toString() {
        StringBuffer s = new StringBuffer("updated initial states:");
        for (int i=0 ; i<v.size() ; i++) {
            s.append(" ");
            s.append(v.get(i));
        }
        return s.toString();
    }
}