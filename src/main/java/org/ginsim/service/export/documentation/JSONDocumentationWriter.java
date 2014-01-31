package org.ginsim.service.export.documentation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.annotation.AnnotationLink;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.service.export.image.ImageExportService;

/**
 * Export the documentation of a model into an interactive web page.
 */
public class JSONDocumentationWriter {

	private final RegulatoryGraph graph;
	
	public JSONDocumentationWriter(RegulatoryGraph graph) {
		this.graph = graph;
	}
	
	private void addAnnotation(Map<String, Object> info, Annotation note) {
		String comment = note.getComment();
		if (comment != null && comment.length() > 0) {
			comment = comment.replace("\n", "<br>");
			comment = comment.replace("\"", "\\\"");
			info.put("comment", comment);
		}
		
		List<AnnotationLink> links = note.getLinkList();
		if (links != null && links.size() > 0) {
			List<String[]> lks = new ArrayList<String[]>();
			for (AnnotationLink l: links) {
			 	String link = l.getLink();
			 	lks.add( new String[] {l.toString(), link} );
			}
			info.put("links", lks);
		}
	}

	private void writeJSON(Writer f, Object o) throws IOException {
		if (o instanceof List) {
			f.write('[');
			for (Object v: (List)o) {
				writeJSON(f, v);
				f.write(", ");
			}
			f.write(']');
			return;
		}
	
		if (o.getClass().isArray() ) {
			f.write('[');
			for (Object v: (Object[])o) {
				writeJSON(f, v);
				f.write(", ");
			}
			f.write(']');
			return;
		}
	
		if (o instanceof Map ) {
			Map<String, Object> info = (Map<String, Object>)o;
			f.write('{');
			for (String k: info.keySet()) {
				f.write("\""+k+"\": ");
				writeJSON(f, info.get(k));
				f.write(", ");
			}
			f.write("}");
			return;
		}

		f.write("\""+o+"\"");
	}



	private Map<String, Object> getNodeInfo(RegulatoryNode n) {
		Map<String, Object> info = new HashMap<String, Object>();
		String name = n.getName();
		info.put("max", n.getMaxValue());
		if (n.isInput()) {
			info.put("input", "true");
		}
		
		if (name != null && name.length() > 0) {
			info.put("name", name);
		}
		addAnnotation(info, n.getAnnotation());
		return info;
	}

	private Map<String, Object> getEdgeInfo(RegulatoryMultiEdge e) {
		
		String src = e.getSource().getId();
		String tgt = e.getTarget().getId();
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("from", src);
		info.put("to", tgt);
		addAnnotation(info, e.getAnnotation());
		return info;
	}


	/**
	 * Export a model as documentation data (SVG+JSON).
	 * 
	 * @param export_name
	 * @throws IOException
	 */
	public void exportDocumentation(String export_name) throws IOException {
		ImageExportService service = ServiceManager.getManager().getService(ImageExportService.class);
		service.exportSVG(graph, null, null, export_name+".svg");
	
		Writer f = new FileWriter(export_name+".js");
		
		f.write("model = ");
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("name", graph.getGraphName());
		addAnnotation(info, graph.getAnnotation());
		writeJSON(f, info);
		f.write("\n\n");
	
		f.write("nodes = {\n");
		for (RegulatoryNode n: graph.getNodes()) {
			info = getNodeInfo(n);
			f.write("  "+n.getId()+":");
			writeJSON(f, info);
			f.write(",\n");
		}
		f.write("}\n");
	
	
		f.write("edges = {\n");
		for (RegulatoryMultiEdge e: graph.getEdges()) {
			info = getEdgeInfo(e);
			f.write("  \""+info.get("from")+"_"+info.get("to")+"\": ");
			writeJSON(f, info);
			f.write(",\n");
		}
		f.write("}\n\n");
		f.close();
	}

}
		