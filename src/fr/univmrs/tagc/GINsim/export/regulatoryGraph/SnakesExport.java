package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;

import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;


/**
 * Export the logical functions from regulatory graphs to python for use with the Snakes python library.
 * http://lacl.univ-paris12.fr/pommereau/soft/snakes/
 * 
 *
 */
public class SnakesExport extends GsAbstractExport  {

	GsExportConfig config = null;
	FileWriter out = null;

	public SnakesExport() {
		id = "Logical function to snakes";
		extension = ".py";
		filter = new String[] { "py" };
		filterDescr = "Python files";
	}
	
	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		if (graph instanceof GsRegulatoryGraph) {
			return new GsPluggableActionDescriptor[] { new GsPluggableActionDescriptor(
					"STR_snakes", "STR_snakes_descr", null, this, ACTION_EXPORT, 0) };
		}
		return null;
	}
	
	protected void doExport(GsExportConfig config) {
		this.config = config;
		try {
			run();
		} catch (IOException e) {
			e.printStackTrace();
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}		
	}

	protected synchronized void run() throws IOException {
		out = new FileWriter(config.getFilename());
		
		//data
		GsRegulatoryGraph graph = (GsRegulatoryGraph) config.getGraph();
		List nodeOrder = graph.getNodeOrder();
		OmddNode[] nodes = graph.getAllTrees(true);
		
		out.write("class Toy(Module):\n");
		int [][] parcours = new int[nodeOrder.size()][4];
		for (int node_i = 0; node_i < nodes.length; node_i++) {
			//generate the argument list from incoming edges : a, b, _a, _b
			GsRegulatoryVertex current_node = (GsRegulatoryVertex) nodeOrder.get(node_i);
			Collection<GsRegulatoryMultiEdge> incomingEdges = graph.getIncomingEdges(current_node);
			String current_node_name = getVertexNameForLevel(node_i, nodeOrder);
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
			GsRegulatoryMultiEdge edge;
			GsRegulatoryVertex source;
			Iterator<GsRegulatoryMultiEdge> it = incomingEdges.iterator();
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
			exploreNode(parcours, 0 ,nodes[node_i], nodeOrder);
			if (nodes[node_i].next != null) {//if it's not a leaf
				out.write("        return 0\n\n");
			} else {
				out.write("\n");
			}
		}
		
		//End
		out.close(); //Close filewriter
	}
	
	protected void exploreNode(int[][] parcours, int deep, OmddNode node, List nodeOrder) throws IOException {
		if (node.next == null) {
			if (node.value > 0) {
				String nodeName;
				String indent = "        ";
				boolean and;
				for (int i = 0; i < deep; i++, indent+="    ") {
					nodeName = getVertexNameForLevel(parcours[i][2], nodeOrder);//level
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
		OmddNode currentChild;
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
			exploreNode(parcours, deep+1, node.next[begin], nodeOrder);
		}
	}

	/**
	 * Return the ID of a node using his order and node order for the graph.
	 * @param order : The order of the node
	 * @param nodeOrder : The node order (in the graph)
	 * @return the ID as string
	 */
	private String getVertexNameForLevel(int order, List nodeOrder) {
		return ((GsRegulatoryVertex) nodeOrder.get(order)).getId();
	}
	

}
