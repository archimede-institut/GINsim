package org.ginsim.servicegui.tool.scc;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Action;

import org.colomoto.common.task.Task;
import org.colomoto.common.task.TaskListener;
import org.colomoto.common.task.TaskStatus;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.core.graph.view.style.StyleProvider;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.shell.actions.GenericGraphAction;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.shell.actions.ToolAction;
import org.ginsim.service.tool.scc.SCCGraphService;
import org.mangosdk.spi.ProviderFor;


/**
 * register the scc service
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(SCCGraphService.class)
@ServiceStatus( EStatus.RELEASED)
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


class SCCGraphAction extends ToolAction implements TaskListener {
	private static final long serialVersionUID = 8294301473668672512L;
	private final Graph graph;
    private Task<ReducedGraph> task = null;
	
	protected SCCGraphAction( Graph graph, ServiceGUI serviceGUI) {
        super( "STR_constructReducedGraph", "STR_constructReducedGraph_descr", serviceGUI);
		this.graph = graph;
	}
	
	@Override
	public synchronized void actionPerformed( ActionEvent arg0) {
        if (task != null && task.getStatus() == TaskStatus.RUNNING) {
            return;
        }
        setEnabled(false);
		SCCGraphService service = GSServiceManager.getService(SCCGraphService.class);
        task = service.backgroundSCCGraph(graph, this);
	}

    @Override
    public void taskUpdated(Task task) {
        if (task != this.task) {
            return;
        }
        ReducedGraph result = this.task.getResult();
        GUIManager.getInstance().whatToDoWithGraph(result);
        this.task = null;
        setEnabled(true);
    }
}


class ConnectivityColorizeGraphAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
    private StyleProvider styler;

	protected ConnectivityColorizeGraphAction( Graph graph, ServiceGUI serviceGUI) {
        super( graph, "STR_connectivity", null, "STR_connectivity_descr", null, serviceGUI);
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		SCCGraphService service = GSServiceManager.getService(SCCGraphService.class);
        List<NodeReducedData> components = service.getComponents(graph);
        styler = service.getStyleProvider(components, graph);
        graph.getStyleManager().setStyleProvider(styler);
        if (GUIManager.getInstance().getFrame(graph) == null) {
        		GUIManager.getInstance().whatToDoWithGraph(graph);
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
			subgraph = GSGraphManager.getInstance().open(set, f);
		} catch (GsException e) {
			e.printStackTrace();
		}
	    
	    if (subgraph == null) {
			LogManager.debug("SCC extraction led to a null graph");
			return;
	    }
	    
        GUIManager.getInstance().whatToDoWithGraph(subgraph, graph);
	}
}
