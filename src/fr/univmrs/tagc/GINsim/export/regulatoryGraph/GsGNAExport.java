package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * Encode a graph to GNA format.
 * 
 * TODO: revive the GNA export (waiting for new format)
 */
public class GsGNAExport extends GsAbstractExport {
	static transient Hashtable hash;

    public GsGNAExport() {
		id = "GNA";
		extension = ".gna";
		filter = new String[] { "gna" };
		filterDescr = "GNA files";
    }
    
	protected void doExport(GsExportConfig config) {
		List nodeOrder = config.getGraph().getNodeOrder();
		Iterator it = nodeOrder.iterator();
		try {
			FileWriter out = new FileWriter(config.getFilename());
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

	public GsPluggableActionDescriptor[] getT_action(int actionType, GsGraph graph) {
        if (graph instanceof GsRegulatoryGraph) {
        	return new GsPluggableActionDescriptor[] {
        			new GsPluggableActionDescriptor("STR_GNA", "STR_GNA_descr", null, this, ACTION_EXPORT, 0)
        	};
        }
        return null;
	}

	private static String getGNAEq(GsRegulatoryVertex node) {
		return "";
	}
}
