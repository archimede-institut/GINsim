package fr.univmrs.tagc.common.mdd;

import fr.univmrs.tagc.common.mdd.DecisionDiagramInfo;


public class TestHashMDD extends TestMDD {
	public TestHashMDD() {
		ddi = DecisionDiagramInfo.getHashDDI(maxlevel);
	}
}
