package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.List;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.datastore.SimpleGenericList;


public class PriorityClassManager extends SimpleGenericList {

	List nodeOrder;
	
	public PriorityClassManager(GsRegulatoryGraph graph) {
		this.nodeOrder = graph.getNodeOrder();
		canAdd = true;
		canOrder = true;
		canRemove = true;
		canEdit = true;
		enforceUnique = true;
		prefix = "priorities_";
	}
	
	public Object doCreate(String name) {
		return new PriorityClassDefinition(nodeOrder.iterator(), name);
	}
	public Object doCreate(String name, int pos) {
		return doCreate(name);
	}
}
