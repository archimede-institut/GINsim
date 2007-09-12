package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.ibdm.GINsim.data.GsAnnotation;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.jgraph.GsJgraphDirectedEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;
import fr.univmrs.ibdm.GINsim.xml.GsGinmlHelper;
import fr.univmrs.ibdm.GINsim.xml.GsXMLHelper;

/**
 * parses a ginml regulatory graph.
 */
public final class GsRegulatoryParser extends GsXMLHelper {

    private static final int POS_OUTSIDE = -1;  // outside of the graph (or in an ignored one)
    private static final int POS_OUT = 0;       // in the graph, outise of all vertices/edges
    private static final int POS_FILTERED = 50;
    private static final int POS_GRAPH_NOTES = 1;
    private static final int POS_GRAPH_NOTES_LINKLIST = 2;
    private static final int POS_VERTEX = 10;
    private static final int POS_VERTEX_VS = 11;
    private static final int POS_VERTEX_NOTES = 12;
    private static final int POS_VERTEX_NOTES_LINKLIST = 13;
    private static final int POS_EDGE = 20;
    private static final int POS_EDGE_VS = 21;
    private static final int POS_EDGE_NOTES = 22;
    private static final int POS_EDGE_NOTES_LINKLIST = 23;

    private int pos = POS_OUTSIDE;
    private GsRegulatoryGraph graph;

    private int vslevel = 0;

    private GsRegulatoryVertex vertex = null;
    private GsVertexAttributesReader vareader = null;
    private GsEdgeAttributesReader ereader = null;
    private GsRegulatoryEdge edge = null;
    private GsAnnotation annotation = null;
    private Map m_edges = new HashMap();
    private Vector v_waitingInteractions = new Vector();
    private String s_nodeOrder;
    private Map map;

    private Hashtable values;
    private String val = "";

    /** some more stuff to check consistency of "old" models (with explicit and free maxvalue) */
    Map m_checkMaxValue;
    
    /**
     */
    public GsRegulatoryParser() {
    }

    /**
     * @param map
     * @param attributes
     * @param s_dtd
     * @param s_filename
     * @throws SAXException
     */
    public GsRegulatoryParser(Map map, Attributes attributes, String s_dtd, String s_filename) throws SAXException {
        this.graph = new GsRegulatoryGraph(s_filename);
        graph.setDTD(s_dtd);
        this.map = map;
		s_nodeOrder = attributes.getValue("nodeorder");
        if (s_nodeOrder == null) {
            throw new SAXException("missing nodeOrder");
        }
		try {
			graph.setGraphName(attributes.getValue("id"));
		} catch (GsException e) {
			GsEnv.error(new GsException(GsException.GRAVITY_ERROR, "invalidGraphName"), null);
		}

		vareader = graph.getGraphManager().getVertexAttributesReader();
		ereader = graph.getGraphManager().getEdgeAttributesReader();
        pos = POS_OUT;
        values = new Hashtable();
    }

