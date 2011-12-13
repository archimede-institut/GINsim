package org.ginsim.service.imports.truthtable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraphFactory;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameterList;

public final class TruthTableParser {

	// Truth table including all states and their images
	private byte baTruthTable[][];
	// Number of components
	private int iN;
	// Max value of all components
	private byte iMax[];

	public TruthTableParser(FileReader fr) throws GsException {
		Map<String, byte[]> tmLines = new TreeMap<String, byte[]>();

		try {
			BufferedReader br = new BufferedReader(fr);
			String line;
			do {
				line = br.readLine();
			} while (line != null && line.trim().isEmpty());
			String[] saLine = line.split("\\s+");
			iN = saLine[0].length();
			iMax = new byte[iN];

			do {
				if (!line.trim().isEmpty()) {
					byte[] baLine = new byte[2 * iN];
					saLine = line.split("\\s+");
					for (int i = 0; i < iN; i++) {
						baLine[i] = (byte) Character.getNumericValue(saLine[0]
								.charAt(i));
						if (iMax[i] < baLine[i])
							iMax[i] = baLine[i];
						baLine[i + iN] = (byte) Character
								.getNumericValue(saLine[1].charAt(i));
						if (iMax[i] < baLine[i + iN])
							iMax[i] = baLine[i + iN];
					}
					tmLines.put(line, baLine);
				}
			} while ((line = br.readLine()) != null);
		} catch (IOException e) {
			throw new GsException(GsException.GRAVITY_ERROR, e);
		}
		if (tmLines.size() == 0 || tmLines.size() < getTableLines()) {
			// components are at least boolean and there are lines missing
			throw new GsException(GsException.GRAVITY_ERROR,
					"TruthTableParser.error: not enough lines");
		}
		baTruthTable = new byte[tmLines.size()][];
		ArrayList<String> alTmp = new ArrayList<String>(tmLines.keySet());
		for (int i = 0; i < tmLines.size(); i++)
			baTruthTable[i] = tmLines.get(alTmp.get(i));
	}

	private int getTableLines() {
		if (iMax == null || iMax.length == 0)
			return 0;
		int size = 1;
		for (int i = 0; i < iMax.length; i++)
			size *= (iMax[i] + 1);
		return size;
	}

	public RegulatoryGraph buildGraph() {
		RegulatoryGraph graph = new RegulatoryGraphFactory().create();

		for (int i = 0; i < baTruthTable.length; i++) {
			for (int j = 0; j < baTruthTable[i].length; j++) {
				System.out.print(" " + baTruthTable[i][j]);
			}
			System.out.println();
		}
		
		// Add each RegNode with corresponding Max value
		RegulatoryNode[] naNodes = new RegulatoryNode[iN];
		for (int i = 0; i < iN; i++) {
			naNodes[i] = graph.addNewNode("g" + i, null, iMax[i]);
		}

		// Search the table to define, for each component, the size of each bloc
		// defined as the nb of lines for which its value remains cste
		int iBlock[] = new int[iN];
		for (int i = 0; i < iN; i++) {
			// Calculation of the size of blocks
			iBlock[i] = 1;
			// All the combinations of variation of components i+!...n
			for (int j = i + 1; j < iN; j++) {
				iBlock[i] = iBlock[i] * (iMax[j] + 1);
			}
		}
		// the number of occurrences of the blocks for each component
		int iOccur[] = new int[iN];
		for (int i = 0; i < iN; i++) {
			iOccur[i] = 1;
			for (int j = i - 1; j >= 0; j--) {
				iOccur[i] = iOccur[i] * (iMax[j] + 1);
			}
		}

		ArrayList<RegulatoryEdge> incoming_edges[] = new ArrayList[iN];
		ArrayList<Integer> table_interactors[] = new ArrayList[iN];
		byte sign;
		for (int i = 0; i < iN; i++) {
			// instantiate one list for each component
			// to store the incoming edges
			incoming_edges[i] = new ArrayList<RegulatoryEdge>();
			table_interactors[i] = new ArrayList<Integer>();
		}
		// for each component i, we want to determine
		// its influence to determine all the interactions
		for (int i = 0; i < iN; i++) {
			// for each occurrence of the series of blocks
			for (int j = 0; j < iOccur[i]; j++) {
				// On all the values of i
				for (int k = 0; k < iMax[i]; k++) {
					// the first line of the block
					int L1 = (k + j * (iMax[i] + 1)) * iBlock[i];
					// for all the lines of the block
					for (int l = L1; l < L1 + iBlock[i]; l++) {
						// scan the target values of each
						// component (second column of image states)
						for (int u = iN; u < 2 * iN; u++) {
							// sign defines the type of the interaction
							// (1 positive/-1 negative/ 0 unknown)
							sign = 0; 
System.out.println("i:"+i + " j:"+j+" k:"+k+" L1:"+L1+" l:"+l+" u:"+u
		+ " b:"+iBlock[i] + " m:" + iMax[i]);
							if (baTruthTable[l][u] > baTruthTable[l + iBlock[i]][u]) {
								sign = -1;
							} else if (baTruthTable[l][u] < baTruthTable[l
									+ iBlock[i]][u]) {
								sign = 1;
							}
							if (sign != -1) {
								RegulatoryMultiEdge edge = graph.getEdge(
										naNodes[i], naNodes[u - iN]);
								if (edge == null) {
									edge = graph.addEdge(naNodes[i], naNodes[u
											- iN], sign);
									incoming_edges[u - iN].add(edge.getEdge(0));
									edge.setMin(0, (byte) baTruthTable[l
											+ iBlock[i]][i]);
									table_interactors[u - iN].add(i);
								}
							}
						}
					}
				}
			}
		}

		// defining the logical parameters
		for (int i = 0; i < iN; i++) {
			int deg = incoming_edges[i].size();
			for (int j = 0; j < baTruthTable.length; j++) { 
				// search for the lines where the
				// target value of i > 0
				if (baTruthTable[j][i + iN] > 0) {
					List<RegulatoryEdge> activeInteractions = new ArrayList<RegulatoryEdge>();
					// search for the active interactions ie for the components,
					// sources of incoming edge, which value is not zero in
					// state of line j
					for (int k = 0; k < deg; k++) {
						// get the index of the k.th regulator of i
						int reg = ((Integer) table_interactors[i].get(k))
								.intValue(); 
						if (baTruthTable[j][reg] != 0) { // TODO >= threshold
							RegulatoryMultiEdge me = graph.getEdge(
									naNodes[reg], naNodes[i]);
							activeInteractions.add(me.getEdge(0));
						}
					}
					LogicalParameterList listOfParam = naNodes[i]
							.getV_logicalParameters();
					if (!listOfParam.contains(activeInteractions)) {
						// check if the parameter already exists
						// in the list, if not create it
//	System.out.println("i:"+i+" j:"+j + " <- " + baTruthTable[j][i+iN]);
						// TODO: replace value, not create a new LogicalParameter
						LogicalParameter newParam = new LogicalParameter(
								activeInteractions, baTruthTable[j][i+iN]);
						naNodes[i].addLogicalParameter(newParam, true);

					}
				}
			}
		}
		return graph;
	}
}
