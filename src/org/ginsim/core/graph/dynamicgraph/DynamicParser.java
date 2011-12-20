package org.ginsim.core.graph.dynamicgraph;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.io.parser.GinmlHelper;
import org.ginsim.core.io.parser.GsXMLHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * parses a ginml regulatory graph.
 */
public final class DynamicParser extends GsXMLHelper {
    
    private static final int POS_OUT = 0;
    private static final int POS_FILTERED = 50;
    private static final int POS_GRAPH_NOTES = 1;
    private static final int POS_GRAPH_NOTES_LINKLIST = 2;
    private static final int POS_VERTEX = 10;
    private static final int POS_VERTEX_VS = 11;
    private static final int POS_EDGE = 20;
    private static final int POS_EDGE_VS = 21;

    private int pos = POS_OUT;
    private DynamicGraph graph;
    private int vslevel = 0;
    
    private DynamicNode vertex = null;
    private Edge<?> edge = null;
    private NodeAttributesReader vareader = null;
    private EdgeAttributesReader ereader = null;
    private Annotation annotation = null;
    private Map map;

    /**
     */
    public DynamicParser() {
    }
    /**
     * @param map
     * @param attributes
     * @param s_dtd
     * @param s_filename
     */
    public DynamicParser(Map map, Attributes attributes, String s_dtd)throws GsException {
    	
        this.graph = GraphManager.getInstance().getNewGraph( DynamicGraph.class, true);
    	this.map = map;
		vareader = graph.getNodeAttributeReader();
		ereader = graph.getEdgeAttributeReader();
		
		try {
			graph.setGraphName(attributes.getValue("id"));
		} catch (GsException e) {
			throw new GsException(GsException.GRAVITY_ERROR, "invalidGraphName");
		}
		String[] t_nodeOrder = attributes.getValue("nodeorder").split(" ");
		Vector<NodeInfo> nodeOrder = new Vector<NodeInfo>(t_nodeOrder.length);
		for (int i=0 ; i<t_nodeOrder.length ; i++) {
		    nodeOrder.add( new NodeInfo( t_nodeOrder[i]));
		}
		graph.setNodeOrder(nodeOrder);

    }

    /**
     * create a new dynamical graph from a file.
     * 
     * @param file the file to read.
     * @param map "filter" to open only partially a graph
     * @param graph the graph to fill with this data.
     */
    public void parse(File file, Map map, Graph graph) {
    	
    	this.graph = (DynamicGraph) graph;
    	this.map = map;
		vareader = graph.getNodeAttributeReader();
		ereader = graph.getEdgeAttributeReader();

		startParsing(file);
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        
        switch (pos) {
			case POS_FILTERED:
			    if (qName.equals("node") || qName.equals("edge")) {
			        pos = POS_OUT;
			    }
			    break;
			case POS_VERTEX:
			    if (qName.equals("node")) {
			        pos = POS_OUT;
			    }
			    break; // POS_VERTEX
			case POS_GRAPH_NOTES:
			    if (qName.equals("annotation")) {
			    		pos = POS_OUT;
			    } else if (qName.equals("comment")) {
			        annotation.setComment(curval);
			        curval = null;
			    }
			    break; // POS_GRAPH_NOTES
			case POS_GRAPH_NOTES_LINKLIST:
			    if (qName.equals("linklist")) {
			        pos = POS_GRAPH_NOTES;
			    }
			    break; // POS_GRAPH_NOTES_LINKLIST
			case POS_VERTEX_VS:
			    if (qName.equals("nodevisualsetting")) {
			        pos = POS_VERTEX;
			    }
			    break; // POS_VERTEX_VS
			case POS_EDGE:
			    if (qName.equals("edge")) {
			        pos = POS_OUT;
			    }
			    break; // POS_EDGE
			case POS_EDGE_VS:
			    if (qName.equals("edgevisualsetting")) {
			        pos = POS_EDGE;
			    }
			    break; // POS_EDGE_VS
            case POS_OUT:
                if (qName.equals("graph")) {
                    graph.setSaveMode(vslevel);
                }
                break;
        }
        super.endElement(uri, localName, qName);
    }
    
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        switch(pos) {
        	case POS_OUT:
                if (qName.equals("node")) {
                    String id = attributes.getValue("id");
                    if (map == null || map.containsKey(id.substring(1))) {
	                    pos = POS_VERTEX;
	                    vertex = new DynamicNode(id);
	                    graph.addNode(vertex);
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("edge")) {
                    String s_from = attributes.getValue("from");
                    String s_to = attributes.getValue("to");
                    if (map == null || map.containsKey(s_from.substring(1)) && map.containsKey(s_to.substring(1))) {
                        pos = POS_EDGE;
                        DynamicNode from = new DynamicNode(s_from);
                        DynamicNode to = new DynamicNode(s_to);
                        int c = 0;
                        for ( int i=0 ; i<from.state.length ; i++) {
                        	if (from.state[i] != to.state[i]) {
                        		c++;
                        	}
                        }
                        edge = graph.addEdge(from, to, c>1);
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("graph")) {
            			if (!"dynamical".equals(attributes.getValue("class"))) {
            				throw new SAXException( new GsException( GsException.GRAVITY_ERROR, "STR_DG_NotDynamicalGraph"));
            			}
            			try {
							graph.setGraphName(attributes.getValue("id"));
						} catch (GsException e) {
							throw new SAXException( new GsException(GsException.GRAVITY_ERROR, "STR_InvalidGraphName"));
						}
            			String[] t_nodeOrder = attributes.getValue("nodeorder").split(" ");
            			Vector<NodeInfo> nodeOrder = new Vector<NodeInfo>(t_nodeOrder.length);
            			for (int i=0 ; i<t_nodeOrder.length ; i++) {
            			    nodeOrder.add( new NodeInfo( t_nodeOrder[i]));
            			}
            			graph.setNodeOrder(nodeOrder);
                } else if (qName.equals("annotation")) {
                    pos = POS_GRAPH_NOTES;
                    annotation = graph.getAnnotation();
                } else if (qName.equals("link")) {
                    graph.setAssociatedGraphID(attributes.getValue("xlink:href"));
                }
                break; // POS_OUT
            case POS_GRAPH_NOTES:
                if (qName.equals("linklist")) {
                    pos = POS_GRAPH_NOTES_LINKLIST;
			    } else if (qName.equals("comment")) {
			        curval = "";
			    }
            		break; // POS_GRAPH_NOTES
            case POS_GRAPH_NOTES_LINKLIST:
                if (qName.equals("link")) {
                    annotation.addLink(attributes.getValue("xlink:href"), graph);
                }
                break; // POS_GRAPH_NOTES_LINKLIST

            case POS_VERTEX:
                if (vareader != null && qName.equals("nodevisualsetting")) {
                	pos = POS_VERTEX_VS;
                	vareader.setNode(vertex);
                }
                break; // POS_VERTEX
                
            case POS_EDGE:
                if (ereader != null && qName.equals("edgevisualsetting")) {
                	pos = POS_EDGE_VS;
                    ereader.setEdge(edge);
                }
                break; // POS_EDGE
                
            case POS_EDGE_VS:
            	GinmlHelper.applyEdgeVisualSettings(edge, ereader, vareader, qName, attributes);
                break; // POS_EDGE_VS
            case POS_VERTEX_VS:
            	vslevel = GinmlHelper.applyNodeVisualSettings(vareader, qName, attributes);
                break; // POS_VERTEX_VS
        }
        
    }

    public Graph getGraph() {
    	
        return graph;
    }
}
