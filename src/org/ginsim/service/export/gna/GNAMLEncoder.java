package org.ginsim.service.export.gna;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;

import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * Exports a GINsim Regulatory graph into the xml GNAML model description.
 * 
 * @author Pedro T. Monteiro
 */
public class GNAMLEncoder {

	public void write(RegulatoryGraph graph, FileWriter out) throws IOException {
		XMLWriter xml = new XMLWriter(out, null);

		xml.openTag("gnaml");
		xml.addAttr("xmlns", "http://www-gna.inrialpes.fr/gnaml/version1");
		xml.addAttr("version", "1.0");

		xml.openTag("model");
		xml.addAttr("id", graph.getGraphName());
		writeStatesVariables(graph, xml);
		xml.closeTag(); // model
		xml.closeTag(); // gnaml
	}

	private void writeStatesVariables(RegulatoryGraph graph, XMLWriter xml)
			throws IOException {
		for (RegulatoryNode node : graph.getNodeOrder()) {
			int thresholdLevels = node.getMaxValue();
			String id = node.getId();
			xml.openTag("state-variable");
			xml.addAttr("id", id);

			xml.addTag("zero-parameter", new String[] { "id", "zero_" + id });
			xml.addTag("box-parameter", new String[] { "id", "max_" + id });

			xml.openTag("list-of-threshold-parameters");
			for (int i = 1; i <= thresholdLevels; i++) {
				xml.addTag("threshold-parameter", new String[] { "id",
						"t_" + id + "_" + i });
			}
			xml.closeTag();// list-of-threshold-parameters

			xml.openTag("list-of-synthesis-parameters");
			for (int i = 0; i <= thresholdLevels; i++) {
				xml.addTag("synthesis-parameter", new String[] { "id",
						"k_" + id + "_" + i });
			}
			xml.closeTag();// list-of-synthesis-parameters

			xml.openTag("list-of-degradation-parameters");
			xml.addTag("degradation-parameter",
					new String[] { "id", "g_" + id });
			xml.closeTag();// list-of-degradation-parameters

			xml.openTag("state-equation");
			writeStateEquation(xml, graph, node, thresholdLevels, id);
			xml.closeTag();// state-equation

			xml.openTag("parameter-inequalities");
			writeParameterInequalities(xml, node, thresholdLevels, id);
			xml.closeTag();// parameter-inequalities

			xml.closeTag();// state-variable
		}
	}

	private void writeStateEquation(XMLWriter xml, RegulatoryGraph graph,
			RegulatoryNode node, int thresholdLevels, String id)
			throws IOException {
		xml.openTag("math");
		xml.addAttr("xmlns", "http://www.w3.org/1998/Math/MathML");
		xml.openTag("apply");
		xml.addTag("minus");

		OMDDNode mdd = node.getTreeParameters(graph).reduce();
		if (mdd.next != null) {
			List<RegulatoryNode> nodeOrder = graph.getNodeOrder();
			int[][] parcours = new int[nodeOrder.size()][4];
			boolean hasMoreThanOne = countNonZeroPath(mdd) > 1;
			if (hasMoreThanOne) {
				xml.openTag("apply"); // K*s*s + K*s*s + ...
				xml.addTag("plus");
				exploreNode(xml, parcours, 0, id, mdd, nodeOrder);
				xml.closeTag();// apply plus
			} else {
				exploreNode(xml, parcours, 0, id, mdd, nodeOrder);
			}
		} else {
			xml.openTag("ci");
			xml.addContent("k_" + id + "_" + mdd.value);
			xml.closeTag();
		}

		xml.openTag("apply"); // - g_a * a
		xml.addTag("times");
		xml.addTagWithContent("ci", "g_" + id);
		xml.addTagWithContent("ci", id);
		xml.closeTag();// apply times

		xml.closeTag();// apply minus
		xml.closeTag();// math
	}

