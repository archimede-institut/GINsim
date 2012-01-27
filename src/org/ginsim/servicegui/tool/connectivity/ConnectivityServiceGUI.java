package org.ginsim.servicegui.tool.connectivity;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.core.graph.view.css.Colorizer;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.tool.connectivity.ConnectivityResult;
import org.ginsim.service.tool.connectivity.ConnectivitySelector;
import org.ginsim.service.tool.connectivity.ConnectivityService;
import org.mangosdk.spi.ProviderFor;


/**
 * register the connectivity service
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ConnectivityService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class ConnectivityServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof ReducedGraph) {
			actions.add( new ExtractFromSCCGraphAction( (ReducedGraph)graph));
		} else {
			actions.add( new ConnectivityColorizeGraphAction( graph));
		}
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
        ((ConnectivitySelector)colorizer.getSelector()).setCache(result.getComponents(), graph);
        colorizer.doColorize(graph);
	}
}

class ExtractFromSCCGraphAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
	
	protected ExtractFromSCCGraphAction( ReducedGraph<?, ?, ?> graph) {
        super( graph, "STR_connectivity_extract", null, "STR_connectivity_extract_descr", null);

	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		
		ReducedGraph<?, ?, ?> g = (ReducedGraph<?, ?, ?>)graph;
		GraphGUI<ReducedGraph<?, ?, ?>, NodeReducedData, ?> gui = GUIManager.getInstance().getGraphGUI(g);
		GraphSelection<NodeReducedData, ?> selection = gui.getSelection();
		
		if (selection == null || selection.getSelectedNodes() == null || selection.getSelectedNodes().size() < 1) {
			System.out.println("Select some nodes");
			return;
		}

		String s_ag = null;
		try {
			s_ag = g.getAssociatedGraphID();
		} catch (Exception e) {	}
		if (s_ag == null) {
			System.out.println("Missing associated Graph");
			return;
		}
		
		File f = new File(s_ag);
		if (!f.exists()) {
			System.out.println("Missing associated Graph file: "+s_ag);
			return;
		}
		
		
	    Graph subgraph = null;
	    try {
			Set<?> m = g.getSelectedSet( selection.getSelectedNodes());
			LogManager.debug("Extracting from: "+s_ag+". Selected: "+m);
			subgraph = GraphManager.getInstance().open(m, f);
		} catch (GsException e) {
			e.printStackTrace();
		}
	    if (subgraph != null) {
	        GUIManager.getInstance().whatToDoWithGraph(null, subgraph, true);
	    }
	}
}
