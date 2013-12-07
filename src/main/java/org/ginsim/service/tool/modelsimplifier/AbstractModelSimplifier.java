package org.ginsim.service.tool.modelsimplifier;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.ginsim.common.application.GsException;


abstract public class AbstractModelSimplifier extends Thread implements Runnable {


	/* *************************************************************
	 *  
	 *  Deleting a node means removing it (while taking into account
	 *  its logical function) from all of its targets
	 *  
	 ***************************************************************/
	
	/**
	 * Internal hack to get the list of children.
	 * This should be moved the the MDDFactory itself.
	 * 
	 * @param factory
	 * @param node
	 * @return
	 */
	private int[] getChildren(MDDManager factory, int node) {
		if (factory.isleaf(node)) {
			return null;
		}
		
		int nbchildren = factory.getNodeVariable(node).nbval;
		int[] next = new int[nbchildren];
		for (int i=0 ; i<nbchildren ; i++) {
			next[i] = factory.getChild(node, i);
		}
		return next;
	}
	
	/**
	 * Remove <code>regulator</code> from its target <code>node</code>.
	 * This is the first part of the algo: we have not yet found the 
	 * regulator in the logical function.
	 * It will be called recursively until we find it (or go too far)
	 * 
	 * @param node
	 * @param regulator
	 * @param rmVar
	 * @return
	 */
	public int remove(MDDManager factory, int node, int regulator, MDDVariable rmVar) throws GsException {
		MDDVariable nvar = factory.getNodeVariable(node);
		if (nvar == null || nvar.after(rmVar)) {
			return node;
		}
		
		MDDVariable regVar = factory.getNodeVariable(regulator);
		if (nvar == rmVar) {
			if (regVar == null) {
				return factory.getChild(node, regulator);
			}
			if (regVar == rmVar) {
				throw new GsException(GsException.GRAVITY_ERROR, 
						"Can not continue the simplification: a circuit would get lost");
			}
			
			return remove(factory, getChildren(factory, node), regulator);
		}
		
		MDDVariable nextVariable;
		int[] next;
		if (regVar == null || regVar.after(nvar)) {
			nextVariable = nvar;
			next = new int[nextVariable.nbval];
			for (int i=0 ; i<next.length ; i++) {
				next[i] = remove(factory, factory.getChild(node,i), regulator, rmVar);
			}
		} else if (nvar.after(regVar)) {
			nextVariable = regVar;
			next = new int[nextVariable.nbval];
			for (int i=0 ; i<next.length ; i++) {
				next[i] = remove(factory, node, factory.getChild(regulator, i), rmVar);
			}
		} else {
			nextVariable = nvar;
			next = new int[nextVariable.nbval];
			for (int i=0 ; i<next.length ; i++) {
				next[i] = remove(factory, factory.getChild(node, i), factory.getChild(regulator, i), rmVar);
			}
		}
		
		return nextVariable.getNode(next);
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
	public int remove(MDDManager factory, int[] t_ori, int regulator) {
		if (factory.isleaf(regulator)) {
			return t_ori[regulator];
		}
		// first, lookup for the best next step
		MDDVariable regVar = factory.getNodeVariable(regulator);
		MDDVariable bestVar = regVar;
		int index = -1;
		for (int i=0 ; i<t_ori.length ; i++) {
			MDDVariable nvar = factory.getNodeVariable(t_ori[i]);
			if (nvar != null && bestVar.after(nvar)) { 
				// also update when equal to avoid stupid optimisations...
				bestVar = nvar;
				index = i;
			}
		}
		
		int[] next = new int[bestVar.nbval];
		if (index == -1) {
			for (int i=0 ; i<bestVar.nbval ; i++) {
				next[i] = remove(factory, t_ori, factory.getChild(regulator,i));
			}
		} else {
			for (int i=0 ; i<bestVar.nbval ; i++) {
				int[] t_recur = new int[t_ori.length];
				for (int j=0 ; j<t_recur.length ; j++) {
					int node = t_ori[j];
					MDDVariable nvar = factory.getNodeVariable(node);
					if (nvar == null || nvar.after(bestVar)) {
						t_recur[j] = node;
					} else {
						t_recur[j] = factory.getChild(node, i);
					}
				}
				if (regVar == bestVar) {
					next[i] = remove(factory, t_recur, factory.getChild(regulator,i));
				} else {
					next[i] = remove(factory, t_recur, regulator);
				}
			}
		}
		return bestVar.getNode(next);
	}

}
