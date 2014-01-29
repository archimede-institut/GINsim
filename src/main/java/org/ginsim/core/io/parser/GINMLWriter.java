package org.ginsim.core.io.parser;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;

import org.ginsim.common.application.GsException;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.GraphAssociation;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.style.StyleManager;

/**
 * Base GINMLWriter class: it provides the common parts of a GINML file and hooks
 * for specialised writers to add their graph-specific data.
 * 
 * @author Aurelien Naldi
 *
 * @param <G>
 * @param <V>
 * @param <E>
 */
public class GINMLWriter<G extends Graph<V,E>, V,E extends Edge<V>> {

	protected final G graph;
	protected final String graphClassName;
	
	protected final StyleManager<V, E> styleManager; 
	protected final NodeAttributesReader nReader;
	protected final EdgeAttributesReader eReader;
	
	/**
	 * Create a GINML writer.
	 * 
	 * @param graph
	 */
	public GINMLWriter(G graph, String graphClassName) {
		this.graph = graph;
		this.graphClassName = graphClassName;
		this.styleManager = graph.getStyleManager();
		this.nReader = graph.getNodeAttributeReader();
		this.eReader = graph.getEdgeAttributeReader();
	}

	public void write(OutputStreamWriter os, Collection<V> vertices, Collection<E> edges) throws IOException {
		XMLWriter out = new XMLWriter(os, GinmlHelper.DEFAULT_URL_DTD_FILE);

		out.openTag("gxl");
		out.addAttr("xmlns:xlink", "http://www.w3.org/1999/xlink");
		out.openTag("graph");
		out.addAttr("class", graphClassName);
		out.addAttr("id", graph.getGraphName());
		
		hook_graphAttribute(out);
		
		styleManager.styles2ginml(out);
		
		saveNodes(out, vertices);
		saveEdges(out, edges);
		
		Annotation annot = graph.getAnnotation();
		if (annot != null) {
			annot.toXML(out);
		}

		// handle associated graphs!
		if (graph instanceof GraphAssociation) {
			GraphAssociation ga = (GraphAssociation)graph;
			try {
				String associatedID = ga.getAssociatedGraphID();
	            if (associatedID != null) {
	                out.openTag("link");
	                out.addAttr("xlink:href", associatedID);
	                out.closeTag();
	            }
			} catch (GsException e) {
			}
		}
		
		out.closeTag(); // graph
		out.closeTag(); // gxl
	}
	
	protected void saveNodes(XMLWriter out, Collection<V> nodes) throws IOException {
		for (V node: nodes) {
			out.openTag("node");
			hook_nodeAttribute(out, node);
			
			// save visual settings
			nReader.setNode(node);
			nReader.writeGINML(out);

			out.closeTag();
		}
	}
	protected void saveEdges(XMLWriter out, Collection<E> edges) throws IOException {
		for (E edge: edges) {
			out.openTag("edge");
			hook_edgeAttribute(out, edge);
			
			// save visual settings
			eReader.setEdge(edge);
			eReader.writeGINML(out);
			
			out.closeTag();
		}
	}
	
	protected void addAttributeTag(XMLWriter out, String name, int value) throws IOException {
		addAttributeTag(out, name, "int", ""+value);
	}
	
	protected void addAttributeTag(XMLWriter out, String name, boolean value) throws IOException {
		addAttributeTag(out, name, "bool", ""+value);
	}
	
	protected void addAttributeTag(XMLWriter out, String name, String value) throws IOException {
		addAttributeTag(out, name, "string", ""+value);
	}
	
	protected void addAttributeTag(XMLWriter out, String name, Object value) throws IOException {
		if (value instanceof Integer) {
			addAttributeTag(out, name, "int", ""+value);
		} else if (value instanceof Boolean) {
			addAttributeTag(out, name, "bool", ""+value);
		} else if (value instanceof Float) {
			addAttributeTag(out, name, "float", ""+value);
		} else {
			addAttributeTag(out, name, "string", ""+value);
		}
	}
	
	private void addAttributeTag(XMLWriter out, String name, String type, String value) throws IOException {
		out.openTag("attr");
		out.addAttr("name", name);
		out.openTag(type);
		out.addContent(value);
		out.closeTag();
		out.closeTag();
	}
	
	protected void hook_graphAttribute(XMLWriter out) throws IOException {
		
	}
	
	protected void hook_nodeAttribute(XMLWriter out, V node) throws IOException {
		out.addAttr("id", node.toString());
	}
	
	protected void hook_edgeAttribute(XMLWriter out, E edge) throws IOException {
		V source = edge.getSource();
		V target = edge.getTarget();
		out.addAttr("id", edge.toString());
		out.addAttr("from", source.toString());
		out.addAttr("to", target.toString());
	}
	
	protected String stringNodeOrder(Collection nodeOrder) {
		String s = "";
		for (Object o: nodeOrder) {
			s += o+" ";
		}
		if (s.length() > 0) {
			return s.substring(0, s.length()-1);
		}
		return s.trim();
	}

}
