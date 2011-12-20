package org.ginsim.core.notification.resolvable.resolution;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeExpression;

public class InvalidFunctionResolution extends NotificationResolution {

	
	@Override
	public boolean perform( Graph graph, Object[] data, int index) {
		
		byte value = ((Short) data[0]).byteValue();
		RegulatoryNode vertex = (RegulatoryNode) data[1];
		String exp = (String) data[2];
		boolean ok = true;
		switch (index) {
		  case 0 :
		  	try {
		  	  TreeInteractionsModel interactionList = vertex.getInteractionsModel();
		  	  TreeExpression texp = interactionList.addEmptyExpression(value, vertex);
      	  texp.setText(exp);
          texp.setProperty("invalid", new Boolean("true"));
        }
		  	catch (Exception ex) {
		  		ex.printStackTrace();
		  		ok = false;
		  	}
			  break;
		  case 1 :
		  	break;
		}
		
		return ok;
	}
		
	@Override
	public String[] getOptionsName() {
		
		String[] t = {"Keep function", "Discard function"};
		return t;
	}
}
