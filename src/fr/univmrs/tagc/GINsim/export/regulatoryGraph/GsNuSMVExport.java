package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JComponent;

import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.reg2dyn.GsReg2dynPriorityClass;
import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParameterList;
import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParametersManager;
import fr.univmrs.tagc.GINsim.reg2dyn.PriorityClassDefinition;
import fr.univmrs.tagc.GINsim.reg2dyn.PriorityClassManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.LogicalParameterList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.qmc.LogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.widgets.StackDialog;

/**
 * Encode a graph to NuSMV format.
 */
public class GsNuSMVExport extends GsAbstractExport {
	static transient Map hash;

	public GsNuSMVExport() {
		id = "SMV";
		extension = ".smv";
		filter = new String[] { "smv" };
		filterDescr = "NuSMV files";
	}

	protected void doExport(GsExportConfig config) {
		encode((GsRegulatoryGraph) config.getGraph(), config.getFilename(),
				(GsNuSMVConfig) config.getSpecificConfig());
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType,
			GsGraph graph) {
		if (graph instanceof GsRegulatoryGraph) {
			return new GsPluggableActionDescriptor[] { new GsPluggableActionDescriptor(
					"STR_NuSMVmodelChecker",
					"STR_NuSMVmodelCheckerExport_descr", null, this,
					ACTION_EXPORT, 0) };
		}
		return null;
	}

	public boolean needConfig(GsExportConfig config) {
		return true;
	}

	protected JComponent getConfigPanel(GsExportConfig config,
			StackDialog dialog) {
		return new GsNuSMVExportConfigPanel(config, dialog);
	}

	private static String getInitState(Object vertex, Map m) {
		List v = (List) m.get(vertex);
		if (v != null && v.size() > 0) {
			String s = "" + v.get(0);
			if (v.size() > 1) {
				s = "{" + s;
				for (int j = 1; j < v.size(); j++) {
					s += "," + v.get(j);
				}
				s += "}";
			}
			return s;
		}
		return null;
	}

