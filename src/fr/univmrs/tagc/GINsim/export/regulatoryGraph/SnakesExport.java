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
 * Export the logicials functions from regulatory graphs to python for use with the Snakes python library.
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
			long l = System.currentTimeMillis();
			run();
			System.out.println("snakes export: done in "+(System.currentTimeMillis()-l)+"ms");
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
		for (int node_i = 0; node_i < nodes.length; node_i++) {
			//generate the argument list from incoming edges : a, b, _a, _b
			List incomingEdges = graph.getGraphManager().getIncomingEdges((GsRegulatoryVertex)nodeOrder.get(node_i));
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
			exploreNode(nodes[node_i], null, node_i, "\t", nodeOrder);
			out.write("\t\treturn 0\n\n");
		}
		
		//End
		out.close(); //Close filewriter
	}
	
	/**
	 * Explore an OmddNode
	 * @param current : The node to explore
	 * @param parent : The parent of the node
	 * @param parent_value : The value we use in to come from the parent
	 * @param indent : A string containing a prefix of \t
	 * @param nodeOrder : The node order (in the graph)
	 * @throws IOException
	 */
	private void exploreNode(OmddNode current, OmddNode parent, int parent_value, String indent, List nodeOrder) throws IOException {
		if (current.next == null) {
			if (current.value == 0) {
				return;
			}
			out.write(indent+"if "+getVertexNameForLevel(parent.level, nodeOrder)+" == "+parent_value+":\n");
			out.write(indent+"\treturn "+current.value+"\n");
			return;
		}
		if (parent != null) {
			out.write(indent+"if "+getVertexNameForLevel(parent.level, nodeOrder)+" == "+parent_value+":\n");
		}
		for (int node_i = 0; node_i < current.next.length; node_i++) {
			exploreNode(current.next[node_i], current, node_i, indent+"\t", nodeOrder);
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
