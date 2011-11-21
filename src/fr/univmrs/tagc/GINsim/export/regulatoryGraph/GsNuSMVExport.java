package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.tools.reg2dyn.Reg2dynPriorityClass;
import org.ginsim.gui.service.tools.reg2dyn.PriorityClassDefinition;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.action.stablestates.StableStateSearcher;
import org.ginsim.service.action.stablestates.StableStatesService;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.common.gui.dialog.stackdialog.StackDialogHandler;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * GINsim export plugin capable of encoding the working model into a NuSMV
 * specification. It currently uses NuSMV version 2.5.3.
 * <p>
 * It considers the use of priority classes for every model, whichever the
 * updating policy defined inside GINsim. If a given model considers priority
 * classes, it maps the corresponding classes. However, if a given model
 * considers an asynchronous (synchronous) updating policy, it creates an
 * equivalent mapping using priority classes, one class for each (all the)
 * variable(s).
 * </p>
 * 
 * TODO: depends on StableStateService
 */
public class GsNuSMVExport extends ExportAction<RegulatoryGraph> {

	private static final GsFileFilter ffilter = new GsFileFilter(new String[] { "smv" }, "NuSMV files");
	
	private GsNuSMVConfig config;
	
	public GsNuSMVExport(RegulatoryGraph graph) {
		super( graph, "STR_NuSMVmodelChecker", "STR_NuSMVmodelChecker_descr");
	}

