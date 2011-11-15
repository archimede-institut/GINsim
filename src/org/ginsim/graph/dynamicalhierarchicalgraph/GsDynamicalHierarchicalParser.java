package org.ginsim.graph.dynamicalhierarchicalgraph;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.tools.dynamicalhierarchicalsimplifier.NodeInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.GINsim.xml.GsXMLHelper;

public class GsDynamicalHierarchicalParser extends GsXMLHelper {
    
    private static final int POS_OUT = 0;
    private static final int POS_FILTERED = 50;
    private static final int POS_GRAPH_NOTES = 1;
    private static final int POS_GRAPH_NOTES_LINKLIST = 2;
    private static final int POS_VERTEX = 10;
    private static final int POS_VERTEX_VS = 11;
    private static final int POS_VERTEX_TYPE = 12;
    private static final int POS_VERTEX_TYPE_S = 13;
    private static final int POS_VERTEX_STATES = 14;
    private static final int POS_VERTEX_STATES_S = 15;
    private static final int POS_EDGE = 20;
    private static final int POS_EDGE_VS = 21;

    private int pos = POS_OUT;
    private GsDynamicalHierarchicalGraph graph;
    private int vslevel = 0;
    
    private GsDynamicalHierarchicalNode vertex = null;
    private GsVertexAttributesReader vareader = null;
    private GsEdgeAttributesReader ereader = null;
    private Annotation annotation = null;
    private Map map;
    
    /**
     * @param map
     * @param attributes
     * @param s_dtd
     * @param s_filename
     */
    public GsDynamicalHierarchicalParser(Map map, Attributes attributes, String s_dtd, String s_filename) throws GsException{
    	
    	this.graph = new GsDynamicalHierarchicalGraph( true);
    	this.map = map;
		vareader = graph.getVertexAttributeReader();
		ereader = graph.getEdgeAttributeReader();
		
		try {
			graph.setGraphName(attributes.getValue("id"));
		} catch (GsException e) {
			throw new GsException(GsException.GRAVITY_ERROR, "Invalid graph Name");
		}
		try {
			String[] t_nodeOrder = attributes.getValue("nodeorder").split(" ");
			Vector<NodeInfo> nodeOrder = new Vector<NodeInfo>(t_nodeOrder.length);
			byte[] childCount = new byte[t_nodeOrder.length];
			for (int i=0 ; i<t_nodeOrder.length ; i++) {
				String[] args = t_nodeOrder[i].split(":");
			    nodeOrder.add( new NodeInfo(args[0]));
			    childCount[i] = (byte) (Byte.parseByte(args[1])+1);
			}
			graph.setNodeOrder(nodeOrder);
			graph.setChildsCount(childCount);
		} catch (NumberFormatException e) {
			throw new GsException(GsException.GRAVITY_ERROR, "Invalid node order");
		}
	}

    /**
     * 
     */
    public GsDynamicalHierarchicalParser() {
    }

    /**
     * create a new GsReducedGraph from a file.
     * 
     * @param file the file to read.
     * @param map
     * @param graph the graph to fill with this data.
     */
    public void parse(File file, Map map, Graph graph) {
    	this.graph = (GsDynamicalHierarchicalGraph) graph;
    	this.map = map;
		vareader = graph.getVertexAttributeReader();
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
            case POS_VERTEX_TYPE_S:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_TYPE;
                    vertex.setTypeFromString(curval);
                	curval = null;
                }
                break; // POS_VERTEX_TYPE_S
            case POS_VERTEX_TYPE:
                if (qName.equals("attr")) {
                    pos = POS_VERTEX;
                }
                break; // POS_VERTEX_TYPE
            case POS_VERTEX_STATES_S:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_STATES;
                    vertex.parse(curval, graph.getChildsCount());
                	curval = null;
                }
                break; // POS_VERTEX_STATES_S
            case POS_VERTEX_STATES:
                if (qName.equals("attr")) {
                    pos = POS_VERTEX;
                }
                break; // POS_VERTEX_STATES
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
                    if (map == null || map.containsKey(id)) {
                        pos = POS_VERTEX;
                        vertex = new GsDynamicalHierarchicalNode(id);
                        graph.addVertex(vertex);
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("edge")) {
                    String s_from = attributes.getValue("from");
                    String s_to = attributes.getValue("to");
                    if (map == null || map.containsKey(s_from) && map.containsKey(s_to)) {
                        pos = POS_EDGE;
                        graph.addEdge(graph.getNodeById(s_from), graph.getNodeById(s_to));
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("graph")) {
            			if (!"dynamicalHierarchical".equals(attributes.getValue("class"))) {
            				throw new SAXException("not a dynamicalHierarchical graph");
            			}
            			try {
							graph.setGraphName(attributes.getValue("id"));
						} catch (GsException e) {
							throw new SAXException( new GsException(GsException.GRAVITY_ERROR, "invalidGraphName"));
						}
						try {
							String[] t_nodeOrder = attributes.getValue("nodeorder").split(" ");
							Vector<NodeInfo> nodeOrder = new Vector<NodeInfo>(t_nodeOrder.length);
							byte[] childCount = new byte[t_nodeOrder.length];
							for (int i=0 ; i<t_nodeOrder.length ; i++) {
								String[] args = t_nodeOrder[i].split(":");
							    nodeOrder.add( new NodeInfo(args[0]));
							    childCount[i] = (byte) (Byte.parseByte(args[1])+1);
							}
							graph.setNodeOrder(nodeOrder);
							graph.setChildsCount(childCount);
						} catch (NumberFormatException e) {
							throw new SAXException( new GsException(GsException.GRAVITY_ERROR, "invalid node order"));
						}
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
            	if (qName.equals("attr") && attributes.getValue("name").equals("type")) {
            		pos = POS_VERTEX_TYPE;
                } else if (qName.equals("attr") && attributes.getValue("name").equals("states")) {
                    pos = POS_VERTEX_STATES;
                } else if (vareader != null && qName.equals("nodevisualsetting")) {
                    pos = POS_VERTEX_VS;
                    vareader.setVertex(vertex);
                }
                break; // POS_VERTEX

            case POS_VERTEX_TYPE:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_TYPE_S;
                    curval = "";
                }
                break; // POS_VERTEX_TYPE
                
            case POS_VERTEX_STATES:
                if (qName.equals("string")) {
                    pos = POS_VERTEX_STATES_S;
                    curval = "";
                }
                break; // POS_VERTEX_STATES
                
            case POS_EDGE:
                if (qName.equals("edgevisualsetting")) {
                	pos = POS_EDGE_VS;
                }
                break; // POS_EDGE
                
            case POS_EDGE_VS:
            	GsGinmlHelper.applyEdgeVisualSettings(ereader, qName, attributes);
                break; // POS_EDGE_VS
            case POS_VERTEX_VS:
            	vslevel = GsGinmlHelper.applyNodeVisualSettings(vareader, qName, attributes);
                break; // POS_VERTEX_VS
        }
    }

    public Graph getGraph() {
        
    	return graph;
    }
}
