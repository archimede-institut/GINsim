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
			System.out.println("snakes export: done in "+(System.currentTimeMillis()-l)+"ms");
		} catch (IOException e) {
			e.printStackTrace();
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}		
	}

	protected synchronized void run() throws IOException {
		this.graph = (GsRegulatoryGraph) config.getGraph();
  		List nodeOrder = graph.getNodeOrder();
		Iterator it = nodeOrder.iterator();
		out = new FileWriter(config.getFilename());
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
			for (int i = 0; i < thresholdLevels; i++) {
				tmp.append("t_"+id+i);
				if (i < thresholdLevels-1) {
					tmp.append(", ");
				}
			}
			out.write(tmp.toString());
			
			//synthesis-parameters:
			out.write("\n  synthesis-parameters: ");
			tmp = new StringBuffer();
			for (int i = 1; i <= thresholdLevels; i++) {
				tmp.append("K_"+id+i);
				if (i < thresholdLevels-1) {
					tmp.append(", ");
				}
			}
			out.write(tmp.toString());
			out.write("\n");

			out.write("  degradation-parameters: g_" + id + "\n"
					+ "  state-equation:\n    d/dt " + id + " = "
					+ getGNAEq(node));
			
			//threshold-inequalities:
			out.write("threshold-inequalities: zero_"+id+" < ");
			tmp = new StringBuffer();
			for (int i = 1; i <= thresholdLevels; i++) {
				tmp.append("t_"+id+i+" < ");
				tmp.append("K_"+id+"1 / g_"+id+" < ");
			}
			out.write("max_"+id);
			out.write(tmp.toString());

		}
	}

	private String getGNAEq(GsRegulatoryVertex node) {
		OmddNode params = node.getTreeParameters(graph);
		//TODO : get equation
		return "";
	}
}
