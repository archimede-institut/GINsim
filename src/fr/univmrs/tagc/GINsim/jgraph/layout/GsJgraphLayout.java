package fr.univmrs.tagc.GINsim.jgraph.layout;

import javax.swing.JFrame;

import org.ginsim.graph.Graph;
import org.jgraph.JGraph;
import org.jgraph.layout.CircleGraphLayout;
import org.jgraph.layout.JGraphLayoutAlgorithm;
import org.jgraph.layout.TreeLayoutAlgorithm;
import org.jgrapht.ext.JGraphModelAdapter;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;

import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphtGraphManager;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;

/**
 * use jgraph-addon to apply some auto-layout on our graph.
 * 
 * as they seems broken/slow/not adapted, this plugin is disabled for now.
 */
public class GsJgraphLayout implements GsPlugin, GsActionProvider {

	private static final int LAYOUT_CIRCLE		= 0;
	private static final int LAYOUT_ANNEALING	= 1;
	private static final int NBLAYOUT 			= 2;
	
	private GsPluggableActionDescriptor[] t_layout = null;
	
	/*
	 * @see fr.univmrs.tagc.GINsim.plugin.GsPlugin#registerPlugin()
	 */
	public void registerPlugin() {
	    // jgraphaddons layouts are disabled
		// GsGraph.registerLayoutProvider(this);
	}

	public void runAction (int actionType, int ref, Graph graph, JFrame frame) {
        if (actionType != ACTION_LAYOUT) {
            return;
        }

		JGraph jgraph = ((GsJgraphtGraphManager)graph.getGraphManager()).getJgraph();
		JGraphLayoutAlgorithm layoutAlgo = null;
		switch (ref) {
			case LAYOUT_CIRCLE:
				layoutAlgo = new CircleGraphLayout();
				break;
			case LAYOUT_ANNEALING:
				layoutAlgo = new TreeLayoutAlgorithm();
				break;
			default:
				return;
		}
		Object[] roots = jgraph.getSelectionCells();
		if (roots == null || roots.length == 0) {
			roots = jgraph.getRoots();
		} 
		
		layoutAlgo.run(jgraph, roots);
		((JGraphModelAdapter)jgraph.getModel()).cellsChanged(roots);
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		
        if (actionType != ACTION_LAYOUT) {
            return null;
        }
        if (t_layout == null) {
            t_layout = new GsPluggableActionDescriptor[NBLAYOUT];
            t_layout[LAYOUT_CIRCLE] = new GsPluggableActionDescriptor("STR_Circlelayout", "STR_Circlelayout_descr", null, this, ACTION_LAYOUT, LAYOUT_CIRCLE);
            t_layout[LAYOUT_ANNEALING] = new GsPluggableActionDescriptor("STR_Annealinglayout", "STR_Annealinglayout_descr", null, this, ACTION_LAYOUT, LAYOUT_ANNEALING);
        }
        return t_layout;
	}
	
}