	/**
	 * @param graph
	 * @param selectedOnly
	 * @param fileName
	 * @param config
	 *            store the configuration
	 */
	public static void encode(GsRegulatoryGraph graph, String fileName,
			GsNuSMVConfig config) {
		hash = new HashMap();

		DateFormat dateformat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG);
		String date = dateformat.format(new Date());
		try {
			FileWriter out = new FileWriter(fileName);
			Iterator it = config.getInitialState().keySet().iterator();
			Map m_initstates;
			if (it.hasNext()) {
				m_initstates = ((GsInitialState) it.next()).getMap();
			} else {
				m_initstates = new HashMap();
			}
			if (m_initstates == null) {
				m_initstates = new HashMap();
			}

			GsRegulatoryMutantDef mutant = (GsRegulatoryMutantDef) config.store
					.getObject(0);
			List nodeOrder = graph.getNodeOrder();
			String[] t_regulators = new String[nodeOrder.size()];
			int[] t_cst = new int[nodeOrder.size()];
			GsRegulatoryVertex[] t_vertex = new GsRegulatoryVertex[nodeOrder
					.size()];
			for (int i = 0; i < t_vertex.length; i++) {
				GsRegulatoryVertex vertex = (GsRegulatoryVertex) nodeOrder
						.get(i);
				t_vertex[i] = vertex;
				t_regulators[i] = vertex.getId();
			}
			OmddNode[] t_tree = graph.getAllTrees(true);
			if (mutant != null) {
				mutant.apply(t_tree, graph);
			}

			out.write("-- GINsim implicit representation for NuSMV --\n");
			out.write("-- " + date + "\n");
			out.write("\nMODULE main\n\nVAR\n");

			PriorityClassDefinition priorities = (PriorityClassDefinition) config.store
					.getObject(1);
			TreeMap tmPcNum2Name = new TreeMap();
			// classNum -> className
			TreeMap tmVarNum2PcNum = new TreeMap();
			// varNum -> classNum
			TreeMap tmVarNum2PcName = new TreeMap();
			// varNum -> className
			TreeMap tmVarNum2SubPcName = new TreeMap();
			// varNum-> subClassName
			TreeMap tmPcNum2Rank = new TreeMap();
			// classNum -> RankNum
			TreeMap tmPcRank2Name = new TreeMap();
			// rankNum -> rankName

			String sTmp;
			int[][] iaTmp = null;

			out.write("-- Priority classes declaration\n");
			switch (config.getType()) {
			case GsNuSMVConfig.CFG_SYNC:
				out.write("-- Synchronous\n  PCs : { PC_c1 };\n  PC_c1_vars : { ");
				sTmp = "PC_c1";
				tmPcNum2Name.put(new Integer(1), sTmp);
				for (int i = 0; i < nodeOrder.size(); i++) {
					sTmp += "_" + t_regulators[i];
					tmVarNum2PcNum.put(new Integer(i), new Integer(1)); // every variable -> 1 class
				}
				for (int i = 0; i < nodeOrder.size(); i++) {
					tmVarNum2SubPcName.put(new Integer(i), sTmp);
					tmVarNum2PcName.put(new Integer(i), "PC_c1");
				}
				out.write(sTmp + " };\n");
				break;

			case GsNuSMVConfig.CFG_PCLASS:
				out.write("-- Priority classes\n  PCs : { ");
				for (int i = 0; i < priorities.getNbElements(); i++) {
					GsReg2dynPriorityClass pc = (GsReg2dynPriorityClass) priorities
							.getElement(null, i);
					if (i > 0)
						out.write(", ");
					sTmp = "PC_" + pc.getName();
					out.write(sTmp);
					tmPcNum2Name.put(new Integer(i + 1), sTmp);
				}
				out.write(" };\n");

				iaTmp = priorities.getPclass(nodeOrder);
				for (int i = 0; i < iaTmp.length; i++) {
					sTmp = (String) tmPcNum2Name.get(new Integer(i + 1));
					out.write("  " + sTmp + "_vars : { ");
					tmPcNum2Rank.put(new Integer(i + 1), new Integer(iaTmp[i][0]));

					switch (iaTmp[i][1]) {
					case 0: // Synchronous
						for (int j = 2; j < iaTmp[i].length; j += 2) {
							sTmp += "_" + t_regulators[iaTmp[i][j]];
							tmVarNum2PcNum.put(new Integer(iaTmp[i][j]), new Integer(i + 1));
							tmVarNum2PcName.put(new Integer(iaTmp[i][j]),
									tmPcNum2Name.get(new Integer(i + 1)));
						}
						out.write(sTmp);
						for (int j = 2; j < iaTmp[i].length; j += 2)
							tmVarNum2SubPcName.put(new Integer(iaTmp[i][j]), sTmp);
						break;

					default: // Asynchronous
						for (int j = 2; j < iaTmp[i].length; j += 2) {
							if (j > 2)
								out.write(", ");
							out.write(sTmp + "_");
							out.write(t_regulators[iaTmp[i][j]]);
							tmVarNum2PcNum.put(new Integer(iaTmp[i][j]), new Integer(i + 1));
							tmVarNum2PcName.put(new Integer(iaTmp[i][j]), sTmp);
							tmVarNum2SubPcName.put(new Integer(iaTmp[i][j]), sTmp + "_"
									+ t_regulators[iaTmp[i][j]]);
						}
						break;
					}
					out.write(" };\n");
				}
				break;

			default:
				out.write("-- Asynchronous\n  PCs : { ");
				for (int i = 0; i < nodeOrder.size(); i++) {
					if (i > 0)
						out.write(", ");
					sTmp = "PC_c" + (i + 1);
					out.write(sTmp);
					tmVarNum2PcNum.put(new Integer(i), new Integer(i + 1));
					tmVarNum2PcName.put(new Integer(i), sTmp);
					tmPcNum2Name.put(new Integer(i + 1), sTmp);
				}
				out.write(" };\n");
				for (int i = 0; i < nodeOrder.size(); i++) {
					sTmp = "PC_c" + (i + 1) + "_" + t_regulators[i];
					out.write("  PC_c" + (i + 1) + "_vars : { " + sTmp
							+ " };\n");
					tmVarNum2SubPcName.put(new Integer(i), sTmp);
				}
				break;
			}

			if (config.getType() == GsNuSMVConfig.CFG_PCLASS && iaTmp != null
					&& iaTmp.length > 1) {
				out.write("  PCrank : { ");
				int iLast = 0;
				sTmp = "";
				boolean bWrote = false;
				for (int c = 0; c < iaTmp.length; c++) {
					if (c == 0 || iaTmp[c][0] > iLast) {
						iLast = iaTmp[c][0];
						sTmp = "rank" + iLast;
					}
					sTmp += "_" + tmPcNum2Name.get(new Integer(c + 1));
					if (c + 1 == iaTmp.length || iaTmp[c + 1][0] > iLast) {
						if (bWrote)
							out.write(", ");
						bWrote = true;
						out.write(sTmp);
						tmPcRank2Name.put(new Integer(iLast), sTmp);
					}
				}
				out.write(" };\n");
			}

			// Topological sorting of the state variables
			HashMap hmRegulators = new HashMap();
			for (int i = 0; i < t_vertex.length; i++) {
				for (int j = 0; j < t_cst.length; j++)
					t_cst[j] = -1;
				hmRegulators.put(t_regulators[i], new ArrayList(
						nodeRegulators(t_tree[i], t_vertex, t_cst, i)));
			}
			// Starting Nodes
			ArrayList alStarting = new ArrayList();
			int min = hmRegulators.size(), pos = -1;
			boolean[] visited = new boolean[t_vertex.length];
			for (int i = 0; i < t_vertex.length; i++) {
				visited[i] = false;
				ArrayList alTmp = (ArrayList)hmRegulators.get(t_regulators[i]);
				if (alTmp.isEmpty() || alTmp.get(0) == t_regulators[i])
					alStarting.add(new Integer(i));
				else if (alTmp.size() < min) {
					min = alTmp.size();
					pos = i;
				}
			}
			if (alStarting.isEmpty())
				alStarting.add(new Integer(pos));
			ArrayList alSorted = new ArrayList();
			ListIterator li = alStarting.listIterator();
			while (li.hasNext())
				topoSortVisit(hmRegulators, t_regulators, t_vertex, ((Integer)li.next()).intValue(),
						visited, alSorted);
			Collections.reverse(alSorted);

			// Print State variables according to the Topological Sort!!!
			out.write("\n-- State variables declaration\n");
			for (int i = 0; i < t_vertex.length; i++) {
				int currIndex = ((Integer)alSorted.get(i)).intValue();
				String s_levels = "0";

				for (int j = 1; j <= t_vertex[currIndex].getMaxValue(); j++)
					s_levels += ", " + j;

				out.write("  " + t_regulators[currIndex] + " : {" + s_levels + "};\n");
//				out.write("  " + t_regulators[currIndex] + "_nlevel : {" + s_levels
//						+ "};\n");
			}

			out.write("\nASSIGN\n");
			out.write("-- Variable initialization\n");
			for (int i = 0; i < nodeOrder.size(); i++) {
				String s_init = getInitState(nodeOrder.get(i), m_initstates);
				if (s_init == null) {
					out.write("--  init(" + t_regulators[i] + ") := 0;\n");
				} else {
					out.write("  init(" + t_regulators[i] + ") := " + s_init
							+ ";\n");
				}
			}

			if (config.getType() == GsNuSMVConfig.CFG_PCLASS && iaTmp != null
					&& iaTmp.length > 1) {
				if (tmPcRank2Name.size() > 1) {
					out.write("\n-- Establishing priorities\n");
					out.write("  PCrank :=\n    case\n");
					for (int c = 0; c < iaTmp.length; c++) {
						sTmp = "      (";
						if (c + 1 == iaTmp.length) {
							sTmp += "1";
						} else {
							for (int v = 2; v < iaTmp[c].length; v += 2) {
								if (v > 2)
									sTmp += " | ";
								sTmp += "!" + t_regulators[iaTmp[c][v]]
										+ "_std";
							}
						}
						out.write(sTmp + ") : "
								+ tmPcRank2Name.get(tmPcNum2Rank.get(new Integer(c + 1)))
								+ ";\n");
					}
					out.write("    esac;\n");
				}
			}

			out.write("\n-- Variable update if conditions are met\n");
			for (int i = 0; i < nodeOrder.size(); i++) {
				// The real next(Variable) if conditions are satisfied
				out.write("next(" + t_vertex[i].getId() + ") := \n");
				out.write("  case\n");

				// TODO: put the conditions for the other class
				// for the async + between classes

				// Class entry conditions
				out.write("    update_" + t_vertex[i].getId() + "_OK & (");
				out.write(t_vertex[i].getId() + "_inc) : ");
				out.write(((t_vertex[i].getMaxValue() > 1) ? t_vertex[i]
						.getId() + " + 1" : "1")
						+ ";\n");
				out.write("    update_" + t_vertex[i].getId() + "_OK & (");
				out.write(t_vertex[i].getId() + "_dec) : ");
				out.write(((t_vertex[i].getMaxValue() > 1) ? t_vertex[i]
						.getId() + " - 1" : "0")
						+ ";\n");
				out.write("    1 : " + t_vertex[i].getId() + ";\n");
				out.write("  esac;\n");
			}

			out.write("\nDEFINE\n");
			
			out.write("-- Variable next level regulation\n");
			for (int i = 0; i < nodeOrder.size(); i++) {
				out.write(t_vertex[i].getId() + "_nlevel :=\n");
				out.write("  case\n");
				for (int j = 0; j < t_cst.length; j++)
					t_cst[j] = -1;
				node2SMV(t_tree[i], out, t_vertex, t_cst, i);
				out.write("  esac;\n");
			}
			out.write("\n");
			
			for (int v = 0; v < nodeOrder.size(); v++) {
				out.write(t_regulators[v] + "_inc := case ");
				out.write(t_regulators[v] + "_nlevel > ");
				out.write(t_regulators[v] + " : 1; 1 : 0; esac;\n");
				out.write(t_regulators[v] + "_dec := case ");
				out.write(t_regulators[v] + "_nlevel < ");
				out.write(t_regulators[v] + " : 1; 1 : 0; esac;\n");
				out.write(t_regulators[v] + "_std := case ");
				out.write(t_regulators[v] + "_nlevel = ");
				out.write(t_regulators[v] + " : 1; 1 : 0; esac;\n\n");
			}
			out.write("stableState := ");
			for (int v = 0; v < nodeOrder.size(); v++) {
				if (v > 0)
					out.write(" & ");
				out.write(t_regulators[v] + "_std");
			}
			out.write(";\n\n");

			for (int v = 0; v < nodeOrder.size(); v++) {
				int pc = ((Integer)tmVarNum2PcNum.get(new Integer(v))).intValue();
				out.write("update_" + t_regulators[v] + "_OK := (PCs = ");
				out.write(tmPcNum2Name.get(new Integer(pc)) + ") & (");
				out.write(tmPcNum2Name.get(new Integer(pc)) + "_vars = ");
				out.write((String)tmVarNum2SubPcName.get(new Integer(v)));
				if (config.getType() == GsNuSMVConfig.CFG_PCLASS) {
					out.write(") & (PCrank = ");
					out.write(((Integer)tmPcRank2Name.get(tmPcNum2Rank.get(new Integer(pc)))).intValue());
				}
				out.write(");\n");
			}

			out.write("\n");
			out.write("-- Property specification\n");
			out.write("-- SPEC !EF ( stableState )\n");

			// Close main tags
			out.close();

		} catch (IOException e) {
			GsEnv.error(
					new GsException(GsException.GRAVITY_ERROR, e
							.getLocalizedMessage()), null);
		}
	}

	static private void topoSortVisit(
			HashMap hmRegulators,
			String[] t_regulators, GsRegulatoryVertex[] t_vertex,
			int currindex, boolean[] visited, ArrayList alSorted) {
		if (visited[currindex])
			return;
		visited[currindex] = true;
		String sReg = t_regulators[currindex];
		for (int i = 0; i < t_vertex.length; i++) {
			if (i == currindex)
				continue;
			ArrayList alRegulators = (ArrayList)hmRegulators.get(t_regulators[i]);
			if (alRegulators.contains(sReg))
				topoSortVisit(hmRegulators, t_regulators, t_vertex, i, visited,
						alSorted);
		}
		alSorted.add(new Integer(currindex));
	}

	static private HashSet nodeRegulators(OmddNode node,
			GsRegulatoryVertex[] t_names, int[] t_cst, int index) {
		HashSet hs = new HashSet();
		if (node.next == null) {
			for (int i = 0; i < t_cst.length; i++)
				if (t_cst[i] != -1)
					hs.add(t_names[i].toString());
			return hs;
		}
		for (int i = 0; i < node.next.length; i++) {
			t_cst[node.level] = i;
			hs.addAll(nodeRegulators(node.next[i], t_names, t_cst, index));
		}
		t_cst[node.level] = -1;
		return hs;
	}

	static private void node2SMV(OmddNode node, FileWriter out,
			GsRegulatoryVertex[] t_names, int[] t_cst, int index)
			throws IOException {
		if (node.next == null) // this is a leaf, write the constraint
		{
			String s = "";

			for (int i = 0; i < t_cst.length; i++) {
				if (t_cst[i] != -1) {
					s += "(" + t_names[i] + " = " + t_cst[i] + ") & ";
				}
			}
			if (s.isEmpty()) {
				s = "1 ";
			} else {
				s = s.substring(0, s.length() - 2);
			}
			// FIXME: replace node.value with smart incremental move
			out.write("    " + s + ": " + node.value + ";\n");
			return;
		}
		for (int i = 0; i < node.next.length; i++) {
			t_cst[node.level] = i;
			node2SMV(node.next[i], out, t_names, t_cst, index);
		}
		t_cst[node.level] = -1;
	}
}
