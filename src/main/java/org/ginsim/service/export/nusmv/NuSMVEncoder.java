package org.ginsim.service.export.nusmv;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.tool.fixpoints.FixpointSearcher;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.common.application.GsException;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClass;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.stablestates.StableStatesService;

/**
 * Exports a GINsim Regulatory graph into a NuSMV model description.
 * 
 * @author Pedro T. Monteiro
 */
public class NuSMVEncoder {

	private static String[] nusmvReserved = { "MODULE", "DEFINE", "MDEFINE",
			"CONSTANTS", "VAR", "IVAR", "FROZENVAR", "INIT", "TRANS", "INVAR",
			"SPEC", "CTLSPEC", "LTLSPEC", "PSLSPEC", "COMPUTE", "NAME",
			"INVARSPEC", "FAIRNESS", "JUSTICE", "COMPASSION", "ISA", "ASSIGN",
			"CONSTRAINT", "SIMPWFF", "CTLWFF", "LTLWFF", "PSLWFF", "COMPWFF",
			"IN", "MIN", "MAX", "MIRROR", "PRED", "PREDICATES", "process",
			"array", "of", "boolean", "integer", "real", "word", "word1",
			"bool", "signed", "unsigned", "extend", "resize", "sizeof",
			"uwconst", "swconst", "EX", "AX", "EF", "AF", "EG", "AG", "E", "F",
			"O", "G", "H", "X", "Y", "Z", "A", "U", "S", "V", "T", "BU", "EBF",
			"ABF", "EBG", "ABG", "case", "esac", "mod", "next", "init",
			"union", "in", "xor", "xnor", "self", "TRUE", "FALSE", "count" };

	private String avoidNuSMVNames(String keyword) {
		if (keyword == null) {
			return keyword;
		}
		if (keyword.length() == 1) {
			return "_" + keyword;
		}

		for (String reserved : nusmvReserved) {
			if (keyword.compareToIgnoreCase(reserved) == 0) {
				return "_" + keyword;
			}
		}
		return keyword;
	}

	/**
	 * Export the graph to a NUSMV model description
	 * 
	 * @param config
	 *            the user defined parameters with the model
	 * @param out
	 *            the writer receiving the encoded model description
	 */
	public void write(NuSMVConfig config, Writer out) throws IOException,
			GsException {

		DateFormat dateformat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG);
		out.write("-- " + dateformat.format(new Date()) + "\n");
		out.write("--\n");
		out.write("-- NuSMV implicit representation of a logical model exported by GINsim\n");
		out.write("--\n");
		out.write("-- Requires NuSMV v2.1+ for CTL properties\n");
		out.write("--\n");
		out.write("-- Requires NuSMV-ARCTL for ARCTL properties\n");
		out.write("-- http://lvl.info.ucl.ac.be/Tools/NuSMV-ARCTL-TLACE\n");

		out.write("\nMODULE main\n");

