package org.ginsim.core.io.parser;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.List;

import org.ginsim.common.application.GsException;
import org.ginsim.common.utils.ColorPalette;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphAssociation;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.ViewHelper;

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
public abstract class GINMLWriter<G extends Graph<V,E>, V,E extends Edge<V>> {

	public static final String DEFAULT_URL_DTD_FILE = "http://gin.univ-mrs.fr/GINsim/GINML_2_2.dtd";
	
	protected final G graph;
	
	protected final NodeAttributesReader nReader;
	protected final EdgeAttributesReader eReader;
	
	public GINMLWriter(G graph) {
		this.graph = graph;
		this.nReader = graph.getNodeAttributeReader();
		this.eReader = graph.getEdgeAttributeReader();
	}

	public void write(OutputStreamWriter os, Collection<V> vertices, Collection<E> edges, int mode) throws IOException {
		XMLWriter out = new XMLWriter(os, DEFAULT_URL_DTD_FILE);

		out.openTag("gxl");
		out.addAttr("xmlns:xlink", "http://www.w3.org/1999/xlink");
		out.openTag("graph");
		out.addAttr("class", getGraphClassName());
		out.addAttr("id", graph.getGraphName());
		hook_graphAttribute(out);
		
		saveNodes(out, mode, vertices);
		saveEdges(out, mode, edges);
		
		Annotation annot = graph.getAnnotation();
		if (annot != null) {
			annot.toXML(out, null, 0);
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
	
	protected void saveNodes(XMLWriter out, int mode, Collection<V> nodes) throws IOException {
		for (V node: nodes) {
			out.openTag("node");
			hook_nodeAttribute(out, node);
			
			// save visual settings
	    	switch (mode) {
    		case 1:
    			nReader.setNode(node);
    			out.openTag("nodevisualsetting");
            	out.openTag("point");
                out.addAttr("x", ""+nReader.getX());
                out.addAttr("y", ""+nReader.getY());
                out.closeTag();
        		out.closeTag();
    			break;
    		case 2:
    			nReader.setNode(node);
    			out.openTag("nodevisualsetting");
    			if (nReader.getShape() == NodeShape.ELLIPSE) {
    	        	out.openTag("ellipse");
    	        } else {
    	        	out.openTag("rect");
    	        }
    	        out.addAttr("x",      ""+nReader.getX());
    	        out.addAttr("y",      ""+nReader.getY());
    	        out.addAttr("width",  ""+nReader.getWidth());
    	        out.addAttr("height", ""+nReader.getHeight());

    			Color bg = nReader.getBackgroundColor();
    			out.addAttr("backgroundColor", "#"+ColorPalette.getColorCode(bg));
    			Color fg = nReader.getForegroundColor();
    			out.addAttr("foregroundColor", "#"+ColorPalette.getColorCode(fg));
    			Color txt = nReader.getTextColor();
    			if (!txt.equals(fg)) {
    				out.addAttr("textColor", "#"+ColorPalette.getColorCode(txt));
    			}
    	        out.closeTag();
    			out.closeTag();
    			break;
			}
	    	
			out.closeTag();
		}
	}
	protected void saveEdges(XMLWriter out, int mode, Collection<E> edges) throws IOException {
		for (E edge: edges) {
			out.openTag("edge");
			hook_edgeAttribute(out, edge);
			
			// save visual settings
	    	switch (mode) {
    		case 2:
    			eReader.setEdge(edge);
    			out.openTag("edgevisualsetting");
    			out.openTag("polyline");
    			
    			
    	        String s = "";
    	        List l_point = ViewHelper.getPoints(nReader, eReader, edge);
    	        if (l_point != null) {
    	            for (int i=0 ; i<l_point.size() ; i++) {
    	                Point2D pt = (Point2D)l_point.get(i); 
    	                s += (int)pt.getX()+","+(int)pt.getY()+" ";
    	            }
    	            if (s.length() > 1) {
    	            	out.addAttr("points", s.substring(0, s.length()-1));
    	            } else {
    	            	out.addAttr("points", "");
    	            }
    	        } else {
	            	out.addAttr("points", "");
    	        }

    	        if (eReader.isCurve()) {
        	        out.addAttr("line_style", "curve");
    	        } else {
        	        out.addAttr("line_style", "straight");
    	        }
    	        
    	        out.addAttr("line_color", "#"+ColorPalette.getColorCode(eReader.getLineColor()));
    	        EdgePattern pattern = eReader.getDash();
    	        if (pattern == EdgePattern.DASH) {
    	            out.addAttr("pattern", "dash");
    	        }
    	        out.addAttr("line_width", ""+(int)eReader.getLineWidth());
    	        
    	        // FIXME: should we remove it completely or need a new system?
    	        out.addAttr("routage","auto");
    			
    			out.closeTag();
    			out.closeTag();
    			break;
			}

			out.closeTag();
		}
	}
	
	public void addAttributeTag(XMLWriter out, String name, int value) throws IOException {
		addAttributeTag(out, name, "int", ""+value);
	}
	
	public void addAttributeTag(XMLWriter out, String name, boolean value) throws IOException {
		addAttributeTag(out, name, "bool", ""+value);
	}
	
	public void addAttributeTag(XMLWriter out, String name, String value) throws IOException {
		addAttributeTag(out, name, "string", ""+value);
	}
	
	public void addAttributeTag(XMLWriter out, String name, Object value) throws IOException {
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
	
	public abstract String getGraphClassName();
	
	public void hook_graphAttribute(XMLWriter out) throws IOException {
		
	}
	
	public void hook_nodeAttribute(XMLWriter out, V node) throws IOException {
		out.addAttr("id", node.toString());
	}
	
	public void hook_edgeAttribute(XMLWriter out, E edge) throws IOException {
		V source = edge.getSource();
		V target = edge.getTarget();
		out.addAttr("id", edge.toString());
		out.addAttr("from", source.toString());
		out.addAttr("to", target.toString());
	}
}
