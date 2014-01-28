package org.ginsim.service.tool.reg2dyn.priorityclass;

import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.NamedList;

/**
 * The list of all available priority set definitions.
 *
 * @author aurelien Naldi
 */
public class PrioritySetList extends NamedList<PrioritySetDefinition> {

	public static final String SYNCHRONOUS = "synchronous", ASYNCHRONOUS = "asynchronous"; 
	
	public final List<RegulatoryNode> nodeOrder;

	private static List<RegulatoryNode> filterInputVariables(List<RegulatoryNode> nodeOrder) {
		List<RegulatoryNode> alFiltered = new ArrayList<RegulatoryNode>();
		for (int i = 0; i < nodeOrder.size(); i++) {
			if (!nodeOrder.get(i).isInput())
				alFiltered.add(nodeOrder.get(i));
		}
		return alFiltered;
	}
	
	public PrioritySetList(RegulatoryGraph graph) {
		this.nodeOrder = filterInputVariables(graph.getNodeOrder());

		// add default priority classes
		int index = addDefinition(null);
		PrioritySetDefinition pcdef = get(index);
		pcdef.setName(ASYNCHRONOUS);
		PriorityClass pc = pcdef.get(0);
		pc.setName("all");
		pc.setMode(PriorityClass.ASYNCHRONOUS);
		pcdef.lock();

		index = addDefinition(null);
		pcdef = get(index);
		pcdef.setName(SYNCHRONOUS);
		pc = pcdef.get(0);
		pc.setName("all");
		pc.setMode(PriorityClass.SYNCHRONOUS);
		pcdef.lock();
	}

    public int addDefinition(Object mode) {
        PrioritySetAddMode addMode = PrioritySetAddMode.SIMPLE;
        if (mode instanceof PrioritySetAddMode) {
            addMode = (PrioritySetAddMode)mode;
        }
        String name = findUniqueName("priorities ");

		PrioritySetDefinition pcdef = new PrioritySetDefinition(nodeOrder, name);
        PriorityClass lastClass = pcdef.get(0);
        PriorityClass currentClass;
        switch (addMode) {
            case SPLIT:
                // should be equivalent to the old priority system: add one class per node
            	pcdef.clear();
            	pcdef.m_elt.clear();
                int rank = 0;
                for (RegulatoryNode node: nodeOrder) {
                    currentClass = pcdef.get(pcdef.add());
                    pcdef.associate(node, currentClass);
                    currentClass.setName(node.getId());
                }
                break;
            case FINE_GRAINED:
                for (RegulatoryNode node: nodeOrder) {
                    pcdef.associate(node, lastClass, lastClass);
                }
                break;
        }

        add(pcdef);
        return size()-1;
    }

    public boolean canMoveItems(int[] selection) {
    	if (selection.length < 1) {
    		return false;
    	}

        // check that the first two definitions are not selected
        for (int i: selection) {
            if (i<2) {
                return false;
            }
        }
        return true;
    }

    public boolean removeSelection(int[] sel) {
        List<PrioritySetDefinition> toRemove = new ArrayList<PrioritySetDefinition>();
        for (int i: sel) {
            if (i < 2) {
                return false;
            }
            toRemove.add(get(i));
        }
        return removeAll(toRemove);
    }
}
