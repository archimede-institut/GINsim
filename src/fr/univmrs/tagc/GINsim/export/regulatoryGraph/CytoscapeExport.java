package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.export.GsAbstractExport;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.xml.XMLWriter;

public class CytoscapeExport extends GsAbstractExport {
	
	int EDGE_INHIBIT = 1;
	int EDGE_ACTIVATE = 2;
	int EDGE_UNDEFINED = 3;
	GsExportConfig config = null;
	GsRegulatoryMutants mlist = null;
	FileWriter fout = null;
	XMLWriter out = null;
	
	public CytoscapeExport() {
		id = "Cytoscape";
		extension = ".xgmml";
		filter = new String[] { "xgmml" };
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
		
		System.out.println(graph.getGraphName());
		
		//Header
		out.openTag("graph");
		out.addAttr("label", graph.getGraphName());
		out.addAttr("id", graph.getGraphName());
		out.addAttr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		out.addAttr("xmlns:dc", "http://purl.org/dc/elements/1.1/");
		out.addAttr("xmlns:xlink", "http://www.w3.org/1999/xlink");
		out.addAttr("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		out.addAttr("xmlns", "http://www.cs.rpi.edu/XGMML");
		
		out.openTag("att");
		out.addAttr("name", "documentVersion");
		out.addAttr("value", "1.0");
		out.closeTag();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		out.openTag("att");
		out.addAttr("name", "networkMetadata");
		out.openTag("rdf:RDF");
		out.openTag("rdf:Description");
		out.addAttr("rdf:about", "http://www.cytoscape.org/");
		out.addTagWithContent("dc:type", "Protein-Protein Interaction");
		out.addTagWithContent("dc:description", graph.getAnnotation().getHTMLComment());
		out.addTagWithContent("dc:identifier", "N/A");
		out.addTagWithContent("dc:date", simpleDateFormat.format(new Date()).toString());
		out.addTagWithContent("dc:title",  graph.getGraphName());
		out.addTagWithContent("dc:source", "http://www.cytoscape.org/");
		out.addTagWithContent("dc:format", "Cytoscape-XGMML");
		out.closeTag();//Description
		out.closeTag();//RDF
		out.closeTag();//Att
		
		out.openTag("att");
		out.addAttr("name", "backgroundColor");
		out.addAttr("value", "#ffffff");
		out.closeTag();
		
		out.openTag("att");
		out.addAttr("name", "GRAPH_VIEW_ZOOM");
		out.addAttr("label", "GRAPH_VIEW_ZOOM");
		out.addAttr("type", "real");
		out.addAttr("value", "1.0");
		out.closeTag();
		
		out.openTag("att");
		out.addAttr("name", "GRAPH_VIEW_CENTER_X");
		out.addAttr("label", "GRAPH_VIEW_CENTER_X");
		out.addAttr("type", "real");
		out.addAttr("value", "0.0");
		out.closeTag();
		
		out.openTag("att");
		out.addAttr("name", "GRAPH_VIEW_CENTER_Y");
		out.addAttr("label", "GRAPH_VIEW_CENTER_Y");
		out.addAttr("type", "real");
		out.addAttr("value", "0.0");
		out.closeTag();
		
		//vertex
		int current_index_of_node_id = -graph.getGraphManager().getVertexCount();
		Hashtable gs2cyt_Ids = new Hashtable(graph.getGraphManager().getVertexCount());
		for (Iterator it=graph.getGraphManager().getVertexIterator() ; it.hasNext() ;) {
			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
			String name = vertex.getName();
			if (name.length() == 0) name = vertex.getId();
			Integer node_id = new Integer(current_index_of_node_id++);
			gs2cyt_Ids.put(vertex.getId(), node_id);
			out.openTag("node");
			out.addAttr("label", name);
			out.addAttr("id", node_id.toString());
			
			out.openTag("att");
			out.addAttr("name", "hiddenLabel");
			out.addAttr("label", "hiddenLabel");
			out.addAttr("type", "string");
			out.closeTag();
			out.openTag("att");
			out.addAttr("name", "canonicalName");
			out.addAttr("label", "canonicalName");
			out.addAttr("type", "string");
			out.addAttr("value", name);
			out.closeTag();

			out.closeTag();
		}
		
		//edges
		for (Iterator it=graph.getGraphManager().getEdgeIterator() ; it.hasNext() ;) {
			GsRegulatoryMultiEdge edge = (GsRegulatoryMultiEdge)((GsDirectedEdge)it.next()).getUserObject();

			
			String source_id = ((GsRegulatoryVertex)edge.getTargetVertex()).getId();
			String target_id = ((GsRegulatoryVertex)edge.getSourceVertex()).getId();
			String edge_type;
			switch (edge.getSign()) {
				case GsRegulatoryMultiEdge.SIGN_NEGATIVE: 
					edge_type = "inhibit"; 
					break;
				case GsRegulatoryMultiEdge.SIGN_POSITIVE: 
					edge_type = "activate"; 
					break;
				default:
					edge_type = "undefined"; 
					break;
			}			
			String long_label = source_id+" ("+edge_type+") "+target_id;

			
			out.openTag("edge");
			out.addAttr("target", gs2cyt_Ids.get(source_id).toString());
			out.addAttr("source", gs2cyt_Ids.get(target_id).toString());
			out.addAttr("label", long_label);
			out.addAttr("id", long_label);
			
			out.openTag("att");
			out.addAttr("name", "interaction");
			out.addAttr("label", "interaction");
			out.addAttr("type", "string");
			out.addAttr("value", edge_type);
			out.closeTag();
			out.openTag("att");
			out.addAttr("name", "canonicalName");
			out.addAttr("label", "canonicalName");
			out.addAttr("type", "string");
			out.addAttr("value", long_label);
			out.closeTag();

			out.closeTag();
		}
		
		//End
		out.closeTag();//graph
		fout.close(); //Close filewriter
	}
}