package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.LogicalFunctionBrowser;

import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

/**
 * Encode a graph to GNA format.
 */
public class GsGNAExport extends GsAbstractExport {
	private GsExportConfig config = null;
	private FileWriter out = null;
	private GsRegulatoryGraph graph;
	private GNAFunctionBrowser f_browser;

    public GsGNAExport() {
		id = "GNA";
		extension = ".gna";
		filter = new String[] { "gna" };
		filterDescr = "GNA files";
    }
    
	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		
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
  		f_browser = new GNAFunctionBrowser(nodeOrder, out);
		Iterator it = nodeOrder.iterator();
		while (it.hasNext()) {
			GsRegulatoryVertex node = (GsRegulatoryVertex) it.next();
			int thresholdLevels = node.getMaxValue();
			String id = node.getId();
			// TODO: use "input-variable" instead of "state-variable" for "constant" variables
			// i.e. nodes that are only positively self-regulated
			
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
			OmddNode mdd = node.getTreeParameters(graph).reduce();
			if (mdd.next == null && mdd.value == 0) {
				out.write("k_"+id+"0, ");
			}
			for (int i = 1; i <= thresholdLevels; i++) {
				tmp.append("k_"+id+i);
				if (i < thresholdLevels) {
					tmp.append(", ");
				}
			}
			out.write(tmp.toString());
			out.write("\n");

			out.write("  degradation-parameters: g_" + id + "\n"
					+ "  state-equation:\n    d/dt " + id + " = ");
			if (mdd.next == null && mdd.value == 0) {
				out.write("k_"+id+"0");
				out.write(" - g_"+id+" * "+id+"\n");
				//threshold-inequalities:
				out.write("  parameter-inequalities: zero_"+id+" < k_"+id+"0 / g_"+id+"< ");
			} else {
				f_browser.browse(mdd, node.getId());
				out.write(" - g_"+id+" * "+id+"\n");
				//threshold-inequalities:
				out.write("  parameter-inequalities: zero_"+id+" < ");
			}
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
}

class GNAFunctionBrowser extends LogicalFunctionBrowser {
	OutputStreamWriter out;
	boolean first = true;
	String nodeID;

	public GNAFunctionBrowser(List nodeOrder, OutputStreamWriter out) {
		super(nodeOrder);
		this.out = out;
	}

	public void browse(OmddNode node, String name) {
		this.nodeID = name;
		first = true;
		browse(node);
	}

	protected void leafReached(OmddNode leaf) {
		if (leaf.value == 0) {
			return;
		}
		try {
			if (first) {
				first = false;
			} else {
				out.write(" + ");
			}
			out.write("k_"+nodeID+leaf.value);
			for (int i=0 ; i<path.length ; i++) {
				if (path[i][0] != -1) {
					String nodeName = ((GsRegulatoryVertex)nodeOrder.get(i)).getId();
					int begin = path[i][0];
					int end = path[i][1]+1;
					if (begin > 0) {
						out.write(" * s+("+nodeName+",t_"+nodeName+begin+")");
					}
					if (end != -1 && end <= path[i][2]) {
						out.write(" * s-("+nodeName+",t_"+nodeName+end+")");
					}
				}
			}
		} catch (IOException e) {
			// TODO: error!
		}
	}
}
