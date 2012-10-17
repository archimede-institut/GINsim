package org.ginsim.service.tool.circuit;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.NodeRelation;

/**
 * Extract functionality context for one interaction.
 * This will compute a MDD giving the part of the state space for which the given function is affected by a specific regulator.
 * More precisely, in the multi-valued case, it requires that crossing a specific threshold of the regulator will
 * make the target cross another specific threshold.
 *
 * <p>The resulting MDD will have 3 types of leaves:<ul>
 * <li>0 for the absence of effect</li>
 * <li>1 for a positive effect</li>
 * <li>2 for a negative effect</li>
 * </ul>
 * 
 * @author Aurelien Naldi
 */
public class ContextExtractor {

	private final MDDManager ddmanager;
	
	private MDDVariable regulator;
	private int regulatorThreshold;
	private int targetThreshold;
	
	public ContextExtractor(MDDManager ddmanager) {
		this.ddmanager = ddmanager;
	}
	
	public int getContext(int function, MDDVariable regulator) {
		return getContext(function, regulator, 1, 1);
	}
	
	public int getContext(int function, MDDVariable regulator, int regulatorThreshold, int targetThreshold) {
		this.regulator = regulator;
		this.regulatorThreshold = regulatorThreshold;
		this.targetThreshold = targetThreshold;
		
		return browse(function);
	}
	
	private int browse(int function) {
		
		if (ddmanager.isleaf(function)) {
			return 0;
		}
		
		MDDVariable curVariable = ddmanager.getNodeVariable(function);
		if (curVariable == regulator) {
			return browse(ddmanager.getChild(function, regulatorThreshold-1), ddmanager.getChild(function, regulatorThreshold));
		}
		
		if (curVariable.after(regulator)) {
			return 0;
		}

		if (curVariable.nbval == 2) {
			int v1 = browse(ddmanager.getChild(function, 0));
			int v2 = browse(ddmanager.getChild(function, 1));
			return curVariable.getNodeFree(v1, v2);
		}
		
		int[] children = new int[curVariable.nbval];
		for (int i=0 ; i<children.length ; i++) {
			children[i] = browse(ddmanager.getChild(function, i));
		}
		return curVariable.getNodeFree(children);
	}
	
	private int browse (int v1, int v2) {
		NodeRelation status = ddmanager.getRelation(v1, v2);
		
		switch (status) {
		case LL:
			// hooray, we can conclude!
			if (v1 < targetThreshold && v2 >= targetThreshold) {
				return 1;
			}
			if (v1 >= targetThreshold && v2 < targetThreshold) {
				return 2;
			}
			return 0;
			
			
		case LN:
		case NNf:
			MDDVariable var = ddmanager.getNodeVariable(v2);
			if (var.nbval == 2) {
				int l = browse(v1, ddmanager.getChild(v2,0));
				int r = browse(v1, ddmanager.getChild(v2,1));
				return var.getNodeFree(l, r);
			} else {
				int[] children = new int[var.nbval];
				for (int i=0 ; i<children.length ; i++) {
					children[i] = browse(v1, ddmanager.getChild(v2, i));
				}
				return var.getNodeFree(children);
			}

		case NL:
		case NNn:
			var = ddmanager.getNodeVariable(v1);
			if (var.nbval == 2) {
				int l = browse(ddmanager.getChild(v1,0), v2);
				int r = browse(ddmanager.getChild(v1,1), v2);
				return var.getNodeFree(l, r);
			} else {
				int[] children = new int[var.nbval];
				for (int i=0 ; i<children.length ; i++) {
					children[i] = browse(ddmanager.getChild(v1,i), v2);
				}
				return var.getNodeFree(children);
			}

			
		case NN:
			var = ddmanager.getNodeVariable(v1);
			if (var.nbval == 2) {
				int l = browse(ddmanager.getChild(v1,0), ddmanager.getChild(v2,0));
				int r = browse(ddmanager.getChild(v1,1), ddmanager.getChild(v2,1));
				return var.getNodeFree(l, r);
			} else {
				int[] children = new int[var.nbval];
				for (int i=0 ; i<children.length ; i++) {
					children[i] = browse(ddmanager.getChild(v1,i), ddmanager.getChild(v2,i));
				}
				return var.getNodeFree(children);
			}
		}
		throw new RuntimeException("Should not reach this point");
	}
}
