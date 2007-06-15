package fr.univmrs.tagc.mdd;


public class TestBalancedMDD extends TestMDD {
	public TestBalancedMDD() {
		ddi = DecisionDiagramInfo.getBalancedDDI(maxlevel);
	}
}
