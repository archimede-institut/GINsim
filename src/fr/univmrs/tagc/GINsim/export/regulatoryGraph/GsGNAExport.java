package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
 * Encode a graph to GNA format.
 * 
 * 
 */
public class GsGNAExport extends GsAbstractExport {
	private GsExportConfig config = null;
	private FileWriter out = null;
	private GsRegulatoryGraph graph;

    public GsGNAExport() {
		id = "GNA";
		extension = ".gna";
		filter = new String[] { "gna" };
		filterDescr = "GNA files";
    }
    
	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (graph instanceof GsRegulatoryGraph) {
        	return new GsPluggableActionDescriptor[] {
        			new GsPluggableActionDescriptor("STR_GNA", "STR_GNA_descr", null, this, ACTION_EXPORT, 0)
        	};
        }
        return null;
	}

	protected void doExport(GsExportConfig config) {
		this.config = config;
		try {
			long l = System.currentTimeMillis();
			run();
			System.out.println("gna export: done in "+(System.currentTimeMillis()-l)+"ms");
		} catch (IOException e) {
			e.printStackTrace();
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}		
	}

	protected synchronized void run() throws IOException {
		this.graph = (GsRegulatoryGraph) config.getGraph();
		this.out = new FileWriter(config.getFilename());
  		List nodeOrder = graph.getNodeOrder();
		Iterator it = nodeOrder.iterator();
		while (it.hasNext()) {
			GsRegulatoryVertex node = (GsRegulatoryVertex) it.next();
			int thresholdLevels = node.getMaxValue();
			String id = node.getId();
			
			out.write("state-variable: " + id + "\n"
					+ "  zero-parameter: zero_" + id + "\n"
					+ "  box-parameter: max_" + id + "\n"
					+ "  threshold-parameters: ");
			
			//threshold-parameters: 
			StringBuffer tmp = new StringBuffer();
			for (int i = 1; i <= thresholdLevels; i++) {
				tmp.append("t_"+id+i);
				if (i < thresholdLevels) {
					tmp.append(", ");
				}
			}
			out.write(tmp.toString());
			
			//synthesis-parameters:
			out.write("\n  production-parameters: ");
			tmp = new StringBuffer();
			for (int i = 1; i <= thresholdLevels; i++) {
				tmp.append("k_"+id+i);
				if (i < thresholdLevels) {
					tmp.append(", ");
				}
			}
			out.write(tmp.toString());
			out.write("\n");

			out.write("  degradation-parameters: g_" + id + "\n"
					+ "  state-equation:\n    d/dt " + id + " = "
					+ getGNAEq(node, nodeOrder)
					+ " - g_"+id+" * "+id+"\n");
			
			//threshold-inequalities:
			out.write("  threshold-inequalities: zero_"+id+" < ");
			tmp = new StringBuffer();
			for (int i = 1; i <= thresholdLevels; i++) {
				tmp.append("t_"+id+i+" < ");
				tmp.append("k_"+id+i+" / g_"+id+" < ");
			}
			out.write(tmp.toString());
			out.write("max_"+id+"\n\n");
		}
		out.close();
	}

	private String getGNAEq(GsRegulatoryVertex node, List nodeOrder) {
		String s = exploreNode(node.getId(), node.getTreeParameters(graph).reduce(), nodeOrder);
		return s.substring(0, s.length()-3);
	}

	private String exploreNode(String topNodeId, OmddNode node, List nodeOrder) {
		if (node.next == null) {
			return "k_"+topNodeId+node.value+" + ";
		} else {
			String res = "";
			String nodeName = getVertexNameForLevel(node.level, nodeOrder);
			OmddNode currentChild;
			for (int i = 0; i < node.next.length; i++) {
				currentChild = node.next[i];
				int begin = i;
				int end = i+1;
				for (end=i+1 ; end < node.next.length && currentChild == node.next[end]; end++, i++);
				if (begin > 0) {
					res += "s+("+nodeName+", t_"+nodeName+begin+")";
				}
				if (end < node.next.length) {
					if (res.length() > 0) {
						res += " * ";
					}
					res += "s-("+nodeName+", t_"+nodeName+end+")";
				}
				res += " * "+exploreNode(topNodeId, node.next[begin], nodeOrder);
			}
			return res;
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
