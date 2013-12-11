package org.ginsim.service.tool.modelsimplifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.colomoto.common.task.AbstractTask;
import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.tool.reduction.ModelReducer;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;

/**
 * Build a simplified model, based on a complete one, by removing some nodes.
 * 
 * The first step is to build new MDD for the targets of the removed nodes.
 * If this succeeded (no circuit was removed...), a new regulatory graph is created
 * and all non-removed nodes are copied into it, as well as all remaining interactions.
 * Then the logical parameters of the unaffected nodes are restored.
 * For the affected nodes, some work is required, using the newly built MDD for their logical function:
 * <ul>
 *   <li>new edges are added if needed (coming from the regulators of their deleted regulators)</li> 
 *   <li>new logical parameters are extracted from the MDD</li>
 * </ul>
 *
 * @author Aurelien Naldi
 */
public class ReductionTask extends AbstractTask<LogicalModel> {

    private final List<NodeInfo> nodeOrder;
    private final ReductionLauncher launcher;
    private final ModelReducer reducer;
    private final Collection<NodeInfo> to_remove;
    private final Collection<NodeInfo> l_removed;

    public ReductionTask(RegulatoryGraph graph, ModelSimplifierConfig config) {
        this(graph.getModel(), config, null);
    }

    public ReductionTask(LogicalModel model, ModelSimplifierConfig config) {
        this(model, config, null);
    }

    public ReductionTask(RegulatoryGraph graph, ModelSimplifierConfig config, ReductionLauncher launcher) {
        this(graph.getModel(), config, launcher);
    }

	public ReductionTask(LogicalModel model, ModelSimplifierConfig config, ReductionLauncher launcher) {
        this.nodeOrder = model.getNodeOrder();
        this.reducer = new ModelReducer(model);
        this.to_remove = new ArrayList<NodeInfo>(config.m_removed);
        this.l_removed = new ArrayList<NodeInfo>();
        this.launcher = launcher;
	}
	
    @Override
    public LogicalModel doGetResult() {
    	// prepare the list of removal requests
		List<NodeInfo> l_todo = new ArrayList<NodeInfo>();
		for (NodeInfo ni: to_remove) {
			l_todo.add(ni);
		}

		// perform the actual reduction
		l_todo = remove_all(l_todo);

		// the "main" part is done, did it finish or fail ?
		if (l_todo.size() > 0) {
			if (launcher != null) {
				if (!launcher.showPartialReduction(l_todo)) {
					return null;
				}
				
				LogManager.trace( "Partial reduction result...");
			} else {
				// it failed, trigger an error message
				StringBuffer sb = new StringBuffer("Reduction failed.\n  Removed: ");
				for (NodeInfo ni: l_removed) {
					sb.append(" "+ni);
				}
				sb.append("\n  Failed: ");
				for (NodeInfo ni: l_todo) {
					sb.append(" "+ni);
				}
				throw new RuntimeException(sb.toString());
			}
		}

        return reducer.getModel(false);
    }
    
    private List<NodeInfo> remove_all(List<NodeInfo> l_todo) {
		// first do the "real" simplification work
		int todoSize = l_todo.size();
		int oldSize = todoSize + 1;
		while (todoSize > 0 && todoSize < oldSize) {
			oldSize = todoSize;
			l_todo = remove_batch(l_todo);
			todoSize = l_todo.size();
		}
		return l_todo;
    }
	
    /**
     * Go through a list of nodes to remove and try to remove all of them.
     * <p>
     * It may fail on some removals, in which case it will go on with the others and add them to the list of failed.
     * 
     * @param l_todo
     * @return the list of failed removals.
     */
    private List<NodeInfo> remove_batch(List<NodeInfo> l_todo) {
    	LogManager.trace( "batch of removal...");
    	List<NodeInfo> l_failed = new ArrayList<NodeInfo>();
    	
		for (NodeInfo ni: l_todo) {
            try {
                int idx = nodeOrder.indexOf(ni);
                if (idx >= 0) {
                    reducer.remove(idx);
                }
                l_removed.add(ni);
            } catch (RuntimeException e) {
                // this removal failed, remember that we may get a second chance
                l_failed.add(ni);
            }
		}
    	return l_failed;
    }
}
