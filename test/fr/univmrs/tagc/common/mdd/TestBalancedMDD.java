package fr.univmrs.tagc.common.mdd;

import fr.univmrs.tagc.common.mdd.DecisionDiagramInfo;


public class TestBalancedMDD extends TestMDD {
	public TestBalancedMDD() {
		ddi = DecisionDiagramInfo.getBalancedDDI(maxlevel);
	}
}
