package org.ginsim.service.tool.reg2dyn.priorityclass;

import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.NamedList;


public class PriorityClassManager extends NamedList<PriorityClassDefinition> {

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
	
	public PriorityClassManager(RegulatoryGraph graph) {
		this.nodeOrder = filterInputVariables(graph.getNodeOrder());

		// add default priority classes
		int index = addDefinition(null);
		PriorityClassDefinition pcdef = get(index);
		pcdef.setName(ASYNCHRONOUS);
		Reg2dynPriorityClass pc = pcdef.get(0);
		pc.setName("all");
		pc.setMode(Reg2dynPriorityClass.ASYNCHRONOUS);
		pcdef.lock();

		index = addDefinition(null);
		pcdef = get(index);
		pcdef.setName(SYNCHRONOUS);
		pc = pcdef.get(0);
		pc.setName("all");
		pc.setMode(Reg2dynPriorityClass.SYNCHRONOUS);
		pcdef.lock();
	}

    public int addDefinition(Object mode) {
        PriorityClassAddMode addMode = PriorityClassAddMode.SIMPLE;
        if (mode instanceof PriorityClassAddMode) {
            addMode = (PriorityClassAddMode)mode;
        }
        String name = findUniqueName("priorities ");

		PriorityClassDefinition pcdef = new PriorityClassDefinition(nodeOrder, name);
        Object lastClass = pcdef.get(0);
        Reg2dynPriorityClass currentClass;
        switch (addMode) {
            case SPLIT:
                // should be equivalent to the old priority system: add one class per node
            	pcdef.clear();
            	pcdef.m_elt.clear();
                for (int i=0 ; i<nodeOrder.size() ; i++) {
                    currentClass = new Reg2dynPriorityClass();
                    pcdef.add(i, currentClass);
                    pcdef.m_elt.put(nodeOrder.get(i), currentClass);
                    currentClass.setName(""+nodeOrder.get(i));
                }
                break;
            case FINE_GRAINED:
                for (int i=0 ; i<nodeOrder.size() ; i++) {
                    Object[] t = {lastClass, lastClass};
                    pcdef.m_elt.put(nodeOrder.get(i), t);
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
        List<PriorityClassDefinition> toRemove = new ArrayList<PriorityClassDefinition>();
        for (int i: sel) {
            if (i < 2) {
                return false;
            }
            toRemove.add(get(i));
        }
        return removeAll(toRemove);
    }
}