		LogicalModel model = config.getModel();
		List<NodeInfo> coreNodes = model.getComponents();
		List<NodeInfo> outputNodes = model.getExtraComponents();
		if (coreNodes.isEmpty() && outputNodes.isEmpty()) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"NuSMV does not support empty graphs");
		}
		if (!hasCoreNodes(coreNodes)) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"NuSMV needs at least one core (non-input/non-output) node");
		}

		NodeInfo[] aNodeOrder = new NodeInfo[coreNodes.size()];
		boolean hasInputVars = false;
		for (int i = 0; i < aNodeOrder.length; i++) {
			NodeInfo node = model.getComponents().get(i);
			aNodeOrder[i] = node;
			if (node.isInput())
				hasInputVars = true;
		}

		// TODO: correct PCs
		// when a subset has the same rank distinct from the rest

		String sTmp;
		int[][] iaTmp = null;
		// classNum -> className
		TreeMap<Integer, String> tmPcNum2Name = new TreeMap<Integer, String>();
		// classNum -> RankNum
		TreeMap<Integer, Integer> tmPcNum2Rank = new TreeMap<Integer, Integer>();
		// rankNum -> rankName
		TreeMap<Integer, String> tmPcRank2Name = new TreeMap<Integer, String>();
		// varNum -> classNum
		TreeMap<Integer, Integer[]> tmVarNum2PcNum = new TreeMap<Integer, Integer[]>();
		// varNum-> subClassName
		TreeMap<Integer, String[]> tmVarNum2SubPcName = new TreeMap<Integer, String[]>();

		out.write("\nIVAR\n-- Simulation mode declaration --\n");
		switch (config.getUpdatePolicy()) {
		case NuSMVConfig.CFG_SYNC:
			out.write("-- Synchronous\n  PCs : { PC_c1 };\n  PC_c1_vars : { ");
			sTmp = "PC_c1";
			tmPcNum2Name.put(1, sTmp);
			// every variable -> 1 class
			for (int i = 0; i < coreNodes.size(); i++) {
				sTmp += "_" + avoidNuSMVNames(coreNodes.get(i).getNodeID());
				tmVarNum2PcNum.put(i, new Integer[] { 0, 1, 0 });
			}
			for (int i = 0; i < coreNodes.size(); i++)
				tmVarNum2SubPcName.put(i, new String[] { null, sTmp, null });
			out.write(sTmp + " };\n");
			break;

		case NuSMVConfig.CFG_PCLASS:
			PrioritySetDefinition priorities = (PrioritySetDefinition)config.getUpdatingMode();
			out.write("-- Priority classes\n  PCs : { ");
			for (int i = 0; i < priorities.size(); i++) {
				PriorityClass pc = (PriorityClass) priorities.get(i);
				if (i > 0)
					out.write(", ");
				sTmp = "PC_" + pc.getName().replaceAll("\\s+", "");
				out.write(sTmp);
				tmPcNum2Name.put(i + 1, sTmp);
			}
			out.write(" };\n");

			iaTmp = priorities.getPclass(model.getComponents());
			for (int i = 0; i < iaTmp.length; i++) {
				sTmp = tmPcNum2Name.get(i + 1);
				out.write("  " + sTmp + "_vars : { ");
				tmPcNum2Rank.put(i + 1, iaTmp[i][0]);

				switch (iaTmp[i][1]) {
				case 0: // Synchronous
					for (int j = 2; j < iaTmp[i].length; j += 2) {
						int f = iaTmp[i][j];
						String s = aNodeOrder[f].getNodeID();
						sTmp += "_" + avoidNuSMVNames(s);
						Integer[] aiSplits = (tmVarNum2PcNum
								.containsKey(iaTmp[i][j])) ? tmVarNum2PcNum
								.get(iaTmp[i][j]) : new Integer[] { 0, 0, 0 };
						aiSplits[iaTmp[i][j + 1] + 1] = i + 1;
						tmVarNum2PcNum.put(iaTmp[i][j], aiSplits);
						if (iaTmp[i][j + 1] != 0)
							sTmp += (iaTmp[i][j + 1] == 1) ? "Plus" : "Minus";
					}
					out.write(sTmp);
					for (int j = 2; j < iaTmp[i].length; j += 2) {
						String[] saTmp = (tmVarNum2SubPcName
								.containsKey(iaTmp[i][j])) ? tmVarNum2SubPcName
								.get(iaTmp[i][j]) : new String[] { null, null,
								null };
						saTmp[iaTmp[i][j + 1] + 1] = sTmp;
						tmVarNum2SubPcName.put(iaTmp[i][j], saTmp);
					}
					break;

				default: // Asynchronous
					for (int j = 2; j < iaTmp[i].length; j += 2) {
						if (j > 2)
							out.write(", ");
						String sub = sTmp
								+ "_"
								+ avoidNuSMVNames(aNodeOrder[iaTmp[i][j]]
										.getNodeID());
						if (iaTmp[i][j + 1] != 0)
							sub += (iaTmp[i][j + 1] == 1) ? "Plus" : "Minus";
						out.write(sub);
						String[] saTmp = (tmVarNum2SubPcName
								.containsKey(iaTmp[i][j])) ? tmVarNum2SubPcName
								.get(iaTmp[i][j]) : new String[] { null, null,
								null };
						saTmp[iaTmp[i][j + 1] + 1] = sub;
						tmVarNum2SubPcName.put(iaTmp[i][j], saTmp);
						Integer[] aiSplits = (tmVarNum2PcNum
								.containsKey(iaTmp[i][j])) ? tmVarNum2PcNum
								.get(iaTmp[i][j]) : new Integer[] { 0, 0, 0 };
						aiSplits[iaTmp[i][j + 1] + 1] = i + 1;
						tmVarNum2PcNum.put(iaTmp[i][j], aiSplits);
					}
					break;
				}
				out.write(" };\n");
			}
			break;

		default:
			GUIMessageUtils.openWarningDialog("Unsupported updating mode. Considering the Asynchronous one.");
		case NuSMVConfig.CFG_ASYNC:
			out.write("-- Asynchronous\n  PCs : { ");
			boolean bFirst = true;
			for (int i = 0; i < coreNodes.size(); i++) {
				if (coreNodes.get(i).isInput())
					continue;
				if (!bFirst)
					out.write(", ");
				else
					bFirst = false;
				sTmp = "PC_c" + (i + 1);
				out.write(sTmp);
				tmVarNum2PcNum.put(i, new Integer[] { 0, i + 1, 0 });
				tmPcNum2Name.put(i + 1, sTmp);
			}
			out.write(" };\n");
			for (int i = 0; i < coreNodes.size(); i++) {
				if (coreNodes.get(i).isInput())
					continue;
				sTmp = "PC_c" + (i + 1) + "_"
						+ avoidNuSMVNames(coreNodes.get(i).getNodeID());
				out.write("  PC_c" + (i + 1) + "_vars : { " + sTmp + " };\n");
				tmVarNum2SubPcName.put(i, new String[] { null, sTmp, null });
			}
			break;
		}

		if (hasInputVars) {
			out.write("\n-- Input variables declaration\n");
			for (int i = 0; i < coreNodes.size(); i++) {
				if (coreNodes.get(i).isInput()
						&& !config.hasFixedInput(coreNodes.get(i).getNodeID())) {
					String s_levels = "0";
					for (int j = 1; j <= coreNodes.get(i).getMax(); j++)
						s_levels += ", " + j;
					out.write("  "
							+ avoidNuSMVNames(coreNodes.get(i).getNodeID())
							+ " : { " + s_levels + "};\n");
				}
			}
		}
		
		out.write("\nVAR\n");
		for (int i = 0; i < coreNodes.size(); i++) {
			if (!coreNodes.get(i).isInput()
					|| !config.hasFixedInput(coreNodes.get(i).getNodeID())) {
				continue;
			}
			String s_levels = "0";
			for (int j = 1; j <= coreNodes.get(i).getMax(); j++)
				s_levels += ", " + j;
			out.write("  " + avoidNuSMVNames(coreNodes.get(i).getNodeID())
					+ " : { " + s_levels + "};\n");
		}

		// PCrank depends on the state variables
		// Should therefore be declared after
		// But after some tests
		if (config.getUpdatePolicy() == NuSMVConfig.CFG_PCLASS && iaTmp != null
				&& iaTmp.length > 1) {
			out.write("\n-- Priority definition\n");
			out.write("  PCrank : { ");
			int iLast = 0;
			sTmp = "";
			boolean bWrote = false;
			for (int c = 0; c < iaTmp.length; c++) {
				if (c == 0 || iaTmp[c][0] > iLast) {
					iLast = iaTmp[c][0];
					sTmp = "rank" + iLast;
				}
				sTmp += "_" + tmPcNum2Name.get(c + 1);
				if (c + 1 == iaTmp.length || iaTmp[c + 1][0] > iLast) {
					if (bWrote)
						out.write(", ");
					bWrote = true;
					out.write(sTmp);
					tmPcRank2Name.put(iLast, sTmp);
				}
			}
			out.write(" };\n");
		}

		// Topological sorting of the state variables
		NodeInfoSorter nis = new NodeInfoSorter();
		List<NodeInfo> nodeOrderDecl = nis.getNodesByIncNumberRegulators(model);

		out.write("\n-- State variables declaration\n");
		for (int i = 0; i < coreNodes.size(); i++) {
			if (nodeOrderDecl.get(i).isInput())
				continue;
			String s_levels = "0";

			for (int j = 1; j <= nodeOrderDecl.get(i).getMax(); j++)
				s_levels += ", " + j;

			out.write("  " + avoidNuSMVNames(nodeOrderDecl.get(i).getNodeID())
					+ " : {" + s_levels + "};\n");
		}

		out.write("\nASSIGN");
		if (config.getUpdatePolicy() == NuSMVConfig.CFG_PCLASS && iaTmp != null
				&& iaTmp.length > 1) {
			if (tmPcRank2Name.size() > 1) {
				out.write("\n-- Establishing priorities\n");
				out.write("  PCrank :=\n    case\n");
				for (int c = 0; c < iaTmp.length; c++) {
					sTmp = "";
					if (c + 1 == iaTmp.length) {
						sTmp += "TRUE";
					} else {
						for (int v = 2; v < iaTmp[c].length; v += 2) {
							if (sTmp.length() > 0)
								sTmp += " | ";
							switch (iaTmp[c][v + 1]) {
							case 1:
								sTmp += avoidNuSMVNames(aNodeOrder[iaTmp[c][v]]
										.getNodeID()) + "_inc";
								break;
							case -1:
								sTmp += avoidNuSMVNames(aNodeOrder[iaTmp[c][v]]
										.getNodeID()) + "_dec";
								break;
							default:
								sTmp += "!"
										+ avoidNuSMVNames(aNodeOrder[iaTmp[c][v]]
												.getNodeID()) + "_std";
							}
						}
						if (sTmp.length() == 0)
							sTmp = "FALSE";
					}
					out.write("      " + sTmp + " : "
							+ tmPcRank2Name.get(tmPcNum2Rank.get(c + 1))
							+ ";\n");
				}
				out.write("    esac;\n");
			}
		}
		// TODO: Solve major problem when using PCs with Input variables
		// A proper component focal function may depend on Input variables
		// When that component is used in the PCRank NuSMV says
		// that there is a dependency on Input variables :/

		out.write("\n-- Variable update if conditions are met\n");
		for (int v = 0; v < coreNodes.size(); v++) {
			String nodeID = avoidNuSMVNames(coreNodes.get(v).getNodeID());
			if (coreNodes.get(v).isInput()) {
				if (config.hasFixedInput(nodeID)) {
					// Writes the rule for a fixed input
					out.write("next(" + nodeID + ") := \n");
					out.write("  case\n");
					out.write("    TRUE: " + nodeID + ";\n");
					out.write("  esac;\n");
				}
				continue;
			}

			// The real next(Variable) if conditions are satisfied
			out.write("next(" + nodeID + ") := \n");
			out.write("  case\n");

			// Class entry conditions
			Integer[] aiSplits = tmVarNum2PcNum.get(v);
			out.write("    update_" + nodeID);
			if (aiSplits[2] > 0)
				out.write("Plus");
			out.write("_OK & (" + nodeID + "_inc) : ");
			out.write(((coreNodes.get(v).getMax() > 1) ? nodeID + " + 1" : "1")
					+ ";\n");
			out.write("    update_" + nodeID);
			if (aiSplits[0] > 0)
				out.write("Minus");
			out.write("_OK & (" + nodeID + "_dec) : ");
			out.write(((coreNodes.get(v).getMax() > 1) ? nodeID + " - 1" : "0")
					+ ";\n");
			out.write("    TRUE : " + nodeID + ";\n");
			out.write("  esac;\n");
		}

		out.write("\nDEFINE\n");

		// Nodes actual logical rules
		out.write("-- Variable next level regulation\n");
		int[] kMDDs = model.getLogicalFunctions();
		for (int i = 0; i < coreNodes.size(); i++) {
			NodeInfo node = coreNodes.get(i);
			if (node.isInput())
				continue;
			out.write(avoidNuSMVNames(node.getNodeID()) + "_focal :=\n");
			out.write("  case\n");
			nodeRules2NuSMV(out, model, kMDDs[i], coreNodes, node);
			out.write("  esac;\n");
		}
		out.write("\n");

		for (int v = 0; v < coreNodes.size(); v++) {
			if (coreNodes.get(v).isInput())
				continue;
			out.write(avoidNuSMVNames(coreNodes.get(v).getNodeID())
					+ "_inc := ");
			out.write(avoidNuSMVNames(coreNodes.get(v).getNodeID())
					+ "_focal > ");
			out.write(avoidNuSMVNames(coreNodes.get(v).getNodeID()) + ";\n");
			out.write(avoidNuSMVNames(coreNodes.get(v).getNodeID())
					+ "_dec := ");
			out.write(avoidNuSMVNames(coreNodes.get(v).getNodeID())
					+ "_focal < ");
			out.write(avoidNuSMVNames(coreNodes.get(v).getNodeID()) + ";\n");
			out.write(avoidNuSMVNames(coreNodes.get(v).getNodeID())
					+ "_std := ");
			out.write(avoidNuSMVNames(coreNodes.get(v).getNodeID())
					+ "_focal = ");
			out.write(avoidNuSMVNames(coreNodes.get(v).getNodeID()) + ";\n\n");
		}

		for (int v = 0; v < coreNodes.size(); v++) {
			if (coreNodes.get(v).isInput())
				continue;
			Integer[] aiSplits = tmVarNum2PcNum.get(v);
			String[] saSubName = tmVarNum2SubPcName.get(v);
			boolean bPlus = (aiSplits[2] > 0);
			int pc = (bPlus) ? aiSplits[2] : aiSplits[1];
			String sub = (bPlus) ? saSubName[2] : saSubName[1];
			out.write("update_" + avoidNuSMVNames(coreNodes.get(v).getNodeID())
					+ ((bPlus) ? "Plus" : "") + "_OK := (PCs = ");
			out.write(tmPcNum2Name.get(pc) + ") & (");
			out.write(tmPcNum2Name.get(pc) + "_vars = ");
			out.write(sub);
			if (config.getUpdatePolicy() == NuSMVConfig.CFG_PCLASS) {
				out.write(") & (PCrank = ");
				out.write((String) tmPcRank2Name.get(tmPcNum2Rank.get(pc)));
			}
			out.write(");\n");

			if (bPlus) { // There's also a Minus transition
				pc = aiSplits[0];
				out.write("update_" + coreNodes.get(v).getNodeID()
						+ "Minus_OK := (PCs = ");
				out.write(tmPcNum2Name.get(pc) + ") & (");
				out.write(tmPcNum2Name.get(pc) + "_vars = ");
				out.write(saSubName[0]);
				if (config.getUpdatePolicy() == NuSMVConfig.CFG_PCLASS) {
					out.write(") & (PCrank = ");
					out.write((String) tmPcRank2Name.get(tmPcNum2Rank.get(pc)));
				}
				out.write(");\n");
			}
		}

		out.write("\n-- DISCLAIMER: There are no INput nor OUTput variables ");
		out.write("in the weak/strong stable states description\n");
		out.write("stableStates := weakSS | strongSS;\n\n");
		if (config.exportStableStates()) {
			out.write("-- Weak stable states differing only on input variables ");
			out.write("will not be distinguished !!");
			out.write(writeStableStates(model));
		} else {
			out.write("-- ATTENTION: This export does not include the stable states enumeration");
			out.write("\nweakSS := FALSE;");
			out.write("\nstrongSS := FALSE;\n");
		}

		out.write("\n");
		out.write("-- Declaration of output variables\n");
		if (outputNodes.size() > 0) {
			kMDDs = model.getExtraLogicalFunctions();
			for (int i = 0; i < outputNodes.size(); i++) {
				NodeInfo node = outputNodes.get(i);
				out.write(avoidNuSMVNames(node.getNodeID()) + " :=\n");
				out.write("  case\n");
				nodeRules2NuSMV(out, model, kMDDs[i], coreNodes, node);
				out.write("  esac;\n");
			}
		} else {
			out.write("-- Empty !\n");
		}
		out.write("\n");

		out.write("-- Authorized NuSMV transitions");
		out.write("\nTRANS\n");
		for (int i = 0; i < coreNodes.size(); i++) {
			if (coreNodes.get(i).isInput()){
//					&& !config.hasFixedInput(coreNodes.get(i).getNodeID())) {
				continue;
			}
			out.write("next(" + avoidNuSMVNames(coreNodes.get(i).getNodeID())
					+ ") != ");
			out.write(avoidNuSMVNames(coreNodes.get(i).getNodeID()) + " |\n");
		}
		// out.write("stableStates;\n");
		out.write("(");
		sTmp = "";
		for (int i = 0; i < coreNodes.size(); i++) {
			if (coreNodes.get(i).isInput()) {
				continue;
			}
			if (!sTmp.isEmpty())
				sTmp += " & ";
			sTmp += avoidNuSMVNames(coreNodes.get(i).getNodeID()) + "_std";
		}
		out.write(sTmp + "); -- or it is a steady state\n");


		// Initial States Macro definition
		out.write("\nDEFINE\n");
		out.write("-- Declaration of core variables restriction list\n");
		out.write(writeStateList(aNodeOrder, config.getInitialState().keySet()
				.iterator()));
		out.write("\n");
		out.write("-- Declaration of input variables restriction list\n");
		out.write(writeStateList(aNodeOrder, config.getInputState().keySet()
				.iterator()));

		out.write("\n");
		out.write("--------------------------------------------------\n");
		out.write("-- Reachability Properties using VARYING INPUTS --\n");
		out.write("-- i.e. there is NO CONTROL on the input change at each transition\n");
		out.write("--\n");
		out.write("-- EXAMPLES --\n");
		out.write("-- 1. Between an initial state (pattern) and a stable state (pattern)\n");
		out.write("--   a. Existence of at least one path connecting two state patterns\n");
		out.write("-- INIT initState;\n");
		out.write("-- SPEC EF ( stableState );\n");
		out.write("--   b. Existence of all the paths connecting two state patterns\n");
		out.write("-- INIT initState;\n");
		out.write("-- SPEC AF ( stableState );\n");
		out.write("--\n");
		out.write("-- 2. Between all the weak/strong stable states\n");
		out.write("-- INIT weakSS1;\n");
		out.write("--  SPEC EF ( weakSS2 );\n");
		out.write("--  ...\n");
		out.write("--  SPEC EF ( weakSSn );\n");
		out.write("--");
		out.write("------------------------------------------------\n");
		out.write("-- Reachability Properties using FIXED INPUTS --\n");
		out.write("-- i.e. a VALUE RESTRICTION can be forced at each transition\n");
		out.write("-- \n");
		out.write("-- 1. Between an initial state (pattern) and a stable state (pattern)\n");
		out.write("--   a. Existence of at least one path connecting two state patterns\n");
		out.write("-- INIT initState; SPEC EAF ( inpVar1=0 & inpVar3=1 )( stableState );\n");
		out.write("--   b. Existence of all the paths\n");
		out.write("-- INIT initState; SPEC AAF ( inpVar1=0 & inpVar3=1 )( stableState );\n");
		out.write("--\n");
		out.write("-- 2. Testing input combinations\n");
		out.write("-- INIT weakSS1;\n");
		out.write("--  SPEC EAF ( inpVar1=0 & inpVar2=0 )( weakSS2 );\n");
		out.write("--  SPEC EAF ( inpVar1=0 & inpVar2=1 )( weakSS2 );\n");
		out.write("--  SPEC EAF ( inpVar1=1 & inpVar2=0 )( weakSS2 );\n");
		out.write("--  SPEC EAF ( inpVar1=1 & inpVar2=1 )( weakSS2 );\n");
		out.write("--  ...\n");

	}

	private void nodeRules2NuSMV(Writer out, LogicalModel model, int nodeMDD,
			List<NodeInfo> coreNodeOrder, NodeInfo node) throws IOException {
		PathSearcher searcher = new PathSearcher(model.getMDDManager(), 1,
				node.getMax());
		int[] path = searcher.getPath();
		searcher.setNode(nodeMDD);

		int leafValue = 0;
		String s = "";
		for (int l : searcher) {
			boolean bWrite = false;
			for (int i = 0; i < path.length; i++) {
				if (path[i] != -1) {
					if (!bWrite)
						s += "    ";
					if (bWrite)
						s += " & ";
					s += "("
							+ avoidNuSMVNames(coreNodeOrder.get(i).getNodeID())
							+ " = " + path[i] + ")";
					bWrite = true;
				}
			}
			if (!s.isEmpty()) {
				s += " : " + l + ";\n";
			} else {
				leafValue = l;
			}
		}
		out.write(s);
		out.write("    TRUE : " + leafValue + ";\n");
	}

	private String writeStateList(NodeInfo[] t_vertex, Iterator<NamedState> iter) {
		StringBuffer sb = new StringBuffer();
		if (!iter.hasNext())
			sb.append("-- Empty !\n");
		else {
			while (iter.hasNext()) {
				NamedState iState = iter.next();
				Map<NodeInfo, List<Integer>> m_states = iState.getMap();
				String s_init = "";

				for (int i = 0; i < t_vertex.length; i++) {
					List<Integer> v = m_states.get(t_vertex[i]);
					if (v != null && v.size() > 0) {
						if (!s_init.isEmpty())
							s_init += " & ";
						s_init += "(";
						for (int j = 0; j < v.size(); j++) {
							if (j > 0)
								s_init += " | ";
							s_init += avoidNuSMVNames(t_vertex[i].getNodeID())
									+ "=" + v.get(j);
						}
						s_init += ")";
					}
				}
				sb.append(avoidNuSMVNames(iState.getName())).append(" := ")
						.append(s_init).append(";\n");
			}
		}
		return sb.toString();
	}

	private String writeStableStates(LogicalModel model) {
		NodeInfoSorter nis = new NodeInfoSorter();
		List<NodeInfo> newNodeOrder = nis.getNodesWithInputsAtEnd(model);
		int inputs = 0;
		for (int i = newNodeOrder.size() - 1; i > 0; i--) {
			if (newNodeOrder.get(i).isInput()) {
				inputs++;
			} else
				break;
		}
		String sRet = "";
		try {
			FixpointSearcher sss = GSServiceManager.getService(StableStatesService.class)
					.getStableStateSearcher(model);
			int omdds = sss.call();
			MDDManager ddmanager = sss.getMDDManager().getManager(newNodeOrder);
			PathSearcher psearcher = new PathSearcher(ddmanager, 1);
			psearcher.setNode(omdds);

			sRet = writeSSs(psearcher, newNodeOrder, newNodeOrder.size()
					- inputs);
			sRet += ";\n";
		} catch (Exception e) {
			sRet = "\nweakSS := FALSE;\nstrongSS := FALSE;";
			sRet += "\n-- An error occurred when computing the stable states!!";
			sRet += "\n-- This SMV description may no longer be valid!!\n";
		}
		return sRet;
	}

	private String writeSSs(PathSearcher paths, List<NodeInfo> nodeOrder,
			int stateNodesSize) {
		Set<String> sWeak = new HashSet<String>();
		Set<String> sStrong = new HashSet<String>();
		boolean bWeak = false;

		int[] iaSSPath = paths.getPath();
		for (@SuppressWarnings("unused")
		int v : paths) {
			String sSSdesc = "";

			for (int i = 0; i < nodeOrder.size(); i++) {
				// if (nodeOrder.get(i).isOutput())
				// continue;
				if (iaSSPath[i] < 0) {
					bWeak = true;
					continue;
				} else if (i < stateNodesSize) {
					if (sSSdesc.length() > 0)
						sSSdesc += " & ";
					sSSdesc += avoidNuSMVNames(nodeOrder.get(i).getNodeID())
							+ "=" + iaSSPath[i];
				}
			}
			if (bWeak)
				sWeak.add(sSSdesc);
			else
				sStrong.add(sSSdesc);
		}

		String sRet = "\nweakSS := ";
		if (sWeak.size() == 0)
			sRet += "FALSE";
		else {
			for (int i = 0; i < sWeak.size(); i++) {
				if (i > 0)
					sRet += " | ";
				sRet += "weakSS" + (i + 1);
			}
			int i = 0;
			for (String ss : sWeak)
				sRet += ";\nweakSS" + (++i) + " := " + ss;
		}
		sRet += ";\n\n-- Strong stable states - for every valuation "
				+ "of input variables\nstrongSS := ";
		if (sStrong.size() == 0)
			sRet += "FALSE";
		else {
			for (int i = 0; i < sStrong.size(); i++) {
				if (i > 0)
					sRet += " | ";
				sRet += "strongSS" + (i + 1);
			}
			int i = 0;
			for (String ss : sStrong)
				sRet += ";\nstrongSS" + (++i) + " := " + ss;
		}
		return sRet;
	}

	private boolean hasCoreNodes(List<NodeInfo> nodes) {
		boolean hasCore = false;
		if (nodes == null)
			return hasCore;
		for (NodeInfo node : nodes) {
			if (!node.isInput()) {
				hasCore = true;
				break;
			}
		}
		return hasCore;
	}
}