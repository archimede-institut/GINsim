package org.ginsim.service.export.gna;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalFunctionBrowser;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ExportAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.gui.shell.GsFileFilter;
import org.mangosdk.spi.ProviderFor;


/**
 * Encode a graph to GNA format.
 * 
 *   TODO: extract a standalone service
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
public class GsGNAExport implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new GNAExportAction((RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
}

class GNAExportAction extends ExportAction<RegulatoryGraph> {

	private static final GsFileFilter ffilter = new GsFileFilter(new String[] {"gna"}, "GNA files");
	
	private FileWriter out = null;
	private GNAFunctionBrowser f_browser;

    public GNAExportAction(RegulatoryGraph graph) {
    	super(graph, "STR_GNA", "STR_GNA_descr");
    }

	@Override
	protected GsFileFilter getFileFilter() {
		return ffilter;
	}
    
	protected void doExport( String filename) throws GsException, IOException {
		long l = System.currentTimeMillis();

		this.out = new FileWriter(filename);
  		List nodeOrder = graph.getNodeOrder();
  		f_browser = new GNAFunctionBrowser(nodeOrder, out);
		Iterator it = nodeOrder.iterator();
		while (it.hasNext()) {
			RegulatoryVertex node = (RegulatoryVertex) it.next();
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
			OMDDNode mdd = node.getTreeParameters(graph).reduce();
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
		System.out.println("gna export: done in "+(System.currentTimeMillis()-l)+"ms");
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

	public void browse(OMDDNode node, String name) {
		this.nodeID = name;
		first = true;
		browse(node);
	}

	protected void leafReached(OMDDNode leaf) {
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
					String nodeName = ((RegulatoryVertex)nodeOrder.get(i)).getId();
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