	@Override
	public void doExport( String filename) throws IOException {
		encode(graph, filename, config);
	}

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}

	@Override
	public StackDialogHandler getConfigPanel() {
		return new GsNuSMVExportConfigPanel(config);
	}

	/**
	 * Gets the set of values of a given @see {@link RegulatoryVertex}
	 * 
	 * @param vertex
	 *            The vertex containing the values to be written.
	 * @param m
	 *            The map containing the initial values of all the vertexes.
	 * @return A string of values in the NuSMV format.
	 */
	private static String writeInitialState(RegulatoryVertex[] t_vertex,
			boolean input,
			Map<RegulatoryVertex, List<Integer>> mInitStates) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < t_vertex.length; i++) {
			if (t_vertex[i].isInput() != input)
				continue;
			String s_init = "";
			List<Integer> v = mInitStates.get(t_vertex[i]);
			if (v != null && v.size() > 0) {
				for (int j = 0; j < v.size(); j++) {
					if (j > 0)
						s_init += " | ";
					s_init += t_vertex[i].getId() + "=" + v.get(j);
				}
			}
			if (s_init.isEmpty()) {
				sb.append("--  INIT ").append(t_vertex[i].getId())
						.append(" = 0;\n");
			} else {
				sb.append("  INIT ").append(s_init).append(";\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Main method that knows how to export a given graph @see
	 * {@link RegulatoryGraph} and write it to a specific file.
	 * 
	 * @param graph
	 *            The graph object to be exported.
	 * @param fileName
	 *            The file name to be written.
	 * @param config
	 *            store with the configuration specified by the user in the GUI.
	 */
	public static void encode(RegulatoryGraph graph, String fileName,
			GsNuSMVConfig config) throws IOException{

		DateFormat dateformat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG);
		String date = dateformat.format(new Date());
		FileWriter out = new FileWriter(fileName);
		Iterator<GsInitialState> it = config.getInitialState().keySet()
				.iterator();
		Map<RegulatoryVertex, List<Integer>> m_initstates;
		if (it.hasNext()) {
			m_initstates = it.next().getMap();
		} else {
			m_initstates = new HashMap<RegulatoryVertex, List<Integer>>();
		}
		if (m_initstates == null) {
			m_initstates = new HashMap<RegulatoryVertex, List<Integer>>();
		}
		it = config.getInputState().keySet().iterator();
		Map<RegulatoryVertex, List<Integer>> m_initinputs;
		if (it.hasNext()) {
			m_initinputs = it.next().getMap();
		} else {
			m_initinputs = new HashMap<RegulatoryVertex, List<Integer>>();
		}
		if (m_initinputs == null) {
			m_initinputs = new HashMap<RegulatoryVertex, List<Integer>>();
		}

		GsRegulatoryMutantDef mutant = (GsRegulatoryMutantDef) config.store
				.getObject(0);
		List<RegulatoryVertex> nodeOrder = graph.getNodeOrder();
		String[] t_regulators = new String[nodeOrder.size()];
		int[] t_cst = new int[nodeOrder.size()];
		boolean hasInputVars = false;
		RegulatoryVertex[] t_vertex = new RegulatoryVertex[nodeOrder
				.size()];
		for (int i = 0; i < t_vertex.length; i++) {
			RegulatoryVertex vertex = nodeOrder.get(i);
			t_vertex[i] = vertex;
			t_regulators[i] = vertex.getId();
			if (vertex.isInput())
				hasInputVars = true;
		}
		OmddNode[] t_tree = graph.getAllTrees(true);
		if (mutant != null) {
			mutant.apply(t_tree, graph);
		}
		boolean bType1 = (config.getExportType() == GsNuSMVConfig.CFG_INPUT_FRONZEN);

		out.write("-- " + date + "\n");
		out.write("-- GINsim implicit representation for NuSMV --\n");
		out.write("-- NuSMV version 2.5.1 (or higher) required --\n");
		out.write("-- ");
		if (bType1)
			out.write(Translator.getString("STR_NuSMV_Type1"));
		else
			out.write(Translator.getString("STR_NuSMV_Type2"));
		out.write("\n\nMODULE main\n");

		PriorityClassDefinition priorities = (PriorityClassDefinition) config.store
				.getObject(1);
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

		String sTmp;
		boolean bFirst;
		int[][] iaTmp = null;

		out.write("\nIVAR\n-- Simulation mode declaration --\n");
		switch (config.getUpdatePolicy()) {
		case GsNuSMVConfig.CFG_SYNC:
			out.write("-- Synchronous\n  PCs : { PC_c1 };\n  PC_c1_vars : { ");
			sTmp = "PC_c1";
			tmPcNum2Name.put(1, sTmp);
			// every variable -> 1 class
			for (int i = 0; i < nodeOrder.size(); i++) {
				sTmp += "_" + t_regulators[i];
				tmVarNum2PcNum.put(i, new Integer[] { 0, 1, 0 });
			}
			for (int i = 0; i < nodeOrder.size(); i++)
				tmVarNum2SubPcName
						.put(i, new String[] { null, sTmp, null });
			out.write(sTmp + " };\n");
			break;

		case GsNuSMVConfig.CFG_PCLASS:
			out.write("-- Priority classes\n  PCs : { ");
			for (int i = 0; i < priorities.getNbElements(); i++) {
				Reg2dynPriorityClass pc = (Reg2dynPriorityClass) priorities
						.getElement(null, i);
				if (i > 0)
					out.write(", ");
				sTmp = "PC_" + pc.getName();
				out.write(sTmp);
				tmPcNum2Name.put(i + 1, sTmp);
			}
			out.write(" };\n");

			iaTmp = priorities.getPclass(nodeOrder);
			for (int i = 0; i < iaTmp.length; i++) {
				sTmp = tmPcNum2Name.get(i + 1);
				out.write("  " + sTmp + "_vars : { ");
				tmPcNum2Rank.put(i + 1, iaTmp[i][0]);

				switch (iaTmp[i][1]) {
				case 0: // Synchronous
					for (int j = 2; j < iaTmp[i].length; j += 2) {
						sTmp += "_" + t_regulators[iaTmp[i][j]];
						Integer[] aiSplits = (tmVarNum2PcNum
								.containsKey(iaTmp[i][j])) ? tmVarNum2PcNum
								.get(iaTmp[i][j])
								: new Integer[] { 0, 0, 0 };
						aiSplits[iaTmp[i][j + 1] + 1] = i + 1;
						tmVarNum2PcNum.put(iaTmp[i][j], aiSplits);
						if (iaTmp[i][j + 1] != 0)
							sTmp += (iaTmp[i][j + 1] == 1) ? "Plus"
									: "Minus";
					}
					out.write(sTmp);
					for (int j = 2; j < iaTmp[i].length; j += 2) {
						String[] saTmp = (tmVarNum2SubPcName
								.containsKey(iaTmp[i][j])) ? tmVarNum2SubPcName
								.get(iaTmp[i][j]) : new String[] { null,
								null, null };
						saTmp[iaTmp[i][j + 1] + 1] = sTmp;
						tmVarNum2SubPcName.put(iaTmp[i][j], saTmp);
					}
					break;

				default: // Asynchronous
					for (int j = 2; j < iaTmp[i].length; j += 2) {
						if (j > 2)
							out.write(", ");
						String sub = sTmp + "_" + t_regulators[iaTmp[i][j]];
						if (iaTmp[i][j + 1] != 0)
							sub += (iaTmp[i][j + 1] == 1) ? "Plus"
									: "Minus";
						out.write(sub);
						String[] saTmp = (tmVarNum2SubPcName
								.containsKey(iaTmp[i][j])) ? tmVarNum2SubPcName
								.get(iaTmp[i][j]) : new String[] { null,
								null, null };
						saTmp[iaTmp[i][j + 1] + 1] = sub;
						tmVarNum2SubPcName.put(iaTmp[i][j], saTmp);
						Integer[] aiSplits = (tmVarNum2PcNum
								.containsKey(iaTmp[i][j])) ? tmVarNum2PcNum
								.get(iaTmp[i][j])
								: new Integer[] { 0, 0, 0 };
						aiSplits[iaTmp[i][j + 1] + 1] = i + 1;
						tmVarNum2PcNum.put(iaTmp[i][j], aiSplits);
					}
					break;
				}
				out.write(" };\n");
			}
			break;

		default:
			out.write("-- Asynchronous\n  PCs : { ");
			bFirst = true;
			for (int i = 0; i < t_vertex.length; i++) {
				if (t_vertex[i].isInput())
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
			for (int i = 0; i < t_vertex.length; i++) {
				if (t_vertex[i].isInput())
					continue;
				sTmp = "PC_c" + (i + 1) + "_" + t_regulators[i];
				out.write("  PC_c" + (i + 1) + "_vars : { " + sTmp
						+ " };\n");
				tmVarNum2SubPcName
						.put(i, new String[] { null, sTmp, null });
			}
			break;
		}

		if (hasInputVars) {
			if (bType1)
				out.write("\nFROZENVAR\n");
			out.write("-- Input variables declaration\n");
			for (int i = 0; i < t_vertex.length; i++) {
				if (t_vertex[i].isInput()) {
					String s_levels = "0";
					for (int j = 1; j <= t_vertex[i].getMaxValue(); j++)
						s_levels += ", " + j;
					out.write("  " + t_regulators[i] + " : { " + s_levels
							+ "};\n");
				}
			}
		}

		out.write("\nVAR");
		// PCrank depends on the state variables
		// Should therefore be declared after
		// But after some tests
		if (config.getUpdatePolicy() == GsNuSMVConfig.CFG_PCLASS
				&& iaTmp != null && iaTmp.length > 1) {
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
		HashMap<String, ArrayList<String>> hmRegulators = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < t_vertex.length; i++) {
			for (int j = 0; j < t_cst.length; j++)
				t_cst[j] = -1;
			hmRegulators.put(t_regulators[i], new ArrayList<String>(
					nodeRegulators(t_tree[i], t_vertex, t_cst)));
		}
		// Starting Nodes
		ArrayList<Integer> alStarting = new ArrayList<Integer>();
		int min = hmRegulators.size(), pos = -1;
		boolean[] visited = new boolean[t_vertex.length];
		for (int i = 0; i < t_vertex.length; i++) {
			visited[i] = false;
			ArrayList<String> alTmp = hmRegulators.get(t_regulators[i]);
			if (alTmp.isEmpty() || alTmp.get(0) == t_regulators[i])
				alStarting.add(new Integer(i));
			else if (alTmp.size() < min) {
				min = alTmp.size();
				pos = i;
			}
		}
		if (alStarting.isEmpty())
			alStarting.add(new Integer(pos));
		ArrayList<Integer> alSorted = new ArrayList<Integer>();
		ListIterator<Integer> li = alStarting.listIterator();
		while (li.hasNext())
			topoSortVisit(hmRegulators, t_regulators, t_vertex,
					((Integer) li.next()).intValue(), visited, alSorted);
		Collections.reverse(alSorted);

		// Print State variables according to the Topological Sort!!!
		out.write("\n-- State variables declaration\n");
		for (int i = 0; i < t_vertex.length; i++) {
			int currIndex = alSorted.get(i).intValue();
			if (t_vertex[currIndex].isInput())
				continue;
			String s_levels = "0";

			for (int j = 1; j <= t_vertex[currIndex].getMaxValue(); j++)
				s_levels += ", " + j;

			out.write("  " + t_regulators[currIndex] + " : {" + s_levels
					+ "};\n");
		}

		out.write("\nASSIGN");
		if (config.getUpdatePolicy() == GsNuSMVConfig.CFG_PCLASS
				&& iaTmp != null && iaTmp.length > 1) {
			if (tmPcRank2Name.size() > 1) {
				out.write("\n-- Establishing priorities\n");
				out.write("  PCrank :=\n    case\n");
				for (int c = 0; c < iaTmp.length; c++) {
					sTmp = "      ";
					if (c + 1 == iaTmp.length) {
						sTmp += "TRUE";
					} else {
						for (int v = 2; v < iaTmp[c].length; v += 2) {
							if (v > 2)
								sTmp += " | ";
							switch (iaTmp[c][v + 1]) {
							case 1:
								sTmp += t_regulators[iaTmp[c][v]] + "_inc";
								break;
							case -1:
								sTmp += t_regulators[iaTmp[c][v]] + "_dec";
								break;
							default:
								sTmp += "!" + t_regulators[iaTmp[c][v]]
										+ "_std";
							}
						}
					}
					out.write(sTmp + " : "
							+ tmPcRank2Name.get(tmPcNum2Rank.get(c + 1))
							+ ";\n");
				}
				out.write("    esac;\n");
			}
		}

		out.write("\n-- Variable update if conditions are met\n");
		for (int v = 0; v < t_vertex.length; v++) {
			if (t_vertex[v].isInput())
				continue;
			// The real next(Variable) if conditions are satisfied
			out.write("next(" + t_regulators[v] + ") := \n");
			out.write("  case\n");

			// Class entry conditions
			Integer[] aiSplits = tmVarNum2PcNum.get(v);
			out.write("    update_" + t_regulators[v]);
			if (aiSplits[2] > 0)
				out.write("Plus");
			out.write("_OK & (" + t_regulators[v] + "_inc) : ");
			out.write(((t_vertex[v].getMaxValue() > 1) ? t_regulators[v]
					+ " + 1" : "1")
					+ ";\n");
			out.write("    update_" + t_regulators[v]);
			if (aiSplits[0] > 0)
				out.write("Minus");
			out.write("_OK & (" + t_regulators[v] + "_dec) : ");
			out.write(((t_vertex[v].getMaxValue() > 1) ? t_regulators[v]
					+ " - 1" : "0")
					+ ";\n");
			out.write("    TRUE : " + t_regulators[v] + ";\n");
			out.write("  esac;\n");
		}

		out.write("\nDEFINE\n");

		out.write("-- Variable next level regulation\n");
		for (int i = 0; i < t_vertex.length; i++) {
			if (t_vertex[i].isInput())
				continue;
			out.write(t_vertex[i].getId() + "_focal :=\n");
			out.write("  case\n");
			for (int j = 0; j < t_cst.length; j++)
				t_cst[j] = -1;
			node2SMV(t_tree[i], out, t_vertex, t_cst);
			out.write("  esac;\n");
		}
		out.write("\n");

		for (int v = 0; v < t_vertex.length; v++) {
			if (t_vertex[v].isInput())
				continue;
			out.write(t_regulators[v] + "_inc := ");
			out.write(t_regulators[v] + "_focal > ");
			out.write(t_regulators[v] + ";\n");
			out.write(t_regulators[v] + "_dec := ");
			out.write(t_regulators[v] + "_focal < ");
			out.write(t_regulators[v] + ";\n");
			out.write(t_regulators[v] + "_std := ");
			out.write(t_regulators[v] + "_focal = ");
			out.write(t_regulators[v] + ";\n\n");
		}

		for (int v = 0; v < t_vertex.length; v++) {
			if (t_vertex[v].isInput())
				continue;
			Integer[] aiSplits = tmVarNum2PcNum.get(v);
			String[] saSubName = tmVarNum2SubPcName.get(v);
			boolean bPlus = (aiSplits[2] > 0);
			int pc = (bPlus) ? aiSplits[2] : aiSplits[1];
			String sub = (bPlus) ? saSubName[2] : saSubName[1];
			out.write("update_" + t_regulators[v] + ((bPlus) ? "Plus" : "")
					+ "_OK := (PCs = ");
			out.write(tmPcNum2Name.get(pc) + ") & (");
			out.write(tmPcNum2Name.get(pc) + "_vars = ");
			out.write(sub);
			if (config.getUpdatePolicy() == GsNuSMVConfig.CFG_PCLASS) {
				out.write(") & (PCrank = ");
				out.write((String) tmPcRank2Name.get(tmPcNum2Rank.get(pc)));
			}
			out.write(");\n");

			if (bPlus) { // There's also a Minus transition
				pc = aiSplits[0];
				out.write("update_" + t_regulators[v]
						+ "Minus_OK := (PCs = ");
				out.write(tmPcNum2Name.get(pc) + ") & (");
				out.write(tmPcNum2Name.get(pc) + "_vars = ");
				out.write(saSubName[0]);
				if (config.getUpdatePolicy() == GsNuSMVConfig.CFG_PCLASS) {
					out.write(") & (PCrank = ");
					out.write((String) tmPcRank2Name.get(tmPcNum2Rank
							.get(pc)));
				}
				out.write(");\n");
			}
		}

		out.write("\nstrongSS := ");
		boolean bIsFirst = true;
		for (int v = 0; v < t_vertex.length; v++) {
			if (t_vertex[v].isInput())
				continue;
			if (!bIsFirst) {
				out.write(" & ");
			}
			out.write(t_regulators[v] + "_std");
			bIsFirst = false;
		}
		out.write(";\n");

		if (!bType1) {
			out.write("\nweakSS := FALSE\n");
			// -- Computing Weak Stable States for compacted STG (type 2) --
			List<RegulatoryVertex> sortedVars = new ArrayList<RegulatoryVertex>();
			List<RegulatoryVertex> orderStateVars = new ArrayList<RegulatoryVertex>();
			List<RegulatoryVertex> orderInputVars = new ArrayList<RegulatoryVertex>();
			for (int i = 0; i < nodeOrder.size(); i++) {
				if (nodeOrder.get(i).isInput())
					orderInputVars.add(nodeOrder.get(i));
				else {
					orderStateVars.add(nodeOrder.get(i));
					sortedVars.add(nodeOrder.get(i));
				}
			}
			sortedVars.addAll(orderInputVars);
			// OMDDs reordered [ stateVars inputVars]
			OmddNode[] tReordered = new OmddNode[nodeOrder.size()];
			for (int i = 0; i < nodeOrder.size(); i++) {
				tReordered[i] = sortedVars.get(i)
						.getTreeParameters(sortedVars).reduce();
			}

			StableStateSearcher sss = ServiceManager.getManager().getService(StableStatesService.class).getSearcher(graph);
			sss.setNodeOrder(sortedVars, tReordered);
			sss.setPerturbation(mutant);
			OmddNode omdds = sss.getStables();
			int[] stateValues = new int[sortedVars.size()];
			for (int i = 0; i < stateValues.length; i++)
				stateValues[i] = -1;

			out.write(writeStableStates(stateValues, omdds, orderStateVars,
					0));
			out.write(";\n");
		}

		out.write("\nTRANS\n");
		for (int i = 0; i < t_vertex.length; i++) {
			if (t_vertex[i].isInput())
				continue;
			out.write("next(" + t_regulators[i] + ") != ");
			out.write(t_regulators[i] + " |\n");
		}
		if (bType1)
			out.write("strongSS;\n");
		else
			out.write("weakSS;\n");

		// TODO: make use of the name given by the user
		// referencing the atomic proposition
		out.write("\n-- State variables initialization\n");
		out.write(writeInitialState(t_vertex, false, m_initstates));
		if (bType1 && hasInputVars) {
			out.write("-- Input variables initialization\n");
			out.write(writeInitialState(t_vertex, true, m_initinputs));
		}

		out.write("\n");
		out.write("-- Property specification\n");
		if (bType1)
			out.write("-- SPEC !EF ( strongSS )\n");
		else
			out.write("-- SPEC AF ( weakSS )\n-- LTLSPEC G F ( weakSS )\n");

		// Close main tags
		out.close();
	}

	private static String writeStableStates(int[] stateValues, OmddNode nodes,
			List<RegulatoryVertex> stateVars, int level) {
		String sRet = "";
		if (nodes.next == null) {
			if (nodes.value == 1 && level > stateVars.size()) {
				// we have a stable state:
				sRet += "  | ";
				for (int i = 0; i < stateVars.size(); i++) {
					if (stateValues[i] == -1) continue;
					if (i > 0)
						sRet += " & ";
					sRet += stateVars.get(i) + "=" + stateValues[i];
				}
				sRet += "\n";
			}
			return sRet;
		}
		for (int i = 0; i < nodes.next.length; i++) {
			stateValues[nodes.level] = i;
			sRet += writeStableStates(stateValues, nodes.next[i], stateVars,
					level + 1);
		}
		stateValues[nodes.level] = -1;
		return sRet;
	}

	/**
	 * Implements a simple (recursive) topological sort for sorting, in
	 * ascending order, the model variables by the number of regulators
	 * affecting each one.
	 * 
	 * @param hmRegulators
	 *            a String containing the set of regulators per variable.
	 * @param t_regulators
	 *            The name of each regulator.
	 * @param t_vertex
	 *            The set of vertexes.
	 * @param currindex
	 *            The current index in the recursion.
	 * @param visited
	 *            A mark of visited vertexes.
	 * @param alSorted
	 *            The list of vertexes already sorted.
	 */
	static private void topoSortVisit(
			HashMap<String, ArrayList<String>> hmRegulators,
			String[] t_regulators, RegulatoryVertex[] t_vertex,
			int currindex, boolean[] visited, ArrayList<Integer> alSorted) {
		if (visited[currindex])
			return;
		visited[currindex] = true;
		String sReg = t_regulators[currindex];
		for (int i = 0; i < t_vertex.length; i++) {
			if (i == currindex)
				continue;
			ArrayList<String> alRegulators = hmRegulators.get(t_regulators[i]);
			if (alRegulators.contains(sReg))
				topoSortVisit(hmRegulators, t_regulators, t_vertex, i, visited,
						alSorted);
		}
		alSorted.add(new Integer(currindex));
	}

	/**
	 * It creates an HashSet with the regulators of a given vertex given its
	 * OMDD. It is used for the topological sort algorithm.
	 * 
	 * @param node
	 *            The OMDD of a given vertex.
	 * @param t_names
	 *            The set of existing vertexes in the model.
	 * @param t_cst
	 *            Auxiliary variable to help navigate through the tree.
	 * @return The set of regulator names of a given vertex.
	 */
	static private HashSet<String> nodeRegulators(OmddNode node,
			RegulatoryVertex[] t_names, int[] t_cst) {
		HashSet<String> hs = new HashSet<String>();
		if (node.next == null) {
			for (int i = 0; i < t_cst.length; i++)
				if (t_cst[i] != -1)
					hs.add(t_names[i].getId());
			return hs;
		}
		for (int i = 0; i < node.next.length; i++) {
			t_cst[node.level] = i;
			hs.addAll(nodeRegulators(node.next[i], t_names, t_cst));
		}
		t_cst[node.level] = -1;
		return hs;
	}

	/**
	 * Knows how to write the given logical function of a given vertex,
	 * specified by its OMDD, into a NuSMV case construct.
	 * 
	 * @param node
	 *            The OMDD to be written.
	 * @param out
	 *            The Writer where the specification is to be written.
	 * @param t_names
	 *            The set of model vertexes.
	 * @param t_cst
	 *            Auxiliary variable to help navigate through the tree.
	 * @throws IOException
	 */
	static private void node2SMV(OmddNode node, FileWriter out,
			RegulatoryVertex[] t_names, int[] t_cst) throws IOException {
		if (node.next == null) // this is a leaf, write the constraint
		{
			String s = "";

			for (int i = 0; i < t_cst.length; i++) {
				if (t_cst[i] != -1) {
					s += "(" + t_names[i] + " = " + t_cst[i] + ") & ";
				}
			}
			if (s.isEmpty()) {
				s = "TRUE ";
			} else {
				s = s.substring(0, s.length() - 2);
			}
			out.write("    " + s + ": " + node.value + ";\n");
			return;
		}
		for (int i = 0; i < node.next.length; i++) {
			t_cst[node.level] = i;
			node2SMV(node.next[i], out, t_names, t_cst);
		}
		t_cst[node.level] = -1;
	}
}
