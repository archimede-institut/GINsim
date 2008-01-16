package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

/**
 * Build a simplified model, based on a complete one, by removing some nodes.
 */
public class ModelSimplifier extends Thread implements Runnable {

	GsGraphManager manager;
	ModelSimplifierConfigDialog dialog;
	ModelSimplifierConfig config;
	int[] t_remove = null;

	GsRegulatoryGraph graph;
	GsRegulatoryGraph simplifiedGraph;
	Map m_modified = new HashMap();

	public ModelSimplifier(GsRegulatoryGraph graph, ModelSimplifierConfig config, ModelSimplifierConfigDialog dialog) {
		this.graph = graph;
		this.dialog = dialog;
		this.config = config;
		if (graph != null) {
			manager = graph.getGraphManager();
		}
		start();
	}

	public void run() {
		Iterator it;
		
		// first do the "real" simplification work
		it = config.m_removed.keySet().iterator();
		while (it.hasNext()) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
			
			// TODO: remove the vertex;
			System.out.println("has to remove: "+vertex.getId());
		}
		
		// then build the new regulatory graph
		simplifiedGraph = new GsRegulatoryGraph();
		GsGraphManager simplifiedManager = simplifiedGraph.getGraphManager();
		GsVertexAttributesReader vreader = manager.getVertexAttributesReader();
		GsVertexAttributesReader simplified_vreader = simplifiedManager.getVertexAttributesReader();
		it = graph.getNodeOrder().iterator();
		while (it.hasNext()) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
			if (!config.m_removed.containsKey(vertex)) {
				GsRegulatoryVertex clone = (GsRegulatoryVertex)vertex.clone();
				simplifiedManager.addVertex(clone);
				vreader.setVertex(vertex);
				simplified_vreader.setVertex(clone);
				simplified_vreader.copyFrom(vreader);
			}
		}
		
		if (dialog != null) {
			dialog.endSimu(simplifiedGraph);
		}
	}
	

	/* *************************************************************
	 *  
	 *  The real algo is here
	 *  
	 *  Deleting a node means removing it (by taking into account its logical
	 *  function) from all of its targets
	 *  
	 ***************************************************************/
	
	/**
	 * Remove <code>regulator</code> from its target <code>node</code>.
	 * This is the first part of the algo: we have not yet found the 
	 * regulator in the logical function.
	 * It will be called recursively until we find it (or go too far)
	 * 
	 * @param node
	 * @param regulator
	 * @param level
	 * @return
	 */
	public OmddNode remove(OmddNode node, OmddNode regulator, int level) throws GsException {
		
		if (node.next == null || node.level > level) {
			return node;
		}
		if (node.level == level) {
			if (regulator.next == null) {
				return node.next[regulator.value];
			}
			if (regulator.level == level) {
				throw new GsException(GsException.GRAVITY_ERROR, "Can not continue the simplification: a circuit would get lost");
			}
			return remove(node.next, regulator);
		}
		
		OmddNode ret = new OmddNode();
		if (regulator.next == null || regulator.level > node.level) {
			ret.level = node.level;
			ret.next = new OmddNode[node.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node.next[i], regulator, level);
			}
		} else if (node.level > regulator.level) {
			ret.level = regulator.level;
			ret.next = new OmddNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node, regulator.next[i], level);
			}
		} else {
			ret.level = node.level;
			ret.next = new OmddNode[node.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node.next[i], regulator.next[i], level);
			}
		}
		return ret;
	}

	/**
	 * Remove <code>regulator</code> from its target <code>node</code>.
	 * This is the second part of the algo: we have found the regulator 
	 * in the logical function.
	 * We must thus follow all branches corresponding to its possible values,
	 * until we can take the final decision.
	 * 
	 * @param t_ori
	 * @param regulator
	 * @return
	 */
	public OmddNode remove(OmddNode[] t_ori, OmddNode regulator) {
		if (regulator.next == null) {
			return t_ori[regulator.value];
		}
		// first, lookup for the best next step
		int best = regulator.level;
		int index = -1;
		for (int i=0 ; i<t_ori.length ; i++) {
			OmddNode node = t_ori[i];
			if (node.next != null && node.level <= best) { 
				// also update when equal to avoid stupid optimisations...
				best = node.level;
				index = i;
			}
		}
		
		OmddNode ret = new OmddNode();
		ret.level = best;
		if (index == -1) {
			ret.next = new OmddNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(t_ori, regulator.next[i]);
			}
		} else {
			ret.next = new OmddNode[t_ori[index].next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				OmddNode[] t_recur = new OmddNode[t_ori.length];
				for (int j=0 ; j<t_recur.length ; j++) {
					OmddNode node = t_ori[j];
					if (node.next == null || node.level > best) {
						t_recur[j] = node;
					} else {
						t_recur[j] = node.next[i];
					}
				}
				if (regulator.level == best) {
					ret.next[i] = remove(t_recur, regulator.next[i]);
				} else {
					ret.next[i] = remove(t_recur, regulator);
				}
			}
		}
		return ret;
	}
}
