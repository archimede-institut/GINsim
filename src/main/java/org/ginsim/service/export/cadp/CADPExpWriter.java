package org.ginsim.service.export.cadp;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

public class CADPExpWriter {

	private CADPExportConfig config = null;

	public CADPExpWriter(CADPExportConfig config) {
		this.config = config;
	}

	public String toString() {
		String out = "";
		List<String> lines = new ArrayList<String>();

		int regularProcesses = config.getTopology().getNumberInstances();
		int mappedInputs = config.getMapping().getMappedInputs().size();
		int integrationProcesses = mappedInputs * regularProcesses;
		int totalProcesses = regularProcesses + integrationProcesses;

		Collection<RegulatoryNode> visibleList = config.getListVisible();
		List<String> visibleGates = new ArrayList<String>();

		for (RegulatoryNode node : visibleList)
			for (int i = 0; i < regularProcesses; i++)
				visibleGates.add(node.getNodeInfo().getNodeID().toUpperCase()
						+ "_" + i);

		visibleGates.add("STABLE");

		out += "hide all but ";
		int index = 0;
		for (String visibleGate : visibleGates) {
			if (index++ > 0)
				out += ", ";
			out += visibleGate;
		}

		out += " in \n\tlabel par in\n";

		String stableLine[] = getNewLine(totalProcesses);
		for (int i = 0; i < regularProcesses; i++)
			stableLine[i] = "STABLE";
		stableLine[stableLine.length - 1] = "STABLE";

		lines.add(syncVec(stableLine));

		int p = 0;
		Map<Map.Entry<RegulatoryNode, Integer>, Integer> orderMapped = new HashMap<Map.Entry<RegulatoryNode, Integer>, Integer>();
		for (RegulatoryNode input : config.getMapping().getMappedInputs())
			for (int i = 0; i < config.getTopology().getNumberInstances(); i++)
				orderMapped.put(
						new AbstractMap.SimpleEntry<RegulatoryNode, Integer>(
								input, new Integer(i)), new Integer(
								regularProcesses + p++));

		for (RegulatoryNode node : config.getGraph().getNodeOrder()) {

			if (!node.isInput()
					&& !config.getMapping().getInfluencedInputs(node).isEmpty()) {

				List<Map.Entry<RegulatoryNode, Integer>> influences = new ArrayList<Map.Entry<RegulatoryNode, Integer>>();

				for (int i = 0; i < config.getTopology().getNumberInstances(); i++) {

					for (int j = 0; j < config.getTopology()
							.getNumberInstances(); j++) {
						if (!config.getTopology().areNeighbours(i, j))
							continue;
						for (RegulatoryNode input : config.getMapping()
								.getInfluencedInputs(node))
							influences
									.add(new AbstractMap.SimpleEntry<RegulatoryNode, Integer>(
											input, new Integer(j)));

					}

					for (int v = 0; v <= node.getMaxValue(); v++) {

						List<String[]> mlines = new ArrayList<String[]>();
						String line[] = getNewLine(totalProcesses);
						line[line.length - 1] = node2GateWithOffer(node, i, v);

						mlines.add(line);
						for (String[] localLine : multiPlex(
								mlines,
								new AbstractMap.SimpleEntry<RegulatoryNode, Integer>(
										node, new Integer(i)), v, influences,
								orderMapped))
							lines.add(syncVec(localLine));

					}

				}
			}

		}

		index = 0;
		for (String line : lines) {
			if (index++ > 0)
				out += ",\n";
			out += line;
		}

		out += "\n";

		out += "\tin\n";

		List<String> processNames = new ArrayList<String>();
		for (int i = 0; i < regularProcesses; i++)
			processNames.add(config.getModelName() + "_" + i + ".bcg");

		for (Map.Entry<RegulatoryNode, Integer> entry : orderMapped.keySet()) {
			processNames.add(orderMapped.get(entry).intValue(), "integration"
					+ "_"
					+ entry.getKey().getNodeInfo().getNodeID().toUpperCase()
					+ "_" + entry.getValue().intValue() + ".bcg");
		}

		index = 0;
		for (String processName : processNames) {
			if (index++ > 0)
				out += " || ";
			out += "\"" + processName + "\"";
		}

		out += "\tend par\nend hide\n\n";

		return out;
	}

	private String[] getNewLine(int size) {
		String line[] = new String[size + 1];

		for (int i = 0; i < line.length; i++)
			line[i] = "_";

		return line;
	}

	private String[] cloneLine(String[] line) {
		String newLine[] = new String[line.length];
		for (int i = 0; i < line.length; i++)
			newLine[i] = line[i];

		return newLine;
	}

	private String syncVec(String[] line) {
		String out = "";
		for (int index = 0; index < line.length; index++) {
			if (index > 0 && index < line.length - 1)
				out += " * ";
			if (index == line.length - 1)
				out += " -> ";
			out += line[index];
		}

		return out;
	}

	private String node2GateWithOffer(RegulatoryNode node, int index, int value) {
		return node.getNodeInfo().getNodeID().toUpperCase() + "_" + index
				+ " !" + value;
	}

	private String integrationWithOffer(RegulatoryNode input,
			RegulatoryNode proper, int indexInput, int indexProper,
			int valueProper, int valueIntegration) {
		return "I_" + input.getNodeInfo().getNodeID().toUpperCase() + "_"
				+ indexInput + "_"
				+ proper.getNodeInfo().getNodeID().toUpperCase() + "_"
				+ indexProper + " !" + valueProper + " !" + valueIntegration;
	}

	private String integrationWithOffer(RegulatoryNode input,
			RegulatoryNode proper, int indexInput, int indexProper,
			int valueProper) {
		return "I_" + input.getNodeInfo().getNodeID().toUpperCase() + "_"
				+ indexInput + "_"
				+ proper.getNodeInfo().getNodeID().toUpperCase() + "_"
				+ indexProper + " !" + valueProper;

	}

	private List<String[]> multiPlex(List<String[]> lines,
			Map.Entry<RegulatoryNode, Integer> mainGate, int value,
			List<Map.Entry<RegulatoryNode, Integer>> influences,
			Map<Map.Entry<RegulatoryNode, Integer>, Integer> orderMapped) {

		List<String[]> multiLines = new ArrayList<String[]>();

		if (influences.isEmpty())
			return lines;

		for (String[] line : lines) {
			Map.Entry<RegulatoryNode, Integer> entry = influences.remove(0);
			RegulatoryNode currentInfluence = entry.getKey();
			int currentTargetModule = entry.getValue().intValue();

			// no change in the integration function
			{
				String[] newLine = cloneLine(line);
				newLine[orderMapped.get(entry).intValue()] = integrationWithOffer(
						mainGate.getKey(), currentInfluence, mainGate
								.getValue().intValue(), currentTargetModule,
						value);
				multiLines.add(newLine);
			}

			// change in the value of the integration function
			for (int v = 0; v <= currentInfluence.getMaxValue(); v++) {
				String[] newLine = cloneLine(line);
				newLine[orderMapped.get(entry).intValue()] = integrationWithOffer(
						mainGate.getKey(), currentInfluence, mainGate
								.getValue().intValue(), currentTargetModule,
						value, v);
				multiLines.add(newLine);
			}

		}

		return multiPlex(multiLines, mainGate, value, influences, orderMapped);

	}
}
