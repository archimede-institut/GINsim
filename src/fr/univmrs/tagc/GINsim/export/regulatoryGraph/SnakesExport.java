package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.common.GsException;


/**
 * Export the logical functions from regulatory graphs to python for use with the Snakes python library.
 * @see http://lacl.univ-paris12.fr/pommereau/soft/snakes/
 * 
 * @author Berenguier Duncan
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
	
	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
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
		
		out.write("class "+graph.getGraphName()+" (Module):\n");
		int [][] parcours = new int[nodeOrder.size()][4];
		for (int node_i = 0; node_i < nodes.length; node_i++) {
			//generate the argument list from incoming edges : a, b, _a, _b
			List incomingEdges = graph.getGraphManager().getIncomingEdges(nodeOrder.get(node_i));
			StringBuffer s = new StringBuffer();
			Iterator it = incomingEdges.iterator();
			while (it.hasNext()) {
				GsDirectedEdge edge = (GsDirectedEdge)it.next();
				GsRegulatoryVertex source = (GsRegulatoryVertex) edge.getSourceVertex();
				s.append(source.getId());
				s.append(", ");
			}
			String arguments;
			if (s.length() == 0) {
				arguments = "";
			} else {
				arguments = s.substring(0, s.length()-2);
			}
			
			out.write("\tdef update_"+getVertexNameForLevel(node_i, nodeOrder)+"("+arguments+"):\n");
			//exploreNode(nodes[node_i], null, node_i, "\t", nodeOrder);
			exploreNode(parcours, 0 ,nodes[node_i], nodeOrder);
			if (nodes[node_i].next != null) {//if it's not a leaf
				out.write("\t\treturn 0\n\n");
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
				String indent = "\t\t";
				boolean and;
				for (int i = 0; i < deep; i++, indent+="\t") {
					nodeName = getVertexNameForLevel(parcours[i][2], nodeOrder);//level
					out.write(indent+"if ");
					and = false;
					if (parcours[i][0] > 0) {
						out.write(nodeName+" >= "+parcours[i][0]);
						and = true;
					}
					if (parcours[i][1] < parcours[i][3]) {
						if (and) {
							out.write(" and ");
						}
						out.write(nodeName+" < "+parcours[i][1]);
					}
					out.write(":\n");
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