    /**
     * create a new regulatory graph from a file.
     *
     * @param file the file to read.
     * @param map
     * @param graph the graph to fill with this data.
     */
    public void parse(File file, Map map, GsGraph graph) {
    	this.graph = (GsRegulatoryGraph) graph;
    	this.map = map;
		vareader = graph.getGraphManager().getVertexAttributesReader();
		ereader = graph.getGraphManager().getEdgeAttributesReader();

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
			case POS_VERTEX_NOTES:
			    if (qName.equals("annotation")) {
			        pos = POS_VERTEX;
			    } else if (qName.equals("comment")) {
			        annotation.setComment(curval);
			        curval = null;
			    }
			    break; // POS_VERTEX_NOTES
			case POS_EDGE_NOTES:
			    if (qName.equals("annotation")) {
			        pos = POS_EDGE;
			    } else if (qName.equals("comment")) {
			        annotation.setComment(curval);
			        curval = null;
			    }
			    break; // POS_EDGE_NOTES
			case POS_VERTEX_NOTES_LINKLIST:
			    if (qName.equals("linklist")) {
			        pos = POS_VERTEX_NOTES;
			    }
			    break; // POS_VERTEX_NOTES_LINKLIST
			case POS_EDGE_NOTES_LINKLIST:
			    if (qName.equals("linklist")) {
			        pos = POS_EDGE_NOTES;
			    }
			    break; // POS_EDGE_NOTES_LINKLIST
			case POS_OUT:
				if (qName.equals("graph")) {
					placeInteractions();
					placeNodeOrder();
                    graph.setSaveMode(vslevel);

                    if (!values.isEmpty()) {
                    	parseBooleanFunctions();
                    }
				}
                pos = POS_OUTSIDE;
				break;
        }
        super.endElement(uri, localName, qName);
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {

        super.startElement(uri, localName, qName, attributes);

        switch(pos) {
            case POS_OUTSIDE:
                if (qName.equals("graph")) {
                    if (!"regulatory".equals(attributes.getValue("class"))) {
                        throw new SAXException("not a regulatory graph");
                    }
                    s_nodeOrder = attributes.getValue("nodeorder");
                    try {
                        graph.setGraphName(attributes.getValue("id"));
                        graph.setDTD(s_dtd);
                    } catch (GsException e) {
                        GsEnv.error(new GsException(GsException.GRAVITY_ERROR, "invalidGraphName"), null);
                    }
                }
                pos = POS_OUT;
                break;
        	case POS_OUT:
                if (qName.equals("node")) {
                    String id = attributes.getValue("id");
                    if (map == null || map.containsKey(id)) {
                        pos = POS_VERTEX;
                        try {
                            short basevalue = (short)Integer.parseInt(getAttributeValueWithDefault(attributes,"basevalue", "1"));
                            short maxvalue = (short)Integer.parseInt(attributes.getValue("maxvalue"));
                            String name = attributes.getValue("name");
                            vertex = graph.addNewVertex(id, name, basevalue, maxvalue);

                            values.put(vertex, new Hashtable());


                        } catch (NumberFormatException e) { throw new SAXException("malformed node's parameters"); }
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("edge")) {
                    String from = attributes.getValue("from");
                    String to = attributes.getValue("to");
                    if (map == null || map.containsKey(from) && map.containsKey(to)) {
                        pos = POS_EDGE;
                        try {
                            String id = attributes.getValue("id");
                            short minvalue = (short)Integer.parseInt(getAttributeValueWithDefault(attributes,"minvalue", "1"));
                            String smax = getAttributeValueWithDefault(attributes,"maxvalue", "-1");
                            short maxvalue = -2;
                            String sign = attributes.getValue("sign");
                            edge = graph.addNewEdge(from, to, minvalue, sign);
                            if (!smax.startsWith("(")) {
                            	maxvalue = (short)Integer.parseInt(smax);
                            	storeMaxValueForCheck(edge, maxvalue);
                            }
                            m_edges.put(id, edge);
                            ereader.setEdge(edge.me);
                        } catch (NumberFormatException e) { throw new SAXException("malformed interaction's parameters"); }
                    } else {
                        pos = POS_FILTERED;
                    }
                } else if (qName.equals("annotation")) {
	                	pos = POS_GRAPH_NOTES;
	                	annotation = graph.getAnnotation();
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
                    annotation.getLinkList().add(attributes.getValue("xlink:href"));
                }
                break; // POS_GRAPH_NOTES_LINKLIST

            case POS_VERTEX:
                if (vareader != null && qName.equals("nodevisualsetting")) {
                	pos = POS_VERTEX_VS;
                	vareader.setVertex(vertex);
                } else if (qName.equals("annotation")) {
                    pos = POS_VERTEX_NOTES;
                    annotation = vertex.getAnnotation();
                } else if (qName.equals("parameter")) {
                		v_waitingInteractions.add(vertex);
                		v_waitingInteractions.add(attributes.getValue("val"));
                		v_waitingInteractions.add(attributes.getValue("idActiveInteractions"));
                }


                else if (qName.equals("value")) {
                  val = attributes.getValue("val");
                  ((Hashtable)values.get(vertex)).put(val, new Vector());
                }
                else if (qName.equals("exp")) {
                  ((Vector)((Hashtable)values.get(vertex)).get(val)).addElement(attributes.getValue("str"));
                  //((Vector)((Hashtable)values.get(vertex)).get(val)).addElement(attributes.getValue("chk"));
                }
                else if (qName.equals("param")) {
                  ((Vector)((Hashtable)values.get(vertex)).get(val)).addElement("PARAM\t" + attributes.getValue("str"));
                }
                break; // POS_VERTEX

            case POS_EDGE:
                if (qName.equals("edgevisualsetting")) {
                	pos = POS_EDGE_VS;
                } else if (qName.equals("annotation")) {
                    pos = POS_EDGE_NOTES;
                    annotation = edge.me.getGsAnnotation(edge.index);
                }
                break; // POS_EDGE

            case POS_EDGE_VS:
            	GsGinmlHelper.applyEdgeVisualSettings(ereader, qName, attributes);
                break; // POS_EDGE_VS
            case POS_VERTEX_VS:
            	vslevel = GsGinmlHelper.applyNodeVisualSettings(vareader, qName, attributes);
                break; // POS_VERTEX_VS
            case POS_VERTEX_NOTES:
                if (qName.equals("linklist")) {
                    pos = POS_VERTEX_NOTES_LINKLIST;
			    } else if (qName.equals("comment")) {
			        curval = "";
                }
                break; // POS_VERTEX_NOTES
            case POS_EDGE_NOTES:
                if (qName.equals("linklist")) {
                    pos = POS_EDGE_NOTES_LINKLIST;
			    } else if (qName.equals("comment")) {
			        curval = "";
                }
                break; // POS_EDGE_NOTES
            case POS_VERTEX_NOTES_LINKLIST:
                if (qName.equals("link")) {
                    annotation.getLinkList().add(attributes.getValue("xlink:href"));
                }
                break; // POS_VERTEX_NOTES
            case POS_EDGE_NOTES_LINKLIST:
                if (qName.equals("link")) {
                    annotation.getLinkList().add(attributes.getValue("xlink:href"));
                }
                break; // POS_EDGE_NOTES
        }
    }

    private void storeMaxValueForCheck(GsRegulatoryEdge key, short maxvalue) {
    	if (m_checkMaxValue == null) {
    		m_checkMaxValue = new HashMap();
    	}
    	m_checkMaxValue.put(key, new Integer(maxvalue));
	}

	/**
     * use the contructed v_waitingInteraction to add the accurate interaction to the nodes.
     */
    private void placeInteractions() {
    	// check the maxvalues of all interactions first
    	if (m_checkMaxValue != null) {
        	Vector v = null;
    		Iterator it = m_checkMaxValue.entrySet().iterator();
    		while (it.hasNext()) {
    			Entry entry = (Entry)it.next();
    			if (((GsRegulatoryEdge)entry.getKey()).getMax() != ((Integer)entry.getValue()).intValue()) {
    				if (v == null) {
    					v = new Vector();
    				}
    				v.add(entry);
    			}
    		}
    		if (v != null) {
    			String s = "consistency problem in the model:";
    			for (int i=0 ; i<v.size() ; i++) {
    				Entry entry = (Entry)v.get(i);
    				int val = ((Integer)entry.getValue()).intValue();
    				s += "\nmaxvalue of "+entry.getKey()+" should be "+(val>0 ? ""+val : "max");
    			}
    			GsEnv.error(s, null);
    		}
    	}
    	
    	for (int i=0 ; i<v_waitingInteractions.size() ; i+=3) {
    		GsRegulatoryVertex vertex = (GsRegulatoryVertex)v_waitingInteractions.get(i);
    		GsLogicalParameter gsi = new GsLogicalParameter(Integer.parseInt( (String)v_waitingInteractions.get(i+1)), true);
    		String[] t_interaction = ((String) v_waitingInteractions.get(i+2)).split(" ");

    		for (int j=0 ; j<t_interaction.length ; j++) {
    			GsRegulatoryEdge e = (GsRegulatoryEdge) m_edges.get(t_interaction[j]);
    			if (e == null) {
    				// we have a problem
    			} else {
    				gsi.addEdge(e);
    			}
    		}
    		vertex.addLogicalParameter(gsi);
    	}
    }

    /**
     * install the correct nodeOrder in the graph: it should match the saved one.
     */
    private void placeNodeOrder() {
    		Vector v_order = new Vector();
    		String[] t_order = s_nodeOrder.split(" ");
    		boolean ok = true;
    		if (map == null) {
	    		for (int i=0 ; i<t_order.length ; i++) {
	    			GsRegulatoryVertex vertex = (GsRegulatoryVertex)graph.getGraphManager().getVertexByName(t_order[i]);
	    			if (vertex == null) {
	    				ok = false;
	    				break;
	    			}
	    			v_order.add(vertex);
	    		}
    		} else {
	    		for (int i=0 ; i<t_order.length ; i++) {
	    		    if (map.containsKey(t_order[i])) {
	    		        GsRegulatoryVertex vertex = (GsRegulatoryVertex)graph.getGraphManager().getVertexByName(t_order[i]);
	    		        if (vertex == null) {
	    		            ok = false;
	    		            break;
	    		        }
	    		        v_order.add(vertex);
	    		    }
	    		}
    		}
    		if (!ok || v_order.size() != graph.getGraphManager().getVertexCount()) {
    			// error
    			GsEnv.error("incoherent nodeOrder, not restoring it", null);
    		} else {
    			graph.setNodeOrder(v_order);
    		}
    }

    private void parseBooleanFunctions() {
      List allowedEdges;
      GsRegulatoryVertex vertex;
      String value, exp/*, chk*/;
      try {
        for (Enumeration enu_vertex = values.keys(); enu_vertex.hasMoreElements(); ) {
          vertex = (GsRegulatoryVertex)enu_vertex.nextElement();
          allowedEdges = graph.getGraphManager().getIncomingEdges(vertex);
          if (allowedEdges.size() > 0) {
            for (Enumeration enu_values = ((Hashtable)values.get(vertex)).keys(); enu_values.hasMoreElements(); ) {
              value = (String)enu_values.nextElement();
              for (Enumeration enu_exp = ((Vector)((Hashtable)values.get(vertex)).get(value)).elements(); enu_exp.hasMoreElements(); ) {
                exp = (String)enu_exp.nextElement();
                //chk = (String)enu_exp.nextElement();
                if (!exp.startsWith("PARAM")) {
					addExpression(Short.parseShort(value), vertex, exp/*, chk.length()*/);
				} else {
					addParam(Short.parseShort(value), vertex, exp.split("\t")[1]);
				}
              }
            }
            vertex.getInteractionsModel().parseFunctions();
            if (vertex.getMaxValue() - vertex.getBaseValue() + 1 == ((Hashtable)values.get(vertex)).size()) {
				((GsTreeElement)vertex.getInteractionsModel().getRoot()).setProperty("add", new Boolean(false));
				//for (Enumeration enu_values = ((Hashtable)values.get(vertex)).keys(); enu_values.hasMoreElements(); ) {
				//  value = (String)enu_values.nextElement();
				//  for (Enumeration enu_exp = ((Vector)((Hashtable)values.get(vertex)).get(value)).elements(); enu_exp.hasMoreElements(); ) {
				//    exp = (String)enu_exp.nextElement();
				    //chk = (String)enu_exp.nextElement();
				    //vertex.getInteractionsModel().checkParams(Short.parseShort(value), chk, exp);
				    //vertex.getInteractionsModel().parseFunctions();
				//  }
				//}
			}
          }
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    public void addExpression(short val, GsRegulatoryVertex vertex, String exp/*, int n*/) {
      try {
        GsBooleanParser tbp = new GsBooleanParser(graph.getGraphManager().getIncomingEdges(vertex));
        GsTreeInteractionsModel interactionList = vertex.getInteractionsModel();
        if (!tbp.compile(exp)) {
          graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "invalid formula",
            GsGraphNotificationMessage.NOTIFICATION_WARNING));
        }
        else {
          interactionList.addExpression(val, vertex, tbp);
          //String chk1 = "";
          //for (int i = 0; i < n; i++) chk1 += "1";
          //interactionList.checkParams(val, chk1, exp);
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    public void addParam(short val, GsRegulatoryVertex vertex, String par) throws Exception {
      GsTreeInteractionsModel interactionList = vertex.getInteractionsModel();
      List l = interactionList.getGraph().getGraphManager().getIncomingEdges(vertex);
      GsTreeParam param = interactionList.addEmptyParameter(val, vertex);
      String[] t_interaction = par.split(" ");
      Vector v = new Vector();
      String srcString, indexString;
      GsRegulatoryMultiEdge o;
      for (int i = 0; i < t_interaction.length; i++) {
        srcString = t_interaction[i].substring(0, t_interaction[i].lastIndexOf("_"));
        indexString = t_interaction[i].substring(t_interaction[i].lastIndexOf("_") + 1);
        for (int j = 0; j < l.size(); j++) {
          o = (GsRegulatoryMultiEdge)((GsJgraphDirectedEdge)l.get(j)).getUserObject();
          if (o.getSource().getId().equals(srcString)) {
        	  // FIXME: edge definition changed, consistency should be checked
            v.addElement(o.getEdge(Integer.parseInt(indexString)));
            break;
          }
        }
      }
      param.setEdgeIndexes(v);
    }
    public GsGraph getGraph() {
        return graph;
    }

    public String getFallBackDTD() {
        return GsGinmlHelper.LOCAL_URL_DTD_FILE;
    }
}
