package fr.univmrs.ibdm.GINsim.modelChecker;

public class GsModelCheckerTestResult {
	protected int expected;
	protected int result;
	protected String output;
	
	public String toString() {
		if (result == -1) {
			return "error";
		}
		if (expected == result) {
			return GsModelCheckerPlugin.v_values.get(expected).toString();
		}
		return GsModelCheckerPlugin.v_values.get(expected)+" -> "+GsModelCheckerPlugin.v_values.get(result);
	}
}
