package org.ginsim.servicegui.tool.connectivity;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.css.Colorizer;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.tool.connectivity.ConnectivityResult;
import org.ginsim.service.tool.connectivity.ConnectivitySelector;
import org.ginsim.service.tool.connectivity.ConnectivityService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.GenericGraphAction;
import org.mangosdk.spi.ProviderFor;


/**
 * register the connectivity service
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ConnectivityService.class)
public class ConnectivityServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ConnectivityColorizeGraphAction( graph));
		return actions;
	}

	@Override
	public int getWeight() {
		return W_GENERIC;
	}
}

class ConnectivityColorizeGraphAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
	private Colorizer colorizer;
	
	protected ConnectivityColorizeGraphAction( Graph graph) {
        super( graph, "STR_connectivity", null, "STR_connectivity_descr", null);
        colorizer = new Colorizer(new ConnectivitySelector());

	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		ConnectivityService service = ServiceManager.getManager().getService(ConnectivityService.class);
        ConnectivityResult result = service.run(graph);
        ((ConnectivitySelector)colorizer.getSelector()).setCache(result.getComponents());
        boolean isColored = colorizer.toggleColorize(graph);
        if (isColored) {
        	//TODO REFACTORING ACTION
        	//TODO change the name of the menu 
        } else {
        	//TODO change the name of the menu 
        }
	}
}
