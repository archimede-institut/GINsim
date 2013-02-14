package org.ginsim.service.export.cadp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * 
 * Class generating an MCL file for each global stable state
 * 
 * @author Nuno D. Mendes
 * 
 */

public class CADPMclWriter extends CADPWriter {

	private List<byte[]> globalStableState = null;

	public CADPMclWriter(CADPExportConfig config, List<byte[]> globalStableState) {
		super(config);
		this.globalStableState = globalStableState;
	}

	public String toString() {

		List<byte[]> globalInitialState = getInitialState();
		Collection<RegulatoryNode> listVisible = getListVisible();
		List<RegulatoryNode> allComponents = getAllComponents();

		List<String> booleanVars = new ArrayList<String>();
		List<String[]> declarations = new ArrayList<String[]>();
		List<String[]> conditions = new ArrayList<String[]>();

		for (int i = 1; i <= getNumberInstances(); i++) {
			for (RegulatoryNode visible : listVisible) {
				String booleanVar = "B"
						+ visible.getNodeInfo().getNodeID().toLowerCase() + i;

				booleanVars.add(booleanVar);
				String[] declaration = new String[3];

				declaration[0] = booleanVar;
				declaration[1] = ""
						+ globalStableState.get(i - 1)[allComponents
								.indexOf(visible)];
				declaration[2] = ""
						+ globalInitialState.get(i - 1)[allComponents
								.indexOf(visible)];

				declarations.add(declaration);

				String[] condition = new String[3];
				condition[0] = visible.getNodeInfo().getNodeID().toUpperCase()
						+ "_" + i;
				condition[1] = ""
						+ globalStableState.get(i - 1)[allComponents
								.indexOf(visible)];
				condition[2] = "" + i;

				conditions.add(condition);

			}
		}

		String initialization = "";

		for (String[] declaration : declarations) {
			if (!initialization.isEmpty())
				initialization += ",";

			initialization += declaration[0] + " : Bool := (" + declaration[1]
					+ " = " + declaration[2] + ")";
		}

		List<String[]> conditionLines = new ArrayList<String[]>();
		for (String[] condition : conditions) {
			List<String> vec = new ArrayList<String>();
			for (String booleanVar : booleanVars)
				vec.add(booleanVar);
			
			int position = Integer.parseInt(condition[2]) - 1;
			vec.set(position, "V=" + condition[1]);			
			
			String[] conditionLine = new String[2];
			conditionLine[0] = condition[0];
			conditionLine[1] = "(" + makeCommaList(vec) + ")";
		}

		String allConditions = "";
		for (String[] conditionLine : conditionLines) {
			if (!allConditions.isEmpty())
				allConditions += "\nor\n";
			allConditions += "(<{" + conditionLine[0] + " ? V:Nat}> X "
					+ conditionLine[1] + ")";
		}

		String finalCondition = "";
		for (String booleanVar : booleanVars) {
			if (!finalCondition.isEmpty())
				finalCondition += " and ";
			finalCondition += "(" + booleanVar + " = true)";
		}

		allConditions += "\nor\n((<" + getStableActionName() + ">true) and ("
				+ finalCondition + "))";

		String property = "mu X (" + initialization + ").\n(" + allConditions
				+ ")\n";
		;

		return property;

	}
}
