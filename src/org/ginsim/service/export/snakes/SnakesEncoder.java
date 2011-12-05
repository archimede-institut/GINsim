package org.ginsim.service.export.snakes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;



/**
 * Export the logical functions from regulatory graphs to python for use with the Snakes python library.
 * http://lacl.univ-paris12.fr/pommereau/soft/snakes/
 */
public class SnakesEncoder {

	/**
	 * Encode the RegulatoryGraph graph into a file named filename
	 * 
	 * @param graph the RegulatoryGraph to encode
	 * @param filename the name of the snakes file
	 * @throws GsException
	 * @throws IOException
	 */
	public void encode(RegulatoryGraph graph, String filename) throws GsException, IOException {
		FileWriter out = new FileWriter(filename);
		
		//data
		List<RegulatoryNode> nodeOrder = graph.getNodeOrder();
		OMDDNode[] nodes = graph.getAllTrees(true);
		
		out.write("class Toy(Module):\n");
		int [][] parcours = new int[nodeOrder.size()][4];
		for (int node_i = 0; node_i < nodes.length; node_i++) {
			//generate the argument list from incoming edges : a, b, _a, _b
			RegulatoryNode current_node = nodeOrder.get(node_i);
			Collection<RegulatoryMultiEdge> incomingEdges = graph.getIncomingEdges(current_node);
			String current_node_name = getNodeNameForLevel(node_i, nodeOrder);
			if (incomingEdges.size() == 0) {
				out.write("    # specification of component \""+current_node_name+"\"\n");
				out.write("    range_"+current_node_name+"=(0, "+current_node.getMaxValue()+")\n");
				if (current_node.isInput()) {
					out.write("    def update_"+current_node_name+"(self, "+current_node_name+"):\n");
					out.write("        return "+current_node_name+"\n");	
				} else {
					out.write("    def update_"+current_node_name+"(self, "+current_node_name+"):\n");
					out.write("        return "+nodes[node_i].value+"\n");	
				}
				continue;
			}
			
			StringBuffer s = new StringBuffer();
			RegulatoryMultiEdge edge;
			RegulatoryNode source;
			Iterator<RegulatoryMultiEdge> it = incomingEdges.iterator();
			while (true) {
				edge = it.next();
				source = edge.getSource();
				s.append(source.getId());
				if (it.hasNext()) {
					s.append(", ");
				} else {
					break;
				}
			}				

			
			out.write("    # specification of component \""+current_node_name+"\"\n");
			out.write("    range_"+current_node_name+"=(0, "+current_node.getMaxValue()+")\n");
			out.write("    def update_"+current_node_name+"(self, "+s+"):\n");
			exploreNode(parcours, 0 ,nodes[node_i], nodeOrder, out);
			if (nodes[node_i].next != null) {//if it's not a leaf
				out.write("        return 0\n\n");
			} else {
				out.write("\n");
			}
		}
		
		//End
		out.close(); //Close filewriter
	}
	
	protected void exploreNode(int[][] parcours, int deep, OMDDNode node, List<RegulatoryNode> nodeOrder, FileWriter out) throws IOException {
		if (node.next == null) {
			if (node.value > 0) {
				String nodeName;
				String indent = "        ";
				boolean and;
				for (int i = 0; i < deep; i++, indent+="    ") {
					nodeName = getNodeNameForLevel(parcours[i][2], nodeOrder);//level
					out.write(indent+"if ");
					and = false;
					if (parcours[i][0] > 0) {
						out.write(nodeName+" >= "+parcours[i][0]+":\n");
						and = true;
					}
					if (parcours[i][1] < parcours[i][3]) {
						if (and) {
							out.write(" and ");
						}
						out.write(nodeName+" < "+parcours[i][1]+":\n");
					}
				}
				out.write(indent+"return "+node.value+"\n");
			}
			return ;
		}
		OMDDNode currentChild;
		for (int i = 0 ; i < node.next.length ; i++) {
			currentChild = node.next[i];
			int begin = i;
			int end;
			for (end=i+1 ; end < node.next.length && currentChild == node.next[end]; end++, i++) {
				// nothing to do
			}
			parcours[deep][0] = begin;
			parcours[deep][1] = end;
			parcours[deep][2] = node.level;
			parcours[deep][3] = node.next.length;
			exploreNode(parcours, deep+1, node.next[begin], nodeOrder, out);
		}
	}

	/**
	 * Return the ID of a node using his order and node order for the graph.
	 * @param order : The order of the node
	 * @param nodeOrder : The node order (in the graph)
	 * @return the ID as string
	 */
	private String getNodeNameForLevel(int order, List<RegulatoryNode> nodeOrder) {
		return nodeOrder.get(order).getId();
	}
}
