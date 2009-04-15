package fr.univmrs.tagc.GINsim.regulatoryGraph.initialState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.tagc.GINsim.graph.GsGraphListener;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class GsInitialStateList implements GsGraphListener {
	GsRegulatoryGraph graph;

	List nodeOrder;
    List inputNodes = new ArrayList();
    List normalNodes = new ArrayList();

    final InitialStateList initialStates;
    final InitialStateList inputConfigs;
    
    public GsInitialStateList(GsGraph graph) {
        this.graph = (GsRegulatoryGraph)graph;
    	nodeOrder = graph.getNodeOrder();
        graph.addGraphListener(this);
        updateLists();
        
        initialStates = new InitialStateList(normalNodes, false);
        inputConfigs = new InitialStateList(inputNodes, true);
    }

	private void updateLists() {
	    inputNodes.clear();
	    normalNodes.clear();
	    Iterator it = nodeOrder.iterator();
	    while (it.hasNext()) {
	        GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
	        if (vertex.isInput()) {
	            inputNodes.add(vertex);
	        } else {
	            normalNodes.add(vertex);
	        }
	    }
	}

	public GsGraphEventCascade edgeAdded(Object data) {
		return null;
	}

	public GsGraphEventCascade edgeRemoved(Object data) {
		return null;
	}

	public GsGraphEventCascade edgeUpdated(Object data) {
		return null;
	}

	public GsGraphEventCascade vertexAdded(Object data) {
	    if (((GsRegulatoryVertex)data).isInput()) {
            inputNodes.add(data);
        } else {
            normalNodes.add(data);
        }
		return null;
	}

	public GsGraphEventCascade graphMerged(Object data) {
		return null;
	}

	public GsGraphEventCascade vertexRemoved(Object data) {
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

	public GsGraphEventCascade vertexUpdated(Object data) {
        List l_changes = new ArrayList();
	    // update lists
	    if (((GsRegulatoryVertex)data).isInput() ? normalNodes.contains(data) : inputNodes.contains(data)) {
	        updateLists();
	        if (((GsRegulatoryVertex)data).isInput()) {
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

class InitialStateCascadeUpdate implements GsGraphEventCascade {
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