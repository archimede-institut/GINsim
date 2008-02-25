package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;

import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.xml.XMLWriter;

public class CytoscapeExport extends GsAbstractExport {

	GsExportConfig config = null;
	GsRegulatoryMutants mlist = null;
	FileWriter fout = null;
	XMLWriter out = null;
	
	public CytoscapeExport() {
		id = "Cytoscape";
		extension = ".cytoscape";
		filter = new String[] { "cytoscape" };
		filterDescr = "Cytoscape files";
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType,
			GsGraph graph) {
		if (graph instanceof GsRegulatoryGraph) {
			return new GsPluggableActionDescriptor[] { new GsPluggableActionDescriptor(
					"STR_cytoscape", "STR_cytoscape_descr", null, this, ACTION_EXPORT, 0) };
		}
		return null;
	}

	protected void doExport(GsExportConfig config) {
		this.config = config;
		try {
			long l = System.currentTimeMillis();
			run();
			System.out.println("cytoscape export: done in "+(System.currentTimeMillis()-l)+"ms");
		} catch (IOException e) {
			e.printStackTrace();
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e), null);
		}
	}
	
	protected synchronized void run() throws IOException {
		GsRegulatoryGraph graph = (GsRegulatoryGraph) config.getGraph();
		fout = new FileWriter(config.getFilename());
		out = new XMLWriter(fout, null);

	}
}