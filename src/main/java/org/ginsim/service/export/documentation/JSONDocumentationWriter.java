package org.ginsim.service.export.documentation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.colomoto.common.task.AbstractTask;
import org.ginsim.common.application.LogManager;
import org.ginsim.common.document.XHTMLDocumentWriter;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.annotation.AnnotationLink;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.service.export.image.ImageExportService;

/**
 * Export the documentation of a model into an interactive web page.
 */
public class JSONDocumentationWriter extends AbstractTask {

	private final RegulatoryGraph graph;
    private final String export_name;
	
	public JSONDocumentationWriter(RegulatoryGraph graph, String export_name) {
		this.graph = graph;
        if (export_name.endsWith(".html")) {
            this.export_name = export_name.substring(0, export_name.length()-5);
        } else {
            this.export_name = export_name;
        }
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
	 * @throws IOException
	 */
	private void writeJSON(String filename) throws IOException {

		Writer f = new FileWriter(filename);
		
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

    private void inline(XMLWriter writer, String filename) throws IOException {
        InputStream stream = IOUtils.getStreamForPath(getClass().getPackage(), filename);
        StringBuffer sb = IOUtils.readFromResource(stream);
        writer.addContent("");
        writer.write(sb.toString());
    }

    private void writeHTMLContainer(String filename) throws IOException {
        XMLWriter writer = new XMLWriter(filename+".html");

        writer.openTag("html");
        writer.openTag("head");

        writer.openTag("style", new String[] {"media","screen", "type","text/css"});
        inline(writer, "style.css");
        writer.closeTag();

        writer.addTag("script", new String[] {"type", "text/javascript", "src", "http://code.jquery.com/jquery-1.11.0.min.js"}, "");
        writer.openTag("script", new String[]{"type", "text/javascript"});
        inline(writer, "GINsimDocumentation.js");
        writer.closeTag();
        writer.addTag("script", new String[]{"type", "text/javascript", "src", filename + ".js"}, "");

        writer.closeTag(); // head
         
        writer.openTag("body");

        writer.openTag("header");
        writer.addAttr("class", "banner");
        writer.addTagWithContent("h1", "Interactive documentation test");

        writer.openTag("nav");
        writer.openTag("a", new String[]{"href", "#graph", "onClick", "showGraph(); return false;"});
        writer.addTagWithContent("span", "Graph");
        writer.closeTag();

        writer.openTag("a", new String[] {"href", "#table", "onClick", "showTable(); return false;"});
        writer.addTagWithContent("span", "Table");
        writer.closeTag();
        writer.closeTag();  // nav
        writer.closeTag();  // header


        writer.openTag("div", new String[] {"id", "graphView"});
        writer.openTag("div", new String[] {"id", "infodiv"});
        writer.addContent("Loading...");
        writer.closeTag();

        writer.addTag("embed", new String[]{"id", "container", "type", "image/svg+xml", "src", filename + ".svg"});
        writer.addTag("div", new String[]{"id", "clearer"});
        writer.closeTag();  // main div

        writer.addTag("div", new String[]{"id", "tableView"});

        writer.openTag("div", new String[] {"id", "footer"});
        writer.addContent("Generated by ");
        writer.addTag("a", new String[] {"href", "http://www.ginsim.org"}, "GINsim");
        writer.closeTag();

        writer.close();
    }


    @Override
    public Object doGetResult() {
        try {
            ImageExportService service = GSServiceManager.getService(ImageExportService.class);
            service.exportSVG(graph, null, null, export_name+".svg");
            writeJSON(export_name + ".js");
            writeHTMLContainer(export_name);
        } catch (Exception e) {
            LogManager.error(e);
        }
        return null;
    }
}
