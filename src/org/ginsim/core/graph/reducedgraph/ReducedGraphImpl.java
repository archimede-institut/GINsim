package org.ginsim.core.graph.reducedgraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.AbstractDerivedGraph;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.io.parser.GinmlHelper;


public class ReducedGraphImpl<G extends Graph<V,E>, V, E extends Edge<V>>  extends AbstractDerivedGraph<NodeReducedData, Edge<NodeReducedData>, G,V,E>
	implements ReducedGraph<G,V,E>{

	public static final String GRAPH_ZIP_NAME = "connectedComponent.ginml";
	
	/**
	 * @param parent
	 */
	public ReducedGraphImpl( G parent) {
		
	    this( false);
    	setAssociatedGraph( parent);
	}

	/**
	 * @param map
	 * @param file
	 */
	public ReducedGraphImpl(Map map, File file)  throws GsException{
		
	    this( true);
        ReducedGraphParser parser = new ReducedGraphParser();
        parser.parse(file, map, this);
	}

	/**
	 * @param filename
	 */
	public ReducedGraphImpl( boolean parsing) {
		
        super( parsing);
	}
	/**
     * 
     */
    public ReducedGraphImpl() {
    	
        this( false);
    }

    
	/**
	 * Return the zip extension for the graph type
	 * 
	 * @return the zip extension for the graph type
	 */
    @Override
	public String getGraphZipName(){
		
		return GRAPH_ZIP_NAME;
		
	}
    
    /**
     * Return 0 since no node order is defined on this king of graph
     * 
     * @return 0 since no node order is defined on this king of graph
     */
    @Override
	public int getNodeOrderSize(){
		
		return 0;
	}
    
    /**
     * Indicates if the given graph can be associated to the current one
     * 
     * @param graph the graph to associate to the current one
     * @return true is association is possible, false if not
     */
    @Override
    protected boolean isAssociationValid( Graph<?, ?> graph) {
    	
		return true;
    }

    @Override
	protected void doSave(OutputStreamWriter os, Collection<NodeReducedData> vertices, Collection<Edge<NodeReducedData>> edges, int mode) throws GsException {
        try {
            XMLWriter out = new XMLWriter(os, GinmlHelper.DEFAULT_URL_DTD_FILE);
	  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
			out.write("\t<graph id=\"" + graphName + "\"");
			out.write(" class=\"reduced\">\n");
			saveNodes(out, mode, vertices);
			saveEdge(out, mode, edges);
            if (graphAnnotation != null) {
            	graphAnnotation.toXML(out, null, 0);
            }
            // save the ref of the associated regulatory graph!
            if (associatedGraph != null) {
                associatedID = GraphManager.getInstance().getGraphPath( associatedGraph);
            }
            if (associatedID != null) {
                out.write("<link xlink:href=\""+associatedID+"\"/>\n");
            }
	  		out.write("\t</graph>\n");
	  		out.write("</gxl>\n");
        } catch (IOException e) {
            throw new GsException( "STR_unableToSave", e);
        }
	}

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveEdge(XMLWriter out, int mode, Collection<Edge<NodeReducedData>> edges) throws IOException {
    	if (edges == null) {
    		edges = getEdges();
    	}
        switch (mode) {
        	default:
		        for (Edge<NodeReducedData> edge: edges) {
		            NodeReducedData source = edge.getSource();
		            NodeReducedData target = edge.getTarget();
		            out.write("\t\t<edge id=\""+ source +"_"+target+"\" from=\""+source+"\" to=\""+target+"\"/>\n");
		        }
		        break;
        }
    }

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveNodes(XMLWriter out, int mode, Collection<NodeReducedData> vertices) throws IOException {
    	if (vertices == null) {
    		vertices = getNodes();
    	}
    	NodeAttributesReader vReader = getNodeAttributeReader();
    	switch (mode) {
    		case 1:

                for (NodeReducedData vertex: vertices) {
                    String content = vertex.getContentString();
                    out.write("\t\t<node id=\""+vertex+"\">\n");
                    out.write("<attr name=\"content\"><string>"+content+"</string></attr>");
                    out.write(GinmlHelper.getShortNodeVS(vReader));
                    out.write("\t\t</node>\n");
                }
    			break;
			case 2:
                for (NodeReducedData vertex: vertices) {
                    vReader.setNode(vertex);
                    String content = ((NodeReducedData)vertex).getContentString();
                    out.write("\t\t<node id=\""+vertex+"\">\n");
                    out.write("<attr name=\"content\"><string>"+content+"</string></attr>");
                    out.write(GinmlHelper.getFullNodeVS(vReader));
                    out.write("\t\t</node>\n");
                }
    			break;
    		default:
                for (NodeReducedData vertex: vertices) {
                    String content = vertex.getContentString();
                    out.write("\t\t<node id=\""+vertex+"\">\n");
                    out.write("<attr name=\"content\"><string>"+content+"</string></attr>");
                    out.write("</node>");
    	        }
        }
    }
	
	/**
	 * add an edge to this graph.
	 * @param source source node of this edge.
	 * @param target target node of this edge.
	 */
    @Override
	public void addEdge(NodeReducedData source, NodeReducedData target) {
		Edge<NodeReducedData> edge = new Edge<NodeReducedData>(this, source, target);
		addEdge( edge);
	}
	
    @Override
    protected List doMerge(Graph otherGraph) {
        return null;
    }
    
    @Override
    public Graph getSubgraph(Collection vertex, Collection edges) {
        // no copy for reduced graphs
        return null;
    }

    /**
     * @return a map referencing all real nodes in the selected CC
     */
    @Override
    public Set<String> getSelectedSet(Collection<NodeReducedData> selection) {
        Set set = new HashSet();
        for (NodeReducedData node: selection) {
            Vector content = node.getContent();
            for (int i=0 ; i<content.size() ; i++) {
                set.add(content.get(i).toString());
            }
        }
        return set;
    }
}
