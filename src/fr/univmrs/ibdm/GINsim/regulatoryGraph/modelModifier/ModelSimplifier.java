package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import java.text.DateFormat;
import java.util.*;
import java.util.Map.Entry;

import fr.univmrs.ibdm.GINsim.annotation.Annotation;
import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
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
	Vector oldNodeOrder;
	GsRegulatoryGraph simplifiedGraph;
	Map m_modified = new HashMap();
	Map m_edges = new HashMap();
	Map copyMap = new HashMap();

	
	public ModelSimplifier(GsRegulatoryGraph graph, ModelSimplifierConfig config, ModelSimplifierConfigDialog dialog) {
		this.graph = graph;
		this.oldNodeOrder = graph.getNodeOrder();
		this.dialog = dialog;
		this.config = config;
		manager = graph.getGraphManager();
		start();
	}

	public void run() {
		Iterator it;
		
		// first do the "real" simplification work
		Map m_affected = new HashMap();
		String s_comment = "";
		it = config.m_removed.keySet().iterator();
		while (it.hasNext()) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
			OmddNode deleted = vertex.getTreeParameters(graph);
			int pos = graph.getNodeOrder().indexOf(vertex);
			s_comment += ", "+vertex.getId();
			
			// mark all its targets as affected
			Iterator it_targets = manager.getOutgoingEdges(vertex).iterator();
			while (it_targets.hasNext()) {
				GsRegulatoryVertex target = (GsRegulatoryVertex)((GsDirectedEdge)it_targets.next()).getTargetVertex();
				OmddNode targetNode = (OmddNode)m_affected.get(target);
				if (targetNode == null) {
					targetNode = target.getTreeParameters(graph);
				}
				try {
					System.out.println("add affected node: "+target);
					m_affected.put(target, remove(targetNode, deleted, pos));
				} catch (GsException e) {
					e.printStackTrace();
				}
			}
		}
		
		// create the new regulatory graph
		simplifiedGraph = new GsRegulatoryGraph();
		Annotation note = simplifiedGraph.getAnnotation();
		note.copyFrom(graph.getAnnotation());
		if (s_comment.length() > 2) {
			note.setComment("Model Generated by GINsim on "+
					DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()) +
					", by removing the following nodes: "+s_comment.substring(2)+
					"\n\n"+note.getComment());
		}
		
		GsGraphManager simplifiedManager = simplifiedGraph.getGraphManager();
		Vector simplified_nodeOrder = simplifiedGraph.getNodeOrder();
		
		// Create all the nodes of the new model
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
				copyMap.put(vertex, clone);
				simplified_nodeOrder.add(clone);
			}
		}
		
		// copy all unaffected edges
		GsEdgeAttributesReader ereader = manager.getEdgeAttributesReader();
		GsEdgeAttributesReader simplified_ereader = simplifiedManager.getEdgeAttributesReader();
		it = manager.getEdgeIterator();
		while (it.hasNext()) {
			GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)((GsDirectedEdge)it.next()).getUserObject();
			GsRegulatoryVertex src = (GsRegulatoryVertex)copyMap.get(me.getSource());
			GsRegulatoryVertex target = (GsRegulatoryVertex)copyMap.get(me.getTarget());
			if (src != null && target != null) {
				GsRegulatoryMultiEdge me_clone = new GsRegulatoryMultiEdge(src, target);
				me_clone.copyFrom(me);
				simplifiedManager.addEdge(src, target, me_clone);
				copyMap.put(me, me_clone);
				ereader.setEdge(me);
				simplified_ereader.setEdge(me_clone);
				simplified_ereader.copyFrom(ereader);
			}
		}

		// copy parameters/logical functions on the unaffected nodes
		it = oldNodeOrder.iterator();
		while (it.hasNext()) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
			GsRegulatoryVertex clone = (GsRegulatoryVertex)copyMap.get(vertex);
			if (!config.m_removed.containsKey(vertex)) {
				if (! m_affected.containsKey(vertex)) {
					clone.cleanupInteractionForNewGraph(copyMap);
					// TODO: copy logical functions as well (cf copy/paste)
				} else {
					System.out.println("fixing affected node: "+vertex);
					OmddNode newNode = (OmddNode)m_affected.get(vertex);

					// make sure that the needed edges target the affected node
					m_edges.clear(); 
					extractEdgesFromNode(newNode);
					GsRegulatoryVertex target = (GsRegulatoryVertex)copyMap.get(vertex);
					Iterator it_newEdges = m_edges.entrySet().iterator();
					while (it_newEdges.hasNext()) {
						Entry e = (Entry)it_newEdges.next();
						GsRegulatoryVertex src = (GsRegulatoryVertex)copyMap.get(e.getKey());
						GsRegulatoryMultiEdge new_me = (GsRegulatoryMultiEdge)simplifiedManager.getEdge(src, target);
						if (new_me == null) {
							new_me = new GsRegulatoryMultiEdge(src, target);
							simplifiedManager.addEdge(src, target, new_me);
							System.out.println("  add edge: ("+src+","+target+")");
						}
						boolean[] t_required = (boolean[])e.getValue();
						new_me.copyFrom(t_required);
					}
					
					// rebuild the parameters
					m_edges.clear();
					List edges = simplifiedManager.getIncomingEdges(clone);
					Iterator it2 = edges.iterator();
					while (it2.hasNext()) {
						GsDirectedEdge e = (GsDirectedEdge)it2.next();
						Object src = e.getSourceVertex();
						m_edges.put(src, new int[2]);
					}
					buildParametersFromNode(edges, newNode);
				}
			}
		}
		
		if (dialog != null) {
			dialog.endSimu(simplifiedGraph);
		}
	}
	
	/**
	 * extract the list of required edges for a given logical function.
	 * @param node
	 */
	private void extractEdgesFromNode(OmddNode node) {
		if (node.next == null) {
			return;
		}
		GsRegulatoryVertex vertex = (GsRegulatoryVertex)oldNodeOrder.get(node.level);
		boolean[] t_threshold = (boolean[])m_edges.get(vertex);
		if (t_threshold == null) {
			t_threshold = new boolean[vertex.getMaxValue()+1];
			for (int i=0 ; i<t_threshold.length ; i++) {
				t_threshold[i] = false;
			}
			m_edges.put(vertex, t_threshold);
		}

		OmddNode child = null;
		for (int i=0 ; i<node.next.length ; i++) {
			if (child != node.next[i]) {
				if (child != null) {
					t_threshold[i] = true;
				}
				child = node.next[i];
				extractEdgesFromNode(node.next[i]);
			}
		}
	}

	/**
	 * build the logical parameters corresponding to a given logical function.
	 * @param node
	 */
	private void buildParametersFromNode(List edges, OmddNode node) {
		if (node.next == null) {
			// this is a leaf: build the parameters if needed
			if (node.value == 0) {
				// 0-valued leaf: no parameters
				return;
			}
			System.out.println("--> one set of parameters here:");
			Iterator it = m_edges.entrySet().iterator();
			System.out.print("  ");
			while (it.hasNext()) {
				Entry e = (Entry)it.next();
				System.out.print(e.getKey()+":"+e.getValue()+"  ");
			}
			System.out.println();
			return;
		}
		// continue but remember the selected value for this node
		Object vertex = copyMap.get(oldNodeOrder.get(node.level));
		OmddNode child = null;
		for (int i=0 ; i<node.next.length ; i++) {
			if (child != node.next[i]) {
				child = node.next[i];
				m_edges.put(vertex, null); // FIXME
				buildParametersFromNode(edges, node.next[i]);
			}
		}
		m_edges.remove(vertex);
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
