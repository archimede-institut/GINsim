package fr.univmrs.ibdm.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * Encode a graph to GNA format.
 */
public class GsGNAExport {
	static transient Hashtable hash;

	/**
	 * @param graph
	 * @param selectedOnly
	 * @param fileName
	 * @param config
	 *            store the configuration
	 */
	public static void encode(GsRegulatoryGraph graph, String fileName) {
		Vector nodeOrder = graph.getNodeOrder();
		Iterator it = nodeOrder.iterator();
		try {
			FileWriter out = new FileWriter(fileName);
			while (it.hasNext()) {
				GsRegulatoryVertex node = (GsRegulatoryVertex) it.next();
				String id = node.getId();
				out.write("state-variable: " + id + "\n"
						+ "  zero-parameter: zero_" + id + "\n"
						+ "  box-parameter: max_" + id + "\n"
						+ "  threshold-parameters: ");
				out.write("\n" + "  production-parameters: k_" + id + "\n"
						+ "  degradation-parameters: g_" + id + "\n"
						+ "  state-equation:\n    d/dt " + id + " = "
						+ getGNAEq(node));
				out.write("threshold-inequalities: ");
			}
		} catch (IOException e) {
		}
	}

	private static String getGNAEq(GsRegulatoryVertex node) {
		return "";
	}
}
