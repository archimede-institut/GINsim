package fr.univmrs.tagc.mdd;


public class DecisionDiagramAction {
	public final int[] t;

	DecisionDiagramAction (int[] t) {
		this.t = t;
	}


	protected DecisionDiagramAction() {
		this.t = new int[MDDNode.NBSTATUS];
	}

	static final DecisionDiagramAction ACTION_MIN = new DecisionDiagramAction(new int[] {
			MDDNode.MIN, MDDNode.CHILDOTHER, MDDNode.CHILDTHIS,
			MDDNode.CHILDBOTH, MDDNode.CHILDTHIS, MDDNode.CHILDOTHER
	});
	static final DecisionDiagramAction ACTION_MAX = new DecisionDiagramAction(new int[] {
			MDDNode.MAX, MDDNode.CHILDOTHER, MDDNode.CHILDTHIS,
			MDDNode.CHILDBOTH, MDDNode.CHILDTHIS, MDDNode.CHILDOTHER
	});
	static final DecisionDiagramAction ACTION_AND = new AndAction();
	static final DecisionDiagramAction ACTION_OR = new OrAction();

	/**
	 * if some cases need more info to be tested, put the ASKME value in the array t
	 * and implement this complementary function.
	 *
	 * @param first
	 * @param other
	 * @param type
	 * @return
	 */
	public int ask(MDDNode first, MDDNode other, int type) {
		return 0;
	}

	/**
	 * put the CUSTOM value in t and implement this function to add more complex behaviors
	 * @param ddi
	 * @param first
	 * @param other
	 * @param type
	 * @return
	 */
	public MDDNode custom(DecisionDiagramInfo ddi, MDDNode first, MDDNode other, int type) {
		return null;
	}
}

class AndAction extends DecisionDiagramAction {
	AndAction() {
		super(new int[] {
				MDDNode.MIN, MDDNode.ASKME, MDDNode.ASKME,
				MDDNode.CHILDBOTH, MDDNode.CHILDTHIS, MDDNode.CHILDOTHER
		});
	}

	public int ask(MDDNode first, MDDNode other, int type) {
		switch (type) {
			case MDDNode.LN:
				if (((MDDLeaf)first).value > 0) {
					return MDDNode.OTHER;
				}
				return MDDNode.THIS;
			case MDDNode.NL:
				if (((MDDLeaf)other).value > 0) {
					return MDDNode.THIS;
				}
				return MDDNode.OTHER;
		}
		System.out.println("DEBUG: ask should not come here!");
		return MDDNode.ASKME;
	}
}

class OrAction extends DecisionDiagramAction {
	OrAction() {
		super(new int[] {
				MDDNode.MAX, MDDNode.ASKME, MDDNode.ASKME,
				MDDNode.CHILDBOTH, MDDNode.CHILDTHIS, MDDNode.CHILDOTHER
		});
	}

	public int ask(MDDNode first, MDDNode other, int type) {
		switch (type) {
			case MDDNode.LN:
				if (((MDDLeaf)first).value > 0) {
					return MDDNode.THIS;
				}
				return MDDNode.OTHER;
			case MDDNode.NL:
				if (((MDDLeaf)other).value > 0) {
					return MDDNode.OTHER;
				}
				return MDDNode.THIS;
		}
		System.out.println("DEBUG: ask should not come here!");
		return MDDNode.ASKME;
	}
}