	private int countNonZeroPath(OMDDNode mdd) {
		if (mdd.next == null) {
			if (mdd.value > 0) {
				return 1;
			}
			return 0;
		}
		int ret = 0;
		OMDDNode refNode = null;
		for (int i = 0; i < mdd.next.length; i++) {
			OMDDNode nextNode = mdd.next[i];
			if (nextNode != refNode) {
				refNode = nextNode;
				ret += countNonZeroPath(mdd.next[i]);
			}
		}
		return ret;
	}

	private void exploreNode(XMLWriter xml, int[][] parcours, int deep,
			String topNodeId, OMDDNode node, List<RegulatoryNode> nodeOrder)
			throws IOException {
		if (node.next == null) {
			if (node.value > 0) {
				if (deep > 0) {
					xml.openTag("apply");
					xml.addTag("times");
				}
				xml.addTagWithContent("ci", "k_" + topNodeId + "_" + node.value);
				String nodeName;
				for (int i = 0; i < deep; i++) {
					nodeName = nodeOrder.get(parcours[i][2]).getId();
					if (parcours[i][0] > 0) {
						stepPlus(xml, nodeName, parcours[i][0]);
					}
					if (parcours[i][1] < parcours[i][3]) {
						stepMinus(xml, nodeName, parcours[i][1]);
					}
				}
				if (deep > 0) {
					xml.closeTag();// apply times
				}
			}
			return;
		}
		OMDDNode currentChild;
		for (int i = 0; i < node.next.length; i++) {
			currentChild = node.next[i];
			int begin = i;
			int end;
			for (end = i + 1; end < node.next.length
					&& currentChild == node.next[end]; end++, i++) {
				// nothing to do
			}
			parcours[deep][0] = begin;
			parcours[deep][1] = end;
			parcours[deep][2] = node.level;
			parcours[deep][3] = node.next.length;
			exploreNode(xml, parcours, deep + 1, topNodeId, node.next[begin],
					nodeOrder);
		}
	}

	private void stepPlus(XMLWriter xml, String id, int i) throws IOException {
		xml.openTag("apply");
		xml.addTag("csymbol", new String[] { "encoding", "text",
				"definitionURL",
				"http://www-gna.inrialpes.fr/gnaml/symbols/step-plus" }, "s+");
		xml.addTagWithContent("ci", id);
		xml.addTagWithContent("ci", "t_" + id + "_" + i);
		xml.closeTag();
	}

	private void stepMinus(XMLWriter xml, String id, int i) throws IOException {
		xml.openTag("apply");
		xml.addTag("csymbol", new String[] { "encoding", "text",
				"definitionURL",
				"http://www-gna.inrialpes.fr/gnaml/symbols/step-minus" }, "s-");
		xml.addTagWithContent("ci", id);
		xml.addTagWithContent("ci", "t_" + id + "_" + i);
		xml.closeTag();
	}

	private void writeParameterInequalities(XMLWriter xml, RegulatoryNode node,
			int thresholdLevels, String id) throws IOException {
		String g = "g_" + id;
		String K = "k_" + id + "_";
		String t = "t_" + id + "_";

		xml.openTag("math");
		xml.addAttr("xmlns", "http://www.w3.org/1998/Math/MathML");
		xml.openTag("apply");
		xml.addTag("lt");
		xml.addTagWithContent("ci", "zero_" + id);
		for (int i = 0; i <= thresholdLevels; i++) {
			if (i > 0) {
				xml.addTagWithContent("ci", t + i);
			}
			xml.openTag("apply");
			xml.addTag("divide");
			xml.addTagWithContent("ci", K + i);
			xml.addTagWithContent("ci", g);
			xml.closeTag();// apply
		}
		xml.addTagWithContent("ci", "max_" + id);
		xml.closeTag();// apply
		xml.closeTag();// math
	}

	/**
	 * Return the ID of a node using his order and node order for the graph.
	 * 
	 * @param order
	 *            : The order of the node
	 * @param nodeOrder
	 *            : The node order (in the graph)
	 * @return the ID as string
	 */
	protected String getNodeNameForLevel(int level,
			List<RegulatoryNode> nodeOrder) {
		return ((RegulatoryNode) nodeOrder.get(level)).getId();
	}
}
