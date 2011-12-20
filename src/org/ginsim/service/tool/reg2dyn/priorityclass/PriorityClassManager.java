package org.ginsim.service.tool.reg2dyn.priorityclass;

import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.SimpleGenericList;



public class PriorityClassManager extends SimpleGenericList<PriorityClassDefinition> {

	public static final String SYNCHRONOUS = "synchronous", ASYNCHRONOUS = "asynchronous"; 
	
	public final List<RegulatoryNode> nodeOrder;
	public static final String FILTER_NO_SYNCHRONOUS = "[no-synchronous]";
	
	private List<RegulatoryNode> filterInputVariables(List<RegulatoryNode> nodeOrder) {
		List<RegulatoryNode> alFiltered = new ArrayList<RegulatoryNode>();
		for (int i = 0; i < nodeOrder.size(); i++) {
			if (!nodeOrder.get(i).isInput())
				alFiltered.add(nodeOrder.get(i));
		}
		return alFiltered;
	}
	
	public PriorityClassManager(RegulatoryGraph graph) {
		this.nodeOrder = filterInputVariables(graph.getNodeOrder());
		canAdd = true;
		canOrder = true;
		canRemove = true;
		canEdit = true;
		enforceUnique = true;
		prefix = "priorities_";
		addOptions = new ArrayList<String>();
		addOptions.add("One unique class");
		addOptions.add("One class for each node");
		addOptions.add("Splitting transitions – one unique class");

		// add default priority classes
		int index = add();
		PriorityClassDefinition pcdef = (PriorityClassDefinition)getElement(null, index);
		pcdef.setName(ASYNCHRONOUS);
		Reg2dynPriorityClass pc = (Reg2dynPriorityClass)pcdef.getElement(null, 0);
		pc.setName("all");
		pc.setMode(Reg2dynPriorityClass.ASYNCHRONOUS);
		pcdef.lock();
		index = add();
		pcdef = (PriorityClassDefinition)getElement(null, index);
		pcdef.setName(SYNCHRONOUS);
		pc = (Reg2dynPriorityClass)pcdef.getElement(null, 0);
		pc.setName("all");
		pc.setMode(Reg2dynPriorityClass.SYNCHRONOUS);
		pcdef.lock();
	}
	
	public PriorityClassDefinition doCreate(String name, int mode) {
		PriorityClassDefinition pcdef = new PriorityClassDefinition(nodeOrder, name);
        Object lastClass = pcdef.v_data.get(0);
        Reg2dynPriorityClass currentClass;
        switch (mode) {
            case 1:
                // should be equivalent to the old priority system: add one class per node
            	pcdef.v_data.clear();
            	pcdef.m_elt.clear();
                for (int i=0 ; i<nodeOrder.size() ; i++) {
                    currentClass = new Reg2dynPriorityClass();
                    pcdef.v_data.add(i, currentClass);
                    pcdef.m_elt.put(nodeOrder.get(i), currentClass);
                    currentClass.setName(""+nodeOrder.get(i));
                }
                break;
            case 2:
                for (int i=0 ; i<nodeOrder.size() ; i++) {
                    Object[] t = {lastClass, lastClass};
                    pcdef.m_elt.put(nodeOrder.get(i), t);
                }
                break;
        }
        return pcdef;
    }

	public PriorityClassDefinition doCreate(String name, int pos, int mode) {
		return doCreate(name, mode);
	}
	public boolean remove(String filter, int[] t_index) {
		if (t_index.length < 1 || getRealIndex(filter, t_index[0]) < 2) {
			return false;
		}
		return super.remove(filter, t_index);
	}
	
    protected void doMoveUp(int[] selection, int diff) {
    	if (selection.length < 1 || selection[0] < 3) {
    		return;
    	}
    	super.doMoveUp(selection, diff);
    }
    protected void doMoveDown(int[] selection, int diff) {
    	if (selection.length < 1 || selection[0] < 2) {
    		return;
    	}
    	super.doMoveDown(selection, diff);
    }
	public boolean match(String filter, Object o) {
		if (filter != null && filter.startsWith(FILTER_NO_SYNCHRONOUS)) {
			PriorityClassDefinition pcdef = (PriorityClassDefinition)o;
			
			int l = pcdef.getNbElements();
			boolean hasSync = false;
			for (int i=0 ; i<l ; i++) {
				Reg2dynPriorityClass pc = (Reg2dynPriorityClass)pcdef.getElement(null, i);
				if (pc.getMode() == Reg2dynPriorityClass.SYNCHRONOUS) {
					hasSync = true;
					break;
				}
			}
			if (hasSync) {
				return false;
			}
			String realfilter = filter.substring(FILTER_NO_SYNCHRONOUS.length()).trim();
			if (realfilter.length() == 0) {
				return true;
			}
			return super.match(realfilter, o);
		}
		return super.match(filter, o);
	}
}
