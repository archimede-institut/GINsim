package org.ginsim.core.graph.regulatorygraph.initialstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;


public class GsInitialStateList implements GraphListener<RegulatoryNode, RegulatoryMultiEdge> {
	RegulatoryGraph graph;

	List nodeOrder;
    List inputNodes = new ArrayList();
    List normalNodes = new ArrayList();

    final InitialStateList initialStates;
    final InitialStateList inputConfigs;
    
    public GsInitialStateList( Graph<?,?> graph) {
    	
        this.graph = (RegulatoryGraph) graph;
    	nodeOrder = this.graph.getNodeOrder();
        graph.addGraphListener( (GraphListener) this);
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

	public GraphEventCascade edgeAdded(RegulatoryMultiEdge data) {
		return null;
	}

	public GraphEventCascade edgeRemoved(RegulatoryMultiEdge data) {
		return null;
	}

	public GraphEventCascade edgeUpdated(RegulatoryMultiEdge data) {
		return null;
	}

	public GraphEventCascade nodeAdded(RegulatoryNode data) {
	    if (((RegulatoryNode)data).isInput()) {
            inputNodes.add(data);
        } else {
            normalNodes.add(data);
        }
		return null;
	}

	public GraphEventCascade graphMerged(Collection<RegulatoryNode> data) {
		return null;
	}

	public GraphEventCascade nodeRemoved(RegulatoryNode data) {
	    // update lists
        inputNodes.remove(data);
        normalNodes.remove(data);
        
        List l_changes = new ArrayList();
        initialStates.vertexRemoved(data, l_changes);
        inputConfigs.vertexRemoved(data, l_changes);
        if (l_changes.size() > 0) {
            return new InitialStateCascadeUpdate(l_changes);
        }
        return null;
	}

	public GraphEventCascade nodeUpdated(RegulatoryNode data) {
        List l_changes = new ArrayList();
	    // update lists
	    if (data.isInput() ? normalNodes.contains(data) : inputNodes.contains(data)) {
	        updateLists();
	        if (data.isInput()) {
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
        return null;
	}

	public void endParsing() {
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