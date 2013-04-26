package org.ginsim.servicegui.tool.connectivity;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Action;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.core.graph.view.css.Colorizer;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.connectivity.ConnectivityResult;
import org.ginsim.service.tool.connectivity.ConnectivitySelector;
import org.ginsim.service.tool.connectivity.ConnectivityService;
import org.ginsim.service.tool.sccgraph.SCCGraphResult;
import org.ginsim.service.tool.sccgraph.SCCGraphService;
import org.mangosdk.spi.ProviderFor;


/**
 * register the connectivity service
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ConnectivityService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class ConnectivityServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof ReducedGraph) {
			actions.add( new ExtractFromSCCGraphAction( (ReducedGraph)graph, this));
		} else {
			actions.add( new ConnectivityColorizeGraphAction( graph, this));
			actions.add( new SCCGraphAction( graph, this));
		}
		return actions;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 35;
	}
}


class SCCGraphAction extends ToolAction {
	private static final long serialVersionUID = 8294301473668672512L;
	private Graph graph;
	
	protected SCCGraphAction( Graph graph, ServiceGUI serviceGUI) {
        super( "STR_constructReducedGraph", "STR_constructReducedGraph_descr", serviceGUI);
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		SCCGraphService service = ServiceManager.getManager().getService(SCCGraphService.class);
        SCCGraphResult result = service.run(graph);
        GUIManager.getInstance().whatToDoWithGraph(result.getReducedGraph(), true);
	}
}


class ConnectivityColorizeGraphAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
	private Colorizer colorizer;
	
	protected ConnectivityColorizeGraphAction( Graph graph, ServiceGUI serviceGUI) {
        super( graph, "STR_connectivity", null, "STR_connectivity_descr", null, serviceGUI);
        colorizer = new Colorizer(new ConnectivitySelector());

	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		ConnectivityService service = ServiceManager.getManager().getService(ConnectivityService.class);
        ConnectivityResult result = service.run(graph);
        ((ConnectivitySelector)colorizer.getSelector()).setCache(result.getComponents(), graph);
        colorizer.doColorize(graph);
        if (GUIManager.getInstance().getFrame(graph) == null) {
        		GUIManager.getInstance().whatToDoWithGraph(graph, true);
        }
	}
}

class ExtractFromSCCGraphAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
	
	protected ExtractFromSCCGraphAction( ReducedGraph<?, ?, ?> graph, ServiceGUI serviceGUI) {
        super( graph, "STR_connectivity_extract", null, "STR_connectivity_extract_descr", null, serviceGUI);

	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		
		ReducedGraph<?, ?, ?> g = (ReducedGraph<?, ?, ?>)graph;
		GraphGUI<ReducedGraph<?, ?, ?>, NodeReducedData, ?> gui = GUIManager.getInstance().getGraphGUI(g);
		GraphSelection<NodeReducedData, ?> selection = gui.getSelection();
		
		if (selection == null || selection.getSelectedNodes() == null || selection.getSelectedNodes().size() < 1) {
			LogManager.debug("Select some nodes");
			GUIMessageUtils.openErrorDialog( "STR_unableToOpen_SeeLogs");
			return;
		}

		String s_ag = null;
		try {
			s_ag = g.getAssociatedGraphID();
			
		} catch (Exception e) {
		}
		
		if (s_ag == null) {
			try {
				Graph ag = g.getAssociatedGraph();
				if (ag != null) {
					boolean save = GUIMessageUtils.openConfirmationDialog("This requires to save the original graph. Save it now?", "Save associated graph?");
					if (save) {
						GraphGUI graph_gui = GUIManager.getInstance().getGraphGUI( ag);
						graph_gui.saveAs();
						s_ag = g.getAssociatedGraphID();
					}
				}
			} catch (GsException e1) {
			}
		}
		
		if (s_ag == null) {
			LogManager.debug("Missing associated Graph");
			GUIMessageUtils.openErrorDialog( "STR_unableToOpen_SeeLogs");
			return;
		}
		
		File f = new File(s_ag);
		if (!f.exists()) {
			LogManager.debug("Missing associated Graph file: "+s_ag);
			GUIMessageUtils.openErrorDialog( "STR_unableToOpen_SeeLogs");
			return;
		}
		
		
	    Graph subgraph = null;
	    try {
			Set<?> set = g.getSelectedSet( selection.getSelectedNodes());
			subgraph = GraphManager.getInstance().open(set, f);
		} catch (GsException e) {
			e.printStackTrace();
		}
	    
	    if (subgraph == null) {
			LogManager.debug("SCC extraction led to a null graph");
			return;
	    }
	    
        GUIManager.getInstance().whatToDoWithGraph(subgraph, graph, true);
	}
}
